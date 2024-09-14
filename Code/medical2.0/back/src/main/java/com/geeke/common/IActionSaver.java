package com.geeke.common;

import com.geeke.sys.entity.Action;

/**
 * 写入操作日期接口
 * @author lys
 * 2021/11/11
 */
public interface IActionSaver {
	/**
	 * 保存日志
	 * @param action
	 */
	public void saveAction(Action action);
}
