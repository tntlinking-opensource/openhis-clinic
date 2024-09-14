package com.geeke.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.basicdata.entity.ManufactureFactory;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 出库明细Entity
 * @author txl
 * @version 2022-06-09/
 */
public class SupplierOutboundOrder extends DataEntity<SupplierOutboundOrder> {

	private static final long serialVersionUID = 4051158509070424438L;
	private Company company;      // 诊所id
	private Supplier supplierId;      // 供应商
	private Drug drug;      // 药品名称
	private SupplierOutbound supplierOutbound;      // 出库单id
	private Stuff stuff;      // 材料名称
	private String norms;		// 规格
	private ManufactureFactory factory;      // 厂家
	private String batchNo;		// 批号
	private Date produceDate;		// 生产日期
	private Date endDate;		// 有效期
	private Integer number;		// 数量
	private BigDecimal bid;		// 进价
	private BigDecimal leastBid;		// 最小单位进价
	private BigDecimal retailPrice;		// 零售价
	private BigDecimal allBid;		// 总进价
	private BigDecimal allRetailPrice;		// 总零售价
	private int initial;     //初始库存
	private String cancellation;  //作废标记
	private MedicinalStorageControl medicinalStorageControl;

	public SupplierOutboundOrder() {
		super();
	}

	public SupplierOutboundOrder(String id){
		super(id);
	}

	public SupplierOutboundOrder(SupplierOutbound supplierOutbound){
		this.supplierOutbound = supplierOutbound;
	}

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
    public Supplier getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Supplier supplierId) {
        this.supplierId = supplierId;
    }
    
    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }
    
    public SupplierOutbound getSupplierOutbound() {
        return supplierOutbound;
    }

    public void setSupplierOutbound(SupplierOutbound supplierOutbound) {
        this.supplierOutbound = supplierOutbound;
    }
    
    public Stuff getStuff() {
        return stuff;
    }

    public void setStuff(Stuff stuff) {
        this.stuff = stuff;
    }
    
	@Length(min=0, max=64, message="规格长度必须介于 0 和 64 之间")
	public String getNorms() {
		return norms;
	}
	public void setNorms(String norms) {
		this.norms = norms;
	}
	
	
    public ManufactureFactory getFactory() {
        return factory;
    }

    public void setFactory(ManufactureFactory factory) {
        this.factory = factory;
    }
    
	@Length(min=0, max=45, message="批号长度必须介于 0 和 45 之间")
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getProduceDate() {
		return produceDate;
	}
	public void setProduceDate(Date produceDate) {
		this.produceDate = produceDate;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	
	public BigDecimal getBid() {
		return bid;
	}
	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}

	public BigDecimal getLeastBid() {
		return leastBid;
	}

	public void setLeastBid(BigDecimal leastBid) {
		this.leastBid = leastBid;
	}

	public BigDecimal getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(BigDecimal retailPrice) {
		this.retailPrice = retailPrice;
	}
	
	
	public BigDecimal getAllBid() {
		return allBid;
	}
	public void setAllBid(BigDecimal allBid) {
		this.allBid = allBid;
	}
	
	
	public BigDecimal getAllRetailPrice() {
		return allRetailPrice;
	}
	public void setAllRetailPrice(BigDecimal allRetailPrice) {
		this.allRetailPrice = allRetailPrice;
	}

	public int getInitial() {
		return initial;
	}

	public void setInitial(int initial) {
		this.initial = initial;
	}

	public String getCancellation() {
		return cancellation;
	}

	public void setCancellation(String cancellation) {
		this.cancellation = cancellation;
	}

	public MedicinalStorageControl getMedicinalStorageControl() {
		return medicinalStorageControl;
	}

	public void setMedicinalStorageControl(MedicinalStorageControl medicinalStorageControl) {
		this.medicinalStorageControl = medicinalStorageControl;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "supplier_stock";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1005591224273698934";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}