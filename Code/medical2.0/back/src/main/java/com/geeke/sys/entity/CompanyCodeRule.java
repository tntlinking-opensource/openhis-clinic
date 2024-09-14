package com.geeke.sys.entity;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
/**
 * 公司编码规则Entity
 * @author lys
 * @version 2021-05-17
 */
public class CompanyCodeRule extends DataEntity<CompanyCodeRule> {

	private static final long serialVersionUID = 443292628418674688L;
	private String companyId;      // 公司 
	private CodeRule sysRule;      // 系统编号规则 
	private String code;		// 编码
	private String ruleDef;		// 规则

	
	public CompanyCodeRule() {
		super();
	}

	public CompanyCodeRule(String id){
		super(id);
	}
	
	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	@NotNull(message="系统编号规则不能为空")
    public CodeRule getSysRule() {
        return sysRule;
    }

    public void setSysRule(CodeRule sysRule) {
        this.sysRule = sysRule;
    }
    
	@Length(min=1, max=20, message="编码长度必须介于 1 和 20 之间")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	@Length(min=1, max=512, message="规则长度必须介于 1 和 512 之间")
	public String getRuleDef() {
		return ruleDef;
	}
	public void setRuleDef(String ruleDef) {
		this.ruleDef = ruleDef;
	}
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_company_code_rule";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "443292628418674688";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}