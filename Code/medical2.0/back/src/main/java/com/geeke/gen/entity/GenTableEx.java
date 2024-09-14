package com.geeke.gen.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
/**
 * 业务表管理Entity
 * @author lys
 * @version 2019-12-06
 */
public class GenTableEx extends GenTable {
	private static final long serialVersionUID = 57593538386163538L;
	
	private List<GenTable> childList = Lists.newArrayList();	// 关联子表列表

/*	//@Override
	public List<GenTableColumn> getGenTableColumnList() {
		//return genTableColumnList;
	}*/
	
	@JsonIgnore
	public List<GenTable> getChildList() {
		return childList;
	}
	public void setChildList(List<GenTable> childList) {
		this.childList = childList;
	}

	/**
	 * 获取导入依赖包字符串
	 * @return
	 */
	@JsonIgnore
	public List<String> getImportList(){
		List<String> importList = Lists.newArrayList(); // 引用列表
		
		//List<GenTableColumnEx>  columns = getGenTableColumnList();
		
		for (GenTableColumn c : getGenTableColumnList()){
			
			
			GenTableColumnEx column = (GenTableColumnEx)c;
			
			//自定义类型
			if(column.getJavaType() != null && "Custom".equals(column.getJavaType().getValue())) {
				GenScheme scheme = column.getCustomType();
				if(scheme != null) {
					String customer = scheme.getPackageName() + "." + scheme.getModuleName() ;
					if(!StringUtils.isNullOrEmpty(scheme.getSubModuleName())) {
						customer = customer + "." + scheme.getSubModuleName()+ ".entity";
					}else {
						customer = customer + ".entity" ;
					}
					customer = customer+"."+ scheme.getGenTable().getClassName();
					if (!importList.contains(customer)){
						importList.add(customer);
					}
				}
			}
			
			if (column.getIsNotBaseField() || ("1".equals(column.getIsQuery()) && "between".equals(column.getQueryType().getValue())
							&& ("createDate".equals(column.getSimpleJavaField()) || "updateDate".equals(column.getSimpleJavaField())))){
				// 导入类型依赖包， 如果类型中包含“.”，则需要导入引用。
				if (StringUtils.indexOf(column.getJavaType().getValue(), ".") != -1 && !importList.contains(column.getJavaType().getValue())){
					importList.add(column.getJavaType().getValue());
				}
			}
			if (column.getIsNotBaseField()){
				// 导入JSR303、Json等依赖包
				for (String ann : column.getAnnotationList()){
					if (!importList.contains(StringUtils.substringBeforeLast(ann, "("))){
						importList.add(StringUtils.substringBefore(ann, "("));
					}
				}
			}
			
			// Long类型字段序列化处理，解决转前端js溢出问题
			if("Long".equals(column.getJavaType().getValue())) {
				if (!importList.contains("com.fasterxml.jackson.databind.annotation.JsonSerialize")){
					importList.add("com.fasterxml.jackson.databind.annotation.JsonSerialize");
				}
				if (!importList.contains("com.fasterxml.jackson.databind.ser.std.ToStringSerializer")){
					importList.add("com.fasterxml.jackson.databind.ser.std.ToStringSerializer");
				}
			}
		}
		
		// 如果有子表，则需要导入List相关引用
		if (getChildList() != null && getChildList().size() > 0){
			if (!importList.contains("java.util.List")){
				importList.add("java.util.List");
			}
			if (!importList.contains("com.google.common.collect.Lists")){
				importList.add("com.google.common.collect.Lists");
			}
		}
		
		
		return importList;
	}
	
	
	/**
	 * 是否存在Id标识字段
	 * @return
	 */
	@JsonIgnore
	public Boolean getParentExists(){
		GenTable parentTable = this.getParentTable();
		return parentTable != null && StringUtils.isNotBlank(parentTable.getId()) && StringUtils.isNotBlank(this.getParentTableFk());
	}	
	
	/**
	 * 是否存在父类
	 * @return
	 */
	@JsonIgnore
	public Boolean getIdExists(){
		for (GenTableColumn c : getGenTableColumnList()){
			if (StringUtils.equalsIgnoreCase("id", c.getName())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否存在create_date列
	 * @return
	 */
	@JsonIgnore
	public Boolean getCreateDateExists(){
		for (GenTableColumn c : getGenTableColumnList()){
			if (StringUtils.equalsIgnoreCase("create_date", c.getName())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否存在update_date列
	 * @return
	 */
	@JsonIgnore
	public Boolean getUpdateDateExists(){
		for (GenTableColumn c : getGenTableColumnList()){
			if (StringUtils.equalsIgnoreCase("update_date", c.getName())){
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否存在update_by列
	 * @return
	 */
	@JsonIgnore
	public Boolean getUpdateByExists(){
		for (GenTableColumn c : getGenTableColumnList()){
			if (StringUtils.equalsIgnoreCase("update_by", c.getName())){
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否存在sort列
	 * @return
	 */
	@JsonIgnore
	public Boolean getSortExists(){
		for (GenTableColumn c : getGenTableColumnList()){
			if (StringUtils.equalsIgnoreCase("sort", c.getName())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获得排序列
	 * @return
	 */
	@JsonIgnore
	public GenTableColumn getSortColumn(){
		for (GenTableColumn c : getGenTableColumnList()){
			if (StringUtils.equalsIgnoreCase("sort", c.getName())){
				return c;
			}
		}
		return null;
	}
	

	/**
	 * 是否存在del_flag列
	 * @return
	 */
	@JsonIgnore
	public Boolean getDelFlagExists(){
		for (GenTableColumn c : getGenTableColumnList()){
			if (StringUtils.equalsIgnoreCase("del_flag", c.getName())){
				return true;
			}
		}
		return false;
	}	
	
	/**
	 * 是否包含树形选择框--编辑框
	 * @return
	 */
	@JsonIgnore
	public boolean getHasTreeSelectInput() {
		for (GenTableColumn column : getGenTableColumnList()){
			if(column.getShowType() != null 
					&& "SelectTree".equals(column.getShowType().getValue())
					&& "1".equals(column.getIsEdit())){
				return true;
			}
		}
		
		for(GenTable child: getChildList()) {
			for (GenTableColumn column : child.getGenTableColumnList()){
				if(column.getShowType() != null 
						&& "SelectTree".equals(column.getShowType().getValue())
						&& "1".equals(column.getIsEdit())){
					return true;
				}
			}			
		}		
		return false;
	}
	
	/**
	 * 是否包含StringMultiSelect--编辑框
	 * @return
	 */
	@JsonIgnore
	public boolean getHasStringMultiSelectInput() {
		for (GenTableColumn column : getGenTableColumnList()){
			if(column.getShowType() != null 
					&& "StringMultiSelect".equals(column.getShowType().getValue())
					&& "1".equals(column.getIsEdit())){
				return true;
			}
		}
		
		for(GenTable child: getChildList()) {
			for (GenTableColumn column : child.getGenTableColumnList()){
				if(column.getShowType() != null 
						&& "StringMultiSelect".equals(column.getShowType().getValue())
						&& "1".equals(column.getIsEdit())){
					return true;
				}
			}			
		}
		return false;
	}
	
	/**
	 * 是否包含树形选择框--查询条件
	 * @return
	 */
	@JsonIgnore
	public boolean getHasTreeSelectQuery() {
		for (GenTableColumn column : getGenTableColumnList()){
			if(column.getShowType() != null 
					&& "SelectTree".equals(column.getShowType().getValue())
					&& "1".equals(column.getIsQuery())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否包含日期范围选择框--查询条件
	 * @return
	 */
	@JsonIgnore
	public boolean getHasDataRangePicker4Query() {
		for (GenTableColumn column : getGenTableColumnList()){
			if(column.getShowType() != null && column.getQueryType() != null
					&& ("DatePicker".equals(column.getShowType().getValue()) || "DateTimePicker".equals(column.getShowType().getValue()))
					&& "between".equals(column.getQueryType().getValue())  
					&& "1".equals(column.getIsQuery())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否包含数值范围选择框--查询条件
	 * @return
	 */
	@JsonIgnore
	public boolean getHasNumberRangePicker4Query() {
		for (GenTableColumn column : getGenTableColumnList()){
			if(column.getShowType() != null && column.getQueryType() != null
					&& ("SingleInput".equals(column.getShowType().getValue()) || "InputNumber".equals(column.getShowType().getValue()) || "IncNumber".equals(column.getShowType().getValue()))
					&& "between".equals(column.getQueryType().getValue())  
					&& "1".equals(column.getIsQuery())){
				return true;
			}
		}
		return false;
	}
		
	
	/**
	 * 是否为树形表
	 * @return
	 */
	@JsonIgnore
	public boolean getIsTreeTable() {
		for (GenTableColumn column : getGenTableColumnList()){
			//java类型为This, 并且字段名为parent_id
			if(column.getJavaType() != null && "This".equals(column.getJavaType().getValue()) && StringUtils.equalsIgnoreCase("parent_id", column.getName())){
				return true;
			}
		}
		return false;		
	}
	
	/**
	 * 获取parent列
	 * @return
	 */
	@JsonIgnore
	public GenTableColumn getParentColumn() {
		for (GenTableColumn column : getGenTableColumnList()){
			//java类型为This, 并且字段名为parent_id
			if(column.getJavaType() != null && "This".equals(column.getJavaType().getValue()) && StringUtils.equalsIgnoreCase("parent_id", column.getName())){
				return column;
			}
		}
		return null;
	}
	
	

	
	/**
	 * 客户端查询框导入的自定义类型处理接口，去除重复的
	 * @return
	 */
	@JsonIgnore
	public List<GenTableColumn> getQueryImportList() {
		List<GenTableColumn> clientImports = Lists.newArrayList();
		getClientImportList(this, clientImports, "query");
		
		for (GenTable genTable : this.getChildList()){
			getClientImportList(genTable, clientImports, "query");
		}
		
		return clientImports;
	}
	
	/**
	 * 客户端编辑框导入的自定义类型处理接口，去除重复的
	 * @return
	 */
	@JsonIgnore
	public List<GenTableColumn> getEditImportList() {
		List<GenTableColumn> clientImports = Lists.newArrayList();
		getClientImportList(this, clientImports, "edit");
		
		for (GenTable genTable : this.getChildList()){
			getClientImportList(genTable, clientImports, "edit");
		}
		
		return clientImports;
	}
	
	/**
	 * 是否包含密码框
	 * @return
	 */
	@JsonIgnore
	public boolean getHasPassWordInput() {
		for (GenTableColumn column : getGenTableColumnList()){
			if(column.getShowType() != null && "PassWordInput".equals(column.getShowType().getValue())
					&& "1".equals(column.getIsEdit())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否多文件上传组件
	 * @return
	 */
	@JsonIgnore
	public boolean getMutilFileUploadExists () {
		for (GenTableColumn column : getGenTableColumnList()){
			if(column.getShowType() != null && "MultiFileUpload".equals(column.getShowType().getValue())
					&& "1".equals(column.getIsEdit())){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 获取查询窗联动变量及其联动所联动的值
	 * @return
	 */
	@JsonIgnore
	public Map<String, List<GenTableColumn>> getQueryInteractions(){
		Map<String, List<GenTableColumn>> map = Maps.newHashMap();
		for (GenTableColumn column : this.getGenTableColumnList()){
			if(!"1".equals(column.getIsQuery())) {
				continue;
			}
			String interaction = column.getInteraction();
			if(!StringUtils.isBlank(interaction)) {
				String arr[] = interaction.split(",");
				for(String key : arr) {
					// 触发事件列
					GenTableColumnEx actionColumn = getColumByName(key);
					if(actionColumn == null || !"1".equals(actionColumn.getIsQuery())) {
						continue;
					}
					if(map.containsKey(key)) {
						List<GenTableColumn> colList = map.get(key);
						colList.add(column);
					}
					else {
						List<GenTableColumn> colList = Lists.newArrayList();
						colList.add(column);
						map.put(key, colList);
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 获取编辑窗联动变量及其联动所联动的值
	 * @return
	 */
	@JsonIgnore
	public Map<String, List<GenTableColumn>> getEditInteractions(){
		Map<String, List<GenTableColumn>> map = Maps.newHashMap();
		for (GenTableColumn column : this.getGenTableColumnList()){
			if(!"1".equals(column.getIsEdit())) {
				continue;
			}
			String interaction = column.getInteraction();
			if(!StringUtils.isBlank(interaction)) {
				String arr[] = interaction.split(",");
				for(String key : arr) {
					// 触发事件列
					GenTableColumnEx actionColumn = getColumByName(key);
					if(actionColumn == null || !"1".equals(actionColumn.getIsEdit())) {
						continue;
					}
					if(map.containsKey(key)) {
						List<GenTableColumn> colList = map.get(key);
						colList.add(column);
					}
					else {
						List<GenTableColumn> colList = Lists.newArrayList();
						colList.add(column);
						map.put(key, colList);
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 获取列对象
	 * @param columnName  数据表列名
	 * @return
	 */
	@JsonIgnore
	public GenTableColumnEx getColumByName(String columnName) {
		for (GenTableColumn cc : this.getGenTableColumnList()){
			GenTableColumnEx column = (GenTableColumnEx)cc;
			if(columnName.equals(column.getName())) {
				return column;
			}
		}
		return null;
	}
	
	/**
	 * 客户端导入的自定义类型处理接口，去除重复的
	 * @return
	 */
	private void getClientImportList(GenTable genTable, List<GenTableColumn> clientImports, String type) {
		for (GenTableColumn column : genTable.getGenTableColumnList()){
			// 作为查询框的控件，如果为This，并且为SelectTree避免重复import函数
			if("query".equals(type) && "This".equals(column.getJavaType().getValue()) && "SelectTree".equals(column.getShowType().getValue())) {  
				continue;
			}
			
			if(column.getShowType() != null 
					&& ("Select".equals(column.getShowType().getValue()) || "SelectTree".equals(column.getShowType().getValue()))
					&& (("query".equals(type) && "1".equals(column.getIsQuery())) ||  ("edit".equals(type) && "1".equals(column.getIsEdit())))){
				if(column.getJavaType() != null && "This".equals(column.getJavaType().getValue())) {
					boolean found = false;
					for(GenTableColumn c: clientImports) {
						if(column.getJavaType().getValue().equals(c.getJavaType().getValue()) 
								&& column.getShowType().getValue().equals(c.getShowType().getValue())) {
							found = true;
							break;
						}
					}
					
					if(!found) {
						clientImports.add(column);
					}
				} else if(column.getCustomType() != null && !StringUtils.isNullOrEmpty(column.getCustomType().getId())) {
					boolean found = false;
					for(GenTableColumn c: clientImports) {
						if(column.getCustomType().getId().equals(c.getCustomType().getId()) 
								&& column.getShowType().getValue().equals(c.getShowType().getValue())) {
							found = true;
							break;
						}
					}
					
					if(!found) {
						clientImports.add(column);
					}
				}
			}
		}
	}
	
	/**
	 * 是否包含流程实例字段
	 * @return
	 */
	@JsonIgnore
	public boolean getHasProcInst() {
		for (GenTableColumn column : getGenTableColumnList()){
			if(StringUtils.equalsIgnoreCase(column.getName(), "proc_inst")){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否包含流程实例状态字段
	 * @return
	 */
	@JsonIgnore
	public boolean getHasProcStatus() {
		for (GenTableColumn column : getGenTableColumnList()){
			if(StringUtils.equalsIgnoreCase(column.getName(), "proc_status")){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否包含流程实例状态字段
	 * @return
	 */
	@JsonIgnore
	public boolean getLockedExists() {
		for (GenTableColumn column : getGenTableColumnList()){
			if(StringUtils.equalsIgnoreCase(column.getName(), "is_locked")){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否有非关联的子表
	 * @return
	 */
	@JsonIgnore
	public boolean getHasUnAssChildTable() {
		if(childList != null && childList.size() > 0) {
			for(GenTable child : childList) {
				if("0".equals( child.getIsAssociation())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 是否包含自动编号类型的字段
	 * @return
	 */
	@JsonIgnore
	public boolean getAutoSerialExists() {
		for (GenTableColumn column : getGenTableColumnList()){
			if(StringUtils.equalsIgnoreCase(column.getShowType().getValue(), "AutoSerial")){
				return true;
			}
		}
		
		// 子表中是否有自动编号类型字段
		if(childList != null && childList.size() > 0) {
			for(GenTable child : childList) {
				for (GenTableColumn column : child.getGenTableColumnList()){
					if(StringUtils.equalsIgnoreCase(column.getShowType().getValue(), "AutoSerial")){
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 是否包含属性定义器的字段
	 * @return
	 */
	@JsonIgnore
	public boolean getPropertyDefExists() {
		for (GenTableColumn column : getGenTableColumnList()){
			if(StringUtils.equalsIgnoreCase(column.getShowType().getValue(), "PropertyDef")){
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否包含属性输入器的字段
	 * @return
	 */
	@JsonIgnore
	public boolean getPropertyInputExists() {
		for (GenTableColumn column : getGenTableColumnList()){
			if(StringUtils.equalsIgnoreCase(column.getShowType().getValue(), "PropertyInput")){
				return true;
			}
		}
		return false;
	}
}