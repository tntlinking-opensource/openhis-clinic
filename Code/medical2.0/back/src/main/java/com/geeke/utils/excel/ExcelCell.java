package com.geeke.utils.excel;

import java.lang.annotation.*;

/**
 * 
 * @author skydu
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelCell {

	String name() default "";

	/**
	 * 日期格式化
	 * @return
	 */
	String dateFormat() default "yyyy-MM-dd HH:mm:ss";

	/**
	 * 链接字段
	 * @return
	 */
	boolean hyperLink() default false;

	/**
	 * 转换的key，如果需要，要在ExcelCellProcessor中注册
	 * 为什么不用 name，万一重复了有很多问题，现在也没时间想那么多，项目快上线了
	 * @return
	 */
	String convertKey() default "";
}
