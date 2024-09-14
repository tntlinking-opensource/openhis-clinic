package com.geeke.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;

import javax.validation.constraints.NotNull;

/**
 * 会员卡管理Entity
 * @author rys
 * @version 2022-10-26
 */

public class MemberManagementDetail extends DataEntity<MemberManagementDetail> {

	private static final long serialVersionUID = 1225301049409470622L;
	private Company company;      // 诊所id
	private MemberItem memberItem;      // 患者信息
	private MemberManagement memberManagement;
	private Integer number;
	private Integer useNumber;

	public MemberManagementDetail() {
		super();
	}

	public MemberManagementDetail(String id){
		super(id);
	}
	

	@NotNull(message="诊所id不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

	public MemberItem getMemberItem() {
		return memberItem;
	}

	public void setMemberItem(MemberItem memberItem) {
		this.memberItem = memberItem;
	}

	public MemberManagement getMemberManagement() {
		return memberManagement;
	}

	public void setMemberManagement(MemberManagement memberManagement) {
		this.memberManagement = memberManagement;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getUseNumber() {
		return useNumber;
	}

	public void setUseNumber(Integer useNumber) {
		this.useNumber = useNumber;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "member_management";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1225301049409470622";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return false;
    }
}