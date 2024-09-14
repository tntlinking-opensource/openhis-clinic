package com.geeke.stock.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.common.persistence.CrudDao;
import com.geeke.stock.entity.SupplierStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 供应商库存DAO接口
 * @author txl
 * @version 2022-06-09
 */
@Mapper
public interface SupplierStockDao extends CrudDao<SupplierStock> {
    List<SupplierStock> getByStorageId(@Param("storageId")String id);

    BigDecimal getByDrugId(String drugOrStuffId);

    void updateCancel(@Param("list") List<SupplierStock> supplierStockList);

    BigDecimal getByStuffId(String stuffId);

    List<SupplierStock> getByStuffIdDetail(String drugStuffId);

    int countWarning(PageRequest pageRequest);

    List<SupplierStock> getDrugIndateWarning(PageRequest pageRequest);

    int countStuffWarning(PageRequest pageRequest);

    List<SupplierStock> getStuffIndateWarning(PageRequest pageRequest);

}