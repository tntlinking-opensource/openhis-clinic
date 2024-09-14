package com.geeke.gen.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.sys.entity.DictItem;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
/**
 * 代码方案管理Entity
 * @author lys
 * @version 2021-08-26
 */
public class GenScheme extends DataEntity<GenScheme> {

	private static final long serialVersionUID = 1L;
	private DictItem category;      // 模板分类 
	private String packageName;		// 包路径
	private String moduleName;		// 模块名
	private String subModuleName;		// 子模块名
	private String functionName;		// 功能名
	private String functionNameSimple;		// 功能名简写
	private String functionAuthor;		// 作者
	private String dialogWidth;		// 编辑宽度
	private Integer colCounts;		// 编辑列数
	private GenTable genTable;      // 业务表 

	
	public GenScheme() {
		super();
	}

	public GenScheme(String id){
		super(id);
	}
	

	@NotNull(message="模板分类不能为空")
    public DictItem getCategory() {
        return category;
    }

    public void setCategory(DictItem category) {
        this.category = category;
    }
    
	@Length(min=1, max=500, message="包路径长度必须介于 1 和 500 之间")
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	
	@Length(min=1, max=30, message="模块名长度必须介于 1 和 30 之间")
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	
	@Length(min=0, max=30, message="子模块名长度必须介于 0 和 30 之间")
	public String getSubModuleName() {
		return subModuleName;
	}
	public void setSubModuleName(String subModuleName) {
		this.subModuleName = subModuleName;
	}
	
	
	@Length(min=1, max=500, message="功能名长度必须介于 1 和 500 之间")
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	
	
	@Length(min=1, max=100, message="功能名简写长度必须介于 1 和 100 之间")
	public String getFunctionNameSimple() {
		return functionNameSimple;
	}
	public void setFunctionNameSimple(String functionNameSimple) {
		this.functionNameSimple = functionNameSimple;
	}
	
	
	@Length(min=0, max=100, message="作者长度必须介于 0 和 100 之间")
	public String getFunctionAuthor() {
		return functionAuthor;
	}
	public void setFunctionAuthor(String functionAuthor) {
		this.functionAuthor = functionAuthor;
	}
	
	
	@Length(min=1, max=20, message="编辑宽度长度必须介于 1 和 20 之间")
	public String getDialogWidth() {
		return dialogWidth;
	}
	public void setDialogWidth(String dialogWidth) {
		this.dialogWidth = dialogWidth;
	}
	
	
	@NotNull(message="编辑列数不能为空")
	public Integer getColCounts() {
		return colCounts;
	}
	public void setColCounts(Integer colCounts) {
		this.colCounts = colCounts;
	}
	
	
	@NotNull(message="业务表不能为空")
    public GenTable getGenTable() {
        return genTable;
    }

    public void setGenTable(GenTable genTable) {
        this.genTable = genTable;
    }
    
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "gen_scheme";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4008";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}