package com.geeke.stock.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.common.persistence.CrudDao;
import com.geeke.stock.entity.Stuff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 材料信息DAO接口
 * @author txl
 * @version 2022-06-22
 */
@Mapper
public interface StuffDao extends CrudDao<Stuff> {
    void updateInventory(@Param("id") String id,@Param("inventory") int inventory);

    List<Stuff> getAll(@Param("companyId") String companyId,@Param("type") String type);

    int updateAllIndate(@Param("indate") String indate,@Param("companyId") String companyId);

    int updateAllInventory(@Param("inventoryFloor") String inventoryFloor,@Param("companyId") String companyId);

    int getCountWarning(PageRequest pageRequest);

    List<Stuff> getStuffInventoryWarning(PageRequest pageRequest);

    List<Stuff> listPages(PageRequest pageRequest);

    List<Stuff> listAlls(PageRequest pageRequest);

    List<Stuff> inventory(PageRequest pageRequest);

    int repeat(Stuff Stuff);

    int repeatInCompany(Stuff stuff);

    int countByInstitution(PageRequest pageRequest);

    List<Stuff> listByInstitution(PageRequest pageRequest);
}