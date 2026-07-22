package com.geeke.org.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
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
public class CompanyController extends CrudController<CompanyService, Company> {

    @Autowired
    protected CompanyService companyService;

    @Override
    protected CompanyService getService() {
        return companyService;
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

    @PostMapping(value = "saveWithFile")
    public ResponseEntity<JSONObject> saveWithFile(@RequestParam("entity") String strEntity,
      @RequestParam("fileIdUploads") MultipartFile[] fileIdUploads,
      @RequestParam("deleteIds")String strDeleteIds) throws java.io.IOException {
        Company entity = JSONObject.parseObject(strEntity, Company.class);
        String[] deleteIds = JSONObject.parseObject(strDeleteIds, String[].class);
        String id = companyService.save(entity,
            fileIdUploads,
            deleteIds
        ).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
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