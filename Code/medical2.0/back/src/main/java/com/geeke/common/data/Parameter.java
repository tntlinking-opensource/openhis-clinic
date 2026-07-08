package com.geeke.common.data;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 请求数据筛选条件
 *
 * @author lys
 * @date 2020/06/27
 */
public final class Parameter {

	/**
	 * 列名白名单：只允许字母、数字、下划线、点号、反引号
	 * 例如: company_id, a.name, `company_id`
	 */
	private static final Pattern COLUMN_NAME_PATTERN = Pattern.compile("^[a-zA-Z_`][a-zA-Z0-9_`.]*$");

	/**
	 * 查询类型白名单
	 */
	private static final java.util.Set<String> ALLOWED_QUERY_TYPES = new java.util.HashSet<>(java.util.Arrays.asList(
			"=", "!=", "<>", ">", "<", ">=", "<=",
			"like", "not like", "left_like", "right_like",
			"in", "not in",
			"between", "not between",
			"is null", "is not null",
			"(", ")"
	));

	/**
	 * 逻辑连接符白名单
	 */
	private static final java.util.Set<String> ALLOWED_LOGIC = new java.util.HashSet<>(java.util.Arrays.asList(
			"AND", "OR", "and", "or"
	));

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
		setColumnName(columnName);
		setQueryType(queryType);
		this.value = value;
		setLogic(logic);
	}


	public String getLogic() {
		return logic;
	}


	public void setLogic(String logic) {
		if (logic != null && !ALLOWED_LOGIC.contains(logic)) {
			throw new IllegalArgumentException("Invalid logic value: " + logic);
		}
		this.logic = logic;
	}


	public String getColumnName() {
		return columnName;
	}


	public void setColumnName(String columnName) {
		if (columnName != null && !COLUMN_NAME_PATTERN.matcher(columnName).matches()) {
			throw new IllegalArgumentException("Invalid column name: " + columnName);
		}
		this.columnName = columnName;
	}


	public String getQueryType() {
		return queryType;
	}


	public void setQueryType(String queryType) {
		if (queryType != null && !ALLOWED_QUERY_TYPES.contains(queryType.toLowerCase())) {
			throw new IllegalArgumentException("Invalid query type: " + queryType);
		}
		this.queryType = queryType;
	}


	public Object getValue() {
		return value;
	}


	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * 从参数列表中提取并移除 company_id 参数，返回其值。
	 * 兼容 company_id 和 `company_id` 两种写法。
	 * @param parameters 查询参数列表
	 * @return company_id 的值，如果不存在则返回 null
	 */
	public static String extractAndRemoveCompanyId(List<Parameter> parameters) {
		if (parameters == null) {
			return null;
		}
		Optional<Parameter> optional = parameters.stream()
				.filter(item -> "company_id".equals(item.getColumnName()) || "`company_id`".equals(item.getColumnName()))
				.findFirst();
		if (optional.isPresent()) {
			String id = (String) optional.get().getValue();
			parameters.removeIf(item -> "company_id".equals(item.getColumnName()) || "`company_id`".equals(item.getColumnName()));
			return id;
		}
		return null;
	}

}
