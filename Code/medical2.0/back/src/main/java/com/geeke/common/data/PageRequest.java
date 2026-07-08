package com.geeke.common.data;

import java.util.List;
import java.util.regex.Pattern;

import com.geeke.common.persistence.BaseEntity;
import com.geeke.gen.utils.GenConfigure;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

/**
 * 分页请求类
 * 
 * @author Lining
 * @date 2017/11/7
 */
public final class PageRequest implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	/** ORDER BY 白名单正则（预编译） */
	private static final Pattern ORDER_BY_PATTERN = Pattern.compile(
			"^[a-z_`][a-z0-9_`.]*\\s+(asc|desc)(,\\s*[a-z_`][a-z0-9_`.]*\\s+(asc|desc))*$");

	private int offset;

	private int limit;

	private String order;

	private String id;

	private String institution;


	private List<Parameter> params;
	
	
	public PageRequest(List<Parameter> parameters) {
		this(0, 1, parameters, null, null, null);
	}

	public PageRequest(List<Parameter> parameters, String order) {
		this(0, 1, parameters, order, null, null);
	}

	public PageRequest(List<Parameter> parameters, String order, String id) {
		this(0, 1, parameters, order, id, null);
	}

	public PageRequest(List<Parameter> parameters, String order, String id, String institution) {
		this(0, 1, parameters, order, id, institution);
	}

	public PageRequest(int offset, int limit, List<Parameter> parameters, String order) {
		this(offset, limit, parameters, order, null, null);
	}

	public PageRequest(int offset, int limit, List<Parameter> parameters, String order, String id) {
		this(offset, limit, parameters, order, id, null);
	}

	/**
	 * 全参数构造器 — 所有其他构造器最终委托到此
	 */
	public PageRequest(int offset, int limit, List<Parameter> parameters, String order, String id, String institution) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must not be less than zero!");
		}
		if (limit < 1) {
			throw new IllegalArgumentException("Limit must not be less than one!");
		}
		this.offset = offset;
		this.limit = limit;
		this.params = parameters != null ? parameters : Lists.newArrayList();
		this.order = sanitizeOrderBy(order);
		this.id = id;
		this.institution = institution;
	}

	public PageRequest() {
		this.params = Lists.newArrayList();
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPage() {
		return this.offset;
	}

	public int getSize() {
		return this.limit;
	}

	public String getOrder() {
		return this.order;
	}

	public int getOffset() {
		return this.offset;
	}
	
	public List<Parameter> getParams() {
		return params;
	}

	public void setParams(List<Parameter> params) {
		this.params = params;
	}

	/**
	 * 获取数据库名称
	 */
	public String getDbName(){
		return GenConfigure.dbName;
	}

	
	/* 数据库存储状态 */
	public String getDEL_FLAG_NORMAL() {
		return BaseEntity.DEL_FLAG_NORMAL;
	}

	public String getDEL_FLAG_DELETE() {
		return BaseEntity.DEL_FLAG_DELETE;
	}

	public String getDEL_FLAG_AUDIT() {
		return BaseEntity.DEL_FLAG_AUDIT;
	}

	/**
	 * SQL注入防护：白名单校验ORDER BY子句
	 * 只允许字母、数字、下划线、点、反引号，以及ASC/DESC关键字（不区分大小写）
	 */
	private static String sanitizeOrderBy(String orderby) {
		if (StringUtils.isBlank(orderby)) {
			return "";
		}
		// 转换为小写进行校验，允许反引号包围的列名
		String orderbyLower = orderby.toLowerCase().trim();
		// 校验ORDER BY格式：column_name ASC/DESC, column_name2 ASC/DESC
		if (!ORDER_BY_PATTERN.matcher(orderbyLower).matches()) {
			throw new IllegalArgumentException("Invalid order by clause: " + orderby);
		}
		return orderby;
	}

}
