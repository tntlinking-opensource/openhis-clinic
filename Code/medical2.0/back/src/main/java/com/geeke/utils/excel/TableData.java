package com.geeke.utils.excel;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 
 * @author lc
 *
 */
public class TableData {

	public String sheetName;
	
	public List<String> headers;
	
	public List<List<String>> contents;


	//
	public TableData() {
		headers = Lists.newArrayList();
		contents = Lists.newArrayList();
	}

	@Override
	public String toString() {
		return "TableData{" +
				"sheetName='" + sheetName + '\'' +
				", headers=" + headers +
				", contents=" + contents +
				'}';
	}
}
