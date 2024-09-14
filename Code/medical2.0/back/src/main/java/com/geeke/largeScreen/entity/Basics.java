package com.geeke.largeScreen.entity;

import lombok.Data;


/**
 * 大屏基础数据
 * @author zhanghan
 * @version 2023-09-05
 */
@Data
public class Basics {
	/** 新增任务数 */
	private String quest;

	/** 今日挂号人数 */
	private String register;

	/** 今日接诊数 */
	private String receivePatients;

	/** 今日收入 */
	private String income;

	/** 今日新增签约数 */
	private String signing;

}
