package com.geeke.outpatient.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.medicareutils.config.MedicareConfigProperties;
import com.geeke.medicareutils.service.MdPsnDataService;
import com.geeke.medicareutils.util.MdRequestUtil;
import com.geeke.outpatient.dao.PatientDao;
import com.geeke.outpatient.dao.PatientMdDataMapper;
import com.geeke.outpatient.entity.Patient;
import com.geeke.outpatient.entity.PatientMdData;
import com.geeke.utils.IdGen;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 患者信息Service
 * @author txl
 * @version 2022-06-23
 */
 
@Service("patientService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PatientService extends CrudService<PatientDao, Patient>{
    @Autowired
    private PatientDao patientDao;


    @Override
    @Transactional(readOnly = false)
    public Patient save(Patient patient) {
        Map<String, String> colMaps = Maps.newHashMap();
        // 患者联系方式已存在
        colMaps.clear();
        colMaps.put("phone", "phone");
        
//        if(exists(dao, patient, colMaps)) {
//            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "患者联系方式已存在"));
//        }
        System.out.println(patient.toString());
       if(patient.getId()==null){
           //判断电话号码是否存在
           if(!Objects.equals(patient.getPhone(),"") && patient.getPhone()!=null){
               if(patientDao.countPatientByPhone(patient.getPhone(),patient.getCompany().getId(),null).size()>0){
                   throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "患者联系方式已存在"));

               }
           }

           //判断身份证是否存在
           if(!Objects.equals(patient.getCard(),"") && patient.getCard()!=null){
               List<Patient> patientByCard = patientDao.getPatientByCard(patient.getCard(), patient.getCompany().getId(),null);
               if(!CollectionUtils.isEmpty(patientByCard)){
                       throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "患者身份证号已存在"));
               }
           }
       }

       if(patient.getId()!=null){
           //判断电话号码是否存在
           if(!Objects.equals(patient.getPhone(),"") && patient.getPhone()!=null){
               List<Patient> patients = patientDao.countPatientByPhone(patient.getPhone(), patient.getCompany().getId(),patient.getId());
               if(patients.size()>0&&!Objects.equals(patients.get(0).getPhone(),patient.getPhone())){
                   throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "患者联系方式已存在"));

               }
           }

           //判断身份证是否存在
           if(!Objects.equals(patient.getCard(),"") && patient.getCard()!=null){
               List<Patient> patientByCard = patientDao.getPatientByCard(patient.getCard(), patient.getCompany().getId(),patient.getId());
               if(!CollectionUtils.isEmpty(patientByCard)){
                   if(Objects.equals(patientByCard.get(0).getCard(),patient.getCard())) {
                       throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "患者身份证号已存在"));
                   }
               }
           }
       }


        return super.save(patient);
    }

    @Transactional(readOnly = false)
    public Patient wxSave(Patient patient) {


        //根据用户身份证号获取是否有该用户
        List<Patient> patientByCard = this.dao.getPatientByCard(patient.getCard(), patient.getCompany().getId(),null);

        //如果存在该用户，但是没有绑定openid的话就进行绑定操作
        if(!ObjectUtils.isEmpty(patientByCard.get(0)) && patientByCard.get(0).getOpenId()==null || Objects.equals(patientByCard.get(0).getOpenId(),"") && !ObjectUtils.isEmpty(patientByCard.get(0))){
            patient.setId(patientByCard.get(0).getId());
            patient.setUpdateDate(new Date());
            this.dao.wxUpdate(patient);
            return patient;
        }else if(!ObjectUtils.isEmpty(patientByCard.get(0)) && !ObjectUtils.isEmpty(patientByCard.get(0).getOpenId())){
            if(Objects.equals(patient.getOpenId(),patientByCard.get(0).getOpenId())){
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "该患者身份证号已绑定！"));
            }else {
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "该患者身份证号已绑定其他微信账号，不能重复绑定！"));
            }
        }else {
            patient.setId(IdGen.uuid());
            patient.setCreateBy("微信");
            patient.setUpdateBy("微信");
            patient.setCreateDate(new Date());
            patient.setUpdateDate(new Date());
            this.dao.wxInsert(patient);
            return patient;
        }

//        Patient patientByPhone = this.dao.getPatientByPhone(patient.getPhone(), patient.getCompany().getId());
//        if(!ObjectUtils.isEmpty(patientByPhone) && patientByPhone.getOpenId()==null){
//
//        }


    }

    @Transactional(readOnly = false)
    public Patient saveSuper(Patient patient) {
        return super.save(patient);
    }

    public Patient getPatientByPhone(String phoneNumber){
        return this.dao.getPatientByPhone(phoneNumber, SessionUtils.getLoginTenantId());
    }

    public Patient getByCard(Patient patient){
        return this.dao.getPatientByCard(patient.getCard(), patient.getCompany().getId(),null).get(0);
    }

    public Patient getPatientByregistrationId(String registrationId) {
        Patient patient=this.dao.getPatientByregistrationId(registrationId);
        return patient;
    }

    public List<Patient> getByOpenId(String openId,String companyId) {
        List<Patient> patients = this.dao.getByOpenId(openId,companyId);
        return patients;
    }

    /* 根据患者姓名身份证查询患者信息 */
    public Patient inquire(Patient patient) {
        Patient patients = this.dao.inquire(patient);
        return patients;
    }

}