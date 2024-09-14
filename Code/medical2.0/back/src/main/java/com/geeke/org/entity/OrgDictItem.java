//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.geeke.org.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class OrgDictItem extends DataEntity<OrgDictItem> {
    private static final long serialVersionUID = 4005L;
    private Company company;      // 诊所id
    private OrgDictType dictType;
    private String value;
    private String seq;

    public OrgDictItem() {
    }

    public OrgDictItem(String id) {
        super(id);
    }

    public OrgDictItem(OrgDictType dictType) {
        this.dictType = dictType;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @NotNull(
            message = "字典类型表ID不能为空"
    )
    public OrgDictType getDictType() {
        return this.dictType;
    }

    public void setDictType(OrgDictType dictType) {
        this.dictType = dictType;
    }

    @Length(
            min = 1,
            max = 128,
            message = "值长度必须介于 1 和 128 之间"
    )
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    @JsonIgnore
    public String getBusTableName() {
        return "sys_dict_item";
    }

    @JsonIgnore
    public String getBusTableId() {
        return "4005";
    }

    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
        return true;
    }
}
