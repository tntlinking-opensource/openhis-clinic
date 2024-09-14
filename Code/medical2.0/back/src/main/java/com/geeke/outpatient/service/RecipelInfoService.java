package com.geeke.outpatient.service;

import java.util.*;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.outpatient.entity.*;
import com.geeke.stock.service.MedicinalStorageControlService;
import com.geeke.sys.entity.DictItem;
import com.geeke.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.outpatient.dao.RecipelInfoDao;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 处方信息Service
 * @author txl
 * @version 2022-06-15
 */
 
@Service("recipelInfoService")
@Transactional(readOnly = true)
public class RecipelInfoService extends CrudService<RecipelInfoDao, RecipelInfo>{
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private RecipelInfoDao recipelInfoDao;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    @Transactional(readOnly = false)
    public int invalid(String id) {
        RecipelInfo recipelInfo = super.get(id);
        if (Objects.nonNull(recipelInfo))
        {
            if (Objects.equals(recipelInfo.getStatus(), 1) && Objects.equals(recipelInfo.getChargeStatus(), 0))
            {
                recipelInfo.setStatus(-1);
                super.save(recipelInfo);

                //动态库存释放操作
                this.medicinalStorageControlService.cancelOccupy(recipelInfo);

                //根据病例id获取所有的处方信息
                List<RecipelInfo> byMedicalId = recipelInfoDao.getByMedicalId(recipelInfo.getRegistration().getId());
                int recipelInfoLength=byMedicalId.size();
                int count=0;
                for (RecipelInfo recipelInfos : byMedicalId  ) {
                    if(recipelInfos.getStatus()==-1){
                        count++;
                    }
                }

                //所有处方都作废后，将登记信息的也标志
                if(count==recipelInfoLength){
                    //根据病例信息获取登记信息
                    //MedicalRecord medicalRecord = medicalRecordService.get(recipelInfo.getRegistration().getId());
                    //根据病例信息获取登记id，设置登记的处方状态
                    registrationService.updateRecipeStatus(recipelInfo.getRegistration().getId());
                }
                return 1;
            }
        }

        return 0;
    }

    public List<RecipelInfoDTO> getHistoryRecipel(PageRequest pageRequest) {
        List<RecipelInfoDTO> recipelInfos=recipelInfoDao.getHistoryRecipel(pageRequest);
        return recipelInfos;
    }

    @Transactional(readOnly=false)
    public void updateSchedule(String id, int cureType){
        recipelInfoDao.updateSchedule(id,cureType);
    }

    @Transactional(readOnly = false)
    public int updateNotShowById(String id) {
        RecipelInfo recipelInfo = this.get(id);
        if (ObjectUtil.isNull(recipelInfo)) {
            return 0;
        }
        recipelInfo.setNotShow(0);
        return this.doUpdate(recipelInfo);
    }
}