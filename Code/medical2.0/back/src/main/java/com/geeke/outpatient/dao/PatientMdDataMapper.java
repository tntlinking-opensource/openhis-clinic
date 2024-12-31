package com.geeke.outpatient.dao;

import com.geeke.outpatient.entity.PatientMdData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mapstruct.Mapper;

/**
* @author 47826
* @description 针对表【patient_md_data(患者医保信息表)】的数据库操作Mapper
* @createDate 2024-09-09 16:14:57
* @Entity com.geeke.outpatient.entity.PatientMdData
*/
@Mapper
public interface PatientMdDataMapper extends BaseMapper<PatientMdData> {

}




