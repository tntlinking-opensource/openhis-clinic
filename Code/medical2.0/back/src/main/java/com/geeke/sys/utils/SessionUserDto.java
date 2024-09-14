package com.geeke.sys.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jin_deng
 */
@NoArgsConstructor
@Data
public class SessionUserDto {


    @JsonProperty("updateDate")
    private Long updateDate;
    @JsonProperty("clinics")
    private List<?> clinics;
    @JsonProperty("delFlag")
    private String delFlag;
    @JsonProperty("userRoleList")
    private List<?> userRoleList;
    @JsonProperty("qyOpenId")
    private String qyOpenId;
    @JsonProperty("updateBy")
    private String updateBy;
    @JsonProperty("isLocked")
    private Integer isLocked;
    @JsonProperty("loginName")
    private String loginName;
    @JsonProperty("loginPassword")
    private String loginPassword;
    @JsonProperty("company")
    private CompanyDTO company;
    @JsonProperty("busTableHasDelFlag")
    private Boolean busTableHasDelFlag;
    @JsonProperty("id")
    private String id;
    @JsonProperty("department")
    private DepartmentDTO department;
    @JsonProperty("busTableId")
    private String busTableId;
    @JsonProperty("email")
    private String email;
    @JsonProperty("createDate")
    private Long createDate;
    @JsonProperty("isAdmin")
    private Integer isAdmin;
    @JsonProperty("companyId")
    private String companyId;
    @JsonProperty("createBy")
    private String createBy;
    @JsonProperty("loginPasswordUpdate")
    private Boolean loginPasswordUpdate;
    @JsonProperty("busTableName")
    private String busTableName;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("name")
    private String name;
    @JsonProperty("isWxuser")
    private String isWxuser;
    @JsonProperty("remarks")
    private String remarks;

    @NoArgsConstructor
    @Data
    public static class CompanyDTO {
        @JsonProperty("code")
        private String code;
        @JsonProperty("delFlag")
        private String delFlag;
        @JsonProperty("busTableHasDelFlag")
        private Boolean busTableHasDelFlag;
        @JsonProperty("id")
        private String id;
        @JsonProperty("busTableId")
        private String busTableId;
        @JsonProperty("fullName")
        private String fullName;
        @JsonProperty("label")
        private String label;
        @JsonProperty("busTableName")
        private String busTableName;
        @JsonProperty("name")
        private String name;
        @JsonProperty("ids")
        private String ids;
    }

    @NoArgsConstructor
    @Data
    public static class DepartmentDTO {
        @JsonProperty("code")
        private String code;
        @JsonProperty("label")
        private String label;
        @JsonProperty("delFlag")
        private String delFlag;
        @JsonProperty("busTableName")
        private String busTableName;
        @JsonProperty("name")
        private String name;
        @JsonProperty("busTableHasDelFlag")
        private Boolean busTableHasDelFlag;
        @JsonProperty("id")
        private String id;
        @JsonProperty("busTableId")
        private String busTableId;
    }
}
