package com.geeke.utils;

import com.geeke.common.data.Parameter;
import com.geeke.gen.utils.GenConfigure;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * 新增租户相关概念
 */
public final class PageRequestClinic implements Serializable {
    private static final long serialVersionUID = 1L;
    private int offset;
    private int limit;
    private String order;
    private String tenantId;
    private List<Parameter> params;

    public PageRequestClinic(List<Parameter> parameters,String tenantId) {
        this(0, 1, parameters, (String)null,tenantId);
    }

    public PageRequestClinic(List<Parameter> parameters, String order,String tenantId) {
        this(0, 1, parameters, order,tenantId);
    }

    public PageRequestClinic(int offset, int limit, List<Parameter> parameters, String order,String tenantId) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be less than zero!");
        } else if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        } else {
            this.offset = offset;
            this.limit = limit;
            if (parameters == null) {
                this.params = Lists.newArrayList();
            } else {
                this.params = parameters;
            }

            this.order = order;
            this.tenantId = tenantId;
        }
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
        return this.params;
    }

    public void setParams(List<Parameter> params) {
        this.params = params;
    }

    public String getDbName() {
        return GenConfigure.dbName;
    }

    public String getDEL_FLAG_NORMAL() {
        return "0";
    }

    public String getDEL_FLAG_DELETE() {
        return "1";
    }

    public String getDEL_FLAG_AUDIT() {
        return "2";
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
