package com.geeke.clinic.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.clinic.entity.ClinicVersion;
import com.geeke.clinic.service.ClinicVersionService;
import com.geeke.common.controller.CrudController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 诊所版本Controller
 * @author txl
 * @version 2022-05-23
 */
@RestController
@RequestMapping(value = "/clinic/clinicVersion")
public class ClinicVersionController extends CrudController<ClinicVersionService, ClinicVersion> {

    @Autowired
    protected ClinicVersionService clinicVersionService;

    @Override
    protected ClinicVersionService getService() {
        return clinicVersionService;
    }

    @Override
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody ClinicVersion entity) {
        if (entity == null) {
            return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_400));
        }
        int rows = clinicVersionService.deleteClinic(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @Override
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<ClinicVersion> entitys) {
        int rows = clinicVersionService.bulkDeleteClinic(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }
}