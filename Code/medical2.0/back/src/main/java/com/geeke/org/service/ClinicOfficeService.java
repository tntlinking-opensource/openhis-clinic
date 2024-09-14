package com.geeke.org.service;

import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.org.dao.ClinicOfficeDao;
import com.geeke.org.entity.ClinicOffice;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 科室信息Service
 * @author ch
 * @version 2022-06-14
 */

@Service("clinicOfficeService")
@Transactional(readOnly = false)
public class ClinicOfficeService extends CrudService<ClinicOfficeDao, ClinicOffice>{
    @Autowired
    private ClinicOfficeDao clinicOfficeDao;

    @Transactional(readOnly = false)
    public void updateDefault(String companyId) {
        clinicOfficeDao.updateDefault(companyId);
    }

    @Transactional(readOnly = false)
    public ClinicOffice save(ClinicOffice clinicOffice){
       if(StringUtils.isBlank(clinicOffice.getId())){
           //进行编号校验
           List<ClinicOffice> clinicOffices = this.dao.getByCode(clinicOffice);
           if(!CollectionUtils.isEmpty(clinicOffices)){
               throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "已存在相同编号科室,请重新输入"));
           }

           //进行科室名称校验
           List<ClinicOffice> clinicOfficeList = this.dao.getByName(clinicOffice);
           if(!CollectionUtils.isEmpty(clinicOfficeList)){
               throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "已存在相同名称科室,请重新输入"));
           }
       }
        ClinicOffice save = super.save(clinicOffice);
        return clinicOffice;
    }
}