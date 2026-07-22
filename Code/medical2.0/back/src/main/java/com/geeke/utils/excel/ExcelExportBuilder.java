package com.geeke.utils.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;

import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Excel导出构建器 - 基于 EasyExcel，流式API
 *
 * <p>使用示例:</p>
 * <pre>{@code
 * new ExcelExportBuilder("药品明细")
 *     .addColumns(
 *         new ExcelExportBuilder.Column("药品名称", "drugName"),
 *         new ExcelExportBuilder.Column("规格", "spec"),
 *         new ExcelExportBuilder.Column("数量", "quantity")
 *     )
 *     .data(dataList)
 *     .write(response, "药品明细.xlsx");
 * }</pre>
 *
 * <p>支持嵌套属性访问，例如 "company.name" 将调用 getCompany().getName()。</p>
 * <p>基于 EasyExcel，内存占用低，适合大数据量导出。</p>
 */
public class ExcelExportBuilder implements Closeable {

    private String sheetName;
    private final List<Column> columns = new ArrayList<>();
    private List<?> dataList;
    private OutputStream outputStream;

    public ExcelExportBuilder(String sheetName) {
        this.sheetName = sheetName;
    }

    public ExcelExportBuilder addColumns(Column... cols) {
        this.columns.addAll(Arrays.asList(cols));
        return this;
    }

    public ExcelExportBuilder addColumns(List<Column> cols) {
        this.columns.addAll(cols);
        return this;
    }

    public ExcelExportBuilder data(List<?> dataList) {
        this.dataList = dataList;
        return this;
    }

    /**
     * 将Excel写入HttpServletResponse，触发浏览器下载
     */
    public void write(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())
                .replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

        try (OutputStream os = response.getOutputStream()) {
            this.outputStream = os;
            buildAndWrite(os);
        }
    }

    /**
     * 将Excel写入指定的输出流
     */
    public void writeTo(OutputStream outputStream) throws IOException {
        this.outputStream = outputStream;
        buildAndWrite(outputStream);
    }

    private void buildAndWrite(OutputStream os) {
        if (columns.isEmpty()) {
            throw new IllegalStateException("未定义任何列，请先调用 addColumns() 方法");
        }

        // 构建表头
        List<List<String>> head = new ArrayList<>();
        for (Column col : columns) {
            List<String> headRow = new ArrayList<>();
            headRow.add(col.getHeaderName());
            head.add(headRow);
        }

        // 构建数据（将对象/Map转换为List<List<Object>>）
        List<List<Object>> data = new ArrayList<>();
        if (dataList != null) {
            for (Object rowData : dataList) {
                List<Object> row = new ArrayList<>();
                for (Column col : columns) {
                    row.add(resolveValue(rowData, col.getFieldPath()));
                }
                data.add(row);
            }
        }

        // 写入 Excel
        EasyExcel.write(os)
                .head(head)
                .sheet(sheetName)
                .doWrite(data);
    }

    /**
     * 解析对象的属性值，支持嵌套属性和Map
     */
    private Object resolveValue(Object obj, String fieldPath) {
        if (obj == null || fieldPath == null) return null;

        if (obj instanceof Map) {
            return resolveMapValue((Map<?, ?>) obj, fieldPath);
        }

        String[] parts = fieldPath.split("\\.");
        Object current = obj;
        for (String part : parts) {
            if (current == null) return null;
            current = invokeGetter(current, part);
        }
        return current;
    }

    private Object resolveMapValue(Map<?, ?> map, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        Object current = map;
        for (String part : parts) {
            if (current == null) return null;
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                current = invokeGetter(current, part);
            }
        }
        return current;
    }

    private Object invokeGetter(Object obj, String methodName) {
        Class<?> clazz = obj.getClass();
        String capitalized = Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);

        try {
            return clazz.getMethod("get" + capitalized).invoke(obj);
        } catch (Exception e1) {
            try {
                return clazz.getMethod("is" + capitalized).invoke(obj);
            } catch (Exception e2) {
                try {
                    java.lang.reflect.Field field = clazz.getDeclaredField(methodName);
                    field.setAccessible(true);
                    return field.get(obj);
                } catch (Exception e3) {
                    return null;
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        // EasyExcel 自动管理资源
    }

    /**
     * 列定义
     */
    public static class Column {
        private final String headerName;
        private final String fieldPath;
        private int width = 0;

        public Column(String headerName, String fieldPath) {
            this.headerName = headerName;
            this.fieldPath = fieldPath;
        }

        public Column(String headerName, String fieldPath, int width) {
            this.headerName = headerName;
            this.fieldPath = fieldPath;
            this.width = width;
        }

        public String getHeaderName() { return headerName; }
        public String getFieldPath() { return fieldPath; }
        public int getWidth() { return width; }
    }
}
