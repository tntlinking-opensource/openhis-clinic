package com.geeke.gen.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.google.common.collect.Lists;
import org.hibernate.validator.constraints.Length;

import java.util.List;
/**
 * 业务表管理Entity
 * @author lys
 * @version 2021-08-26
 */
public class GenTable extends DataEntity<GenTable> {

	private static final long serialVersionUID = 1L;
	private String comments;		// 说明
	private String source;		// 来源
	private String className;		// 实体类名称
	private GenTable parentTable;      // 关联父表 
	private String parentTableFk;		// 父表外键
	private String isAssociation;		// 是关联表
	private String filter;		// 筛选条件
	private String orderColumns;	// 排序字段

	private List<Constraint> constraintList = Lists.newArrayList();		// 子表列表
	private List<GenTableColumn> genTableColumnList = Lists.newArrayList();		// 子表列表
	
	public GenTable() {
		super();
	}

	public GenTable(String id){
		super(id);
	}
	

	@Length(min=0, max=500, message="说明长度必须介于 0 和 500 之间")
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
	@Length(min=0, max=128, message="来源长度必须介于 0 和 128 之间")
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	
	@Length(min=1, max=100, message="实体类名称长度必须介于 1 和 100 之间")
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	
    public GenTable getParentTable() {
        return parentTable;
    }

    public void setParentTable(GenTable parentTable) {
        this.parentTable = parentTable;
    }
    
	@Length(min=0, max=100, message="父表外键长度必须介于 0 和 100 之间")
	public String getParentTableFk() {
		return parentTableFk;
	}
	public void setParentTableFk(String parentTableFk) {
		this.parentTableFk = parentTableFk;
	}
	
	
	@Length(min=1, max=1, message="是关联表长度必须介于 1 和 1 之间")
	public String getIsAssociation() {
		return isAssociation;
	}
	public void setIsAssociation(String isAssociation) {
		this.isAssociation = isAssociation;
	}
	
	
	@Length(min=0, max=2000, message="筛选条件长度必须介于 0 和 2000 之间")
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public String getOrderColumns() {
		return orderColumns;
	}
	
	public void setOrderColumns(String orderColumns) {
		this.orderColumns = orderColumns;
	}

	public List<Constraint> getConstraintList() {
		return constraintList;
	}

	public void setConstraintList(List<Constraint> constraintList) {
		this.constraintList = constraintList;
	}
	public List<GenTableColumn> getGenTableColumnList() {
		return genTableColumnList;
	}

	public void setGenTableColumnList(List<GenTableColumn> genTableColumnList) {
		this.genTableColumnList = genTableColumnList;
	}
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "gen_table";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4001";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}