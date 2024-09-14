package com.geeke.treatment.dao;

import com.geeke.common.data.PageRequest;
import com.geeke.common.persistence.CrudDao;
import com.geeke.treatment.entity.CostItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 诊疗项目DAO接口
 * @author txl
 * @version 2022-05-30
 */
@Mapper
public interface CostItemDao extends CrudDao<CostItem> {
    @Select("select count(1) from cost_item where item_name = #{name} and company_id = #{tenantId}")
    int countByName(@Param("name")String name,@Param("tenantId")String tenantId);

    @Select("select id from cost_item where item_name = #{name} and company_id = #{tenantId}")
    String GetIDByName(@Param("name")String name,@Param("tenantId")String tenantId);

    CostItem getByRecipelInfo(String recipelDetailId);


    int countByInstitution(PageRequest pageRequest);

    List<CostItem> listByInstitution(PageRequest pageRequest);
}