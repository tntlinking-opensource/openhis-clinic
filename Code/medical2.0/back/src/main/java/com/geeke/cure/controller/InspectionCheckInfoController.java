package com.geeke.cure.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.geeke.cure.entity.InspectionCheck;
import com.geeke.cure.service.InspectionCheckService;
import com.geeke.sys.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.geeke.cure.entity.InspectionCheckInfo;
import com.geeke.cure.service.InspectionCheckInfoService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 检验检查明细Controller
 * @author rys
 * @version 2022-10-19
 */
@RestController
@RequestMapping(value = "/cure/inspectionCheckInfo")
public class InspectionCheckInfoController extends BaseController {

	@Autowired
	private InspectionCheckInfoService inspectionCheckInfoService;

	@Autowired
    private InspectionCheckService inspectionCheckService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        InspectionCheckInfo entity = inspectionCheckInfoService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @GetMapping("/info/{inspecId}")
    public ResponseEntity<JSONObject> getByInspecId(@PathVariable("inspecId") String inspecId) {
        InspectionCheckInfo entity = inspectionCheckInfoService.getByInspecId(inspecId);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<InspectionCheckInfo> result = inspectionCheckInfoService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<InspectionCheckInfo> result = inspectionCheckInfoService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestParam("entity") String strInspection,
                                               @RequestParam("fileIdUploads") MultipartFile[] fileIdUploads,  // 文件: 用户图片
                                           @RequestParam(value = "deleteIds",required = false)String strDeleteIds) throws IOException {
        InspectionCheckInfo inspectionCheckInfo = JSONObject.parseObject(strInspection, InspectionCheckInfo.class);
       // MultipartFile[] multipartFiles = JSONObject.parseObject(fileIdUploads, MultipartFile[].class);
        String[] deleteIds = JSONObject.parseObject(strDeleteIds, String[].class);
        System.out.println(inspectionCheckInfo);
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
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody InspectionCheckInfo entity) {
        int rows = inspectionCheckInfoService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<InspectionCheckInfo> entitys) {
        List<String> ids = inspectionCheckInfoService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<InspectionCheckInfo> entitys) {
        List<String> ids = inspectionCheckInfoService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<InspectionCheckInfo> entitys) {
        int rows = inspectionCheckInfoService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

}