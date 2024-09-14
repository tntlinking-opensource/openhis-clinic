package com.geeke.stock.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.common.persistence.CrudDao;
import com.geeke.stock.entity.Drug;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 药品信息DAO接口
 * @author txl
 * @version 2022-06-07
 */
@Mapper
public interface DrugDao extends CrudDao<Drug> {
    List<Drug> listPages(PageRequest pageRequest);

    List<Drug> listAlls(PageRequest pageRequest);

    List<Drug> inventory(PageRequest pageRequest);

    List<Drug> listAllStock(PageRequest var1);

    List<Drug> listAllStock2(PageRequest var1);

    int updateInventory(@Param("id") String id,@Param("inventory") int inventory);

    List<Drug> getAll(@Param("companyId") String companyId,@Param("type") String type);

    int updateAllIndate(@Param("indate") String indate,@Param("companyId") String companyId);

    int updateAllInventory(@Param("inventoryFloor") String inventoryFloor,@Param("companyId") String companyId);

    int countInventory(PageRequest pageRequest);

    List<Drug> repeat(Drug drug);

    List<Drug> getDrugInventoryWarning(PageRequest pageRequest);

    String getId(@Param("name")String name, @Param("company")String company);

    int countByInstitution(PageRequest pageRequest);

    List<Drug> listByInstitution(PageRequest pageRequest);

    int checkCompanyAndCode(@Param("companyId") String companyId, @Param("code") String code);

    Drug getByCode(String ypdm, String companyId);
}