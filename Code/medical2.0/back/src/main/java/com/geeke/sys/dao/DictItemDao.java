package com.geeke.sys.dao;

import org.apache.ibatis.annotations.Mapper;

import com.geeke.common.persistence.CrudDao;
import com.geeke.sys.entity.DictItem;

/**
 * 字典类型DAO接口
 * @author lys
 * @version 2021-12-07
 */
@Mapper
public interface DictItemDao extends CrudDao<DictItem> {
    /**
     * 查询行业大类、二级分类
     * @param substring
     * @return
     */
    DictItem getTrade(String substring);

    /** 根据字典name获取字典value */
    DictItem getValue(String name, String id);
}