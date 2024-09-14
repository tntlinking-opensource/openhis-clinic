package com.geeke.common.data;

import java.util.List;

import com.geeke.common.persistence.BaseEntity;
import com.geeke.gen.utils.GenConfigure;
import com.google.common.collect.Lists;

/**
 * 分页请求类
 * 
 * @author Lining
 * @date 2017/11/7
 */
public final class PageRequest implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int offset;

	private int limit;

	private String order;

	private String id;

	private String institution;


	private List<Parameter> params;
	
	
	public PageRequest(List<Parameter> parameters) {
		this(0, 1, parameters, null);
	}

	public PageRequest(List<Parameter> parameters, String order) {
		this(0, 1, parameters, order);
	}
	
	public PageRequest(List<Parameter> parameters, String order, String id) {
		this(0, 1, parameters, order);
		this.id = id;
	}

	public PageRequest(List<Parameter> parameters, String order, String id, String institution) {
		this(0, 1, parameters, order);
		this.id = id;
		this.institution = institution;
	}
	

	public PageRequest(int offset, int limit, List<Parameter> parameters, String order) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must not be less than zero!");
		}

		if (limit < 1) {
			throw new IllegalArgumentException("Limit must not be less than one!");
		}

		this.offset = offset;
		this.limit = limit;
		if(parameters == null){
			this.params = Lists.newArrayList();
		}else{
			this.params = parameters;
		}
		
		this.order = order;
	}

	public PageRequest(int offset, int limit, List<Parameter> parameters, String order, String id) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must not be less than zero!");
		}

		if (limit < 1) {
			throw new IllegalArgumentException("Limit must not be less than one!");
		}

		this.offset = offset;
		this.limit = limit;
		if(parameters == null){
			this.params = Lists.newArrayList();
		}else{
			this.params = parameters;
		}

		this.order = order;

		this.id = id;

	}

	public PageRequest(int offset, int limit, List<Parameter> parameters, String order, String id, String institution) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must not be less than zero!");
		}

		if (limit < 1) {
			throw new IllegalArgumentException("Limit must not be less than one!");
		}

		this.offset = offset;
		this.limit = limit;
		if(parameters == null){
			this.params = Lists.newArrayList();
		}else{
			this.params = parameters;
		}

		this.order = order;

		this.id = id;

		this.institution = institution;

	}

	public PageRequest() {
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPage() {
		return this.offset;
	}

	public int getSize() {
		return this.limit;
	}

	public String getOrder() {
		return this.order;
	}

	public int getOffset() {
		return this.offset;
	}
	
	public List<Parameter> getParams() {
		return params;
	}

	public void setParams(List<Parameter> params) {
		this.params = params;
	}

	/**
	 * 获取数据库名称
	 */
	public String getDbName(){
		return GenConfigure.dbName;
	}

	
	/* 数据库存储状态 */
	public String getDEL_FLAG_NORMAL() {
		return BaseEntity.DEL_FLAG_NORMAL;
	}

	public String getDEL_FLAG_DELETE() {
		return BaseEntity.DEL_FLAG_DELETE;
	}

	public String getDEL_FLAG_AUDIT() {
		return BaseEntity.DEL_FLAG_AUDIT;
	}
	
}
