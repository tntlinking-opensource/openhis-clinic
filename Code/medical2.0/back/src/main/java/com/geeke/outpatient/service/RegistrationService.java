package com.geeke.outpatient.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.medicareutils.service.MdRegistrationService;
import com.geeke.outpatient.dao.MedicalRecordDao;
import com.geeke.outpatient.dao.RecipelInfoDao;
import com.geeke.outpatient.dao.RegistrationDao;
import com.geeke.outpatient.entity.*;
import com.geeke.sys.entity.Action;
import com.geeke.utils.IdGen;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 挂号信息Service
 * @author txl
 * @version 2022-06-15
 */
 
@Service("registrationService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegistrationService extends CrudService<RegistrationDao, Registration>{
    @Autowired
    private RegistrationDao registrationDao;

    @Autowired
    private MedicalRecordDao medicalRecordDao;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    //医保挂号
    private final MdRegistrationService mdRegistrationService;

    @Transactional(readOnly = false)
    public int updateStatus(String id,String status,String departmentId,String doctorId)
    {
        return dao.updateStatus(id,status,departmentId,doctorId);
    }
    @Transactional(readOnly = false)
    public int updateStatusByCompanyId(String companyId){
        //计算过期时间
        long time = new Date().getTime();
        Date date = new Date(time - 60 * 60 * 24 * 1000);
        Date date1 = new Date(time - 60 * 60 * 24 * 1000);
        return dao.updateStatusByCompanyId(companyId,date,date1);
    }
    /**
     * 退号
     * @author lin.zeng
     **/
    @Transactional(readOnly = false)
    public int refundRegistrationPay(String id,String status,
                              String refundRegistrationPayType, String refundRegistrationRemarks,Date exitNumberDate
    )
    {
        int i = dao.refundRegistrationPay(id, status, refundRegistrationPayType, refundRegistrationRemarks, exitNumberDate);
        if(i>0){

        }

        return i;
    }

    public Page<Registration> listConditionPage(String param, int offset, int limit) {
        int total = this.dao.countConditionList(param,SessionUtils.getLoginTenantId());
        List<Registration> list = null;
        if (total > 0) {
            list = this.dao.conditionList(param, SessionUtils.getLoginTenantId(),limit,offset);
        }

        return new Page((long)total, list);
    }
    @Transactional(readOnly = false)
    public void updateRecipeStatus(String id) {
        registrationDao.updateRecipeStatus(id);
    }

    @Transactional(readOnly = false)
    public Page<Registration> listPages(PageRegistration pageRegistration) {
        List<String> strings = this.dao.countId(pageRegistration);
        System.out.println(strings.size());
        int total=strings.size();
        List<Registration> list = null;
        if (total > 0) {

            List<String> strings1 = this.dao.listPages(pageRegistration);
            System.out.println(strings1);
            if(strings1.size()>0){
               list = this.dao.findById(strings1,pageRegistration);
            }
        }

        return new Page((long)total, list);
    }


    public List<String> whetherTheDispensing(PageRegistration pageRegistration) {
        List<String> strings = this.dao.countId(pageRegistration);
        return strings;
    }

    public List<String> whetherChargeOrNot(PageRegistration pageRegistration) {
        List<String> strings = this.dao.countId(pageRegistration);
        return strings;
    }

    @Transactional(readOnly = false)
    public Registration wxSave(Registration entity) {
        if (StringUtils.isBlank(entity.getId())) {
            entity.preInsert();
            this.doInsert(entity);
            Action created = this.createAction("created", entity);
            created.setCreateBy("微信");
            created.setUpdateBy("微信");
            this.saveAction(created);
        } else {
            entity.preUpdate();
            this.doUpdate(entity);
            this.saveAction(this.createAction("updated", entity));
        }
        return entity;
    }

    @Transactional(readOnly = false)
    public Page<Registration> getRegistrationByOpenId(List<Parameter> parameters, int offset, int limit, String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        List<Parameter> params = pageRequest.getParams();
        String openId = (String) params.get(1).getValue();
        String companyId = (String) params.get(0).getValue();
        TreeSet<Registration> registrations = new TreeSet<>(new Comparator<Registration>() {
            @Override
            public int compare(Registration o1, Registration o2) {
                int i =(int)(o2.getUpdateDate().getTime()-o1.getUpdateDate().getTime());
                return i;
            }
        });
        //通过openid获取到对应的用户
        List<Patient> byOpenId = patientService.getByOpenId(openId,companyId);
        for (Patient patient : byOpenId) {
            List<Registration> registrationList = this.dao.getByPatientId(patient.getId());
            for (Registration registration : registrationList) {
                registrations.add(registration);
            }
        }
        List<Registration> list = new ArrayList<>(registrations);
        List<Registration> registrationList = null;
        if(list.size()<=10&&offset==0){
            registrationList = list;
        }else if(list.size()<=10&&offset>0){
            registrationList = null;
        }else {
            registrationList = list.subList(offset*limit, (offset*limit+limit));
        }
        return new Page(list.size(),registrationList);
    }

    @Transactional(readOnly = false)
    public int wxReturnPay(Registration registrations) {
        int i = dao.wxReturnPay(registrations);

        return i;
    }


    public Page<ReceptionEvt> wxListPages(PageRegistration pageRegistration) {
        List<String> strings = this.dao.wxCount(pageRegistration);
        int total=strings.size();
        List<Registration> list = null;
        List<ReceptionEvt> lists = new ArrayList<>();
        if (total > 0) {

            List<String> strings1 = this.dao.wxListPages(pageRegistration);
            System.out.println(strings1);

            if(strings1.size()>0){
               // list = this.dao.findById(strings1,pageRegistration);
                for (String s : strings1) {
                    ReceptionEvt receptionEvt = medicalRecordService.allNewQuery(s, null);
                    lists.add(receptionEvt);
                }
            }
        }

        return new Page((long)total, lists);
    }

    public Page<ReceptionEvt> wxDispensingListPages(PageRegistration pageRegistration) {
        List<String> strings = this.dao.wxDispensingCount(pageRegistration);
        int total=strings.size();
        List<Registration> list = null;
        List<ReceptionEvt> lists = new ArrayList<>();
        if (total > 0) {

            List<String> strings1 = this.dao.wxDispensingListPages(pageRegistration);
            System.out.println(strings1);

            if(strings1.size()>0){
                // list = this.dao.findById(strings1,pageRegistration);
                for (String s : strings1) {
                    ReceptionEvt receptionEvt = medicalRecordService.allNewQuery(s, null);
                    lists.add(receptionEvt);
                }
            }
        }

        return new Page((long)total, lists);
    }

    @Transactional(readOnly = false)
    public int registrationupdate(Registration registrations) {
        int i = dao.registrationupdate(registrations);

        return i;
    }

    @Transactional(readOnly = false)
    public int medicalrecordinserts(MedicalRecord medicalRecord) {
        medicalRecord.setDiagnose("");
        medicalRecord.setId(IdGen.uuid());
        medicalRecord.setCompany(SessionUtils.getUser().getCompany());
        medicalRecord.setCreateBy(SessionUtils.getUser().getUpdateBy());
        medicalRecord.setCreateDate(SessionUtils.getUser().getUpdateDate());
        medicalRecord.setUpdateBy(SessionUtils.getUser().getUpdateBy());
        medicalRecord.setUpdateDate(SessionUtils.getUser().getUpdateDate());
        medicalRecord.setDelFlag("0");
        int result = medicalRecordDao.insert(medicalRecord);
        return result;
    }

}