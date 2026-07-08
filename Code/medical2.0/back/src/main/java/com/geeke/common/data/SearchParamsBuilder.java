package com.geeke.common.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询参数构建器
 * 提供流式API构建查询参数，提高代码可读性
 *
 * 使用示例：
 * List<Parameter> params = SearchParamsBuilder.create()
 *     .eq("company_id", companyId)
 *     .like("name", name)
 *     .like("linkman", linkman)
 *     .eq("type", type)
 *     .build();
 */
public class SearchParamsBuilder {

    private final List<Parameter> parameters;

    private SearchParamsBuilder() {
        this.parameters = new ArrayList<>();
    }

    /**
     * 创建新的构建器实例
     */
    public static SearchParamsBuilder create() {
        return new SearchParamsBuilder();
    }

    /**
     * 创建带租户过滤的构建器实例
     */
    public static SearchParamsBuilder createWithTenant(String companyId) {
        return new SearchParamsBuilder().eq("company_id", companyId);
    }

    /**
     * 等于查询
     */
    public SearchParamsBuilder eq(String columnName, Object value) {
        if (value != null) {
            parameters.add(new Parameter(columnName, "=", value));
        }
        return this;
    }

    /**
     * 不等于查询
     */
    public SearchParamsBuilder ne(String columnName, Object value) {
        if (value != null) {
            parameters.add(new Parameter(columnName, "!=", value));
        }
        return this;
    }

    /**
     * 模糊查询
     */
    public SearchParamsBuilder like(String columnName, Object value) {
        if (value != null && !"".equals(value.toString().trim())) {
            parameters.add(new Parameter(columnName, "like", value));
        }
        return this;
    }

    /**
     * 左模糊查询
     */
    public SearchParamsBuilder leftLike(String columnName, Object value) {
        if (value != null && !"".equals(value.toString().trim())) {
            parameters.add(new Parameter(columnName, "left_like", value));
        }
        return this;
    }

    /**
     * 右模糊查询
     */
    public SearchParamsBuilder rightLike(String columnName, Object value) {
        if (value != null && !"".equals(value.toString().trim())) {
            parameters.add(new Parameter(columnName, "right_like", value));
        }
        return this;
    }

    /**
     * 大于查询
     */
    public SearchParamsBuilder gt(String columnName, Object value) {
        if (value != null) {
            parameters.add(new Parameter(columnName, ">", value));
        }
        return this;
    }

    /**
     * 大于等于查询
     */
    public SearchParamsBuilder ge(String columnName, Object value) {
        if (value != null) {
            parameters.add(new Parameter(columnName, ">=", value));
        }
        return this;
    }

    /**
     * 小于查询
     */
    public SearchParamsBuilder lt(String columnName, Object value) {
        if (value != null) {
            parameters.add(new Parameter(columnName, "<", value));
        }
        return this;
    }

    /**
     * 小于等于查询
     */
    public SearchParamsBuilder le(String columnName, Object value) {
        if (value != null) {
            parameters.add(new Parameter(columnName, "<=", value));
        }
        return this;
    }

    /**
     * IN查询
     */
    public SearchParamsBuilder in(String columnName, List<?> values) {
        if (values != null && !values.isEmpty()) {
            parameters.add(new Parameter(columnName, "in", values));
        }
        return this;
    }

    /**
     * NOT IN查询
     */
    public SearchParamsBuilder notIn(String columnName, List<?> values) {
        if (values != null && !values.isEmpty()) {
            parameters.add(new Parameter(columnName, "not in", values));
        }
        return this;
    }

    /**
     * BETWEEN查询
     */
    public SearchParamsBuilder between(String columnName, Object start, Object end) {
        if (start != null && end != null) {
            List<Object> value = new ArrayList<>();
            value.add(start);
            value.add(end);
            parameters.add(new Parameter(columnName, "between", value));
        }
        return this;
    }

    /**
     * IS NULL查询
     */
    public SearchParamsBuilder isNull(String columnName) {
        parameters.add(new Parameter(columnName, "is null", null));
        return this;
    }

    /**
     * IS NOT NULL查询
     */
    public SearchParamsBuilder isNotNull(String columnName) {
        parameters.add(new Parameter(columnName, "is not null", null));
        return this;
    }

    /**
     * 右模糊匹配（用于树形结构查询）
     */
    public SearchParamsBuilder rightLikeForTree(String columnName, Object value) {
        if (value != null && !"".equals(value.toString().trim())) {
            parameters.add(new Parameter(columnName, "right_like", value));
        }
        return this;
    }

    /**
     * 添加自定义参数
     */
    public SearchParamsBuilder add(Parameter parameter) {
        if (parameter != null) {
            parameters.add(parameter);
        }
        return this;
    }

    /**
     * 添加OR逻辑参数
     */
    public SearchParamsBuilder or(String columnName, String queryType, Object value) {
        if (value != null) {
            parameters.add(new Parameter(columnName, queryType, value, "OR"));
        }
        return this;
    }

    /**
     * 构建参数列表
     */
    public List<Parameter> build() {
        return new ArrayList<>(parameters);
    }
}
