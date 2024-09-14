package com.geeke.basicdata.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.stock.entity.Drug;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.basicdata.entity.ManufactureFactory;

import java.util.List;

/**
 * 生产厂家DAO接口
 * @author txl
 * @version 2022-06-22
 */
@Mapper
public interface ManufactureFactoryDao extends CrudDao<ManufactureFactory> {
    public String findBy(@Param("name") String name,@Param("company_id")String company_id);

    List<ManufactureFactory> listAlls(PageRequest pageRequest);
}