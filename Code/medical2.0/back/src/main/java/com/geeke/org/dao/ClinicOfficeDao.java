package com.geeke.org.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.org.entity.ClinicOffice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 科室信息DAO接口
 * @author ch
 * @version 2022-06-14
 */
@Mapper
public interface ClinicOfficeDao extends CrudDao<ClinicOffice> {
    void updateDefault(String companyId);

    List<ClinicOffice> getByCode(ClinicOffice clinicOffice);

    List<ClinicOffice> getByName(ClinicOffice clinicOffice);

}