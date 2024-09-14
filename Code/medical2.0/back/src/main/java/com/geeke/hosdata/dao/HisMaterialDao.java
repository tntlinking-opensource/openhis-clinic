package com.geeke.hosdata.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.hosdata.entity.HisMaterials;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HisMaterialDao extends CrudDao<HisMaterials> {

    List<String> getMaterialCode(String companyId);

}
