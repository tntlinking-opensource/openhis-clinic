package com.geeke.outpatient.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.service.CrudService;
import com.geeke.cure.entity.InspectionCheck;
import com.geeke.cure.service.InspectionCheckService;
import com.geeke.outpatient.dao.RecipelDetailDao;
import com.geeke.outpatient.dao.RecipelInfoDao;
import com.geeke.outpatient.entity.*;
import com.geeke.utils.SessionUtils;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 处方详情 Service
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

    @Lazy
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private InspectionCheckService inspectionCheckService;
    @Transactional
    public void addRetailrecipelDetails(List<RecipelDetail> recipelDetails, RecipelInfo recipelInfo) {
        for (RecipelDetail recipelDetail : recipelDetails) {
            recipelDetail.setRecipelInfo(recipelInfo);
            if (null != recipelInfo.getCompany()) {
                recipelDetail.setCompany(recipelInfo.getCompany());
            }
            DrugStuffEvt drugStuffId = recipelDetail.getDrugStuffId();
            super.save(recipelDetail);
        }

    }

    // 处方id获取处方详情
    @Transactional(readOnly = true)
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

    /**
     * 批量查询多个处方的明细
     * @param recipelInfoIds 处方ID列表
     * @return 按处方ID分组的明细Map
     */
    public Map<String, List<RecipelDetail>> getByRecipelInfoIds(List<String> recipelInfoIds) {
        if (CollectionUtils.isEmpty(recipelInfoIds)) {
            return Collections.emptyMap();
        }
        List<RecipelDetail> details = this.dao.getByRecipelInfoIds(recipelInfoIds);
        return details.stream().collect(Collectors.groupingBy(
                d -> d.getRecipelInfo().getId()
        ));
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
                            
                            // 更新该处方下所有检验检查状态为已填写
                            List<InspectionCheck> inspectionChecks = inspectionCheckService.getByRecipelInfoId(recipelInfo.getId());
                            if (!CollectionUtils.isEmpty(inspectionChecks)) {
                                for (InspectionCheck inspectionCheck : inspectionChecks) {
                                    inspectionCheck.setStatus("1");
                                    inspectionCheckService.save(inspectionCheck);
                                }
                            }
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
                        
                        // 更新该处方下所有检验检查状态为已填写
                        List<InspectionCheck> inspectionChecks = inspectionCheckService.getByRecipelInfoId(recipelInfo.getId());
                        if (!CollectionUtils.isEmpty(inspectionChecks)) {
                            for (InspectionCheck inspectionCheck : inspectionChecks) {
                                inspectionCheck.setStatus("1");
                                inspectionCheckService.save(inspectionCheck);
                            }
                        }
                    }
                }else {
                    recipelInfoDao.updateById(0,0,recipelDetail.getRecipelInfo().getId());
                }
            }
        }
    }
}