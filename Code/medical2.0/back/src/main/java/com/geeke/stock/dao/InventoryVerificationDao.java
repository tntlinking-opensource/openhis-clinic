package com.geeke.stock.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.stock.entity.InventoryVerification;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存盘点DAO接口
 * @author rys
 * @version 2022-11-02
 */
@Mapper
public interface InventoryVerificationDao extends CrudDao<InventoryVerification> {
    int accomplishInventoryVerification(InventoryVerification inventoryVerification);

    void updateAllPrice(@Param("id") String id,@Param("allPrice") BigDecimal allPrice);

    List<InventoryVerification> getByCompanyId(@Param("companyId") String companyId,@Param("status") String status);
}