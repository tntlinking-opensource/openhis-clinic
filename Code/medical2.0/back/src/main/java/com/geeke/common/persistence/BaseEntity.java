
package com.geeke.common.persistence;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.utils.IdGen;
import com.geeke.utils.StringUtils;


/**
 * Entity支持类
 * @author lys
 * @version 2014-05-16
 */
public abstract class BaseEntity<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 实体编号（唯一标识）
	 */
	protected String id;

	public BaseEntity() {
	}
	
	public BaseEntity(String id) {
		this();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	/**
	 * 插入之前执行方法，需要手动调用
	 */
	@JsonIgnore
	public void preInsert(){
		if (StringUtils.isBlank(getId())) {
			setId(IdGen.uuid());
		}
	}

	/**
	 * 更新之前执行方法，需要手动调用
	 */
	@JsonIgnore
	public void preUpdate(){
	}
	
	/**
	 * 获取实体对应表名，在实体类中覆盖
	 */
	@JsonIgnore
	public String getBusTableName() {
		return "";
	}
	
	/**
	 * 获取实体对应表Id，在实体类中覆盖
	 */
	@JsonIgnore
	public String getBusTableId() {
		return "";
	}
	
	/**
	 * 获取实体表中是否存在del_flag字段，在实体类中覆盖
	 */
	@JsonIgnore
	public boolean getBusTableHasDelFlag() {
		return false;
	}	
	
	/**
	 * 删除标记（0：正常；1：删除；2：审核；）
	 */
	public static final String DEL_FLAG_NORMAL = "0";
	public static final String DEL_FLAG_DELETE = "1";
	public static final String DEL_FLAG_AUDIT = "2";
	
}
