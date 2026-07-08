package com.geeke.outpatient.controller;

import java.util.List;

import com.geeke.outpatient.entity.Registration;
import com.geeke.outpatient.service.RegistrationService;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.geeke.outpatient.entity.Patient;
import com.geeke.outpatient.service.PatientService;
import com.geeke.common.controller.CrudController;
import com.geeke.utils.ResultUtil;

/**
 * 患者信息Controller
 * @author txl
 * @version 2022-06-23
 */
@RestController
@RequestMapping(value = "/outpatient/patient")
public class PatientController extends CrudController<PatientService, Patient> {

	@Autowired
	protected PatientService patientService;

    @Autowired
    private RegistrationService registrationService;

    @Override
    protected PatientService getService() {
        return patientService;
    }

    //微信保存
    @PostMapping(value = "wxSave")
    public ResponseEntity<JSONObject> wxSave(@RequestBody Patient entity) {
        String id = patientService.wxSave(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
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
