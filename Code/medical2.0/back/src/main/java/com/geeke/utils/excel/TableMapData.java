package com.geeke.utils.excel;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * @作者 lc
 * @创建时间 2022年11月16日 10:31
 */
public class TableMapData {

    public String sheetName;

    public List<String> headers;

    public List<String> headersIndex;
    public List<Map<String,Object>> contents;

    /**
     * excel
     * key 为excel字段用headersIndex的值
     * value 是excel 合并的单元格4个数据，第5个是合并单元格名字
     *
     */
    public Map<String,List<String>> mergeCells = null;
    public TableMapData() {
        this.headers = Lists.newArrayList();
        this.headersIndex = Lists.newArrayList();
        this.contents = Lists.newArrayList();
    }

    @Override
    public String toString() {
        return "TableMapData{" +
                "sheetName='" + sheetName + '\'' +
                ", headers=" + headers +
                ", headersIndex=" + headersIndex +
                ", contents=" + contents +
                '}';
    }
}
