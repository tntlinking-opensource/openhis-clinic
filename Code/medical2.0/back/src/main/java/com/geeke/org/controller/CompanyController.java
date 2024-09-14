package com.geeke.org.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
import com.geeke.sys.controller.BaseController;
import com.geeke.sys.utils.SessionUserDto;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 公司管理Controller
 * @author lys
 * @version 2022-05-25
 */
@RestController
@RequestMapping(value = "/org/company")
public class CompanyController extends BaseController {

	@Autowired
	private CompanyService companyService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        Company entity = companyService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<Company> result = companyService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<Company> result = companyService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "getCompanys")
    public ResponseEntity<JSONObject> getCompanys(@RequestBody String id) {
        List<Company> result = companyService.getCompanys(id);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "tree")
    public ResponseEntity<JSONObject> tree(@RequestBody SearchParams searchParams) {
        List<Company> result = companyService.tree(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    } 

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestParam("entity") String strEntity,
      @RequestParam("fileIdUploads") MultipartFile[] fileIdUploads,  // 文件: 诊所许可证图片
      @RequestParam("deleteIds")String strDeleteIds) throws java.io.IOException {
        Company entity = JSONObject.parseObject(strEntity, Company.class);
        String[] deleteIds = JSONObject.parseObject(strDeleteIds, String[].class);
        String id = companyService.save(entity,
            fileIdUploads,
            deleteIds
        ).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody Company entity) {
        int rows = companyService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<Company> entitys) {
        List<String> ids = companyService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<Company> entitys) {
        List<String> ids = companyService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<Company> entitys) {
        int rows = companyService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    /**
     * 获取租户下面所有的诊所
     *
     * @return
     */
    @GetMapping(value = "getSubordinateClinic")
    public ResponseEntity<JSONObject> getSubordinateClinic() {
        SessionUserDto userDto = SessionUtils.getUserDto();
        String companyId = userDto.getCompanyId();
        List<Company> result = companyService.getSubordinateClinic(companyId);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
}