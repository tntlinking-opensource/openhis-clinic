package com.geeke.cure.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.geeke.common.controller.CrudController;
import com.geeke.cure.entity.InspectionCheck;
import com.geeke.cure.service.InspectionCheckService;
import com.geeke.sys.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.geeke.cure.entity.InspectionCheckInfo;
import com.geeke.cure.service.InspectionCheckInfoService;
import com.geeke.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * 检验检查明细Controller
 * @author rys
 * @version 2022-10-19
 */
@RestController
@RequestMapping(value = "/cure/inspectionCheckInfo")
public class InspectionCheckInfoController extends CrudController<InspectionCheckInfoService, InspectionCheckInfo> {

    private static final Logger logger = LoggerFactory.getLogger(InspectionCheckInfoController.class);

    @Autowired
    protected InspectionCheckInfoService inspectionCheckInfoService;

    @Autowired
    private InspectionCheckService inspectionCheckService;

    @Override
    protected InspectionCheckInfoService getService() {
        return inspectionCheckInfoService;
    }

    @GetMapping("/info/{inspecId}")
    public ResponseEntity<JSONObject> getByInspecId(@PathVariable("inspecId") String inspecId) {
        InspectionCheckInfo entity = inspectionCheckInfoService.getByInspecId(inspecId);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @PostMapping(value = "saveWithFile")
    public ResponseEntity<JSONObject> saveWithFile(@RequestParam("entity") String strInspection,
                                               @RequestParam("fileIdUploads") MultipartFile[] fileIdUploads,
                                           @RequestParam(value = "deleteIds",required = false)String strDeleteIds) throws IOException {
        InspectionCheckInfo inspectionCheckInfo = JSONObject.parseObject(strInspection, InspectionCheckInfo.class);
        String[] deleteIds = JSONObject.parseObject(strDeleteIds, String[].class);
        logger.debug("保存检验检查明细: {}", inspectionCheckInfo);
        inspectionCheckInfoService.save(inspectionCheckInfo,fileIdUploads,deleteIds);
        InspectionCheck inspectionCheck = inspectionCheckService.get(inspectionCheckInfo.getInspectionCheck().getId());
        inspectionCheck.setStatus("1");
        JSONObject userObj = SessionUtils.getUserJson();
        inspectionCheck.setCreateBy(userObj.getString("name"));
        inspectionCheck.setUpdateBy(userObj.getString("name"));
        inspectionCheck.setCreateDate(new Date());
        inspectionCheck.setUpdateDate(new Date());
        inspectionCheckService.save(inspectionCheck);
        return ResponseEntity.ok(ResultUtil.successJson(inspectionCheckInfo.getId()));
    }
}