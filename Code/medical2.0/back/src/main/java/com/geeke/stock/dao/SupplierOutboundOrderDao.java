package com.geeke.stock.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.common.persistence.CrudDao;
import com.geeke.stock.entity.SupplierOutboundOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 出库明细DAO接口
 * @author txl
 * @version 2022-06-09/
 */
@Mapper
public interface SupplierOutboundOrderDao extends CrudDao<SupplierOutboundOrder> {
    List<SupplierOutboundOrder> getByStorageId(@Param("storageId")String id);

    BigDecimal getByDrugId(String drugOrStuffId);

    void updateCancel(@Param("list") List<SupplierOutboundOrder> supplierStockList);

    BigDecimal getByStuffId(String stuffId);

    List<SupplierOutboundOrder> getByStuffIdDetail(String drugStuffId);

    int countWarning(PageRequest pageRequest);

    List<SupplierOutboundOrder> getDrugIndateWarning(PageRequest pageRequest);

    int countStuffWarning(PageRequest pageRequest);

    List<SupplierOutboundOrder> getStuffIndateWarning(PageRequest pageRequest);

}