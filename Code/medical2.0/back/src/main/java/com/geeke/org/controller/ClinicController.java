package com.geeke.org.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.org.entity.Clinic;
import com.geeke.org.entity.Company;
import com.geeke.org.service.ClinicService;
import com.geeke.org.service.CompanyService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 诊所管理Controller
 * @author txl
 * @version 2022-05-19
 */
@RestController
@RequestMapping(value = "/org/clinic")
public class ClinicController extends BaseController {

	@Autowired
	private ClinicService clinicService;
	@Autowired
    private CompanyService companyService;

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        Company entity = companyService.get(id);
        entity.setLesseeId(entity.getParent());
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<Company> result = companyService.listClinicPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<Company> result = companyService.listAllClinic(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestParam("entity") String strEntity,
      @RequestParam("fileIdUploads") MultipartFile[] fileIdUploads,  // 文件: 诊所许可证图片
      @RequestParam("deleteIds")String strDeleteIds) throws java.io.IOException {
        Company clinic = JSONObject.parseObject(strEntity, Company.class);
//        Company clinic = new Company();
//        BeanUtils.copyProperties(entity,clinic);
        String[] deleteIds = JSONObject.parseObject(strDeleteIds, String[].class);
        String id = clinicService.save(clinic,
            fileIdUploads,
            deleteIds
        ).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody Company entity) {
        clinicService.deleteClinic(entity);
        return ResponseEntity.ok(ResultUtil.successJson());
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<Clinic> entitys) {
        List<String> ids = clinicService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<Clinic> entitys) {
        List<String> ids = clinicService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<Clinic> entitys) {
        int rows = clinicService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }


}