package com.geeke.config.action;

import org.springframework.beans.factory.annotation.Autowired;

import com.geeke.common.IActionSaver;
import com.geeke.sys.dao.ActionDao;
import com.geeke.sys.dao.ActionRecycleDao;
import com.geeke.sys.entity.Action;
import com.geeke.sys.entity.ActionRecycle;

/**
 * 日志直接写入数据库类
 * @author lys
 * version: 2021/11/16
 */
public class DbActionSaver  implements IActionSaver {

	/**
	 * 操作日志持久对象
	 */
	@Autowired
	protected ActionDao actionDao;
	
	/**
	 * 操作回收站持久对象
	 */
	@Autowired
	protected ActionRecycleDao actionRecycleDao;
	
	@Override
	public void saveAction(Action action) {
		if(action == null) {
			return;
		}
		action.preInsert();
		int rows = actionDao.insert(action);
		if(rows > 0) {
			for (ActionRecycle actionRecycle : action.getActionRecycleList()){
	            actionRecycle.setAction(action);
	            actionRecycle.preInsert();
	            actionRecycleDao.insert(actionRecycle);
	        }
		}
		
	}

}
