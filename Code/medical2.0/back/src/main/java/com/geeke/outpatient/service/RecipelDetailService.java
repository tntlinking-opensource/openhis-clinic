package com.geeke.outpatient.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.service.CrudService;
import com.geeke.outpatient.dao.RecipelDetailDao;
import com.geeke.outpatient.dao.RecipelInfoDao;
import com.geeke.outpatient.entity.*;
import com.geeke.utils.SessionUtils;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 处方详情Service
 * @author txl
 * @version 2022-06-07
 */
 
@Service("recipelDetailService")
@Transactional(readOnly = true)
public class RecipelDetailService extends CrudService<RecipelDetailDao, RecipelDetail>{
    @Autowired
    private RecipelDetailDao recipelDetailDao;

    @Autowired
    private RecipelInfoDao recipelInfoDao;

    @Autowired
    private RegistrationService registrationService;
    @Transactional
    public void addRetailrecipelDetails(List<RecipelDetail> recipelDetails, RecipelInfo recipelInfo) {
        for (RecipelDetail recipelDetail : recipelDetails) {
            recipelDetail.setRecipelInfo(recipelInfo);
            if (null != recipelInfo.getCompany()) {
                recipelDetail.setCompany(recipelInfo.getCompany());
            }
            DrugStuffEvt drugStuffId = recipelDetail.getDrugStuffId();
            System.out.println("dddddddddddddd"+ JSONObject.toJSON(drugStuffId));
            super.save(recipelDetail);
        }

    }

    // 处方id获取处方详情
    @Transactional
    public List<RecipelDetail> getRecipelDetail(String id) {
        List<RecipelDetail> recipelDetail = recipelDetailDao.getRecipelDetail(id);
        return recipelDetail;
    }

    // 处方id获取项目执行进度
    @Transactional
    public int schedule(String id) {
        // 如果返回值为负数则未完成，如果为0则已完成
        int recipelDetail = recipelDetailDao.schedule(id);
        return recipelDetail;
    }

    //统计未过期的已经被占用的药品
    public List<RecipelStastics> getDetailStasticsForOccupy(int days)
    {
        return this.dao.getDetailStasticsForOccupy(SessionUtils.getLoginTenantId(),days);
    }

    //统计当前需要的药品
    public List<RecipelStastics> getDetailStasticsForNowByRecordId(String medicalRecordId)
    {
        return this.dao.getDetailStasticsForNowByRecordId(medicalRecordId,SessionUtils.getLoginTenantId());
    }

    public List<RecipelDetail> getByRecipelInfoId(String recipelInfoId)
    {

        return this.dao.getByRecipelInfoId(recipelInfoId);
    }

    @Transactional(readOnly = false)
    public int batchDelete(List<RecipelDetail> var1){
        return this.dao.batchDelete(var1);
    }

    @Transactional(readOnly = false)
    public void updateById(BigDecimal executions, String id, String stuffType, int infuseType, String cureId){
        if("3".equals(stuffType)){
            int i = recipelDetailDao.updateById(executions, id,0);
            if(i>0){
                RecipelDetail recipelDetail = this.dao.get(id);
                List<RecipelDetail> recipelDetails = recipelDetailDao.findByInfoId(recipelDetail.getRecipelInfo().getId(),recipelDetail.getStuffType());
                int count=0;
                if(recipelDetails!=null){
                    for (RecipelDetail recipelDetail1:
                            recipelDetails) {
                        if(recipelDetail1.getTotal().equals(recipelDetail1.getExecutions())){
                            count++;
                        }
                    }
                    //如果所有项目都完成的话，就修改处方的标志
                    if(count==recipelDetails.size()){
                        int i1 = recipelInfoDao.updateById(1,0, recipelDetail.getRecipelInfo().getId());
                        if(i1>0){
                            RecipelInfo recipelInfo = recipelInfoDao.get(recipelDetail.getRecipelInfo().getId());
                            Registration registration = registrationService.get(recipelInfo.getRegistration().getId());
                            registration.setTreatmentDate(new Date());
                            registrationService.save(registration);
                        }
                    }else {
                        recipelInfoDao.updateById(0,0,recipelDetail.getRecipelInfo().getId());
                    }
                }
            }

        }else {
            RecipelDetail recipelDetail = this.dao.get(id);
            List<RecipelDetail> recipelDetails = recipelDetailDao.findByInfoId(recipelDetail.getRecipelInfo().getId(),recipelDetail.getStuffType());
            for (RecipelDetail recipelDetail1:
            recipelDetails) {
                if(recipelDetail.getInfuseGroup()==recipelDetail1.getInfuseGroup()){
                    if(null==cureId||cureId.isEmpty()){
                        recipelDetailDao.updateById(executions, recipelDetail1.getId(),1);
                    }else {
                        recipelDetailDao.updateById(executions, recipelDetail1.getId(),0);
                    }
                }
            }
            List<RecipelDetail> recipelDetails2 = recipelDetailDao.findByInfoId(recipelDetail.getRecipelInfo().getId(),recipelDetail.getStuffType());
            int count=0;
            if(recipelDetails2!=null){
                for (RecipelDetail recipelDetail1:
                        recipelDetails2) {
                    if(recipelDetail1.getDays().getName()==recipelDetail1.getExecutions().toString()){
                        count++;
                    }
                }
                //如果所有项目都完成的话，就修改处方的标志
                if(count==recipelDetails2.size()){
                    int i1 = recipelInfoDao.updateById(1,1, recipelDetail.getRecipelInfo().getId());
                    if(i1>0){
                        RecipelInfo recipelInfo = recipelInfoDao.get(recipelDetail.getRecipelInfo().getId());
                        Registration registration = registrationService.get(recipelInfo.getRegistration().getId());
                        registration.setTreatmentDate(new Date());
                        registrationService.save(registration);
                    }
                }else {
                    recipelInfoDao.updateById(0,0,recipelDetail.getRecipelInfo().getId());
                }
            }
        }
    }
}