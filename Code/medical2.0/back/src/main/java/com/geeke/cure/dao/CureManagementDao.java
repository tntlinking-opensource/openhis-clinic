package com.geeke.cure.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.cure.entity.CureManagement;
import com.geeke.outpatient.entity.MedicalRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CureManagementDao extends CrudDao<CureManagement> {
}
