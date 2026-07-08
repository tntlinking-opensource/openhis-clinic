package com.geeke.utils.excel;

/**
 * Excel导入错误信息
 */
public class ImportError {

    /** 行号（从1开始，对应Excel中的实际行号） */
    private int rowNum;

    /** 字段名（中文） */
    private String fieldName;

    /** 错误信息 */
    private String message;

    public ImportError(int rowNum, String fieldName, String message) {
        this.rowNum = rowNum;
        this.fieldName = fieldName;
        this.message = message;
    }

    public int getRowNum() {
        return rowNum;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "第" + rowNum + "行" + (fieldName != null ? "[" + fieldName + "]" : "") + "：" + message;
    }
}
