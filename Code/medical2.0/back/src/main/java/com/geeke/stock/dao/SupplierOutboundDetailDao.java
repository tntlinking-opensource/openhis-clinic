package com.geeke.stock.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.common.persistence.CrudDao;
import com.geeke.stock.entity.InventoryVerificationDetail;
import com.geeke.stock.entity.MedicinalStorageControl;
import com.geeke.stock.entity.SupplierOutbound;
import com.geeke.stock.entity.SupplierOutboundDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 出库单DAO接口
 * @author txl
 * @version 2022-06-02/
 */
@Mapper
public interface SupplierOutboundDetailDao extends CrudDao<SupplierOutboundDetail> {
    void save(@Param("supplierOutboundDetails") List<SupplierOutboundDetail> supplierOutboundDetails);

    List<SupplierOutboundDetail> getByOutboundId(String outboundId);

    void deleteByOutboundId(String outboundId);
}