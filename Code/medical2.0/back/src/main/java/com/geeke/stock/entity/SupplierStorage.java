package com.geeke.stock.entity;

import com.geeke.org.entity.Company;
import com.geeke.stock.entity.Supplier;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.geeke.sys.entity.DictItem;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 入库单Entity
 * @author txl
 * @version 2022-06-02
 */
public class SupplierStorage extends DataEntity<SupplierStorage> {

	private static final long serialVersionUID = 1007238052174135324L;
	private Company company;      // 诊所id 
	private Supplier supplier;      // 供应商id 
	private String code;		// 入库单
	private Integer breed;		// 品种
	private Integer number;		// 数量
	private BigDecimal allBid;		// 总进价
	private BigDecimal allRetailPrice;		// 总零售价
	private Date cancelDate;		// 作废日期
	private DictItem examine;      // 审核状态 
	private Date examineDate;		// 审批时间
	private String withGoodsNo;		// 随货单
	private String invoiceNumber;		// 发票号
	private int initial;     //初始库存
	private String type;    //入库类型
	
	public SupplierStorage() {
		super();
	}

	public SupplierStorage(String id){
		super(id);
	}
	

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
	@NotNull(message="供应商id不能为空")
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
	@Length(min=0, max=64, message="入库单长度必须介于 0 和 64 之间")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	public Integer getBreed() {
		return breed;
	}
	public void setBreed(Integer breed) {
		this.breed = breed;
	}
	
	
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
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
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCancelDate() {
		return cancelDate;
	}
	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
	
	
    public DictItem getExamine() {
        return examine;
    }

    public void setExamine(DictItem examine) {
        this.examine = examine;
    }
    
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getExamineDate() {
		return examineDate;
	}
	public void setExamineDate(Date examineDate) {
		this.examineDate = examineDate;
	}
	
	
	@Length(min=0, max=64, message="随货单长度必须介于 0 和 64 之间")
	public String getWithGoodsNo() {
		return withGoodsNo;
	}
	public void setWithGoodsNo(String withGoodsNo) {
		this.withGoodsNo = withGoodsNo;
	}
	
	
	@Length(min=0, max=64, message="发票号长度必须介于 0 和 64 之间")
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public int getInitial() {
		return initial;
	}

	public void setInitial(int initial) {
		this.initial = initial;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "supplier_storage";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "1007238052174135324";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}