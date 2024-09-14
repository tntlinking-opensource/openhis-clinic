package com.geeke.member.entity;

import com.geeke.org.entity.Company;
import javax.validation.constraints.NotNull;

import com.geeke.sys.entity.DictItem;
import org.hibernate.validator.constraints.Length;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 会员卡设置Entity
 * @author rys
 * @version 2022-10-25
 */
public class MemberSet extends DataEntity<MemberSet> {

	private static final long serialVersionUID = 1222698883343517159L;
	private Company company;      // 诊所id 
	private DictItem type;		// 会员卡类型
	private BigDecimal money;		// 会员卡金额
	private Integer amount;		// 会员卡总数
	private Integer number;		// 剩余数量
	private String status;		// 是否启用（0：启用，1:禁用）
	private String failure;		// 是否失效（0：否，1：是）
	private Date startDate;		// 开始时间
	private Date endDate;		// 结束时间
	private List<MemberItem> memberItem; //封装详情

	
	public MemberSet() {
		super();
	}

	public MemberSet(String id){
		super(id);
	}
	

	@NotNull(message="诊所id不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@Length(min=1, max=100, message="会员卡类型长度必须介于 1 和 100 之间")
	public DictItem getType() {
		return type;
	}
	public void setType(DictItem type) {
		this.type = type;
	}
	
	
	@NotNull(message="会员卡金额不能为空")
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	
	
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
	
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	
	@Length(min=1, max=1, message="是否启用（0：启用，1:禁用）长度必须介于 1 和 1 之间")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	@Length(min=1, max=1, message="是否失效（0：否，1：是）长度必须介于 1 和 1 之间")
	public String getFailure() {
		return failure;
	}
	public void setFailure(String failure) {
		this.failure = failure;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<MemberItem> getMemberItem() {
		return memberItem;
	}

	public void setMemberItem(List<MemberItem> memberItem) {
		this.memberItem = memberItem;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "member_set";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1222698883343517159";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}