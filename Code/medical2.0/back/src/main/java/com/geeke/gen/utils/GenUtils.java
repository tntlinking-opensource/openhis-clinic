package com.geeke.gen.utils;

import com.geeke.gen.entity.*;
import com.geeke.sys.entity.DictItem;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.utils.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 代码生成工具类
 * @author
 * @version 2013-11-16
 */
public class GenUtils {

	private static Logger logger = LoggerFactory.getLogger(GenUtils.class);
	
	private static final String GEN_CONFIGUR_PATH = "/templates/modules/gen/";
	private static final String GEN_CONFIGUR_NAME = "config.xml";

	/**
	 * 初始化列属性字段
	 * @param genTable
	 */
	public static void initColumnField(List<GenTableColumn> columnList, List<DictItem> itemList){
		if(columnList == null) {
			return;
		}
		for (GenTableColumn cc : columnList){
			
			GenTableColumnEx column = (GenTableColumnEx)cc;
			// 如果是不是新增列，则跳过。
			if (StringUtils.isNotBlank(column.getId())){
				continue;
			}
			// 初始默认值
			column.setIsEdit("0");
			column.setIsInsert("1");
			column.setIsList("0");
			column.setIsQuery("0");
			column.setWidth(180);  // 默认列宽
			column.setJavaType(getDictItem(itemList, "5001", "String"));  // 默认类型
			column.setShowType(getDictItem(itemList, "5003", "SingleInput"));  // 默认输入框
			column.setQueryType(getDictItem(itemList, "5002", "="));    // 默认查询方式
			column.setCustomType(new GenScheme());   // 默认自定义类型
			
			// 设置java字段名
			column.setJavaField(StringUtils.toCamelCase(column.getName()));
			
			// 设置字段说明
			if (StringUtils.isBlank(column.getComments())){
				column.setComments(column.getName());
			}
			
			// 设置java类型、显示类型、列表宽度
			if (StringUtils.startsWithIgnoreCase(column.getJdbcType(), "CHAR")
					|| StringUtils.startsWithIgnoreCase(column.getJdbcType(), "VARCHAR")
					|| StringUtils.startsWithIgnoreCase(column.getJdbcType(), "NARCHAR")){
				
				column.setJavaType(getDictItem(itemList, "5001", "String"));
				//长度为1时，表示为true/false，显示类型设置为 Switch开关
				if(Integer.parseInt(column.getDataLength()) == 1) {
					column.setShowType(getDictItem(itemList, "5003", "Switch"));
					column.setWidth(70);
				}else{
					column.setShowType(getDictItem(itemList, "5003", "SingleInput"));
					column.setWidth(180);
				}
			}else if (StringUtils.startsWithIgnoreCase(column.getJdbcType(), "DATETIME")
					|| StringUtils.startsWithIgnoreCase(column.getJdbcType(), "TIMESTAMP")){
				column.setJavaType(getDictItem(itemList, "5001", "java.util.Date"));
				column.setShowType(getDictItem(itemList, "5003", "DateTimePicker"));
				column.setWidth(160);
			}else if (StringUtils.startsWithIgnoreCase(column.getJdbcType(), "DATE")){
				column.setJavaType(getDictItem(itemList, "5001", "java.util.Date"));
				column.setShowType(getDictItem(itemList, "5003", "DatePicker"));
				column.setWidth(120);
			}else if(StringUtils.startsWithIgnoreCase(column.getJdbcType(), "TIME")) {
				column.setJavaType(getDictItem(itemList, "5001", "java.util.Date"));
				column.setShowType(getDictItem(itemList, "5003", "TimePicker"));
				column.setWidth(100);
			}else if (StringUtils.startsWithIgnoreCase(column.getJdbcType(), "DOUBLE")){
				column.setJavaType(getDictItem(itemList, "5001", "Double"));
				column.setShowType(getDictItem(itemList, "5003", "InputNumber"));
				column.setWidth(80);
			}else if (StringUtils.startsWithIgnoreCase(column.getJdbcType(), "FLOAT")){
				column.setJavaType(getDictItem(itemList, "5001", "Float"));
				column.setShowType(getDictItem(itemList, "5003", "InputNumber"));				
				column.setWidth(80);
			}else if (StringUtils.startsWithIgnoreCase(column.getJdbcType(), "BIGINT")
					|| StringUtils.startsWithIgnoreCase(column.getJdbcType(), "INT")
					|| StringUtils.startsWithIgnoreCase(column.getJdbcType(), "MEDIUMINT")
					|| StringUtils.startsWithIgnoreCase(column.getJdbcType(), "SMALLINT")
					|| StringUtils.startsWithIgnoreCase(column.getJdbcType(), "TINYINT")){
				
				column.setShowType(getDictItem(itemList, "5003", "InputNumber"));
				// 如果是浮点型
				String[] ss = StringUtils.split(StringUtils.substringBetween(column.getJdbcType(), "(", ")"), ",");
				if (ss != null && ss.length == 2 && Integer.parseInt(ss[1])>0){
					column.setJavaType(getDictItem(itemList, "5001", "Double"));
					column.setWidth(100);
				}
				// 如果是整形
				else if (ss != null && ss.length == 1 && Integer.parseInt(ss[0]) <= 16){
					column.setJavaType(getDictItem(itemList, "5001", "Integer"));
					column.setWidth(80);
				}
				// 长整形--超过16位的当作字符串处理
				else{
					column.setJavaType(getDictItem(itemList, "5001", "String"));
					column.setShowType(getDictItem(itemList, "5003", "SingleInput"));
					column.setWidth(80);
				}
			}else if (StringUtils.startsWithIgnoreCase(column.getJdbcType(), "DECIMAL")){
				column.setJavaType(getDictItem(itemList, "5001", "java.math.BigDecimal"));
				column.setWidth(100);
				column.setShowType(getDictItem(itemList, "5003", "InputNumber"));
			}else if (StringUtils.startsWithIgnoreCase(column.getJdbcType(), "LONGBLOB")){
				column.setJavaType(getDictItem(itemList, "5001", "byte[]"));
				column.setWidth(100);
				column.setShowType(getDictItem(itemList, "5003", "InputNumber"));
			}
			
			// 编辑字段
			if (!StringUtils.equalsIgnoreCase(column.getName(), "id")
					&& !StringUtils.equalsIgnoreCase(column.getName(), "create_by")
					&& !StringUtils.equalsIgnoreCase(column.getName(), "create_date")
					&& !StringUtils.equalsIgnoreCase(column.getName(), "del_flag")){
				column.setIsEdit("1");
			}

			// 列表字段
			if (StringUtils.equalsIgnoreCase(column.getName(), "name")
					|| StringUtils.equalsIgnoreCase(column.getName(), "title")
					|| StringUtils.equalsIgnoreCase(column.getName(), "remarks")
					|| StringUtils.equalsIgnoreCase(column.getName(), "update_date")){
				column.setIsList("1");
			}
			
			// 查询字段
			if (StringUtils.equalsIgnoreCase(column.getName(), "name")
					|| StringUtils.equalsIgnoreCase(column.getName(), "title")){
				column.setIsQuery("1");
			}
			
			// 查询字段类型
			if (StringUtils.equalsIgnoreCase(column.getName(), "name")
					|| StringUtils.equalsIgnoreCase(column.getName(), "title")){
				column.setQueryType(getDictItem(itemList, "5002", "like"));
			}

			// 备注、内容
			else if (StringUtils.equalsIgnoreCase(column.getName(), "remarks")
					|| StringUtils.equalsIgnoreCase(column.getName(), "content")){
				column.setShowType(getDictItem(itemList, "5003", "MultipleInput"));
			}
			// 父级ID
			else if (StringUtils.equalsIgnoreCase(column.getName(), "parent_id")){
				column.setJavaType(getDictItem(itemList, "5001", "This"));
				column.setJavaField("parent.id|name");
				column.setShowType(getDictItem(itemList, "5003", "SelectTree"));
			}
			// 全编号
			else if (StringUtils.equalsIgnoreCase(column.getName(), "ids")){
				column.setQueryType(getDictItem(itemList, "5002", "like"));
			}
			
			// 通用字段列表显示宽度
			if(StringUtils.equalsIgnoreCase(column.getName(), "name")) {
				column.setWidth(180);
			}else if(StringUtils.equalsIgnoreCase(column.getName(), "create_date") || StringUtils.equalsIgnoreCase(column.getName(), "update_date")){
				column.setWidth(160);
			}else if(StringUtils.equalsIgnoreCase(column.getName(), "create_by") || StringUtils.equalsIgnoreCase(column.getName(), "update_by")){
				column.setWidth(100);
			}else if(StringUtils.equalsIgnoreCase(column.getName(), "is_locked") || StringUtils.equalsIgnoreCase(column.getName(), "del_flag") || StringUtils.equalsIgnoreCase(column.getName(), "sort")){
				column.setWidth(80);
			}else if(StringUtils.equalsIgnoreCase(column.getName(), "remarks")) {
				column.setWidth(240);
			}else if(StringUtils.equalsIgnoreCase(column.getName(),"code")) {
				column.setWidth(120);
			}else if(StringUtils.equalsIgnoreCase(column.getName(), "comments")) {
				column.setWidth(200);
			}
			
			// 禁用字段
			if(StringUtils.equalsIgnoreCase(column.getName(), "is_locked")) {
				column.setIsEdit("1");
				column.setIsInsert("1");
				column.setIsList("1");
				column.setIsQuery("0");
			}
			
			// 流程实例
			if(StringUtils.equalsIgnoreCase(column.getName(), "proc_inst")) {
				column.setIsEdit("0");
				column.setIsInsert("1");
				column.setIsList("0");
				column.setIsQuery("0");
			}
			
			// 工作流状态字段
			if(StringUtils.equalsIgnoreCase(column.getName(), "proc_status")) {
				column.setIsEdit("0");
				column.setIsInsert("1");
				column.setIsList("0");
				column.setIsQuery("0");
				
				column.setJavaField(column.getJavaField() + ".id|name|value");
				column.setJavaType(getDictItem(itemList, "5001", "Custom"));
				GenScheme scheme = new GenScheme();
				scheme.setId("6006");
				scheme.setName("字典项控件");
				column.setCustomType(scheme);
				column.setShowType(getDictItem(itemList, "5003", "Select"));
				column.setParameters("{'columnName': 'dict_type_id', 'queryType': '=', 'value': '5005'}");
				column.setWidth(120);
			}
		}
	}
	
	/**
	 * 获取模板路径
	 * @return
	 */
	public static String getTemplatePath(){
		try{
			File file = new DefaultResourceLoader().getResource("").getFile();
			if(file != null){
				return file.getAbsolutePath() + File.separator + StringUtils.replaceEach(GenUtils.class.getName(), 
						new String[]{"util."+GenUtils.class.getSimpleName(), "."}, new String[]{"template", File.separator});
			}			
		}catch(Exception e){
			logger.error("{}", e);
		}

		return "";
	}
	
	/**
	 * XML文件转换为对象
	 * @param fileName
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fileToObject(String fileName, Class<?> clazz){
		try {
			String pathName = GEN_CONFIGUR_PATH + fileName;
			logger.debug("File to object: {}", pathName);

			InputStream is = clazz.getResourceAsStream(pathName);
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder sb = new StringBuilder();  
			while (true) {
				String line = br.readLine();
				if (line == null){
					break;
				}
				sb.append(line).append("\r\n");
			}
			if (is != null) {
				is.close();
			}
			if (br != null) {
				br.close();
			}
//			logger.debug("Read file content: {}", sb.toString());
			return (T) JaxbMapper.fromXml(sb.toString(), clazz);
		} catch (IOException e) {
			logger.warn("Error file convert: {}", e.getMessage());
		}
//		String pathName = StringTools.replace(getTemplatePath() + "/" + fileName, "/", File.separator);
//		logger.debug("file to object: {}", pathName);
//		String content = "";
//		try {
//			content = FileUtils.readFileToString(new File(pathName), "utf-8");
////			logger.debug("read config content: {}", content);
//			return (T) JaxbMapper.fromXml(content, clazz);
//		} catch (IOException e) {
//			logger.warn("error convert: {}", e.getMessage());
//		}
		return null;
	}
	
	/**
	 * 获取代码生成配置对象
	 * @return
	 */
	public static GenConfig getConfig(){
		return fileToObject(GEN_CONFIGUR_NAME, GenConfig.class);
	}

	/**
	 * 根据分类获取模板列表
	 * @param config
	 * @param genScheme
	 * @param isChildTable 是否是子表
	 * @return
	 */
	public static List<GenTemplate> getTemplateList(GenConfig config, String category, boolean isChildTable){
		List<GenTemplate> templateList = Lists.newArrayList();
		if (config !=null && config.getCategoryList() != null && category !=  null){
			for (GenCategory e : config.getCategoryList()){
				if (category.equals(e.getValue())){
					List<String> list = null;
					if (!isChildTable){
						list = e.getTemplate();
					}else{
						list = e.getChildTableTemplate();
					}
					if (list != null){
						for (String s : list){
							if (StringUtils.startsWith(s, GenCategory.CATEGORY_REF)){
								templateList.addAll(getTemplateList(config, StringUtils.replace(s, GenCategory.CATEGORY_REF, ""), false));
							}else{
								GenTemplate template = fileToObject(s, GenTemplate.class);
								if (template != null){
									templateList.add(template);
								}
							}
						}
					}
					break;
				}
			}
		}
		return templateList;
	}
	

	/**
	 * 获取数据模型
	 * @param genScheme
	 * @param genTable
	 * @return
	 */
	public static Map<String, Object> getDataModel(GenScheme genScheme){
		Map<String, Object> model = Maps.newHashMap();
		
		model.put("packageName", StringUtils.lowerCase(genScheme.getPackageName()));
		model.put("lastPackageName",  StringUtils.substringAfterLast( genScheme.getPackageName(),"."));
		model.put("moduleName", StringUtils.lowerCase(genScheme.getModuleName()));
		model.put("subModuleName", StringUtils.lowerCase(genScheme.getSubModuleName()));
		model.put("className", StringUtils.uncapitalize(genScheme.getGenTable().getClassName()));
		model.put("ClassName", StringUtils.capitalize(genScheme.getGenTable().getClassName()));
		
		model.put("functionName", genScheme.getFunctionName());
		model.put("functionNameSimple", genScheme.getFunctionNameSimple());
		
		model.put("functionAuthor", StringUtils.isNotBlank(genScheme.getFunctionAuthor())?genScheme.getFunctionAuthor(): SessionUtils.getUserJson().getString("name"));
		model.put("functionVersion", DateUtils.getDate());
		
		model.put("urlPrefix", model.get("moduleName")+(StringUtils.isNotBlank(genScheme.getSubModuleName())
				?"/"+StringUtils.lowerCase(genScheme.getSubModuleName()):"")+"/"+model.get("className"));
		model.put("viewPrefix", //StringTools.substringAfterLast(model.get("packageName"),".")+"/"+
				model.get("urlPrefix"));
		model.put("permissionPrefix", model.get("moduleName")+(StringUtils.isNotBlank(genScheme.getSubModuleName())
				?":"+StringUtils.lowerCase(genScheme.getSubModuleName()):"")+":"+model.get("className"));
		
		// 全局数据库配置   application.yml 数据库类型
		model.put("dbType", GenConfigure.dbName);

		model.put("dialogWidth", genScheme.getDialogWidth());
		model.put("colCounts", genScheme.getColCounts());
		
		model.put("table", genScheme.getGenTable());
		model.put("schemeId", genScheme.getId());
		return model;
	}	
	
	/**
	 * 生成到文件
	 * @param tpl
	 * @param model
	 * @param replaceFile
	 * @return
	 */
	public static String generateToFile(GenTemplate tpl, Map<String, Object> model, boolean isReplaceFile){
		String fileName = GenConfigure.rootPath + File.separator 
				+ StringUtils.replaceEach(FreeMarkers.renderString(tpl.getFilePath() + "/", model), 
						new String[]{"//", "/", "."}, new String[]{File.separator, File.separator, File.separator})
				+ FreeMarkers.renderString(tpl.getFileName(), model);
		logger.debug(" fileName === " + fileName);
		
		// 获取生成文件内容
		String content = FreeMarkers.renderString(StringUtils.trimToEmpty(tpl.getContent()), model, GEN_CONFIGUR_PATH);
		logger.debug(" content === \r\n" + content);
		
		// 如果选择替换文件，则删除原文件
		if (isReplaceFile){
			FileUtils.deleteFile(fileName);
		}
		
		// 创建并写入文件
		if (FileUtils.createFile(fileName)){
			FileUtils.writeToFile(fileName, content, true);
			logger.debug(" file create === " + fileName);
			return "生成成功："+ fileName + "<br/>";
		}else{
			logger.debug(" file extents === " + fileName);
			return "<font color='#E6A23C'>文件已存在：" + fileName + "</font><br/>";
		}
	}
	
	/**
	 * 删除已生成的代码文件
	 * @param tpl
	 * @param model
	 * @return
	 */
	public static String deleteFile(GenTemplate tpl, Map<String, Object> model){
		String fileName = GenConfigure.rootPath + File.separator 
				+ StringUtils.replaceEach(FreeMarkers.renderString(tpl.getFilePath() + "/", model), 
						new String[]{"//", "/", "."}, new String[]{File.separator, File.separator, File.separator})
				+ FreeMarkers.renderString(tpl.getFileName(), model);
		logger.debug(" fileName === " + fileName);
			
		if(!FileUtils.deleteFile(fileName)) {
			logger.debug(" file extents === " + fileName);
			return "删除失败："+fileName+"<br/>";			
		}
		logger.debug(" file create === " + fileName);
		return "删除成功："+fileName+"<br/>";
	}

	
	private static DictItem getDictItem(List<DictItem> itemList, String typeId, String value) {
		if(StringUtils.isBlank(typeId) || StringUtils.isBlank(value) || itemList == null){
			return null;
		}
		for(DictItem item: itemList) {
			
			if(value.equals(item.getValue()) && typeId.equals(item.getDictType().getId())) {
				return item;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		try {
			GenConfig config = getConfig();
			System.out.println(config);
			System.out.println(JaxbMapper.toXml(config));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
