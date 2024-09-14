package com.geeke.common.data;


/**
 * 请求数据筛选条件
 * 
 * @author lys
 * @date 2020/06/27
 */
public final class Parameter {
	private String logic = "AND";
	private String columnName;
	private String queryType;
	private Object value;

	public Parameter() {

	}

	
	public Parameter(String columnName, String queryType, Object value) {
		this(columnName, queryType, value, "AND");
	}

	public Parameter(String columnName, String queryType, Object value, String logic) {
		this.columnName = columnName;
		this.queryType = queryType; 
		this.value = value;
		this.logic = logic;
	}


	public String getLogic() {
		return logic;
	}


	public void setLogic(String logic) {
		this.logic = logic;
	}


	public String getColumnName() {
		return columnName;
	}


	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}


	public String getQueryType() {
		return queryType;
	}


	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}


	public Object getValue() {
		return value;
	}


	public void setValue(Object value) {
		this.value = value;
	}	
	
}
