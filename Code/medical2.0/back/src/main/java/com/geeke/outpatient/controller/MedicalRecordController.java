package com.geeke.outpatient.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.org.entity.Company;
import com.geeke.outpatient.entity.*;
import com.geeke.outpatient.service.MedicalRecordService;
import com.geeke.outpatient.service.PatientService;
import com.geeke.outpatient.service.RegistrationService;
import com.geeke.outpatient.service.RemoteDiagnosisTreatmentService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * 病历填写Controller
 * @author txl
 * @version 2022-06-13
 */
@RestController
@RequestMapping(value = "/outpatient/medicalRecord")
public class MedicalRecordController extends BaseController {

    @Autowired
    private RemoteDiagnosisTreatmentService remoteDiagnosisTreatmentService;
	@Autowired
	private MedicalRecordService medicalRecordService;
	@Autowired
    private PatientService patientService;
	@Autowired
    private RegistrationService registrationService;

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        MedicalRecord entity = medicalRecordService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @GetMapping("/instant/{phone}")
    public ResponseEntity<JSONObject> getByPhone(@PathVariable("phone") String phone) {
        Patient patient = patientService.getPatientByPhone(phone);
        return ResponseEntity.ok(ResultUtil.successJson(patient));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<MedicalRecord> result = medicalRecordService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @GetMapping("/medical/{registration}")
    public ResponseEntity<JSONObject> getByOrder(@PathVariable("registration") String registration) {
        RemoteDiagnosisTreatment entity = remoteDiagnosisTreatmentService.diagnosisById(registration);
        registration = entity.getRegistrationId();
        List<MedicalRecord> result = medicalRecordService.getByOrder(registration);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<MedicalRecord> result = medicalRecordService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestParam("entity") String strEntity,
      @RequestParam("fileIdUploads") MultipartFile[] fileIdUploads,  // 文件: 上传附件
      @RequestParam("deleteIds")String strDeleteIds) throws java.io.IOException {
        MedicalRecord entity = JSONObject.parseObject(strEntity, MedicalRecord.class);
        String[] deleteIds = JSONObject.parseObject(strDeleteIds, String[].class);
        String id = medicalRecordService.save(entity,
            fileIdUploads,
            deleteIds
        ).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "saveAdd")
    public ResponseEntity<JSONObject> save(@RequestBody MedicalRecord entity) {
        RemoteDiagnosisTreatment remote = remoteDiagnosisTreatmentService.diagnosisById(entity.getDiagnosisId());
        Registration registration = new Registration();
        registration.setId(remote.getRegistrationId());
        entity.setRegistration(registration);
        entity.setCompany(remote.getCompany());
        String id = medicalRecordService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody MedicalRecord entity) {
        int rows = medicalRecordService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<MedicalRecord> entitys) {
        List<String> ids = medicalRecordService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<MedicalRecord> entitys) {
        List<String> ids = medicalRecordService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<MedicalRecord> entitys) {
        int rows = medicalRecordService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "allSave")
    public ResponseEntity<JSONObject> allSave(@RequestParam("entity") String strEntity,
                                              @RequestParam(name = "fileIdUploads",required=false) MultipartFile[] fileIdUploads,  // 文件: 上传附件
                                              @RequestParam(name = "deleteIds",required=false)String strDeleteIds) throws java.io.IOException {
        MedicalRecipelEvt medicalRecipelEvt = JSONObject.parseObject(strEntity, MedicalRecipelEvt.class);
        Registration registration = medicalRecordService.allSave(medicalRecipelEvt, fileIdUploads, strDeleteIds);
        return ResponseEntity.ok(ResultUtil.successJson(registration));
    }

    @PostMapping(value = "allQuery")
    public ResponseEntity<JSONObject> allQuery(@RequestBody Registration registration) {
        MedicalRecipelEvt medicalRecipelEvt = medicalRecordService.allQuery(registration,"");
        return ResponseEntity.ok(ResultUtil.successJson(medicalRecipelEvt));
    }

    @GetMapping(value = "details/{infoId}")
    public ResponseEntity<JSONObject> recipelInfoEvt(@PathVariable("infoId") String infoId) {
        RecipelInfoEvt recipelInfoEvt = medicalRecordService.queryByInfoId(infoId);
        return ResponseEntity.ok(ResultUtil.successJson(recipelInfoEvt));
    }


    @PostMapping(value = "v2/allSave")
    public ResponseEntity<JSONObject> allNewSave(@RequestParam("entity") String strEntity,
                                                 @RequestParam(name = "fileIdUploads",required=false) MultipartFile[] fileIdUploads,
                                                 @RequestParam(name = "delFileIds",required=false) String strDelFileIds,
                                                 @RequestParam(name = "medicalFiles",required=false) MultipartFile[] medicalFiles
                                                 ) throws Exception {
        ReceptionEvt receptionEvt = JSONObject.parseObject(strEntity, ReceptionEvt.class);

        for (RecipelInfoEvt recipelInfoEvtList : receptionEvt.getRecipelInfoEvtList()) {
            for (RecipelDetail recipelDetail : recipelInfoEvtList.getRecipelDetailEvtList()) {
                recipelDetail.setExecutions(BigDecimal.valueOf(0));
            }
        }
        String[] delFileIds = StringUtils.commaDelimitedListToStringArray(strDelFileIds);

        String registrationId = this.medicalRecordService.allNewSave(receptionEvt, fileIdUploads, delFileIds,medicalFiles);
        JSONObject result = new JSONObject();
        result.put("registrationId", registrationId);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "addRecipel")
    public ResponseEntity<JSONObject> addRecipel(@RequestBody ReceptionEvt receptionEvt) {
        medicalRecordService.saveRecipelInfoEvtTo(receptionEvt.getRecipelInfoEvtList(), receptionEvt.getId());
        return ResponseEntity.ok(ResultUtil.successJson(receptionEvt.getId()));
    }

    @PostMapping(value = "v2/allQuery/{registrationId}")
    public ResponseEntity<JSONObject> allNewQuery(@PathVariable("registrationId") String registrationId,
                                                  @RequestParam(name = "recipelInfoIds",required=false) String strRecipelInfoIds) {
        String[] recipelInfoIds = StringUtils.commaDelimitedListToStringArray(strRecipelInfoIds);
        ReceptionEvt receptionEvt = this.medicalRecordService.allNewQuery(registrationId, recipelInfoIds);

        return ResponseEntity.ok(ResultUtil.successJson(receptionEvt));
    }
    @PostMapping(value = "history/recipel")
    public ResponseEntity getHistoryRecipel(@RequestBody SearchParams searchParams){
        List<RecipelInfoDTO> byRegistrationId = medicalRecordService.getByRegistrationId(searchParams.getParams(),searchParams.getLimit(),searchParams.getOffset(),searchParams.getOrder());
        return ResponseEntity.ok(ResultUtil.successJson(byRegistrationId));
    }

    @GetMapping(value = "/recordpat/{id}")
    public ResponseEntity recordpat(@PathVariable("id") String id){
        List<Recordpatvo> result = medicalRecordService.recordpatlist(id);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    // 通过挂号id添加病例信息
    @PostMapping(value = "registerSave")
    public ResponseEntity<JSONObject> registerSave(@RequestBody MedicalRecord entity) {
        Registration medicalRecord = registrationService.get(entity.getRegistration().getId());
        entity.setCompany(medicalRecord.getCompany());
        String id = medicalRecordService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

}