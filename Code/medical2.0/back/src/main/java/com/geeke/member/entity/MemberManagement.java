package com.geeke.member.entity;

import com.geeke.org.entity.Company;
import javax.validation.constraints.NotNull;
import com.geeke.outpatient.entity.Patient;
import com.geeke.member.entity.MemberSet;
import com.geeke.sys.entity.DictItem;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;

import java.util.List;

/**
 * 会员卡管理Entity
 * @author rys
 * @version 2022-10-26
 */
public class MemberManagement extends DataEntity<MemberManagement> {

	private static final long serialVersionUID = 1225301049409470622L;
	private Company company;      // 诊所id 
	private Patient patient;      // 患者信息 
	private MemberSet memberSet;      // 会员卡id 
	private String age;		// 年龄
	private DictItem gender;		// 性别
	private String card;		// 卡号
	private DictItem type;		// 会员卡类型
	private String memberName;		// 会员卡名称
	private String status;		// 状态
	private String identityCard;		// 身份证号
	private String phone;   //电话
	private List<MemberManagementDetail> memberManagementDetails;
	private List<MemberItem> memberItems;
	
	public MemberManagement() {
		super();
	}

	public MemberManagement(String id){
		super(id);
	}
	

	@NotNull(message="诊所id不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@NotNull(message="患者信息不能为空")
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
	@NotNull(message="会员卡id不能为空")
    public MemberSet getMemberSet() {
        return memberSet;
    }

    public void setMemberSet(MemberSet memberSet) {
        this.memberSet = memberSet;
    }
    
	@Length(min=0, max=100, message="年龄长度必须介于 0 和 100 之间")
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	
	
	@Length(min=0, max=100, message="性别长度必须介于 0 和 100 之间")
	public DictItem getGender() {
		return gender;
	}
	public void setGender(DictItem gender) {
		this.gender = gender;
	}
	
	
	@Length(min=1, max=100, message="卡号长度必须介于 1 和 100 之间")
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	
	
	@Length(min=1, max=100, message="会员卡类型长度必须介于 1 和 100 之间")
	public DictItem getType() {
		return type;
	}
	public void setType(DictItem type) {
		this.type = type;
	}
	
	
	@Length(min=0, max=100, message="会员卡名称长度必须介于 0 和 100 之间")
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	
	
	@Length(min=1, max=1, message="状态长度必须介于 1 和 1 之间")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	@Length(min=0, max=255, message="身份证号长度必须介于 0 和 255 之间")
	public String getIdentityCard() {
		return identityCard;
	}
	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}

	public List<MemberManagementDetail> getMemberManagementDetails() {
		return memberManagementDetails;
	}

	public void setMemberManagementDetails(List<MemberManagementDetail> memberManagementDetails) {
		this.memberManagementDetails = memberManagementDetails;
	}

	public List<MemberItem> getMemberItems() {
		return memberItems;
	}

	public void setMemberItems(List<MemberItem> memberItems) {
		this.memberItems = memberItems;
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