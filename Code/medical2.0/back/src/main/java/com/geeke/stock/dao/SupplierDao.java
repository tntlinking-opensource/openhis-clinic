package com.geeke.stock.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.stock.entity.Supplier;

/**
 * 供应商管理DAO接口
 * @author txl
 * @version 2022-06-22
 */
@Mapper
public interface SupplierDao extends CrudDao<Supplier> {
    public String findBy(@Param("name") String name,@Param("company_id")String company_id);
}