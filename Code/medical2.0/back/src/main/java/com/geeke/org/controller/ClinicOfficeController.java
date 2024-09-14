package com.geeke.org.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.org.entity.ClinicOffice;
import com.geeke.org.service.ClinicOfficeService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 科室信息Controller
 * @author ch
 * @version 2022-06-14
 */
@RestController
@RequestMapping(value = "/org/clinicOffice")
public class ClinicOfficeController extends BaseController {

    @Autowired
    private ClinicOfficeService clinicOfficeService;

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        ClinicOffice entity = clinicOfficeService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<ClinicOffice> result = clinicOfficeService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<ClinicOffice> result = clinicOfficeService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody ClinicOffice entity) {
        entity.setCompany(SessionUtils.getLoginTenant());
        if("1".equals(entity.getIsDefault())){
            clinicOfficeService.updateDefault(entity.getCompany().getId());
        }
        String id = clinicOfficeService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody ClinicOffice entity) {
        int rows = clinicOfficeService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<ClinicOffice> entitys) {
        List<String> ids = clinicOfficeService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<ClinicOffice> entitys) {
        List<String> ids = clinicOfficeService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<ClinicOffice> entitys) {
        int rows = clinicOfficeService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

}
