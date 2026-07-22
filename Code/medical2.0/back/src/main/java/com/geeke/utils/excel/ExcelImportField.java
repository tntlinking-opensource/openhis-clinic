package com.geeke.utils.excel;

import java.lang.annotation.*;

/**
 * Excel导入字段映射注解
 * 标注实体字段与 Excel 列的映射关系
 *
 * <p>使用示例:</p>
 * <pre>{@code
 * public class Drug {
 *     @ExcelImportField(columnIndex = 0, required = true, maxLength = 128)
 *     private String name;
 *
 *     @ExcelImportField(columnIndex = 3, dictTypeId = "1004078055755374603")
 *     private DictItem type;
 * }
 * }</pre>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelImportField {

    /** Excel列号（从0开始） */
    int columnIndex();

    /** 是否必填，默认false */
    boolean required() default false;

    /** 字符串最大长度，默认不限制 */
    int maxLength() default -1;

    /** 字典类型ID（用于自动解析字典项名称到DictItem） */
    String dictTypeId() default "";

    /** 默认值（当单元格为空时使用） */
    String defaultValue() default "";

    /** 字段中文名（用于错误提示） */
    String label() default "";
}
