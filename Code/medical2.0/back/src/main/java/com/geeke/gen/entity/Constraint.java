package com.geeke.gen.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
/**
 * 业务表管理Entity
 * @author lys
 * @version 2021-08-26
 */
public class Constraint extends DataEntity<Constraint> {

	private static final long serialVersionUID = 1L;
	private GenTable genTable;      // 归属表编号 
	private String comments;		// 描述
	private String constraintColumn;		// 约束字段

	
	public Constraint() {
		super();
	}

	public Constraint(String id){
		super(id);
	}
	

	public Constraint(GenTable genTable){
		this.genTable = genTable;
	}

	@NotNull(message="归属表编号不能为空")
    public GenTable getGenTable() {
        return genTable;
    }

    public void setGenTable(GenTable genTable) {
        this.genTable = genTable;
    }
    
	@Length(min=0, max=500, message="描述长度必须介于 0 和 500 之间")
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
	@Length(min=0, max=500, message="约束字段长度必须介于 0 和 500 之间")
	public String getConstraintColumn() {
		return constraintColumn;
	}
	public void setConstraintColumn(String constraintColumn) {
		this.constraintColumn = constraintColumn;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "gen_table_constraint";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "4009";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}