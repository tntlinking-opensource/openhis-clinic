package com.geeke.common.entity;

import com.geeke.common.persistence.BaseEntity;

/**
 * 表列头
 * 
 * @author lys
 * @date 2019/2/2
 */
public class ColumnView extends BaseEntity<ColumnView> implements Comparable<ColumnView> {
	private static final long serialVersionUID = -4859316271465038916L;

	private String userId;		//用户ID
	private String routerId;	//路由ID
	private String tableId;		//表ID
	
	private String prop;		// 属性名
	private String label;		// 列头	
	private String align;		// 单元格对齐方式
	private Integer miniWidth;		// 最小宽度
	private Integer width;		// 列宽
	private Boolean display;		// 列表中是否显示 	
	private Integer sort;		// 排序
	private String showType;	//显示类型
	private String javaType;	// java类型

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRouterId() {
		return routerId;
	}
	public void setRouterId(String routerId) {
		this.routerId = routerId;
	}
	public String getTableId() {
		return tableId;
	}
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	public String getProp() {
		return prop;
	}
	public void setProp(String prop) {
		this.prop = prop;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}	
	public Integer getMiniWidth() {
		return miniWidth;
	}
	public void setMiniWidth(Integer miniWidth) {
		this.miniWidth = miniWidth;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Boolean getDisplay() {
		return display;
	}
	public void setDisplay(Boolean display) {
		this.display = display;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getShowType() {
		return showType;
	}
	public void setShowType(String showType) {
		this.showType = showType;
	}
	public String getJavaType() {
		return javaType;
	}
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}
	@Override
	public int compareTo(ColumnView o) {
		//if(this.getDisplay() == o.getDisplay()) {
			return this.getSort() - o.getSort();
/*		}else if(this.getDisplay()) {
			return -1;
		}*/
		//return 1;
	}
}
