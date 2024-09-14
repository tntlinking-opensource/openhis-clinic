package com.geeke.stock.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.stock.entity.SupplierOutbound;
import com.geeke.stock.entity.SupplierStorage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出库单DAO接口
 * @author txl
 * @version 2022-06-02/
 */
@Mapper
public interface SupplierOutboundDao extends CrudDao<SupplierOutbound> {
    String getCode(String companyId);
    void examinee(SupplierOutbound entity);
    void cancel(SupplierOutbound entity);
}