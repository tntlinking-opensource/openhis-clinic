package com.geeke.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 出库单Entity
 * @author txl
 * @version 2022-06-02/
 */
public class SupplierOutbound extends DataEntity<SupplierOutbound> {

	private static final long serialVersionUID = -2281869736391969855L;
	private Company company;      // 诊所id 
	private Supplier supplier;      // 供应商id 
	private String code;		// 出库单
	private Integer breed;		// 品种
	private Integer number;		// 数量
	private BigDecimal allBid;		// 总进价
	private BigDecimal allRetailPrice;		// 总零售价
	private Date cancelDate;		// 作废日期
	private String examineBy;      // 审批人
	private Date examineDate;		// 审批时间
	//private Integer initial;     //初始库存
	private String type;    //出库类型 1-药品 2-材料
	private String method;    //出库方式 1-药品报损 2-退还供方 3-调拔出库
	private String reason;    //出库原因
	private  Integer status;//状态 1-未审核  2-已审核 3-已撤销

	public SupplierOutbound() {
		super();
	}

	public SupplierOutbound(String id){
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
    
	@Length(min=0, max=64, message="出库单长度必须介于 0 和 64 之间")
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
	
	
    public String getExamineBy() {
        return examineBy;
    }

    public void setExamineBy(String examineBy) {
        this.examineBy = examineBy;
    }
    
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getExamineDate() {
		return examineDate;
	}
	public void setExamineDate(Date examineDate) {
		this.examineDate = examineDate;
	}



	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "supplier_outbound";
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
            return false;
    }
}