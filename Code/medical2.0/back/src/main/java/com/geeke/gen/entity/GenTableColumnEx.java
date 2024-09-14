package com.geeke.gen.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.utils.StringUtils;
import com.google.common.collect.Lists;

import java.util.List;
/**
 * 业务表管理Entity
 * @author lys
 * @version 2019-12-06
 */
public class GenTableColumnEx extends GenTableColumn {

	private static final long serialVersionUID = 1L;

	
	/**
	 * 获取字符串长度
	 * @return
	 */
	@JsonIgnore
	public String getDataLength(){
		String[] ss = StringUtils.split(StringUtils.substringBetween(getJdbcType(), "(", ")"), ",");
		if (ss != null && ss.length == 1){// && "String".equals(getJavaType())){
			return ss[0];
		}
		return "0";
	}

	/**
	 * 获数值型小数点长度
	 * @return
	 */
	public Integer getDecimalLenth(){
		if ("java.math.BigDecimal".equals(getJavaType().getValue())){ // 金额类型
			return 2;
		} else if ("Long".equals(getJavaType().getValue()) || "Integer".equals(getJavaType().getValue())){ // 整数类型
			return 0;
		} else if ("Double".equals(getJavaType().getValue()) || "Float".equals(getJavaType().getValue())){ // 小数
			String[] ss = StringUtils.split(StringUtils.substringBetween(getJdbcType(), "(", ")"), ",");
			if (ss != null && ss.length >= 2){
				return Integer.parseInt(ss[1]);
			} else {
				return 2; // 默认返回两位小数
			}
		}
		return 0;
	}

	/**
	 * 获取简写Java类型
	 * @return
	 */
	@JsonIgnore
	public String getSimpleJavaType(){
		if (getJavaType() != null && "This".equals(getJavaType().getValue())){
			return StringUtils.capitalize(this.getGenTable().getClassName());
		}
		return StringUtils.indexOf(getJavaType().getValue(), ".") != -1 
				? StringUtils.substringAfterLast(getJavaType().getValue(), ".")
						: getJavaType().getValue();
	}
	
	/**
	 * 获取Java字段名
	 * @return
	 */
	// @JsonIgnore
	public String getFieldId(){
		return StringUtils.substringAfter(StringUtils.substringBefore(getJavaField(), "|"), ".");
	}

	/**
	 * 获取Java字段名
	 * @return
	 */
	public String getFieldName(){
		return StringUtils.substringAfter(getJavaFieldName(), ".");
	}	
	
	/**
	 * 获取简写Java字段
	 * @return
	 */
	public String getSimpleJavaField(){
		return StringUtils.substringBefore(getJavaField(), ".");
	}
	
	/**
	 * 获取Java字段，如果是对象，则获取对象.附加属性1
	 * @return
	 */
	public String getJavaFieldId(){
		return StringUtils.substringBefore(getJavaField(), "|");
	}
	
	/**
	 * 获取Java字段，如果是对象，则获取对象.附加属性2
	 * @return
	 */
	@JsonIgnore
	public String getJavaFieldName(){
		String[][] ss = getJavaFieldAttrs();
		return ss.length>0 ? getSimpleJavaField()+"."+ss[0][0] : getJavaFieldId();
	}
	
	/**
	 * 获取Java字段，所有属性名
	 *   parent.id|name|updateDate 返回： name,name; updateDate,update_date
	 * @return
	 */
	@JsonIgnore
	public String[][] getJavaFieldAttrs(){
		String[] ss = StringUtils.split(StringUtils.substringAfter(getJavaField(), "|"), "|");
		String[][] sss = new String[ss.length][2];
		if (ss!=null){
			for (int i=0; i<ss.length; i++){
				sss[i][0] = ss[i];
				sss[i][1] = StringUtils.toUnderScoreCase(ss[i]).replace(".", "_");  // customer，This对象类型的groupCust.id|name|code|channel.id 属性处理，在生成数据库语句中，把.替换成_
			}
		}
		return sss;
	}
	
	/**
	 * 获取Java字段，id及所有属性名
	 *   parent.id|name|updateDate 返回： id,id; name,name; updateDate,update_date
	 * @return
	 */
	@JsonIgnore
	public String[][] getJavaFieldIDAttrs(){
		String[] ss = StringUtils.split(StringUtils.substringAfter(getJavaField(), "."), "|");
		String[][] sss = new String[ss.length][2];
		if (ss!=null){
			for (int i=0; i<ss.length; i++){
				sss[i][0] = ss[i];
				sss[i][1] = StringUtils.toUnderScoreCase(ss[i]);
			}
		}
		return sss;
	}	
	
	/**
	 * 获取列注解列表
	 * @return
	 */
	@JsonIgnore
	public List<String> getAnnotationList(){
		List<String> list = Lists.newArrayList();
		if ("java.util.Date".equals(getJavaType().getValue())){
			if("DatePicker".equals(getShowType().getValue())) {
				list.add("com.fasterxml.jackson.annotation.JsonFormat(pattern = \"yyyy-MM-dd\")");
			}else if("TimePicker".equals(getShowType().getValue())){
				list.add("com.fasterxml.jackson.annotation.JsonFormat(pattern = \"HH:mm:ss\")");
			}else {
				list.add("com.fasterxml.jackson.annotation.JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
			}
		}
		// 导入JSR303验证依赖包
		if (!"1".equals(getIsNull()) && !"String".equals(getJavaType().getValue())){
			list.add("javax.validation.constraints.NotNull(message=\""+getComments()+"不能为空\")");
		}
		else if (!"1".equals(getIsNull()) && "String".equals(getJavaType().getValue()) && !"0".equals(getDataLength())){
			list.add("org.hibernate.validator.constraints.Length(min=1, max="+getDataLength()
					+", message=\""+getComments()+"长度必须介于 1 和 "+getDataLength()+" 之间\")");
		}
		else if ("String".equals(getJavaType().getValue()) && !"0".equals(getDataLength())){
			list.add("org.hibernate.validator.constraints.Length(min=0, max="+getDataLength()
					+", message=\""+getComments()+"长度必须介于 0 和 "+getDataLength()+" 之间\")");
		}
		return list;
	}
	
	/**
	 * 获取简写列注解列表
	 * @return
	 */
	@JsonIgnore
	public List<String> getSimpleAnnotationList(){
		List<String> list = Lists.newArrayList();
		for (String ann : getAnnotationList()){
			list.add(StringUtils.substringAfterLast(ann, "."));
		}
		return list;
	}
	
	/**
	 * 是否是基类字段
	 * @return
	 */
	@JsonIgnore
	public Boolean getIsNotBaseField(){
		return (!StringUtils.equals(getName(), "id")
				&& !StringUtils.equals(getName(), "name")
				&& !StringUtils.equals(getName(), "remarks")
				&& !StringUtils.equals(getName(), "create_by")
				&& !StringUtils.equals(getName(), "create_date")
				&& !StringUtils.equals(getName(), "update_by")
				&& !StringUtils.equals(getName(), "update_date")
				&& !StringUtils.equals(getName(), "del_flag")
				&& !StringUtils.equals(getName(), "parent_id")
				&& !StringUtils.equals(getName(), "ids")
				&& !StringUtils.equals(getName(), "proc_status")
				&& !StringUtils.equals(getName(), "proc_inst")
				&& !(((GenTableEx)getGenTable()).getIsTreeTable() && StringUtils.equals(getName(), "sort")));
	}

	
	/**
	 * 是否可编辑字段
	 * @return
	 */
	@JsonIgnore
	public Boolean getIsEditableField(){
		return !(StringUtils.equals(getName(), "id")
				|| StringUtils.equals(getName(), "create_by")
				|| StringUtils.equals(getName(), "create_date")
				|| StringUtils.equals(getName(), "update_by")
				|| StringUtils.equals(getName(), "update_date")
				|| StringUtils.equals(getName(), "del_flag")
				|| StringUtils.equals(getName(), "ids")
				|| StringUtils.equals(getName(), "proc_status")
				|| StringUtils.equals(getName(), "proc_inst"));
	}	
}