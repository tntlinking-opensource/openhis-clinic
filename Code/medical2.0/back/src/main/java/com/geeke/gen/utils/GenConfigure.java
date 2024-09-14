package com.geeke.gen.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 代码生成配置类
 * 
 * @author lys
 * @date 2019/09/1
 */
@Component
public class GenConfigure {

	/* 代码生成的路径配置 */
	public static String rootPath;
	
	/* 数据库类型配置配置 */
	public static String dbName;
	
	@Value(value = "${genConfigure.rootPath:c:\\}")
	public void setRootPath(String path) {
		GenConfigure.rootPath = path;
	}
	
	@Value(value = "${genConfigure.dbName:mysql}")
	public void setDbName(String name) {
		GenConfigure.dbName = name;
	}
}
