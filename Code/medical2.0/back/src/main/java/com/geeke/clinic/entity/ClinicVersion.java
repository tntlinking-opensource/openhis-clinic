package com.geeke.clinic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
/**
 * 诊所版本Entity
 * @author txl
 * @version 2022-05-23
 */
public class ClinicVersion extends DataEntity<ClinicVersion> {

	private static final long serialVersionUID = 987744398207139863L;
	private String code;
	private String roleId;
	private BigDecimal price;		// 版本价格
	private String description;		// 版本描述　
	
	public ClinicVersion() {
		super();
	}

	public ClinicVersion(String id){
		super(id);
	}

	@NotNull(message="版本编码不能为空")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	@NotNull(message="版本角色不能为空")
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	@NotNull(message="版本价格不能为空")
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}


	@Length(min=0, max=200, message="版本描述长度必须介于 0 和 200 之间")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "clinic_version";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "987744398207139863";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}