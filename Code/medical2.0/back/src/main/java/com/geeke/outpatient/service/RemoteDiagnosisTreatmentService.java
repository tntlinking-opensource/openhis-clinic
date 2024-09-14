package com.geeke.outpatient.service;

import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.outpatient.dao.PatientDao;
import com.geeke.outpatient.dao.RemoteDiagnosisTreatmentDao;
import com.geeke.outpatient.entity.*;
import com.geeke.toll.entity.TollEvt;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 远程诊疗管理Service
 * @author zh
 * @version 2023-12-06
 */
 
@Service("remoteDiagnosisTreatmentService")
@Transactional(readOnly = true)
public class RemoteDiagnosisTreatmentService extends CrudService<PatientDao, Patient>{
    @Autowired
    private PatientDao patientDao;
    @Autowired
    private RemoteDiagnosisTreatmentDao remoteDiagnosisTreatmentDao;

    /** id查询 */
    public RemoteDiagnosisTreatment diagnosisById(String id) {
        return remoteDiagnosisTreatmentDao.diagnosisById(id);
    }

    /** 挂号id查询 */
    public RemoteDiagnosisTreatment getRegistrationId(String registrationId) {
        return remoteDiagnosisTreatmentDao.getRegistrationId(registrationId);
    }

    /**
     * 查询分页数据
     * @return
     */
    public Page<RemoteDiagnosisTreatment> diagnosisPage(List<Parameter> parameters, int offset, int limit, String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        int total = remoteDiagnosisTreatmentDao.diagnosisCount(pageRequest);
        List<RemoteDiagnosisTreatment> list = null;
        if(total > 0) {
            list = remoteDiagnosisTreatmentDao.diagnosisPage(pageRequest);
        }
        return new Page<RemoteDiagnosisTreatment>(total, list);
    }

    /** 修改保存 */
    @Transactional(readOnly = false)
    public RemoteDiagnosisTreatment diagnosisSave(RemoteDiagnosisTreatment entity) {
        if (StringUtils.isBlank(entity.getId())) {
            entity.preInsert();
            remoteDiagnosisTreatmentDao.diagnosisInsert(entity);
        } else {
            entity.preUpdate();
            remoteDiagnosisTreatmentDao.diagnosisUpdate(entity);
        }
        return entity;
    }

    /** 修改状态
     * @return*/
    @Transactional(readOnly = false)
    public int modifiedState(RemoteDiagnosisTreatment entity) {
        return remoteDiagnosisTreatmentDao.modifiedState(entity);
    }

    /** 远程诊疗收费
     * @return*/
    @Transactional(readOnly = false)
    public int chargeState(TollEvt tollEvt) {
        List<RecipelInfoEvt> recipelInfos = tollEvt.getRecipelInfos();
        for(RecipelInfoEvt e:recipelInfos){
            remoteDiagnosisTreatmentDao.chargeState(e.getRecipelInfo().getId());
        }
        String id = tollEvt.getTollInfo().getMedical().getRegistration().getId();
        List<RecipelInfo> aCase = remoteDiagnosisTreatmentDao.getCase(id);
        int i = 0;
        for (RecipelInfo a:aCase){
            i+=a.getChargeStatus();
        }
        if (i == aCase.size()){
            remoteDiagnosisTreatmentDao.chargeEdit(id);
        }
        return 1;
    }

    /** 删除记录 */
    @Transactional(readOnly = false)
    public int diagnosisDelete(String id) {
        return remoteDiagnosisTreatmentDao.diagnosisDelete(id);
    }

}