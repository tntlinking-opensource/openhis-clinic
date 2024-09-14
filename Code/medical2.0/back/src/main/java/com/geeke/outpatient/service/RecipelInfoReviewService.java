package com.geeke.outpatient.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.outpatient.dao.RecipelInfoReviewDao;
import com.geeke.outpatient.entity.*;
import com.geeke.outpatient.vo.PrescriptionStatisticsVO;
import com.geeke.outpatient.vo.ReviewVO;
import com.geeke.outpatient.vo.StatementVO;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;

/**
 * 系统参数配置Service
 * @author lys
 * @version 2021-07-16
 */
 
@Service("recipellnfoReviewService")
@Transactional(readOnly = true)
public class RecipelInfoReviewService extends CrudService<RecipelInfoReviewDao, RecipelInfoReview>{
    @Resource
    private RecipelInfoReviewDao recipelInfoReviewDao;
    @Resource
    private RecipelInfoService recipelInfoService;
    @Resource
    private RegistrationService registrationService;
    @Resource
    private MedicalRecordService medicalRecordService;
    @Resource
    private RecipelDetailService recipelDetailService;
    public RecipelInfoReview getByRecipelInfoId(String recipelInfoId) {
        return this.recipelInfoReviewDao.getByRecipelInfoId(recipelInfoId);
    }

    public ReviewVO reviewForm(String recipelInfoId) {
        RecipelInfoReview recipelInfoReview = this.getByRecipelInfoId(recipelInfoId);
        if (ObjectUtil.isNull(recipelInfoReview)) {
            return null;
        }
        ReviewVO vo = new ReviewVO();
        vo.setRecipelInfoReview(recipelInfoReview);
        //中药西药
        List<Parameter> parameters = ListUtil.list(Boolean.FALSE, new Parameter("registration_id", "=", recipelInfoReview.getRecipelInfo().getRegistration().getId()));
        List<MedicalRecord> medicalRecordList = medicalRecordService.listAll(parameters, StrUtil.EMPTY);
        if (CollUtil.isNotEmpty(medicalRecordList)) {
            MedicalRecord medicalRecord = medicalRecordList.get(0);
            vo.setWesternDiagnose(medicalRecord.getWesternDiagnose());
            vo.setChinaDiagnose(medicalRecord.getChinaDiagnose());
        }
        //处方明细
        CollUtil.clear(parameters);
        parameters.add(new Parameter("company_id", "=", recipelInfoReview.getRecipelInfo().getCompany().getId()));
        parameters.add(new Parameter("recipel_info_id", "=", recipelInfoId));
        parameters.add(new Parameter("is_extra", "=", 0));
        List<RecipelDetail> recipelDetailList = this.recipelDetailService.listAll(parameters, org.apache.commons.lang3.StringUtils.EMPTY);
        if (CollUtil.isNotEmpty(recipelDetailList)) {
            recipelDetailList.forEach(recipelDetail -> {
                recipelDetail.setDrugStuffId(medicalRecordService.getDrugStuffEvt(recipelDetail));
            });
        }
        vo.setRecipelDetails(recipelDetailList);
        //附加费
        Parameter parameter = parameters.get(2);
        parameter.setValue(1);
        parameters.set(2, parameter);
        List<RecipelDetail> additionalCharges = this.recipelDetailService.listAll(parameters, org.apache.commons.lang3.StringUtils.EMPTY);
        if (CollUtil.isNotEmpty(additionalCharges)) {
            additionalCharges.forEach(recipelDetail -> {
                recipelDetail.setDrugStuffId(medicalRecordService.getDrugStuffEvt(recipelDetail));
            });
        }
        vo.setAdditionalCharges(additionalCharges);
        return vo;
    }

    public Page<StatementVO> listPageStatement(List<Parameter> parameters, String orderby) {
        String groupBy = "";
        for (int i = parameters.size() - 1; i >= 0; i--) {
            Parameter parameter = parameters.get(i);
            if (StrUtil.equals(parameter.getColumnName(), "`groupBy`")) {
                groupBy = parameter.getValue().toString();
                parameters.remove(i);
            }
        }
        List<String> groupByColumns = StrUtil.split(groupBy, ",");
        List<StatementVO> list = dao.listPageStatement(parameters,groupByColumns);
        return new Page<StatementVO>(Long.valueOf((list.size()+"")), list);
    }

    /**
     * 处方统计
     * @param id 机构ID，用于过滤当前机构的数据
     * @return
     */
    public PrescriptionStatisticsVO prescriptionStatistics(String id,String startTime,String endTime) {
        return this.dao.prescriptionStatistics(id,startTime,endTime);
    }
}