package com.geeke.outpatient.controller;

import java.util.List;

import com.geeke.config.exception.CommonJsonException;
import com.geeke.outpatient.entity.Registration;
import com.geeke.outpatient.service.RegistrationService;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.outpatient.entity.Patient;
import com.geeke.outpatient.service.PatientService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;

/**
 * 患者信息Controller
 * @author txl
 * @version 2022-06-23
 */
@RestController
@RequestMapping(value = "/outpatient/patient")
public class PatientController extends BaseController {

	@Autowired
	private PatientService patientService;

    @Autowired
    private RegistrationService registrationService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        Patient entity = patientService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<Patient> result = patientService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<Patient> result = patientService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody Patient entity) {
        String id = patientService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    //微信保存
    @PostMapping(value = "wxSave")
    public ResponseEntity<JSONObject> wxSave(@RequestBody Patient entity) {
        String id = patientService.wxSave(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody Patient entity) {
        int rows = patientService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<Patient> entitys) {
        List<String> ids = patientService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<Patient> entitys) {
        List<String> ids = patientService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<Patient> entitys) {
        int rows = patientService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "getByCard")
    public ResponseEntity<JSONObject> getByCard(@RequestBody Patient patient) {
        Patient byCard = patientService.getByCard(patient);
        return ResponseEntity.ok(ResultUtil.successJson(byCard));
    }

    @PostMapping(value = "getByOpenId")
    public ResponseEntity<JSONObject> getByOpenId(@RequestBody Patient patient){
        List<Patient> patients = patientService.getByOpenId(patient.getOpenId(),patient.getCompany().getId());
        return ResponseEntity.ok(ResultUtil.successJson(patients));
    }

    /* 根据患者姓名身份证查询患者信息并挂号 */
    @PostMapping(value = "inquire")
    public ResponseEntity<JSONObject> inquire(@RequestBody Patient patient) {
        // 查询患者信息
        Patient result = patientService.inquire(patient);
        if (result==null){
            return ResponseEntity.ok(ResultUtil.warningJson(ErrorEnum.E_50001, "查询失败，无该患者信息"));
        }
        // 挂号
        Registration registration = new Registration();
        registration.setPatientId(result);
        registration.setCompany(result.getCompany());
        String id = registrationService.save(registration).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    /* 添加患者档案信息并挂号 */
    @PostMapping(value = "add")
    public ResponseEntity<JSONObject> add(@RequestBody Patient patient) {
        // 添加患者信息
        Patient result = patientService.save(patient);
        // 挂号
        Registration registration = new Registration();
        registration.setPatientId(result);
        registration.setCompany(result.getCompany());
        String id = registrationService.save(registration).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

}