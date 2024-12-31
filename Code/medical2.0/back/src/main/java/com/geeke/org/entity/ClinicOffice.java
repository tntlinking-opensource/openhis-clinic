package com.geeke.org.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 科室信息Entity
 * @author ch
 * @version 2022-06-14
 */
@Data
public class ClinicOffice extends DataEntity<ClinicOffice> {

    private static final long serialVersionUID = 1026680647570219053L;
    private Company company;      // 所属诊所
    private String code;		// 科室编号
    private String isLocked;		// 禁用（0：未禁用；1：禁用）
    private String sort;		// 排序
    private String category;		// 科室大类
    private String address;		// 地址
    private String itro;         //简介
    private String deptResperName;//科室负责人
    private String deptResperTel;//科室负责人电话
    private LocalDateTime deptEstbdat;//科室成立日期
    private String aprvBedCnt;//批准床位数量
    private String poolareaNo;//统筹区编号
    private String drPsncnt;//医师人数
    private String pharPsncnt;//药师人数
    private String nursPsncnt;//护士人数
    private String tecnPsncnt;//技师人数

    private String isDefault;   //默认科室
    private String isRegister;   //是否为登记科室

    public ClinicOffice() {
        super();
    }

    public ClinicOffice(String id){
        super(id);
    }


    @NotNull(message="所属诊所不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    @Length(min=1, max=64, message="科室编号长度必须介于 1 和 64 之间")
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }


    @Length(min=1, max=1, message="禁用（0：未禁用；1：禁用）长度必须介于 1 和 1 之间")
    public String getIsLocked() {
        return isLocked;
    }
    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }


    @Length(min=0, max=21, message="排序长度必须介于 0 和 21 之间")
    public String getSort() {
        return sort;
    }
    public void setSort(String sort) {
        this.sort = sort;
    }


    @Length(min=0, max=128, message="科室大类长度必须介于 0 和 128 之间")
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }


    @Length(min=0, max=255, message="地址长度必须介于 0 和 255 之间")
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getIsDefault(){return isDefault;}
    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getIsRegister(){return isRegister;}
    public void setIsRegister(String isRegister) {
        this.isRegister = isRegister;
    }

    /**
     * 获取实体对应表名
     */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "clinic_office";
    }

    /**
     * 获取实体对应Id
     */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1026680647570219053";
    }


    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
        return true;
    }
}
