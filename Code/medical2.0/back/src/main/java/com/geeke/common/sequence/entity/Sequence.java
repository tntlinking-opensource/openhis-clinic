package com.geeke.common.sequence.entity;

import com.geeke.common.persistence.DataEntity;


/**
 * 表列头
 * 
 * @author lys
 * @date 2021/4/30
 */
public class Sequence extends DataEntity<Sequence> {

	private static final long serialVersionUID = -5698892194611228984L;
	private String companyId;   //  公司Id
	private String code;		// 规则编号code
	private String prefix;		// 前缀
	private int val = 1;	// 序列值
	
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public int getVal() {
		return val;
	}
	public void setVal(int val) {
		this.val = val;
	}

}
