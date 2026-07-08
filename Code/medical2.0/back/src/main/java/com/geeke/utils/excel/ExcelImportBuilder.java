package com.geeke.utils.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.geeke.sys.entity.DictItem;
import com.geeke.sys.service.DictTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * Excel导入构建器 - 基于 EasyExcel，注解驱动
 *
 * <p>使用示例:</p>
 * <pre>{@code
 * ExcelImportResult<Drug> result = new ExcelImportBuilder<>(Drug.class, inputStream)
 *     .dictTypeService(dictTypeService)
 *     .skipRows(2)
 *     .read();
 * }</pre>
 *
 * @param <T> 实体类型
 */
public class ExcelImportBuilder<T> implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportBuilder.class);

    private final Class<T> entityClass;
    private final InputStream inputStream;
    private DictTypeService dictTypeService;
    private int skipRows = 0;

    // 列号 -> 映射信息
    private final Map<Integer, ColumnMapping> columnMapping = new LinkedHashMap<>();

    public ExcelImportBuilder(Class<T> entityClass, InputStream inputStream) {
        this.entityClass = entityClass;
        this.inputStream = inputStream;
        initColumnMapping();
    }

    public ExcelImportBuilder<T> dictTypeService(DictTypeService dictTypeService) {
        this.dictTypeService = dictTypeService;
        return this;
    }

    public ExcelImportBuilder<T> skipRows(int skipRows) {
        this.skipRows = skipRows;
        return this;
    }

    /**
     * 读取并解析Excel
     */
    public ExcelImportResult<T> read() {
        ExcelImportResult<T> result = new ExcelImportResult<>();

        // 使用 EasyExcel 逐行读取
        EasyExcel.read(inputStream, new ReadListener<Map<Integer, String>>() {
            private int currentRow = 0;

            @Override
            public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
                currentRow++;
                if (currentRow <= skipRows) return;

                int rowNum = context.readRowHolder().getRowIndex() + 1;

                try {
                    T entity = entityClass.newInstance();
                    boolean hasError = false;

                    for (Map.Entry<Integer, ColumnMapping> entry : columnMapping.entrySet()) {
                        int colIdx = entry.getKey();
                        ColumnMapping mapping = entry.getValue();
                        String cellValue = rowData.get(colIdx);
                        String label = mapping.getLabel();

                        // 必填校验
                        if (mapping.annotation.required() && isBlank(cellValue)) {
                            result.addError(new ImportError(rowNum, label, "不能为空"));
                            hasError = true;
                            continue;
                        }

                        // 跳过空值
                        if (isBlank(cellValue)) {
                            if (!mapping.annotation.defaultValue().isEmpty()) {
                                mapping.setValue(entity, mapping.annotation.defaultValue());
                            }
                            continue;
                        }

                        // 长度校验
                        if (mapping.annotation.maxLength() > 0 && cellValue.length() > mapping.annotation.maxLength()) {
                            result.addError(new ImportError(rowNum, label,
                                    "长度不能超过" + mapping.annotation.maxLength() + "个字符"));
                            hasError = true;
                            continue;
                        }

                        // 设置字段值
                        try {
                            Object value = convertValue(cellValue, mapping);
                            mapping.setValue(entity, value);
                        } catch (Exception e) {
                            result.addError(new ImportError(rowNum, label,
                                    "格式不正确：" + e.getMessage()));
                            hasError = true;
                        }
                    }

                    if (!hasError) {
                        result.addSuccess(entity);
                    }
                } catch (Exception e) {
                    logger.warn("解析第{}行失败", rowNum, e);
                    result.addError(new ImportError(rowNum, null, "解析失败：" + e.getMessage()));
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 读取完成
            }
        }).sheet().doRead();

        return result;
    }

    /**
     * 初始化列映射（扫描 @ExcelImportField 注解，支持字段和getter）
     */
    private void initColumnMapping() {
        List<Class<?>> classChain = new ArrayList<>();
        Class<?> clazz = entityClass;
        while (clazz != null && clazz != Object.class) {
            classChain.add(0, clazz);
            clazz = clazz.getSuperclass();
        }

        for (Class<?> c : classChain) {
            // 扫描字段
            for (Field field : c.getDeclaredFields()) {
                ExcelImportField annotation = field.getAnnotation(ExcelImportField.class);
                if (annotation != null) {
                    field.setAccessible(true);
                    columnMapping.put(annotation.columnIndex(), new FieldColumnMapping(field, annotation));
                }
            }

            // 扫描getter方法
            for (Method method : c.getDeclaredMethods()) {
                ExcelImportField annotation = method.getAnnotation(ExcelImportField.class);
                if (annotation != null && isGetter(method)) {
                    Method setter = findSetter(c, method);
                    if (setter != null) {
                        setter.setAccessible(true);
                        columnMapping.put(annotation.columnIndex(),
                                new MethodColumnMapping(setter, method.getReturnType(), annotation));
                    }
                }
            }
        }
    }

    private boolean isGetter(Method method) {
        String name = method.getName();
        return (name.startsWith("get") && name.length() > 3 && method.getParameterCount() == 0)
                || (name.startsWith("is") && name.length() > 2 && method.getParameterCount() == 0);
    }

    private Method findSetter(Class<?> clazz, Method getter) {
        String setterName = getter.getName().startsWith("get")
                ? "set" + getter.getName().substring(3)
                : "set" + getter.getName().substring(2);
        try {
            return clazz.getMethod(setterName, getter.getReturnType());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Object convertValue(String cellValue, ColumnMapping mapping) {
        Class<?> fieldType = mapping.getFieldType();
        String dictTypeId = mapping.annotation.dictTypeId();

        // 字典项处理
        if (!dictTypeId.isEmpty() && fieldType == DictItem.class) {
            if (dictTypeService == null) {
                throw new IllegalStateException("使用字典解析时必须设置 dictTypeService");
            }
            String dictValue = dictTypeService.getValue(cellValue, dictTypeId);
            if (dictValue == null) {
                throw new IllegalArgumentException("字典项[" + cellValue + "]不存在");
            }
            DictItem dictItem = new DictItem();
            dictItem.setName(cellValue);
            dictItem.setValue(dictValue);
            return dictItem;
        }

        // 基本类型转换
        if (fieldType == String.class) {
            return cellValue;
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return Integer.parseInt(cellValue.trim());
        } else if (fieldType == Long.class || fieldType == long.class) {
            return Long.parseLong(cellValue.trim());
        } else if (fieldType == Double.class || fieldType == double.class) {
            return Double.parseDouble(cellValue.trim());
        } else if (fieldType == BigDecimal.class) {
            return new BigDecimal(cellValue.trim());
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return "是".equals(cellValue) || "1".equals(cellValue) || "true".equalsIgnoreCase(cellValue);
        }

        return cellValue;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public void close() throws IOException {
        if (inputStream != null) inputStream.close();
    }

    // ---- 内部类 ----

    private static abstract class ColumnMapping {
        final ExcelImportField annotation;
        ColumnMapping(ExcelImportField annotation) { this.annotation = annotation; }
        abstract Class<?> getFieldType();
        abstract void setValue(Object entity, Object value) throws Exception;
        String getLabel() {
            return annotation.label().isEmpty() ? "列" + annotation.columnIndex() : annotation.label();
        }
    }

    private static class FieldColumnMapping extends ColumnMapping {
        final Field field;
        FieldColumnMapping(Field field, ExcelImportField annotation) {
            super(annotation);
            this.field = field;
        }
        Class<?> getFieldType() { return field.getType(); }
        void setValue(Object entity, Object value) throws Exception { field.set(entity, value); }
    }

    private static class MethodColumnMapping extends ColumnMapping {
        final Method setter;
        final Class<?> fieldType;
        MethodColumnMapping(Method setter, Class<?> fieldType, ExcelImportField annotation) {
            super(annotation);
            this.setter = setter;
            this.fieldType = fieldType;
        }
        Class<?> getFieldType() { return fieldType; }
        void setValue(Object entity, Object value) throws Exception { setter.invoke(entity, value); }
    }
}
