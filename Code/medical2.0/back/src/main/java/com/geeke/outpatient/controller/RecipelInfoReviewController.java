package com.geeke.outpatient.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.medicareutils.config.MedicareConfigProperties;
import com.geeke.medicareutils.domain.respo.MdExamine;
import com.geeke.medicareutils.domain.respo.MdPreDrugData;
import com.geeke.medicareutils.service.MdMedicationOrderService;
import com.geeke.outpatient.entity.MedicalRecord;
import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.outpatient.entity.RecipelInfoReview;
import com.geeke.outpatient.entity.Registration;
import com.geeke.outpatient.service.MedicalRecordService;
import com.geeke.outpatient.service.RecipelInfoReviewService;
import com.geeke.outpatient.service.RecipelInfoService;
import com.geeke.outpatient.service.RegistrationService;
import com.geeke.outpatient.vo.PrescriptionStatisticsVO;
import com.geeke.outpatient.vo.ReviewVO;
import com.geeke.outpatient.vo.StatementVO;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * 处方信息审查Controller
 * @author lc
 */
@RestController
@RequestMapping(value = "/outpatient/review")
@RequiredArgsConstructor
public class RecipelInfoReviewController extends BaseController {

    @Resource
    private RecipelInfoReviewService recipelInfoReviewService;

    private  final MdMedicationOrderService mdMedicationOrderService;


    private final RecipelInfoService recipelInfoService;

    private final MedicalRecordService medicalRecordService;

    private final RegistrationService registrationService;

    private  final MedicareConfigProperties medicareConfigProperties;




    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        RecipelInfoReview entity = recipelInfoReviewService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    @GetMapping("/recipelInfo/{recipelInfoId}")
    public ResponseEntity<JSONObject> getByRecipelInfoId(@PathVariable("recipelInfoId") String recipelInfoId) {
        RecipelInfoReview entity = recipelInfoReviewService.getByRecipelInfoId(recipelInfoId);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<RecipelInfoReview> result = recipelInfoReviewService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<RecipelInfoReview> result = recipelInfoReviewService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody RecipelInfoReview entity) {
        //获取完整的处方、病历、挂号信息
        RecipelInfo recipelInfo = recipelInfoService.get(entity.getRecipelInfo().getId());
        MedicalRecord medicalRecord = medicalRecordService.get(entity.getMedicalRecord().getId());
        Registration registration = registrationService.get(entity.getRecipelInfo().getRegistration().getId());
        if(recipelInfo.getIsPre() && entity.getReviewResult().equals(1) && medicareConfigProperties.getCheck().equals("true") ){
            //电子处方审核
            recipelInfo.setRegistration(registration);
            entity.setRecipelInfo(recipelInfo);
            entity.setMedicalRecord(medicalRecord);
            //上传预核验
            JSONObject jsonObject = mdMedicationOrderService.validateElectronicPrescriptionUpload_Ld7801(entity);
            //签名电子处方
            JSONObject signResult = mdMedicationOrderService.signElectronicPrescriptionWithInsurance_Ld7802(entity, SessionUtils.getUserJson().getString("id"));
            //电子处方上传
            JSONObject uploadResult = mdMedicationOrderService.uploadElectronicPrescription_Ld7101(entity,SessionUtils.getUserJson().getString("id"));
        }
        String id = recipelInfoReviewService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "save/list")
    public ResponseEntity<JSONObject> saveList(@RequestBody List<RecipelInfoReview> entitys) {
        if (CollectionUtil.isNotEmpty(entitys)) {
            entitys.forEach(recipelInfoReviewService::save);
        }
        return ResponseEntity.ok(ResultUtil.successJson("操作成功"));
    }


    @DeleteMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody RecipelInfoReview entity) {
        int rows = recipelInfoReviewService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @GetMapping("/form/{recipelInfoId}")
    public ResponseEntity<JSONObject> reviewForm(@PathVariable("recipelInfoId") String recipelInfoId) {
        ReviewVO vo = recipelInfoReviewService.reviewForm(recipelInfoId);
        return ResponseEntity.ok(ResultUtil.successJson(vo));
    }

    @PostMapping(value = {"list/statement", ""})
    public ResponseEntity<JSONObject> listPageStatement(@RequestBody SearchParams searchParams) {
        Page<StatementVO> result = recipelInfoReviewService.listPageStatement(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @GetMapping("/prescription/statistics/{id}")
    public ResponseEntity<JSONObject> prescriptionStatistics(@PathVariable("id") String id,
                                                             @RequestParam String startTime,
                                                             @RequestParam String endTime) {
        PrescriptionStatisticsVO entity = recipelInfoReviewService.prescriptionStatistics(id,startTime,endTime);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }


    /**
     * 医保电子处方取药查询
     * @param entity
     * @return
     */
    @PostMapping("/pre/getMdMedicineInfo")
    public ResponseEntity<JSONObject> getMdMedicineInfo(@RequestBody RecipelInfoReview entity) {
//        JSONObject jsonObject = mdMedicationOrderService.queryPrescriptionDispensingResult_Ld7804(entity);
//        MdPreDrugData mdPreDrugData = JSONObject.parseObject(jsonObject.getString("data"), MdPreDrugData.class);
//        return ResponseEntity.ok(ResultUtil.successJson(mdPreDrugData));

        String json = "{\n" +
                "  \"hiRxno\": \"RX1234567890\",\n" +
                "  \"setlTime\": \"2025-03-25 12:00:00\",\n" +
                "  \"eltdelts\": [\n" +
                "    {\n" +
                "      \"medinsListCodg\": \"MLC1234567890\",\n" +
                "      \"drugGenname\": \"阿莫西林\",\n" +
                "      \"drugProdname\": \"阿莫西林胶囊\",\n" +
                "      \"drugDosform\": \"胶囊\",\n" +
                "      \"drugSpec\": \"250mg\",\n" +
                "      \"ent\": 100,\n" +
                "      \"aprvno\": \"H20100101\",\n" +
                "      \"bchno\": \"BCH12345678\",\n" +
                "      \"manuLotnum\": \"LOT12345678\",\n" +
                "      \"prdrName\": \"华北制药厂\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"medinsListCodg\": \"MLC0987654321\",\n" +
                "      \"drugGenname\": \"对乙酰氨基酚\",\n" +
                "      \"drugProdname\": \"扑尔敏片\",\n" +
                "      \"drugDosform\": \"片剂\",\n" +
                "      \"drugSpec\": \"500mg\",\n" +
                "      \"ent\": 200,\n" +
                "      \"aprvno\": \"H20100202\",\n" +
                "      \"bchno\": \"BCH98765432\",\n" +
                "      \"manuLotnum\": \"LOT98765432\",\n" +
                "      \"prdrName\": \"南方制药厂\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        MdPreDrugData mdPreDrugData = JSONObject.parseObject(json, MdPreDrugData.class);

        return ResponseEntity.ok(ResultUtil.successJson(mdPreDrugData));

    }

    /**
     * 医保审核查询
     * @param entity
     * @return
     */
    @PostMapping("/pre/getMdExamineInfo")
    public ResponseEntity<JSONObject> getMdExamineInfo(@RequestBody RecipelInfoReview entity) {
//        JSONObject jsonObject = mdMedicationOrderService.queryElectronicPrescriptionReviewResult_Ld7805(entity);
//        MdExamine mdExamine = JSONObject.parseObject(jsonObject.getString("data"), MdExamine.class);
        String json = "{\n" +
                "  \"hiRxno\": \"RX123456789\",\n" +
                "  \"pharName\": \"张药师\",\n" +
                "  \"pharCode\": \"P00123456789\",\n" +
                "  \"rxChkStasCodg\": \"001\",\n" +
                "  \"rxChkOpnn\": \"处方审核通过，建议患者继续服用。\",\n" +
                "  \"rxChkTime\": \"2025-03-25T10:15:30\"\n" +
                "}\n";
        MdExamine mdExamine =  JSONObject.parseObject(json, MdExamine.class);
        return ResponseEntity.ok(ResultUtil.successJson(mdExamine));
    }

    /**
     * 处方信息查询
     * @param entity
     * @return
     */
    @PostMapping("/pre/getMdRecInfo")
    public ResponseEntity<JSONObject> getMdPreInfo(@RequestBody RecipelInfoReview entity) {


        return ResponseEntity.ok(ResultUtil.successJson());
    }


    /**
     * 电子处方撤销
     * @param
     * @return
     */
    @PostMapping("/pre/revoke")
    public ResponseEntity<JSONObject> revokePre(@RequestBody RecipelInfoReview recipelInfoReview, @RequestParam String undoRea) {
         mdMedicationOrderService.cancelElectronicPrescription_Ld7104(recipelInfoReview,undoRea);
         recipelInfoReview.setReviewerId(SessionUtils.getUserJson().getString("id"));
         recipelInfoReview.setReviewStatus(2);
         recipelInfoReviewService.save(recipelInfoReview);
        return ResponseEntity.ok(ResultUtil.successJson("撤销成功！"));

    }





}