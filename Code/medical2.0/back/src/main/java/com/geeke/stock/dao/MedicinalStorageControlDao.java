package com.geeke.stock.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.stock.entity.MedicinalStorageControl;
import com.geeke.stock.entity.SupplierStock;

/**
 * 药品/材料入库明细控制DAO接口
 * @author hl
 * @version 2022-09-26
 */
@Mapper
public interface MedicinalStorageControlDao extends CrudDao<MedicinalStorageControl> {
    List<MedicinalStorageControl> getSurplusStockByDrugStuffId(@Param("drugStuffId")String drugStuffId);

    List<MedicinalStorageControl> getSurplusStockByDrugStuffIdTo(@Param("drugStuffId") String drugStuffId,
                                                                 @Param("companyId") String companyId);

    List<MedicinalStorageControl> getAll(@Param("companyId") String compayId,@Param("type") String type,@Param("variety") String variety);

    BigDecimal getByDrugOrStuffId(String drugOrStuffId);
    BigDecimal getByDrugOrStuffIds(@Param("list") List drugOrStuffIds);

}