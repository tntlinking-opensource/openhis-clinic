package com.geeke.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.geeke.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 系统主题Entity
 * @author lys
 * @version 2021-09-19
 */
public class SysParamConfig extends DataEntity<SysParamConfig> {

	private static final long serialVersionUID = 532290982345658937L;
	private String category;        // 类别
	private String configName;		// 配置名称
	private String configValue;		// 配置值
	private String companyId;		// 机构Id




	public SysParamConfig() {
		super();
	}

	public SysParamConfig(String id){
		super(id);
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@NotNull(message="配置项名称不能为空")
	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}
	@Length(min=1, max=2000, message="主题长度必须介于 1 和 64 之间")
	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_param_config";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "532290934221258937";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}