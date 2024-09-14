package com.geeke.outpatient.entity;

import com.geeke.org.entity.Company;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.geeke.sys.entity.DictItem;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;

import java.util.List;

/**
 * 模板处方Entity
 * @author rys
 * @version 2022-10-08
 */
public class Recipetemplate extends DataEntity<Recipetemplate> {

	private static final long serialVersionUID = 1186832625065336477L;
	private Company company;      // 诊所id 
	private String recipetemplateName;		// 模板名称
	private String code;        //模板编号
	private DictItem type;      // 模板类型 
	private String category;		// 模板类别
	private String diagnosis;		// 诊断
	private String singleDosage;		// 单次用量
	private String remarks;        //模板说明
	private String createId;       //创建人id
	private String patientId;       //患者id
	private String registerId;       //挂号id
	private RecipeTemplateInfo recipeTemplateInfo;
	private List<RecipeTemplateDetail> recipeTemplateDetail;
	
	public Recipetemplate() {
		super();
	}

	public Recipetemplate(String id){
		super(id);
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getRegisterId() {
		return registerId;
	}

	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}

	@NotNull(message="诊所id不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@Length(min=1, max=100, message="模板名称长度必须介于 1 和 100 之间")
	public String getRecipetemplateName() {
		return recipetemplateName;
	}
	public void setRecipetemplateName(String recipetemplateName) {
		this.recipetemplateName = recipetemplateName;
	}
	
	
	@NotNull(message="模板类型不能为空")
    public DictItem getType() {
        return type;
    }

    public void setType(DictItem type) {
        this.type = type;
    }
    
	@Length(min=1, max=1, message="模板类别长度必须介于 1 和 1 之间")
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	
	@Length(min=0, max=100, message="诊断长度必须介于 0 和 100 之间")
	public String getDiagnosis() {
		return diagnosis;
	}
	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	
	
	@Length(min=0, max=100, message="单次用量长度必须介于 0 和 100 之间")
	public String getSingleDosage() {
		return singleDosage;
	}
	public void setSingleDosage(String singleDosage) {
		this.singleDosage = singleDosage;
	}

	public List<RecipeTemplateDetail> getRecipeTemplateDetail() {
		return recipeTemplateDetail;
	}

	public void setRecipeTemplateDetail(List<RecipeTemplateDetail> recipeTemplateDetail) {
		this.recipeTemplateDetail = recipeTemplateDetail;
	}

	public RecipeTemplateInfo getRecipeTemplateInfo() {
		return recipeTemplateInfo;
	}

	public void setRecipeTemplateInfo(RecipeTemplateInfo recipeTemplateInfo) {
		this.recipeTemplateInfo = recipeTemplateInfo;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCreateId() {
		return createId;
	}

	public void setCreateId(String createId) {
		this.createId = createId;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "recipetemplate";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1186832625065336477";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}