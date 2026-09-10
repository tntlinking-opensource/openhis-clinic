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
	private static final Pattern COLUMN_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_`.]+$");

	/**
	 * 查询类型白名单。
	 * 说明：与 SearchParams.QUERY_TYPE_LIST 保持一致，额外纳入 "<=>"、"not left_like"、"not right_like"，
	 * 确保所有合法查询类型都能通过校验，避免合法请求被误判为格式错误。
	 */
	private static final java.util.Set<String> ALLOWED_QUERY_TYPES = new java.util.HashSet<>(java.util.Arrays.asList(
			"=", "!=", "<>", "<=>", ">", "<", ">=", "<=",
			"like", "not like", "left_like", "right_like",
			"not left_like", "not right_like",
			"in", "not in",
			"between", "not between",
			"is null", "is not null",
			"(", ")"
	));

	/**
	 * 逻辑连接符白名单。
	 * 注意：必须包含空串 "" —— 前端组合条件时（baseUI.compositeCondition / pushDataPermissions）
	 * 会把第一个条件以及括号类条件（queryType 为 '(' 或 ')'）的 logic 置为 ''，
	 * 若不含 ''，合法请求会在反序列化阶段被拦截，触发 400「请求体格式错误，请检查JSON格式」。
	 */
	private static final java.util.Set<String> ALLOWED_LOGIC = new java.util.HashSet<>(java.util.Arrays.asList(
			"AND", "OR", "and", "or", ""
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
		// 空白值（null 或空串）直接放行，与 ALLOWED_LOGIC 中保留的 '' 语义保持一致
		if (logic != null && !logic.trim().isEmpty() && !ALLOWED_LOGIC.contains(logic)) {
			throw new IllegalArgumentException("Invalid logic value: " + logic);
		}
		this.logic = logic;
	}


	public String getColumnName() {
		return columnName;
	}


	public void setColumnName(String columnName) {
		// 空白值（null 或空串）直接放行，合法性校验交由 SearchParams.getParams() 统一处理
		if (columnName != null && !columnName.trim().isEmpty() && !COLUMN_NAME_PATTERN.matcher(columnName).matches()) {
			throw new IllegalArgumentException("Invalid column name: " + columnName);
		}
		this.columnName = columnName;
	}


	public String getQueryType() {
		return queryType;
	}


	public void setQueryType(String queryType) {
		// 空白值（null 或空串）直接放行；SearchParams 侧已对 queryType 做 trim 校验
		if (queryType != null && !queryType.trim().isEmpty() && !ALLOWED_QUERY_TYPES.contains(queryType.trim().toLowerCase())) {
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
