package com.geeke.outpatient.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.org.entity.Company;
import com.geeke.outpatient.entity.*;
import com.geeke.outpatient.service.*;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
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
public class MedicalRecordController extends CrudController<MedicalRecordService, MedicalRecord> {

    @Autowired
    protected MedicalRecordService medicalRecordService;

    @Autowired
    private RemoteDiagnosisTreatmentService remoteDiagnosisTreatmentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private XtZdService zdService;

    @Autowired
    private XtZyzhService xtZyzhService;

    @Override
    protected MedicalRecordService getService() {
        return medicalRecordService;
    }

    @GetMapping("/instant/{phone}")
    public ResponseEntity<JSONObject> getByPhone(@PathVariable("phone") String phone) {
        Patient patient = patientService.getPatientByPhone(phone);
        return ResponseEntity.ok(ResultUtil.successJson(patient));
    }

    @GetMapping("/medical/{registration}")
    public ResponseEntity<JSONObject> getByOrder(@PathVariable("registration") String registration) {
        RemoteDiagnosisTreatment entity = remoteDiagnosisTreatmentService.diagnosisById(registration);
        registration = entity.getRegistrationId();
        List<MedicalRecord> result = medicalRecordService.getByOrder(registration);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "saveWithFile")
    public ResponseEntity<JSONObject> saveWithFile(@RequestParam("entity") String strEntity,
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


    /**
     * 获取系统诊断
     * @param searchParam
     * @return
     */
    @PostMapping("/getxtzd")
    public ResponseEntity<JSONObject> getxtzd(String searchParam,String zdType){
        LambdaQueryWrapper<XtZd> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(XtZd::getZdlx, zdType)
                .eq(XtZd::getZt, 1)
                .and(!StringUtils.isEmpty(searchParam),wrapper -> wrapper
                        .like(XtZd::getZdmc, searchParam)
                        .or()
                        .like( XtZd::getPy, searchParam) // Py 字段模糊匹配
                        .or()
                        .like(XtZd::getZdcode, searchParam)
                ); // Zdcode 字段模糊匹配
        IPage<XtZd> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 30);
        page = zdService.page(page, queryWrapper);
        return ResponseEntity.ok(ResultUtil.successJson(page));
    }

    /**
     * 获取系统中医证候
     * @param searchParam
     * @return
     */
    @PostMapping("/getzyzh")
    public ResponseEntity<JSONObject> getxtzd(String searchParam){
        LambdaQueryWrapper<XtZyzh> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(XtZyzh::getZt, 1)
                .and(!StringUtils.isEmpty(searchParam),wrapper -> wrapper
                                .like( XtZyzh::getZhmc, searchParam)
                                .or()
                                .like( XtZyzh::getZhcode, searchParam)
                                .or()
                                .like( XtZyzh::getPy, searchParam)
                );
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<XtZyzh> page = xtZyzhService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 30), queryWrapper);
        return ResponseEntity.ok(ResultUtil.successJson(page));
    }




}
