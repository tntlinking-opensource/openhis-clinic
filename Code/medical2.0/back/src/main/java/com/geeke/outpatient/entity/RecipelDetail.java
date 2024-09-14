package com.geeke.outpatient.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.sys.entity.DictItem;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
/**
 * 处方详情Entity
 * @author txl
 * @version 2022-07-20
 */
public class RecipelDetail extends DataEntity<RecipelDetail> {

	private static final long serialVersionUID = 1014474470772900073L;
	private Company company;      // 诊所id 
	private RecipelInfo recipelInfo;      // 处方 
	private DrugStuffEvt drugStuffId;		// 药品材料id
	private BigDecimal singleDosage;		// 单次用量
	private Integer total;		// 总量
	private BigDecimal allFee;		// 应付总价
	private BigDecimal actualPayment;		// 实付总价
	private DictItem westernMedicineUse;      // 西药用法
	private DictItem frequency;      // 频次用量 
	private DictItem days;      // 天数
	private DictItem chineseMedicineUse;      // 中药用法 
	private DictItem infuseUse;      // 输液用法 
	private String drippingSpeed;		// 滴速
	private DictItem skinTest;      // 皮试 
	private Integer infuseGroup;		// 组数
	private String retailType;		// 零售类型
	private Integer isExtra;		// 是否附加费
	private Integer seq;		// 显示顺序
	private Integer isUnpackSell;		// 是否拆零销售
	private BigDecimal unitPrice;		// 单价
	private String unitType;		// 单价单位
	private String stuffType;		// 物料类型
	private Integer minTotal;		// 最小单位总量
	private BigDecimal executions;			//执行进度
	private int infuseType;        //输液执行状态
	private int treatment;        //治疗执行状态

	public int getTreatment() {
		return treatment;
	}

	public void setTreatment(int treatment) {
		this.treatment = treatment;
	}

	public String getStuffType() {
		return stuffType;
	}

	public void setStuffType(String stuffType) {
		this.stuffType = stuffType;
	}


	public RecipelDetail() {
		super();
	}

	public RecipelDetail(String id){
		super(id);
	}
	
	public RecipelDetail(RecipelInfo recipelInfo){
		this.recipelInfo = recipelInfo;
	}

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
    public RecipelInfo getRecipelInfo() {
        return recipelInfo;
    }

    public void setRecipelInfo(RecipelInfo recipelInfo) {
        this.recipelInfo = recipelInfo;
    }

    public DrugStuffEvt getDrugStuffId() {
        return drugStuffId;
    }

    public void setDrugStuffId(DrugStuffEvt drugStuffId) {
        this.drugStuffId = drugStuffId;
    }

    public BigDecimal getSingleDosage() {
		return singleDosage;
	}
	public void setSingleDosage(BigDecimal singleDosage) {
		this.singleDosage = singleDosage;
	}
	
	
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	
	
	public BigDecimal getAllFee() {
		return allFee;
	}
	public void setAllFee(BigDecimal allFee) {
		this.allFee = allFee;
	}

	public BigDecimal getActualPayment() {
		return actualPayment;
	}

	public void setActualPayment(BigDecimal actualPayment) {
		this.actualPayment = actualPayment;
	}

	public DictItem getWesternMedicineUse() {
        return westernMedicineUse;
    }

    public void setWesternMedicineUse(DictItem westernMedicineUse) {
        this.westernMedicineUse = westernMedicineUse;
    }
    
    public DictItem getFrequency() {
        return frequency;
    }

    public void setFrequency(DictItem frequency) {
        this.frequency = frequency;
    }
    
    public DictItem getDays() {
        return days;
    }

    public void setDays(DictItem days) {
        this.days = days;
    }
    
    public DictItem getChineseMedicineUse() {
        return chineseMedicineUse;
    }

    public void setChineseMedicineUse(DictItem chineseMedicineUse) {
        this.chineseMedicineUse = chineseMedicineUse;
    }
    
    public DictItem getInfuseUse() {
        return infuseUse;
    }

    public void setInfuseUse(DictItem infuseUse) {
        this.infuseUse = infuseUse;
    }
    
	@Length(min=0, max=45, message="滴速长度必须介于 0 和 45 之间")
	public String getDrippingSpeed() {
		return drippingSpeed;
	}
	public void setDrippingSpeed(String drippingSpeed) {
		this.drippingSpeed = drippingSpeed;
	}
	
	
    public DictItem getSkinTest() {
        return skinTest;
    }

    public void setSkinTest(DictItem skinTest) {
        this.skinTest = skinTest;
    }
    
	public Integer getInfuseGroup() {
		return infuseGroup;
	}
	public void setInfuseGroup(Integer infuseGroup) {
		this.infuseGroup = infuseGroup;
	}


	@Length(min=0, max=1, message="retail_type长度必须介于 0 和 1 之间")
	public String getRetailType() {
		return retailType;
	}
	public void setRetailType(String retailType) {
		this.retailType = retailType;
	}


	public Integer getIsExtra() {
		return isExtra;
	}
	public void setIsExtra(Integer isExtra) {
		this.isExtra = isExtra;
	}


	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	@NotNull(message="是否拆零销售不能为空")
	public Integer getIsUnpackSell() {
		return isUnpackSell;
	}
	public void setIsUnpackSell(Integer isUnpackSell) {
		this.isUnpackSell = isUnpackSell;
	}


	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}


	@Length(min=0, max=100, message="单价单位长度必须介于 0 和 100 之间")
	public String getUnitType() {
		return unitType;
	}
	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}


	public Integer getMinTotal() {
		return minTotal;
	}

	public void setMinTotal(Integer minTotal) {
		this.minTotal = minTotal;
	}

	public BigDecimal getExecutions() {
		return executions;
	}

	public void setExecutions(BigDecimal executions) {
		this.executions = executions;
	}

	public int getInfuseType() {
		return infuseType;
	}

	public void setInfuseType(int infuseType) {
		this.infuseType = infuseType;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "recipel_detail";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1014474470772900073";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}