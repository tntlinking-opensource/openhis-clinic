package com.geeke.common.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.geeke.common.data.Parameter;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import org.apache.commons.collections.CollectionUtils;

/**
 * 查询参数
 * @author alec
 *
 */
public class SearchParams implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int offset = 0;
    private int limit = 10;
    private String columnName; // 排序字段名（别名）
	private String order; // 排序
	private static String[] ORDER_LIST = {"desc", "asc", ""}; // 排序符号
	private static String[] LOGIC_LIST = {"and", "or", ""}; // 逻辑操作符
	private static String[] QUERY_TYPE_LIST = {"(", ")", "=", "<=>", "between", "left_like",
			"like", "right_like","<=", "!=", "<>", ">", ">=", "<", "in", "not in", "not between",
			"not left_like", "not like", "not right_like", "is null", "is not null"}; // 运算操作符


	private List<Parameter> params = null;
    
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {

		this.columnName = columnName;
	}

	public String getOrderby() {
		// 排序只存在三种情况：“”， ASC，DESC
		if (StringUtils.isNotBlank(this.columnName)) {
			String columnName = this.columnName.trim();
			if (columnName.indexOf("`") >= 0) {
				throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_60001, "排序字段包含非法字符"));
			}
			if (StringUtils.isNotBlank(this.order)) {
				if (!Arrays.asList(ORDER_LIST).contains(this.order.toLowerCase(Locale.ROOT).trim())) {
					throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_60001, "排序方式包含非法字符"));
				} else {
					return "`" + columnName + "` " + this.order;
				}
			} else {
				return "`" + columnName + "`";
			}
		}
		return null;
	}

	public List<Parameter> getParams() {
		if (null != params && params.size() > 0) {
			for (int i = 0; i < params.size(); i++) {
				if (!Arrays.asList(LOGIC_LIST).contains(params.get(i).getLogic().toLowerCase().trim())) {
					throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_60001, "逻辑操作符包含非法字符"));
				}
				String queryType = params.get(i).getQueryType().replaceAll(" +"," ").trim();
				if (!Arrays.asList(QUERY_TYPE_LIST).contains(queryType.toLowerCase())) {
					throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_60001, "运算操作符包含非法字符"));
				}

				String columnName = params.get(i).getColumnName();
				if (StringUtils.isNotBlank(columnName) && StringUtils.isNotBlank(columnName.trim())) {
					columnName = columnName.trim();
					if (columnName.indexOf("`") >= 0) {
						throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_60001, "条件字段名包含非法字符"));
					} else {
						int index = columnName.lastIndexOf(".");
						if (index >= 0) {
							String before = columnName.substring(0, index + 1);
							String after = columnName.substring(index + 1);
							columnName = before + "`" + after + "`";
						} else {
							columnName = "`" + columnName + "`";
						}
					}
				}
				params.get(i).setColumnName(columnName);

			}
		}
		return params;
	}

	public List<Parameter> getNewParams() {
		return this.params;
	}
	public void setParams(List<Parameter> params) {
		this.params = params;
	}
}
