package com.geeke.cure.entity;

import com.geeke.cure.entity.InspectionCheckInfo;
import javax.validation.constraints.NotNull;
import com.geeke.org.entity.Company;
import com.geeke.treatment.entity.CostItem;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 检验检查详情Entity
 * @author rys
 * @version 2022-10-19
 */
public class InspectionCheckDetail extends DataEntity<InspectionCheckDetail> {

	private static final long serialVersionUID = 1213629183723971393L;
	private InspectionCheckInfo inspectionCheckInfo;      // 明细表id 
	private Company company;      // 诊所id 
	private CostItem costItem;      // 对应项目id 
	private String conclusion;		// 结果
	private int sign;		// 指数高低标记
	private String describes;		// 描述
	private int seq;          //排序
	
	public InspectionCheckDetail() {
		super();
	}

	public InspectionCheckDetail(String id){
		super(id);
	}
	

	@NotNull(message="明细表id不能为空")
    public InspectionCheckInfo getInspectionCheckInfo() {
        return inspectionCheckInfo;
    }

    public void setInspectionCheckInfo(InspectionCheckInfo inspectionCheckInfo) {
        this.inspectionCheckInfo = inspectionCheckInfo;
    }
    
	@NotNull(message="诊所id不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@NotNull(message="对应项目id不能为空")
    public CostItem getCostItem() {
        return costItem;
    }

    public void setCostItem(CostItem costItem) {
        this.costItem = costItem;
    }
    
	@Length(min=0, max=255, message="结果长度必须介于 0 和 255 之间")
	public String getConclusion() {
		return conclusion;
	}
	public void setConclusion(String conclusion) {
		this.conclusion = conclusion;
	}
	
	
	@Length(min=0, max=100, message="指数高低标记长度必须介于 0 和 100 之间")
	public int getSign() {
		return sign;
	}
	public void setSign(int sign) {
		this.sign = sign;
	}
	
	
	@Length(min=0, max=255, message="描述长度必须介于 0 和 255 之间")
	public String getDescribes() {
		return describes;
	}
	public void setDescribes(String describes) {
		this.describes = describes;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "inspection_check_detail";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1213629183723971393";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return false;
    }
}