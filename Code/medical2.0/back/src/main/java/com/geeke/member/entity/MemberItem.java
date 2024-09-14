package com.geeke.member.entity;

import com.geeke.org.entity.Company;
import javax.validation.constraints.NotNull;
import com.geeke.member.entity.MemberSet;
import com.geeke.treatment.entity.CostItem;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 会员卡详情Entity
 * @author rys
 * @version 2022-10-25
 */
public class MemberItem extends DataEntity<MemberItem> {

	private static final long serialVersionUID = 1222698883343517631L;
	private Company company;      // 诊所id 
	private MemberSet member;      // 会员卡id 
	private CostItem costItem;      // 对应的项目id 
	private Integer number;		// 赠送项目数量
    private String name;    //暂存项目名称
    private Integer useNumber;  //使用数量
	
	public MemberItem() {
		super();
	}

	public MemberItem(String id){
		super(id);
	}
	

	@NotNull(message="诊所id不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@NotNull(message="会员卡id不能为空")
    public MemberSet getMember() {
        return member;
    }

    public void setMember(MemberSet member) {
        this.member = member;
    }
    
	@NotNull(message="对应的项目id不能为空")
    public CostItem getCostItem() {
        return costItem;
    }

    public void setCostItem(CostItem costItem) {
        this.costItem = costItem;
    }
    
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
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
        return "member_item";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1222698883343517631";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}