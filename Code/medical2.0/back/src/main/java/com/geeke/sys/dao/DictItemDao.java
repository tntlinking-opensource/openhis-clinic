package com.geeke.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /** 根据字典类型code获取所有字典项 */
    List<DictItem> listByDictTypeCode(@Param("code") String code);
}