package com.geeke.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.sys.dao.DictItemDao;
import com.geeke.sys.entity.DictItem;
import com.google.common.collect.Lists;

/**
 * 字典项Service
 * @author lys
 * @version 2019-08-23
 */

@Service("dictItemService")
@Transactional(readOnly = true)
public class DictItemService extends CrudService<DictItemDao, DictItem>{

    @Autowired
    private DictItemDao dictItemDao;

    @Override
    @Cacheable(value = "dict:item", key = "#id")
    public DictItem get(String id) {
        return super.get(id);
    }

    /**
     * 重写分页查询：字典已优化为全局共享，不按租户隔离
     */
    @Override
    public Page<DictItem> listPage(List<Parameter> parameters, int offset, int limit, String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        int total = dao.count(pageRequest);
        List<DictItem> list = total > 0 ? dao.listPage(pageRequest) : java.util.Collections.emptyList();
        return new Page<>(total, list);
    }

    /**
     * 重写列表查询：字典全局共享，所有租户可见
     */
    @Override
    public List<DictItem> listAll(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = new PageRequest(parameters, orderby);
        return dao.listAll(pageRequest);
    }

    @Override
    @Transactional(readOnly = false)
    @Caching(evict = {
        @CacheEvict(value = "dict:item", key = "#entity.id"),
        @CacheEvict(value = "dict:itemsByCode", allEntries = true)
    })
    public DictItem save(DictItem entity) {
        // 字典已优化为全局共享，不再按租户隔离，故不再设置 company
        return super.save(entity);
    }

    @Override
    @Transactional(readOnly = false)
    @Caching(evict = {
        @CacheEvict(value = "dict:item", key = "#entity.id"),
        @CacheEvict(value = "dict:itemsByCode", allEntries = true)
    })
    public int delete(DictItem entity) {
        return super.delete(entity);
    }

    /** 根据字典类型code获取所有字典项（带缓存） */
    @Cacheable(value = "dict:itemsByCode", key = "#code")
    public List<DictItem> listByDictTypeCode(String code) {
        return this.dao.listByDictTypeCode(code);
    }
}