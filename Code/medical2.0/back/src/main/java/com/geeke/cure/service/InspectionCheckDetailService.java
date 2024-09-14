package com.geeke.cure.service;

import java.util.List;
import java.util.Map;

import com.geeke.cure.entity.InspectionCheckDetailCostItem;
import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.entity.CostItemPackage;
import com.geeke.treatment.service.CostItemService;
import com.geeke.treatment.service.impl.CostItemPackageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.cure.dao.InspectionCheckDetailDao;
import com.geeke.cure.entity.InspectionCheckDetail;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 检验检查详情Service
 * @author rys
 * @version 2022-10-19
 */
 
@Service("inspectionCheckDetailService")
@Transactional(readOnly = false)
public class InspectionCheckDetailService extends CrudService<InspectionCheckDetailDao, InspectionCheckDetail>{

    @Autowired
    private CostItemService costItemService;

    @Autowired
    private CostItemPackageService costItemPackageService;
    @Transactional
    public void allSave(InspectionCheckDetail inspectionCheckDetail) {
        this.dao.insert(inspectionCheckDetail);
    }

    public List<InspectionCheckDetail> getByInfoId(String infoId) {
        List<InspectionCheckDetail> inspectionCheckDetails = this.dao.getByInfoId(infoId);
        for (InspectionCheckDetail inspectionCheckDetail : inspectionCheckDetails) {
            CostItem costItem = costItemService.get(inspectionCheckDetail.getCostItem().getId());
            List<CostItemPackage> all = costItemPackageService.getAll(costItem.getId());
            costItem.setCostItemPackages(all);
            inspectionCheckDetail.setCostItem(costItem);
        }
        return inspectionCheckDetails;
    }

    public List<InspectionCheckDetailCostItem> getByInfoId2(String infoId) {
        List<InspectionCheckDetailCostItem> byInfoId2 = this.dao.getByInfoId2(infoId);
//        for (InspectionCheckDetailCostItem inspectionCheckDetail : byInfoId2) {
//            CostItem costItem = costItemService.get(inspectionCheckDetail.getCostItem().getId());
//            List<CostItemPackage> all = costItemPackageService.getAll(costItem.getId());
//            costItem.setCostItemPackages(all);
//            inspectionCheckDetail.setCostItem(costItem);
//        }

        return byInfoId2;
    }

    public void update(InspectionCheckDetail inspectionCheckDetail) {
        this.dao.update(inspectionCheckDetail);
    }

    public void deleteBy(InspectionCheckDetail inspectionCheckDetail) {
        this.dao.deleteBy(inspectionCheckDetail.getId());
    }
}