package com.geeke.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.constants.ActionConstants;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.sys.dao.ActionDao;
import com.geeke.sys.dao.ActionRecycleDao;
import com.geeke.sys.entity.Action;
import com.geeke.sys.entity.ActionRecycle;
import com.geeke.utils.Reflections;
import com.geeke.utils.StringUtils;
import com.google.common.collect.Lists;

/**
 * 操作日志Service
 * @author lys
 * @version 2020-06-29
 */

@Service("actionService")
@Transactional(readOnly = true)
public class ActionService extends CrudService<ActionDao, Action> {

    @Autowired
	protected ActionDao actionDao;

	@Autowired
	private ActionRecycleDao actionRecycleDao;

	@Override
	public Action get(String id) {
		Action action = super.get(id);

		List<Parameter> params = null;
		PageRequest pageRequest;
    	/*获取子表列表   回收站*/
		params = Lists.newArrayList();
        params.add(new Parameter("action_id", "=", action.getId()));
        pageRequest = new PageRequest(params);
        action.setActionRecycleList(actionRecycleDao.listAll(pageRequest));
		return action;
	}

	@Override
	@Transactional(readOnly = false)
	public Action save(Action action) {

		String id = super.save(action).getId();
		if (StringUtils.isNoneBlank(id)) {

            List<Parameter> params = null;
            PageRequest pageRequest;
            /* 处理子表     回收站 */
            params = Lists.newArrayList();
            params.add(new Parameter("action_id", "=", action.getId()));
            pageRequest = new PageRequest(params);
            List<ActionRecycle> list_ActionRecycle = actionRecycleDao.listAll(pageRequest);            
            for(ActionRecycle actionRecycleSaved: list_ActionRecycle) {
                boolean found = false;   
                for (ActionRecycle actionRecycle : action.getActionRecycleList()){
                   if(actionRecycleSaved.getId().equals(actionRecycle.getId())){
                       found = true;
                       break;
                   }
                }
                if(!found) {
                   actionRecycleDao.delete(actionRecycleSaved);
                }
            }
			for (ActionRecycle actionRecycle : action.getActionRecycleList()){
				int childRow = 0;
                if (StringUtils.isBlank(actionRecycle.getId())) {
                    actionRecycle.setAction(action);
                    actionRecycle.preInsert();
                    childRow = actionRecycleDao.insert(actionRecycle);
                } else {
                    actionRecycle.preUpdate();
                    childRow = actionRecycleDao.update(actionRecycle);
                }

            }

	    }
        return action;
	}


    /**
     * 删除
     * @param entity
     */
    @Override
    @Transactional(readOnly = false)
    public int delete(Action action) {
        List<Parameter> params = null;
        PageRequest pageRequest;
        /* 处理子表     回收站 */
        params = Lists.newArrayList();
        params.add(new Parameter("action_id", "=", action.getId()));
        pageRequest = new PageRequest(params);
        action.setActionRecycleList(actionRecycleDao.listAll(pageRequest));

        for(ActionRecycle actionRecycleSaved: action.getActionRecycleList()) {
            actionRecycleDao.delete(actionRecycleSaved);
        }

        int rows = super.delete(action);
        return rows;
    }



    /**
     * 生成操作日志
     * @param actionTypeId  操作类型Id
     * @param entity        操作的实体对象
     * @return
     */
    @Override
    protected Action createAction(String actionTypeId, Action entity) {
        Action action = super.createAction(actionTypeId, entity);
        if(action == null) {
            return null;
        }
        // 删除时记录把保存的数据保存到回收站
        if(ActionConstants.ACTION_DELETED.equals(actionTypeId)) {
            for(ActionRecycle child: entity.getActionRecycleList()) {
                ActionRecycle recycle = new ActionRecycle();
                recycle.setTableName(child.getBusTableName());
                recycle.setObjectId(child.getId());
                recycle.setObjectName((String)Reflections.invokeGetter(child, "name"));
                action.getActionRecycleList().add(recycle);
            }
        }
        return action;
    }

    @Transactional(readOnly = false)
    public void saveMQtoDB(Action action) {

        int rows = super.dao.insert(action);
        if(rows > 0) {
            for (ActionRecycle actionRecycle : action.getActionRecycleList()){
                actionRecycle.setAction(action);
                actionRecycleDao.insert(actionRecycle);
            }
        }
    }
}