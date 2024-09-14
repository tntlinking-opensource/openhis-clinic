package com.geeke.gen.service;

import com.geeke.common.constants.ActionConstants;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.gen.dao.ConstraintDao;
import com.geeke.gen.dao.GenTableColumnDao;
import com.geeke.gen.dao.GenTableDao;
import com.geeke.gen.entity.Constraint;
import com.geeke.gen.entity.GenTable;
import com.geeke.gen.entity.GenTableColumn;
import com.geeke.sys.entity.Action;
import com.geeke.sys.entity.ActionRecycle;
import com.geeke.utils.Reflections;
import com.geeke.utils.StringUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 业务表管理Service
 * @author lys
 * @version 2021-08-26
 */
 
@Service("genTableService")
@Transactional(readOnly = true)
public class GenTableService extends CrudService<GenTableDao, GenTable>{

    @Autowired
    private ConstraintDao constraintDao;
    @Autowired
    private GenTableColumnDao genTableColumnDao;
    
    @Override
    public GenTable get(String id) {
        GenTable genTable = super.get(id);

        List<Parameter> params = null;
        PageRequest pageRequest;
        /*获取子表列表   约束字段*/
        params = Lists.newArrayList();
        params.add(new Parameter("gen_table_id", "=", genTable.getId()));
        pageRequest = new PageRequest(params);
        genTable.setConstraintList(constraintDao.listAll(pageRequest));        
        /*获取子表列表   业务表字段*/
        params = Lists.newArrayList();
        params.add(new Parameter("gen_table_id", "=", genTable.getId()));
        pageRequest = new PageRequest(params);
        genTable.setGenTableColumnList(genTableColumnDao.listAll(pageRequest));        
        return genTable;
    }

    @Override
    @Transactional(readOnly = false)
    public GenTable save(GenTable genTable) {

        String id = super.save(genTable).getId();
        if (StringUtils.isNoneBlank(id)) {

            List<Parameter> params = null;
            PageRequest pageRequest;
            /* 处理子表     约束字段 */
            params = Lists.newArrayList();
            params.add(new Parameter("gen_table_id", "=", genTable.getId()));
            pageRequest = new PageRequest(params);
            List<Constraint> list_Constraint = constraintDao.listAll(pageRequest);            
            List<Constraint> deleteConstraints = Lists.newArrayList(); // 删除列表
            List<Constraint> insertConstraints = Lists.newArrayList(); // 添加列表
            List<Constraint> updateConstraints = Lists.newArrayList(); // 更新列表
            for(Constraint constraintSaved: list_Constraint) {
                boolean found = false;   
                for (Constraint constraint : genTable.getConstraintList()){
                   if(constraintSaved.getId().equals(constraint.getId())){
                       found = true;
                       break;
                   }
                }
                if(!found) {
                   deleteConstraints.add(constraintSaved);
                }
            }
            for (Constraint constraint : genTable.getConstraintList()){

                if (StringUtils.isBlank(constraint.getId())) {
                    constraint.setGenTable(genTable);
                    constraint.preInsert();
                    insertConstraints.add(constraint);
                } else {
                    constraint.preUpdate();
                    updateConstraints.add(constraint);
                }

            }
            if(deleteConstraints.size() > 0) {
            	constraintDao.bulkDelete(deleteConstraints);
            }
            if(updateConstraints.size() > 0) {
            	constraintDao.bulkUpdate(updateConstraints);
            }
            if(insertConstraints.size() > 0) {
            	constraintDao.bulkInsert(insertConstraints);
            }
            /* 处理子表     业务表字段 */
            params = Lists.newArrayList();
            params.add(new Parameter("gen_table_id", "=", genTable.getId()));
            pageRequest = new PageRequest(params);
            List<GenTableColumn> list_GenTableColumn = genTableColumnDao.listAll(pageRequest);            
            List<GenTableColumn> deleteGenTableColumns = Lists.newArrayList(); // 删除列表
            List<GenTableColumn> insertGenTableColumns = Lists.newArrayList(); // 添加列表
            List<GenTableColumn> updateGenTableColumns = Lists.newArrayList(); // 更新列表
            for(GenTableColumn genTableColumnSaved: list_GenTableColumn) {
                boolean found = false;   
                for (GenTableColumn genTableColumn : genTable.getGenTableColumnList()){
                   if(genTableColumnSaved.getId().equals(genTableColumn.getId())){
                       found = true;
                       break;
                   }
                }
                if(!found) {
                   deleteGenTableColumns.add(genTableColumnSaved);
                }
            }
            for (GenTableColumn genTableColumn : genTable.getGenTableColumnList()){

                if (StringUtils.isBlank(genTableColumn.getId())) {
                    genTableColumn.setGenTable(genTable);
                    genTableColumn.preInsert();
                    insertGenTableColumns.add(genTableColumn);
                } else {
                    genTableColumn.preUpdate();
                    updateGenTableColumns.add(genTableColumn);
                }

            }
            if(deleteGenTableColumns.size() > 0) {
            	genTableColumnDao.bulkDelete(deleteGenTableColumns);
            }
            if(updateGenTableColumns.size() > 0) {
            	genTableColumnDao.bulkUpdate(updateGenTableColumns);
            }
            if(insertGenTableColumns.size() > 0) {
            	genTableColumnDao.bulkInsert(insertGenTableColumns);
            }
        }
        return genTable;
    }


    /**
     * 删除
     * @param genTable
     */
    @Override
    @Transactional(readOnly = false)
    public int delete(GenTable genTable) {
        List<Parameter> params = null;
        PageRequest pageRequest;
        /* 处理子表     约束字段 */
        params = Lists.newArrayList();
        params.add(new Parameter("gen_table_id", "=", genTable.getId()));
        pageRequest = new PageRequest(params);
        genTable.setConstraintList(constraintDao.listAll(pageRequest));        
        /* 处理子表     业务表字段 */
        params = Lists.newArrayList();
        params.add(new Parameter("gen_table_id", "=", genTable.getId()));
        pageRequest = new PageRequest(params);
        genTable.setGenTableColumnList(genTableColumnDao.listAll(pageRequest));        
        if(genTable.getConstraintList() != null && genTable.getConstraintList().size() > 0) {
        	constraintDao.bulkDelete(genTable.getConstraintList());
        }
        if(genTable.getGenTableColumnList() != null && genTable.getGenTableColumnList().size() > 0) {
        	genTableColumnDao.bulkDelete(genTable.getGenTableColumnList());
        }

        int rows = super.delete(genTable);
        return rows;
    }

    @Transactional(readOnly = false)
    public GenTable importJson(GenTable genTable) {
        // 没有记录的情况下，先添加，然后再通过更新
        if(get(genTable.getId()) == null ){
        	// 添加
        	genTable.preInsert();
        	doInsert(genTable);
        	this.saveAction(this.createAction(ActionConstants.ACTION_CREATED, genTable));
        }
        // 更新
        GenTable genTableTemp = this.save(genTable);
        return genTableTemp;
    }

    /**
     * 生成操作日志
     * @param actionTypeId  操作类型Id
     * @param entity        操作的实体对象
     * @return
     */
    @Override
    protected Action createAction(String actionTypeId, GenTable entity) {
        Action action = super.createAction(actionTypeId, entity);
        if(action == null) {
            return null;
        }
        // 删除时记录把保存的数据保存到回收站
        if(ActionConstants.ACTION_DELETED.equals(actionTypeId)) {   
            for(Constraint child: entity.getConstraintList()) {
                ActionRecycle recycle = new ActionRecycle();
                recycle.setTableName(child.getBusTableName());
                recycle.setObjectId(child.getId());
                recycle.setObjectName((String)Reflections.invokeGetter(child, "name"));
                action.getActionRecycleList().add(recycle);            
            }
            for(GenTableColumn child: entity.getGenTableColumnList()) {
                ActionRecycle recycle = new ActionRecycle();
                recycle.setTableName(child.getBusTableName());
                recycle.setObjectId(child.getId());
                recycle.setObjectName((String)Reflections.invokeGetter(child, "name"));
                action.getActionRecycleList().add(recycle);            
            }
        }
        return action;
    }
}