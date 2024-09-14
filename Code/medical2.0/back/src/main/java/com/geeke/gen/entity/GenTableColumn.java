package com.geeke.gen.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.sys.entity.DictItem;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
/**
 * 业务表管理Entity
 * @author lys
 * @version 2021-08-26
 */
public class GenTableColumn extends DataEntity<GenTableColumn> {

	private static final long serialVersionUID = 1L;
	private GenTable genTable;      // 表编号 
	private String comments;		// 说明
	private String jdbcType;		// 物理类型
	private DictItem javaType;      // JAVA类型 
	private GenScheme customType;      // 自定义类型 
	private String javaField;		// 属性名
	private String defVal;		// 默认值
	private String isPk;		// 主键
	private String isNull;		// 可空
	private String isInsert;		// 可插
	private String isEdit;		// 可编
	private String isList;		// 列显
	private Integer width;		// 列宽
	private String isQuery;		// 查询
	private DictItem queryType;      // 查询方式 
	private String queryVal;		// 查询条件
	private DictItem showType;      // 控件 
	private String interaction;		// 联动变量
	private String interactionVal;		// 联动值
	private String parameters;		// 联动参数
	private String settings;		// 其它设置（扩展字段JSON）
	private Integer sort;		// 排序

	
	public GenTableColumn() {
		super();
	}

	public GenTableColumn(String id){
		super(id);
	}
	

	public GenTableColumn(GenTable genTable){
		this.genTable = genTable;
	}

	@NotNull(message="表编号不能为空")
    public GenTable getGenTable() {
        return genTable;
    }

    public void setGenTable(GenTable genTable) {
        this.genTable = genTable;
    }
    
	@Length(min=0, max=500, message="说明长度必须介于 0 和 500 之间")
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
	@Length(min=1, max=100, message="物理类型长度必须介于 1 和 100 之间")
	public String getJdbcType() {
		return jdbcType;
	}
	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}
	
	
	@NotNull(message="JAVA类型不能为空")
    public DictItem getJavaType() {
        return javaType;
    }

    public void setJavaType(DictItem javaType) {
        this.javaType = javaType;
    }
    
    public GenScheme getCustomType() {
        return customType;
    }

    public void setCustomType(GenScheme customType) {
        this.customType = customType;
    }
    
	@Length(min=1, max=200, message="属性名长度必须介于 1 和 200 之间")
	public String getJavaField() {
		return javaField;
	}
	public void setJavaField(String javaField) {
		this.javaField = javaField;
	}
	
	
	@Length(min=0, max=2000, message="默认值长度必须介于 0 和 2000 之间")
	public String getDefVal() {
		return defVal;
	}
	public void setDefVal(String defVal) {
		this.defVal = defVal;
	}
	
	
	@Length(min=0, max=1, message="主键长度必须介于 0 和 1 之间")
	public String getIsPk() {
		return isPk;
	}
	public void setIsPk(String isPk) {
		this.isPk = isPk;
	}
	
	
	@Length(min=0, max=1, message="可空长度必须介于 0 和 1 之间")
	public String getIsNull() {
		return isNull;
	}
	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}
	
	
	@Length(min=0, max=1, message="可插长度必须介于 0 和 1 之间")
	public String getIsInsert() {
		return isInsert;
	}
	public void setIsInsert(String isInsert) {
		this.isInsert = isInsert;
	}
	
	
	@Length(min=0, max=1, message="可编长度必须介于 0 和 1 之间")
	public String getIsEdit() {
		return isEdit;
	}
	public void setIsEdit(String isEdit) {
		this.isEdit = isEdit;
	}
	
	
	@Length(min=0, max=1, message="列显长度必须介于 0 和 1 之间")
	public String getIsList() {
		return isList;
	}
	public void setIsList(String isList) {
		this.isList = isList;
	}
	
	
	@NotNull(message="列宽不能为空")
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	
	
	@Length(min=0, max=1, message="查询长度必须介于 0 和 1 之间")
	public String getIsQuery() {
		return isQuery;
	}
	public void setIsQuery(String isQuery) {
		this.isQuery = isQuery;
	}
	
	
    public DictItem getQueryType() {
        return queryType;
    }

    public void setQueryType(DictItem queryType) {
        this.queryType = queryType;
    }
    
	@Length(min=0, max=2000, message="查询条件长度必须介于 0 和 2000 之间")
	public String getQueryVal() {
		return queryVal;
	}
	public void setQueryVal(String queryVal) {
		this.queryVal = queryVal;
	}
	
	
    public DictItem getShowType() {
        return showType;
    }

    public void setShowType(DictItem showType) {
        this.showType = showType;
    }
    
	@Length(min=0, max=255, message="联动变量长度必须介于 0 和 255 之间")
	public String getInteraction() {
		return interaction;
	}
	public void setInteraction(String interaction) {
		this.interaction = interaction;
	}
	
	
	@Length(min=0, max=2048, message="联动值长度必须介于 0 和 2048 之间")
	public String getInteractionVal() {
		return interactionVal;
	}
	public void setInteractionVal(String interactionVal) {
		this.interactionVal = interactionVal;
	}
	
	
	@Length(min=0, max=2000, message="联动参数长度必须介于 0 和 2000 之间")
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
	
	@Length(min=0, max=2000, message="其它设置（扩展字段JSON）长度必须介于 0 和 2000 之间")
	public String getSettings() {
		return settings;
	}
	public void setSettings(String settings) {
		this.settings = settings;
	}
	
	
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "gen_table_column";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4011";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}