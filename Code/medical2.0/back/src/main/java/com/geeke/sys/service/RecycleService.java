package com.geeke.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.constants.ActionConstants;
import com.geeke.sys.dao.RecycleDao;
import com.geeke.sys.entity.Action;
import com.geeke.sys.entity.ActionRecycle;
import com.geeke.sys.entity.DictItem;

/**
 * 操作日志Service
 * @author lys	人工编写
 * @version 2019-12-12
 */
 
@Service("recycleService")
@Transactional(readOnly = true)
public class RecycleService extends ActionService {
	
	@Autowired
	private RecycleDao recycleDao;
	
	@Transactional(readOnly = false)
	public int restore(Action entity) {
		entity = this.get(entity.getId());
		int rows = recycleDao.updateActionStaus(entity.getId());
		if(rows > 0) {
			for(ActionRecycle busObject: entity.getActionRecycleList()) {
				recycleDao.restore(busObject.getTableName(), busObject.getObjectId());
			}
			
			this.saveAction(this.createAction(ActionConstants.ACTION_RESTORED, entity));
		}
		return rows;
	}
	
	/**
	 * 生成操作日志
	 * @param actionTypeId  操作类型Id
	 * @param entity		操作的实体对象
	 * @return
	 */
    @Override
    protected Action createAction(String actionTypeId,  Action entity) {
		Action action = new Action();
		DictItem di = new DictItem();
		di.setValue(actionTypeId);
		action.setActionType(di);
		action.setObjectType(entity.getObjectType());
		action.setObjectId(entity.getObjectId());
		action.setObjectName(entity.getObjectName());
		action.setStatus("0");    	
		return action;
	}
}