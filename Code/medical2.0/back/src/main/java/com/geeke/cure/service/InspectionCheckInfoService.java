package com.geeke.cure.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.geeke.cure.entity.InspectionCheckDetail;
import com.geeke.cure.entity.InspectionCheckDetailCostItem;
import com.geeke.cure.entity.InspectionCheckInfoCostItem;
import com.geeke.sys.entity.SysFile;
import com.geeke.sys.service.SysFileService;
import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.entity.CostItems;
import com.geeke.utils.IdGen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.cure.dao.InspectionCheckInfoDao;
import com.geeke.cure.entity.InspectionCheckInfo;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;
import org.springframework.web.multipart.MultipartFile;

/**
 * 检验检查明细Service
 * @author rys
 * @version 2022-10-19
 */
 
@Service("inspectionCheckInfoService")
@Transactional(readOnly = false)
public class InspectionCheckInfoService extends CrudService<InspectionCheckInfoDao, InspectionCheckInfo>{

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private InspectionCheckDetailService inspectionCheckDetailService;

    public void save(InspectionCheckInfo inspectionCheckInfo, MultipartFile[] fileIdUploads, String[] deleteIds) throws IOException {

        //保存上传文件
        List<SysFile> sysFiles = sysFileService.changeAndSaveSysFileList(fileIdUploads, inspectionCheckInfo.getId());
        if(sysFiles!=null&&sysFiles.size()>0){
            inspectionCheckInfo.setFileId(sysFiles.get(0).getId());
        }
        // 根据附件ID删除附件信息
        if (null != deleteIds && deleteIds.length > 0) {
            sysFileService.delete(deleteIds);
        }
        this.dao.update(inspectionCheckInfo);

        //保存完后保存明细
        List<CostItems> costItemsList = inspectionCheckInfo.getCostItemsList();
        List<InspectionCheckDetail> inspectionCheckDetails = inspectionCheckInfo.getInspectionCheckDetails();
        for (InspectionCheckDetail inspectionCheckDetail : inspectionCheckDetails) {
            inspectionCheckDetailService.update(inspectionCheckDetail);
        }

    }

    @Transactional
    public void allSave(InspectionCheckInfo inspectionCheckInfo) {
        this.dao.insert(inspectionCheckInfo);
    }

    @Transactional
    public InspectionCheckInfo getByInspecId(String inspecId) {
        InspectionCheckInfo inspectionCheckInfo = this.dao.getByInspecId(inspecId);

        List<InspectionCheckDetail> inspectionCheckDetails = inspectionCheckDetailService.getByInfoId(inspectionCheckInfo.getId());

        inspectionCheckInfo.setInspectionCheckDetails(inspectionCheckDetails);
        return inspectionCheckInfo;
    }

    @Transactional
    public InspectionCheckInfoCostItem getByInspecId2(String inspecId) {
        InspectionCheckInfoCostItem inspectionCheckInfoCostItem = this.dao.getByInspecId2(inspecId);

        List<InspectionCheckDetailCostItem> byInfoId2 = inspectionCheckDetailService.getByInfoId2(inspectionCheckInfoCostItem.getId());

        inspectionCheckInfoCostItem.setInspectionCheckDetails(byInfoId2);
        return inspectionCheckInfoCostItem;
    }


    public void deleteBy(InspectionCheckInfo byInspecId) {
        this.dao.deleteBy(byInspecId.getId());
    }
}