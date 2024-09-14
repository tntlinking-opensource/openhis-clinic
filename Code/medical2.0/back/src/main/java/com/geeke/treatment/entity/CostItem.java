package com.geeke.treatment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.cure.entity.InspectionCheckInfoCostItem;
import com.geeke.org.entity.Company;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.sys.entity.DictItem;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
/**
 * 诊疗项目Entity
 * @author txl
 * @version 2022-05-30
 */
public class CostItem extends DataEntity<CostItem> {

	private static final long serialVersionUID = 998465736089977637L;
	private Company company;      // 诊所id

	private String syncId;  //同步ID
	private int syncNum;
	private String itemCode;		// 项目编码
	private String itemName;		// 项目名称
	private DictItem itemType;      // 项目类别 
	private DictItem unit;      // 单位 
	private BigDecimal costPrice;		// 成本价
	private BigDecimal salePrice;		// 销售价
	private String isUse;		// 状态
	private String isPackage;   //是否为打包项目
	private String isDeduct;		// 是否执行划扣
	private String itemCountryCode; //国家编码
	private String pinyinCode;     //拼音码
	private List<CostItemPackage> costItemPackages;  //子项目封装
	private RecipelDetail recipelDetail;
	private InspectionCheckInfoCostItem inspectionCheckInfo;
	
	public CostItem() {
		super();
	}

	public CostItem(String id){
		super(id);
	}
	

	@NotNull(message="诊所id不能为空")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@Length(min=0, max=64, message="项目编码长度必须介于 0 和 64 之间")
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	
	
	@Length(min=1, max=64, message="项目名称长度必须介于 1 和 64 之间")
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getSyncId() {
		return syncId;
	}

	public void setSyncId(String syncId) {
		this.syncId = syncId;
	}

	public int getSyncNum() {
		return syncNum;
	}

	public void setSyncNum(int syncNum) {
		this.syncNum = syncNum;
	}

	public DictItem getItemType() {
        return itemType;
    }

    public void setItemType(DictItem itemType) {
        this.itemType = itemType;
    }
    
    public DictItem getUnit() {
        return unit;
    }

    public void setUnit(DictItem unit) {
        this.unit = unit;
    }
    
	public BigDecimal getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
	}
	
	
	public BigDecimal getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}
	
	
	@Length(min=0, max=2, message="状态长度必须介于 0 和 2 之间")
	public String getIsUse() {
		return isUse;
	}
	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}
	
	
	@Length(min=0, max=2, message="是否执行划扣长度必须介于 0 和 2 之间")
	public String getIsDeduct() {
		return isDeduct;
	}
	public void setIsDeduct(String isDeduct) {
		this.isDeduct = isDeduct;
	}

	@Length(min=0, max=64, message="国家编码长度必须介于 0 和 64 之间")
	public String getItemCountryCode() {
		return itemCountryCode;
	}
	public void setItemCountryCode(String itemCountryCode) {
		this.itemCountryCode = itemCountryCode;
	}

	public String getIsPackage() {
		return isPackage;
	}

	public void setIsPackage(String isPackage) {
		this.isPackage = isPackage;
	}

	public String getPinyinCode() {
		return pinyinCode;
	}

	public void setPinyinCode(String pinyinCode) {
		this.pinyinCode = pinyinCode;
	}

	public List<CostItemPackage> getCostItemPackages() {
		return costItemPackages;
	}

	public void setCostItemPackages(List<CostItemPackage> costItemPackages) {
		this.costItemPackages = costItemPackages;
	}

	public void setRecipelDetail(RecipelDetail recipelDetail) {
		this.recipelDetail = recipelDetail;
	}

	public RecipelDetail getRecipelDetail() {
		return recipelDetail;
	}

	public void setInspectionCheckInfo(InspectionCheckInfoCostItem inspectionCheckInfo) {
		this.inspectionCheckInfo = inspectionCheckInfo;
	}

	public InspectionCheckInfoCostItem getInspectionCheckInfo() {
		return inspectionCheckInfo;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "cost_item";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "998465736089977637";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}