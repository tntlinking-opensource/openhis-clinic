package com.geeke.stock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.basicdata.entity.ManufactureFactory;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.sys.entity.DictItem;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
/**
 * 药品信息Entity
 * @author txl
 * @version 2022-06-07
 */
public class Drug extends DataEntity<Drug> {

	private static final long serialVersionUID = 1004078055755374623L;
	private String goodsName;		// 药品名称

    private String syncId;     //同步过来的ID

    private int syncNum;    //药品是否已同步


	private Company company;      // 诊所id 
	private String brandName;		// 商品名称
	private DictItem type;      // 药品类型 
	private String code;		// 药品编码
	private String source;		// 药品来源
	private DictItem nature;      // 性质 
	private ManufactureFactory factory;      // 生产厂家 
	private String standardCode;		// 国药准字
	private String bitCode;		// 本位码
	private String barCode;		// 条形码
	private String insuranceCode;		// 医保编码
	private String dosis;		// 剂量
	private DictItem dosisUnit;      // 剂量单位 
	private String preparation;		// 制剂
	private DictItem preparationUnit;      // 制剂单位 
	private DictItem pack;      // 包装 
	private BigDecimal price;		// 售价
	private String isUnpackSell;		// 允许拆零销售
	private BigDecimal retailPrice;		// 拆开后零售价
	private String stockNumber;	//库存数量
	private String stockUnit; //库存单位
	private String status;   //启用标志
	private String pinyinCode;  //拼音码
	private int inventory;  //库存
	private DictItem westernMedicineUse;      // 西药用法
	private DictItem chineseMedicineUse;      // 中药用法
	private Integer singleDosage;		// 单次用量
	private Integer total;		// 总量
	private DictItem frequency;      // 频次用量
	private DictItem days;      // 天数
	private String locationNumber;   //货位号
	private String inventoryLimit;   //库存上限
	private String inventoryFloor;   //库存下限
	private BigDecimal bid;    //进价合计

	private InventoryVerification inventoryVerification;  //库存盘点
	private String inventoryVerificationDetailId;       //库存盘点详情id

	private String indate;          //有效期
	private MedicinalStockControl stock;

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

	public int getInventory() {
		return inventory;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}


	public Drug() {
		super();
	}

	public Drug(String id){
		super(id);
	}
	

	@Length(min=1, max=128, message="药品名称长度必须介于 1 和 128 之间")
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
	
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@Length(min=0, max=128, message="商品名称长度必须介于 0 和 128 之间")
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	
	@NotNull(message="药品类型不能为空")
    public DictItem getType() {
        return type;
    }

    public void setType(DictItem type) {
        this.type = type;
    }
    
	@Length(min=0, max=64, message="药品编码长度必须介于 0 和 64 之间")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	@Length(min=0, max=128, message="药品来源长度必须介于 0 和 128 之间")
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	
    public DictItem getNature() {
        return nature;
    }

    public void setNature(DictItem nature) {
        this.nature = nature;
    }
    
    public ManufactureFactory getFactory() {
        return factory;
    }

    public void setFactory(ManufactureFactory factory) {
        this.factory = factory;
    }
    
	@Length(min=0, max=64, message="国药准字长度必须介于 0 和 64 之间")
	public String getStandardCode() {
		return standardCode;
	}
	public void setStandardCode(String standardCode) {
		this.standardCode = standardCode;
	}
	
	
	@Length(min=0, max=64, message="本位码长度必须介于 0 和 64 之间")
	public String getBitCode() {
		return bitCode;
	}
	public void setBitCode(String bitCode) {
		this.bitCode = bitCode;
	}
	
	
	@Length(min=0, max=64, message="条形码长度必须介于 0 和 64 之间")
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	
	
	@Length(min=0, max=64, message="医保编码长度必须介于 0 和 64 之间")
	public String getInsuranceCode() {
		return insuranceCode;
	}
	public void setInsuranceCode(String insuranceCode) {
		this.insuranceCode = insuranceCode;
	}
	
	
	@Length(min=0, max=45, message="剂量长度必须介于 0 和 45 之间")
	public String getDosis() {
		return dosis;
	}
	public void setDosis(String dosis) {
		this.dosis = dosis;
	}
	
	
    public DictItem getDosisUnit() {
        return dosisUnit;
    }

    public void setDosisUnit(DictItem dosisUnit) {
        this.dosisUnit = dosisUnit;
    }
    
	@Length(min=0, max=45, message="制剂长度必须介于 0 和 45 之间")
	public String getPreparation() {
		return preparation;
	}
	public void setPreparation(String preparation) {
		this.preparation = preparation;
	}
	
	
    public DictItem getPreparationUnit() {
        return preparationUnit;
    }

    public void setPreparationUnit(DictItem preparationUnit) {
        this.preparationUnit = preparationUnit;
    }
    
    public DictItem getPack() {
        return pack;
    }

    public void setPack(DictItem pack) {
        this.pack = pack;
    }
    
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	
	@Length(min=0, max=45, message="允许拆零销售长度必须介于 0 和 45 之间")
	public String getIsUnpackSell() {
		return isUnpackSell;
	}
	public void setIsUnpackSell(String isUnpackSell) {
		this.isUnpackSell = isUnpackSell;
	}
	
	
	public BigDecimal getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(BigDecimal retailPrice) {
		this.retailPrice = retailPrice;
	}

	@Length(min=0, max=1, message="启用标志长度必须介于 0 和 1 之间")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getStockNumber() {
		return stockNumber;
	}

	public void setStockNumber(String stockNumber) {
		this.stockNumber = stockNumber;
	}

	public String getStockUnit() {
		return stockUnit;
	}

	public void setStockUnit(String stockUnit) {
		this.stockUnit = stockUnit;
	}

	public String getPinyinCode() {
		return pinyinCode;
	}

	public void setPinyinCode(String pinyinCode) {
		this.pinyinCode = pinyinCode;
	}

	public DictItem getWesternMedicineUse() {
		return westernMedicineUse;
	}

	public void setWesternMedicineUse(DictItem westernMedicineUse) {
		this.westernMedicineUse = westernMedicineUse;
	}

	public DictItem getChineseMedicineUse() {
		return chineseMedicineUse;
	}

	public void setChineseMedicineUse(DictItem chineseMedicineUse) {
		this.chineseMedicineUse = chineseMedicineUse;
	}

	public Integer getSingleDosage() {
		return singleDosage;
	}

	public void setSingleDosage(Integer singleDosage) {
		this.singleDosage = singleDosage;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
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

	public String getLocationNumber() {
		return locationNumber;
	}

	public void setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
	}

	public String getInventoryLimit() {
		return inventoryLimit;
	}

	public void setInventoryLimit(String inventoryLimit) {
		this.inventoryLimit = inventoryLimit;
	}

	public String getInventoryFloor() {
		return inventoryFloor;
	}

	public void setInventoryFloor(String inventoryFloor) {
		this.inventoryFloor = inventoryFloor;
	}

	public InventoryVerification getInventoryVerification() {
		return inventoryVerification;
	}

	public void setInventoryVerification(InventoryVerification inventoryVerification) {
		this.inventoryVerification = inventoryVerification;
	}

	public String getInventoryVerificationDetailId() {
		return inventoryVerificationDetailId;
	}

	public void setInventoryVerificationDetailId(String inventoryVerificationDetailId) {
		this.inventoryVerificationDetailId = inventoryVerificationDetailId;
	}

	public String getIndate() {
		return indate;
	}

	public void setIndate(String indate) {
		this.indate = indate;
	}

	public BigDecimal getBid() {
		return bid;
	}

	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}

	public MedicinalStockControl getStock() {
		return stock;
	}

	public void setStock(MedicinalStockControl stock) {
		this.stock = stock;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "drug";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1004078055755374623";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}