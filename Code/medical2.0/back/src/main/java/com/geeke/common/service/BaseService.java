package com.geeke.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Service基类
 * @author lys
 * @version 2014-05-16
 */
@Transactional(readOnly = true)
public abstract class BaseService {

	/**
	 * 日志对象
	 */
	protected static final Logger logger = LoggerFactory.getLogger(BaseService.class);

	/**
	 * 生成带日期前缀的自增编号
	 * 格式：yyyyMMdd + 7位自增序号（如 202606170000001）
	 *
	 * @param lastCode 数据库中最后一条记录的编号，可为null或空
	 * @return 新编号
	 */
	protected static String generateDatePrefixCode(String lastCode) {
		String datePrefix = new SimpleDateFormat("yyyyMMdd").format(new Date());
		if (lastCode == null || lastCode.length() < 9) {
			return datePrefix + "0000001";
		}
		int increment = Integer.parseInt(lastCode.substring(8)) + 1;
		String incrementStr = String.valueOf(increment);
		StringBuilder prefix = new StringBuilder(datePrefix);
		for (int i = 0; i < 7 - incrementStr.length(); i++) {
			prefix.append("0");
		}
		return prefix.append(incrementStr).toString();
	}

}
