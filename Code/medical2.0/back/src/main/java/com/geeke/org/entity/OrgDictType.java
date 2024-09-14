//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.geeke.org.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.google.common.collect.Lists;
import java.util.List;
import org.hibernate.validator.constraints.Length;

public class OrgDictType extends DataEntity<OrgDictType> {
    private static final long serialVersionUID = 4002L;
    private Company company;      // 诊所id
    private String code;
    private String type;
    private List<OrgDictItem> dictItemList = Lists.newArrayList();

    public OrgDictType() {
    }

    public OrgDictType(String id) {
        super(id);
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Length(
            min = 1,
            max = 64,
            message = "字典类型编码长度必须介于 1 和 64 之间"
    )
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Length(
            min = 1,
            max = 1,
            message = "类型长度必须介于 1 和 1 之间"
    )
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<OrgDictItem> getDictItemList() {
        return this.dictItemList;
    }

    public void setDictItemList(List<OrgDictItem> dictItemList) {
        this.dictItemList = dictItemList;
    }

    @JsonIgnore
    public String getBusTableName() {
        return "sys_dict_type";
    }

    @JsonIgnore
    public String getBusTableId() {
        return "4002";
    }

    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
        return true;
    }
}
