package com.geeke.cure.service;

import java.util.List;
import java.util.Map;

import com.geeke.cure.entity.InspectionCheckDetail;
import com.geeke.cure.entity.InspectionCheckInfo;
import com.geeke.utils.IdGen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.cure.dao.InspectionCheckDao;
import com.geeke.cure.entity.InspectionCheck;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

/**
 * 检验检查Service
 * @author rys
 * @version 2022-10-18
 */
 
@Service("inspectionCheckService")
@Transactional(readOnly = false)
public class InspectionCheckService extends CrudService<InspectionCheckDao, InspectionCheck>{

    @Autowired
    private InspectionCheckDao inspectionCheckDao;

    @Autowired
    private InspectionCheckInfoService inspectionCheckInfoService;

    @Autowired
    private InspectionCheckDetailService inspectionCheckDetailService;

    @Transactional(readOnly = false)
    public void allSave(InspectionCheck inspectionCheck) {

        inspectionCheckDao.allSave(inspectionCheck);
    }

    public List<InspectionCheck> getByRecipelInfoId(String registrationId) {
        List<InspectionCheck> inspectionChecks = this.dao.getByRecipelInfoId(registrationId);
        return inspectionChecks;
    }

    public void deleteByRecipelInfoId(String recipelInfoId) {
        //通过recipelInfoId获取id
        List<InspectionCheck> byRecipelInfoId = this.dao.getByRecipelInfoId(recipelInfoId);
        if(!CollectionUtils.isEmpty(byRecipelInfoId)) {
            for (InspectionCheck inspectionCheck : byRecipelInfoId) {
                InspectionCheckInfo byInspecId = inspectionCheckInfoService.getByInspecId(inspectionCheck.getId());
                List<InspectionCheckDetail> byInfoId = inspectionCheckDetailService.getByInfoId(byInspecId.getId());
                if (!CollectionUtils.isEmpty(byInfoId)) {
                    for (InspectionCheckDetail inspectionCheckDetail : byInfoId) {
                        inspectionCheckDetailService.deleteBy(inspectionCheckDetail);
                    }
                }
                inspectionCheckInfoService.deleteBy(byInspecId);
            }
        }
        this.dao.deleteByRecipelInfoId(recipelInfoId);
    }

    public List<InspectionCheck> getByRecipelDetail(String recipelDetailId) {
        List<InspectionCheck> inspectionChecks = this.dao.getByRecipelDetail(recipelDetailId);
        return inspectionChecks;
    }
}