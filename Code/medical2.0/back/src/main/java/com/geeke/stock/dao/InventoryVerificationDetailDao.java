package com.geeke.stock.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.stock.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;

import java.util.List;

/**
 * 库存盘点详情DAO接口
 * @author 超级管理员
 * @version 2022-11-02
 */
@Mapper
public interface InventoryVerificationDetailDao extends CrudDao<InventoryVerificationDetail> {
    void saveDrug(@Param("medicinalStorageControls") List<MedicinalStorageControl> medicinalStorageControls);

    void saveStuff(@Param("medicinalStorageControls") List<MedicinalStorageControl> medicinalStorageControls);

    List<InventoryVerificationDetail> listPage(@Param("pageRequest") PageRequest pageRequest,@Param("type") String type);

    List<InventoryVerificationDetail> getByInfoId(String infoId);

    void deleteByInventoryVerificationId(String inventoryVerificationId);

    public List<InventoryVerificationDetail> listPages(PageRequest pageRequest);

}