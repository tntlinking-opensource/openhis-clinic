package com.geeke.outpatient.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.User;
import com.geeke.admin.service.UserService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.outpatient.entity.*;
import com.geeke.common.data.Parameter;
import com.geeke.outpatient.service.RecipelDetailService;
import com.geeke.outpatient.service.RegistrationService;
import com.geeke.outpatient.service.ReportFileService;
import com.geeke.sys.controller.BaseController;
import com.geeke.toll.service.TollInfoService;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.logging.Handler;

/**
 * 挂号信息Controller
 * @author txl
 * @version 2022-06-15
 */
@RestController
@RequestMapping(value = "/outpatient/registration")
public class RegistrationController extends BaseController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private TollInfoService tollInfoService;

    @Autowired
    private RecipelDetailService recipelDetailService;

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        Registration entity = registrationService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    /**
     * 查询诊所的医生
     * @return
     */
    @GetMapping("/doctor")
    public ResponseEntity<JSONObject> getDocByCompanyId() {
        List<User> doctors = userService.getUserByCompanyIdAndJob(SessionUtils.getLoginTenantId(), "医生");
        return ResponseEntity.ok(ResultUtil.successJson(doctors));
    }

    @GetMapping("/doctornew")
    public ResponseEntity<JSONObject> doctornew() {
        List<User> doctors = userService.NEWgetUserByCompanyIdAndJob(SessionUtils.getLoginTenantId(), "医生");
        return ResponseEntity.ok(ResultUtil.successJson(doctors));
    }

    @PostMapping("/wxDoctor")
    public ResponseEntity<JSONObject> getWxDocByCompanyId(@RequestBody User user) {
        List<User> doctors = userService.getUserByWxCompanyIdAndJob(user.getCompany().getId(), user.getOffice(), "医生");
        return ResponseEntity.ok(ResultUtil.successJson(doctors));
    }


    @GetMapping("/status")
    public ResponseEntity<JSONObject> updateStatus(@RequestParam("id") String id, @RequestParam("status") String status, @RequestParam("departmentId") String departmentId, @RequestParam("doctorId") String doctorId) {
        int res = registrationService.updateStatus(id, status, departmentId, doctorId);
        return ResponseEntity.ok(ResultUtil.successJson(res));
    }

    @GetMapping("/refundRegistrationPay")
    public ResponseEntity<JSONObject> refundRegistrationPay(String id, String status,
                                                            String refundRegistrationPayType, String refundRegistrationRemarks
    ) {
        int res;
        Date exitNumberDate = new Date();
        if ("registrationStatus_2".equals(refundRegistrationRemarks)) {
            Registration registration = registrationService.get(id);
            if ("registrationStatus_0".equals(registration.getStatus().getValue())) {
                String message = "你已完成签到，如要退号请到医院进行！";
                return ResponseEntity.ok(ResultUtil.successJson(message));
            }
            if ("registrationStatus_1".equals(registration.getStatus().getValue())) {
                String message = "你已完成就诊，如有疑问请到医院了解详情！";
                return ResponseEntity.ok(ResultUtil.successJson(message));
            }
            if ("registrationStatus_2".equals(registration.getStatus().getValue())) {
                String message = "你已取消预约，不可再次取消！";
                return ResponseEntity.ok(ResultUtil.successJson(message));
            }
            if ("registrationStatus_4".equals(registration.getStatus().getValue())) {
                String message = "该预约已失效！";
                return ResponseEntity.ok(ResultUtil.successJson(message));
            }
            String remarks = "";
            res = registrationService.refundRegistrationPay(id, status, refundRegistrationPayType, remarks, exitNumberDate);
        } else {
            res = registrationService.refundRegistrationPay(id, status, refundRegistrationPayType, refundRegistrationRemarks, exitNumberDate);
        }
        return ResponseEntity.ok(ResultUtil.successJson(res));
    }

    @PostMapping("/wxReturnPay")
    public ResponseEntity<JSONObject> wxReturnPay(@RequestBody Registration registration) {
        int res;
        Date exitNumberDate = new Date();
        Registration registrations = registrationService.get(registration.getId());
        if ("registrationStatus_0".equals(registrations.getStatus().getValue())) {
            String message = "你已完成签到，如要退号请到医院进行！";
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, message));
        }
        if ("registrationStatus_1".equals(registrations.getStatus().getValue())) {
            String message = "你已完成就诊，如有疑问请到医院了解详情！";
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, message));
        }
        if ("registrationStatus_2".equals(registrations.getStatus().getValue())) {
            String message = "你已取消预约，不可再次取消！";
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, message));
        }
        if ("registrationStatus_4".equals(registrations.getStatus().getValue())) {
            String message = "该预约已失效！";
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, message));
        }
        registrations.setExitNumberDate(exitNumberDate);
        registrations.setStatus(registration.getStatus());
        registrations.setRefundRegistrationRemarks(registration.getRefundRegistrationRemarks());
        registrations.setRefundRegistrationPayType(registration.getRefundRegistrationPayType());
        res = registrationService.wxReturnPay(registrations);

        return ResponseEntity.ok(ResultUtil.successJson(res));
    }

    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {

        Page<Registration> result = registrationService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        String companyId = null;
        if (result.getTotal() != 0) {
            if (result.getRows().size() != 0) {

                for (Registration row : result.getRows()) {
                    companyId = row.getCompany().getId();
                    break;
                }

                registrationService.updateStatusByCompanyId(companyId);
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<Registration> result = registrationService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody Registration entity) {
        System.out.println(entity);

        String id ="";
        if(Objects.equals(entity.getCreateBy(),"微信")){
            id = registrationService.wxSave(entity).getId();
        }else {
            id = registrationService.save(entity).getId();
        }

        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody Registration entity) {
        int rows = registrationService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<Registration> entitys) {
        List<String> ids = registrationService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<Registration> entitys) {
        List<String> ids = registrationService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    @PostMapping(value = "registationupdate")
    public ResponseEntity<JSONObject> registationupdate(@RequestBody RegistrationMedicalrecordlist entitys) {
        entitys.getRegistration().setUpdateDate(new Date());
        int row = registrationService.registrationupdate(entitys.getRegistration());
        if(entitys.getMedicalRecord()!=null) {
            if (entitys.getMedicalRecord().getPatientTell() != null && entitys.getMedicalRecord().getDoctor() != null) {
                entitys.getMedicalRecord().setRegistration(entitys.getRegistration());
                row = registrationService.medicalrecordinserts(entitys.getMedicalRecord());
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(row));
    }

    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<Registration> entitys) {
        int rows = registrationService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = {"conditionList", ""})
    public ResponseEntity<JSONObject> conditionList(@RequestBody SearchParams searchParams) {
        List<Parameter> params = searchParams.getParams();
        Page<Registration> result;
        if (null != params && 0 != params.size()) {
            result = registrationService.listConditionPage(searchParams.getParams().get(0).getColumnName(), searchParams.getOffset(), searchParams.getLimit());
        } else {
            result = registrationService.listPage(params, searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    //打印处方
    @PostMapping("/print")
    public ResponseEntity<JSONObject> print(@RequestBody JSONObject inJson) {
        String id = inJson.getString("id");
        if (StringUtils.isNotBlank(id)) {
            RecipelDetail recipelDetail = recipelDetailService.get("1020982806516041555");
            Map<String, Object> map = new HashMap<>();
            map.put("buildReport", recipelDetail);
            DataBean dataBean = new DataBean();
            dataBean.buildReport("", "", map);
        }
        return ResponseEntity.ok(ResultUtil.successJson());
    }

    //获取患者登记信息
    @PostMapping("/v2/list")
    public ResponseEntity<JSONObject> listPages(@RequestBody PageRegistration pageRegistration) {

        Page<Registration> result = registrationService.listPages(pageRegistration);
        String companyId = null;
        if (result.getTotal() != 0) {
            if (!CollectionUtils.isEmpty(result.getRows())) {

                for (Registration row : result.getRows()) {
                    companyId = row.getCompany().getId();
                    break;
                }

                registrationService.updateStatusByCompanyId(companyId);
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    //获取费用信息
    @PostMapping("/wx/list")
    public ResponseEntity<JSONObject> wxListPages(@RequestBody PageRegistration pageRegistration) {

        Page<ReceptionEvt> result = registrationService.wxListPages(pageRegistration);
        String companyId = null;
        if (result.getTotal() != 0) {
            if (!CollectionUtils.isEmpty(result.getRows())) {

                for (ReceptionEvt row : result.getRows()) {
                    companyId = row.getRegistration().getCompany().getId();
                    break;
                }

                registrationService.updateStatusByCompanyId(companyId);
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    //获取发药记录
    @PostMapping("/wx/dispensingList")
    public ResponseEntity<JSONObject> wxDispensingListPages(@RequestBody PageRegistration pageRegistration) {

        Page<ReceptionEvt> result = registrationService.wxDispensingListPages(pageRegistration);
        String companyId = null;
        if (result.getTotal() != 0) {
            if (!CollectionUtils.isEmpty(result.getRows())) {

                for (ReceptionEvt row : result.getRows()) {
                    companyId = row.getRegistration().getCompany().getId();
                    break;
                }

                registrationService.updateStatusByCompanyId(companyId);
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    //微信端通过openid获取就诊信息
    @PostMapping("getRegistrationByOpenId")
    public ResponseEntity<JSONObject> getRegistrationByOpenId(@RequestBody SearchParams searchParams) {
        Page<Registration> registrationByOpenId = registrationService.getRegistrationByOpenId(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(registrationByOpenId));
    }
}