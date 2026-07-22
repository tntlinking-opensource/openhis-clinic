package com.geeke.utils.excel;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入结果
 *
 * @param <T> 实体类型
 */
public class ExcelImportResult<T> {

    /** 成功解析的数据列表 */
    private final List<T> dataList;

    /** 错误信息列表 */
    private final List<ImportError> errors;

    /** 成功数 */
    private int successCount;

    /** 失败数 */
    private int failCount;

    public ExcelImportResult() {
        this.dataList = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public List<T> getDataList() {
        return dataList;
    }

    public List<ImportError> getErrors() {
        return errors;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void addSuccess(T data) {
        dataList.add(data);
        successCount++;
    }

    public void addError(ImportError error) {
        errors.add(error);
        failCount++;
    }

    /**
     * 是否有错误
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * 获取所有错误信息的文本摘要
     */
    public String getErrorSummary() {
        StringBuilder sb = new StringBuilder();
        for (ImportError error : errors) {
            sb.append(error.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * 转换为前端期望的格式 [成功数, 失败数, 错误信息]
     */
    public List<String> toResponseList() {
        List<String> response = new ArrayList<>();
        response.add(String.valueOf(successCount));
        response.add(String.valueOf(failCount));
        response.add(getErrorSummary());
        return response;
    }
}
