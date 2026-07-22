package com.geeke.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.constants.ActionConstants;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.data.SearchParamsBuilder;
import com.geeke.common.service.CrudService;
import com.geeke.sys.dao.DictItemDao;
import com.geeke.sys.dao.DictTypeDao;
import com.geeke.sys.entity.Action;
import com.geeke.sys.entity.ActionRecycle;
import com.geeke.sys.entity.DictItem;
import com.geeke.sys.entity.DictType;
import com.geeke.utils.Reflections;
import com.geeke.utils.StringUtils;
import com.google.common.collect.Lists;

/**
 * 字典类型Service
 * @author lys
 * @version 2021-12-07
 */
 
@Service("dictTypeService")
@Transactional(readOnly = true)
public class DictTypeService extends CrudService<DictTypeDao, DictType>{

    @Autowired
    private DictItemDao dictItemDao;

    /**
     * 重写分页查询：系统级字典对所有租户可见，业务级字典按租户隔离
     */
    @Override
    public Page<DictType> listPage(List<Parameter> parameters, int offset, int limit, String orderby) {
        // 字典已优化为全局共享，不再按租户隔离
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        int total = dao.count(pageRequest);
        List<DictType> list = total > 0 ? dao.listPage(pageRequest) : java.util.Collections.emptyList();
        return new Page<>(total, list);
    }

    /**
     * 重写列表查询：字典全局共享，所有租户可见
     */
    @Override
    public List<DictType> listAll(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = new PageRequest(parameters, orderby);
        return dao.listAll(pageRequest);
    }

    @Override
    @Cacheable(value = "dict:type", key = "#id")
    public DictType get(String id) {
        DictType dictType = super.get(id);

        /*获取子表列表   字典项*/
        SearchParamsBuilder builder = SearchParamsBuilder.create()
                .eq("dict_type_id", dictType.getId());
        PageRequest pageRequest = new PageRequest(builder.build());
        dictType.setDictItemList(dictItemDao.listAll(pageRequest));
        return dictType;
    }

    /** 根据字典name获取字典value */
    public String getValue(String name, String id) {
        DictItem value = dictItemDao.getValue(name, id);
        return value.getValue();
    }

    @Override
    @Transactional(readOnly = false)
    @Caching(evict = {
        @CacheEvict(value = "dict:type", key = "#dictType.id"),
        @CacheEvict(value = "dict:itemsByCode", allEntries = true)
    })
    public DictType save(DictType dictType) {
        // 系统级字典的 company_id 应该为 NULL
        if ("1".equals(dictType.getIsSystem())) {
            dictType.setCompany(null);
        }
        DictType dictTypeTemp = super.save(dictType);
        if (StringUtils.isNoneBlank(dictTypeTemp.getId())) {

            /* 保存子表数据     字典项 */
            saveDictItemList(dictTypeTemp);
        }
        return dictTypeTemp;
    }

    @Transactional(readOnly = false)
    public DictType importJson(DictType dictType) {
        // 没有记录的情况下，先添加，然后再通过更新
        if(get(dictType.getId()) == null ){
        	// 添加
        	dictType.preInsert();
        	doInsert(dictType);
        	this.saveAction(this.createAction(ActionConstants.ACTION_CREATED, dictType));
        }
        
        // 更新
        DictType dictTypeTemp = this.save(dictType);
        return dictTypeTemp;
    }

    /**
     * 删除
     * @param entity
     */
    @Override
    @Transactional(readOnly = false)
    @Caching(evict = {
        @CacheEvict(value = "dict:type", key = "#dictType.id"),
        @CacheEvict(value = "dict:itemsByCode", allEntries = true)
    })
    public int delete(DictType dictType) {
        /* 处理子表     字典项 */
        List<Parameter> params = SearchParamsBuilder.create()
                .eq("dict_type_id", dictType.getId())
                .build();
        PageRequest pageRequest = new PageRequest(params);
        dictType.setDictItemList(dictItemDao.listAll(pageRequest));        

        if(dictType.getDictItemList() != null && dictType.getDictItemList().size() > 0) {
            dictItemDao.bulkDelete(dictType.getDictItemList());
        }

        int rows = super.delete(dictType);
        return rows;
    }



    /**
     * 生成操作日志
     * @param actionTypeId  操作类型Id
     * @param entity        操作的实体对象
     * @return
     */
    @Override
    protected Action createAction(String actionTypeId, DictType entity) {
        Action action = super.createAction(actionTypeId, entity);
        if(action == null) {
            return null;
        }
        // 删除时记录把保存的数据保存到回收站
        if(ActionConstants.ACTION_DELETED.equals(actionTypeId)) {   
            for(DictItem child: entity.getDictItemList()) {
                ActionRecycle recycle = new ActionRecycle();
                recycle.setTableName(child.getBusTableName());
                recycle.setObjectId(child.getId());
                recycle.setObjectName((String)Reflections.invokeGetter(child, "name"));
                action.getActionRecycleList().add(recycle);            
            }
        }
        return action;
    }
    
    /* 保存子表数据     字典项 */
    private void saveDictItemList(DictType dictType) {
        SearchParamsBuilder builder = SearchParamsBuilder.create()
                .eq("dict_type_id", dictType.getId());
        PageRequest pageRequest = new PageRequest(builder.build());
        List<DictItem> list_DictItem = dictItemDao.listAll(pageRequest);
        List<DictItem> deletes = Lists.newArrayList(); // 删除列表
        List<DictItem> inserts = Lists.newArrayList(); // 添加列表
        List<DictItem> updates = Lists.newArrayList(); // 更新列表
        for(DictItem dictItemSaved: list_DictItem) {
            boolean found = false;
            for (DictItem dictItem : dictType.getDictItemList()){
               if(dictItemSaved.getId().equals(dictItem.getId())){
                   found = true;
                   break;
               }
            }
            if(!found) {
               deletes.add(dictItemSaved);
            }
        }
        if(deletes.size() > 0) {
            dictItemDao.bulkDelete(deletes);
        }
        for (DictItem dictItem : dictType.getDictItemList()){
            if (StringUtils.isBlank(dictItem.getId())) {
                dictItem.setDictType(dictType);
                dictItem.preInsert();
                inserts.add(dictItem);
            } else {
                dictItem.preUpdate();
                updates.add(dictItem);
            }
        }
        if(updates.size() > 0) {
            dictItemDao.bulkUpdate(updates);
        }
        if(inserts.size() > 0) {
            dictItemDao.bulkInsert(inserts);
        }
    }
    
}