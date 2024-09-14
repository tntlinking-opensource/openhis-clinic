package com.geeke.hosdata.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.hosdata.entity.HisInstitutions;
import com.geeke.hosdata.entity.HisMaterials;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HisInstitutionDao  extends CrudDao<HisInstitutions> {

    //已同步的唯一编码
    List<String> getInstitutionCode(String companyId);

}
