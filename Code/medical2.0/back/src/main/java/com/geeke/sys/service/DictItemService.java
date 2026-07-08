package com.geeke.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.org.entity.Company;
import com.geeke.sys.dao.DictItemDao;
import com.geeke.sys.entity.DictItem;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
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

    @Override
    @Transactional(readOnly = false)
    @Caching(evict = {
        @CacheEvict(value = "dict:item", key = "#entity.id"),
        @CacheEvict(value = "dict:itemsByCode", allEntries = true)
    })
    public DictItem save(DictItem entity) {
        // 新增字典项时设置租户
        if (StringUtils.isBlank(entity.getId()) && entity.getCompany() == null) {
            String companyId = SessionUtils.getLoginTenantId();
            if (StringUtils.isNotBlank(companyId) && !"null".equals(companyId)) {
                Company company = new Company();
                company.setId(companyId);
                entity.setCompany(company);
            }
        }
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