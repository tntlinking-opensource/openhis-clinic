package com.geeke.outpatient.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.User;
import com.geeke.admin.service.UserService;
import com.geeke.common.constants.BizConstants;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.common.service.ServiceException;
import com.geeke.medicareutils.config.MedicareConfigProperties;
import com.geeke.medicareutils.domain.respo.Output_2201;
import com.geeke.medicareutils.service.MdPsnDataService;
import com.geeke.medicareutils.service.MdRegistrationService;
import com.geeke.outpatient.entity.*;
import com.geeke.common.data.Parameter;
import com.geeke.outpatient.service.RecipelDetailService;
import com.geeke.outpatient.service.RegistrationService;
import com.geeke.toll.service.TollInfoService;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 挂号信息Controller
 * @author txl
 * @version 2022-06-15
 */
@RestController
@RequestMapping(value = "/outpatient/registration")
public class RegistrationController extends CrudController<RegistrationService, Registration> {

    @Autowired
    protected RegistrationService registrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private TollInfoService tollInfoService;

    @Autowired
    private RecipelDetailService recipelDetailService;

    @Autowired
    private MdRegistrationService mdRegistrationService;

    @Autowired
    private MdPsnDataService mdPsnDataService;

    @Autowired
    private MedicareConfigProperties medicareConfigProperties;

    @Override
    protected RegistrationService getService() {
        return registrationService;
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
        List<User> doctors = userService.getUserByCompanyIdAndJobV2(SessionUtils.getLoginTenantId(), "医生");
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
        if (BizConstants.REG_STATUS_REFUNDED.equals(refundRegistrationRemarks)) {
            Registration registration = registrationService.get(id);
            if (BizConstants.REG_STATUS_PENDING.equals(registration.getStatus().getValue())) {
                String message = "你已完成签到，如要退号请到医院进行！";
                return ResponseEntity.ok(ResultUtil.successJson(message));
            }
            if (BizConstants.REG_STATUS_VISITED.equals(registration.getStatus().getValue())) {
                String message = "你已完成就诊，如有疑问请到医院了解详情！";
                return ResponseEntity.ok(ResultUtil.successJson(message));
            }
            if (BizConstants.REG_STATUS_REFUNDED.equals(registration.getStatus().getValue())) {
                String message = "你已取消预约，不可再次取消！";
                return ResponseEntity.ok(ResultUtil.successJson(message));
            }
            if (BizConstants.REG_STATUS_FEE_REFUNDED.equals(registration.getStatus().getValue())) {
                String message = "该预约已失效！";
                return ResponseEntity.ok(ResultUtil.successJson(message));
            }
            String remarks = "";
            res = registrationService.refundRegistrationPay(id, status, refundRegistrationPayType, remarks, exitNumberDate);
        } else {
            res = registrationService.refundRegistrationPay(id, status, refundRegistrationPayType, refundRegistrationRemarks, exitNumberDate);
        }
        if ("true".equals(medicareConfigProperties.getCheck())){
            //开启医保退号
            Registration registration = registrationService.get(id);
            mdRegistrationService.revokeRegistrationInfo_2202(registration);
        }
        return ResponseEntity.ok(ResultUtil.successJson(res));
    }

    @PostMapping("/wxReturnPay")
    public ResponseEntity<JSONObject> wxReturnPay(@RequestBody Registration registration) {
        int res;
        Date exitNumberDate = new Date();
        Registration registrations = registrationService.get(registration.getId());
        if (BizConstants.REG_STATUS_PENDING.equals(registrations.getStatus().getValue())) {
            String message = "你已完成签到，如要退号请到医院进行！";
            throw new ServiceException(message);
        }
        if (BizConstants.REG_STATUS_VISITED.equals(registrations.getStatus().getValue())) {
            String message = "你已完成就诊，如有疑问请到医院了解详情！";
            throw new ServiceException(message);
        }
        if (BizConstants.REG_STATUS_REFUNDED.equals(registrations.getStatus().getValue())) {
            String message = "你已取消预约，不可再次取消！";
            throw new ServiceException(message);
        }
        if (BizConstants.REG_STATUS_FEE_REFUNDED.equals(registrations.getStatus().getValue())) {
            String message = "该预约已失效！";
            throw new ServiceException(message);
        }
        registrations.setExitNumberDate(exitNumberDate);
        registrations.setStatus(registration.getStatus());
        registrations.setRefundRegistrationRemarks(registration.getRefundRegistrationRemarks());
        registrations.setRefundRegistrationPayType(registration.getRefundRegistrationPayType());
        res = registrationService.wxReturnPay(registrations);

        return ResponseEntity.ok(ResultUtil.successJson(res));
    }

    @Override
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<Registration> result = registrationService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        registrationService.updateExpiredStatusFromPage(result, row -> row.getCompany().getId());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @Override
    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody Registration entity) {
        String id ="";
        if(Objects.equals(entity.getCreateBy(),"微信")){
            id = registrationService.wxSave(entity).getId();
        }else {
            //开启医保接口
            if("true".equals(medicareConfigProperties.getCheck())){
                //保存用户医保信息
                if (mdPsnDataService.getAndSetPsnData(entity)) {
                    //医保挂号
                    String registrationInfo = mdRegistrationService.getRegistrationInfo_2201(entity);
                    Output_2201 output_2201 = JSONObject.parseObject(registrationInfo, Output_2201.class);
                    entity.setMdtrtId(output_2201.getData().getMdtrt_id());
                    entity.setIptOtpNo(output_2201.getData().getIpt_otp_no());
                }
            }
            id = registrationService.save(entity).getId();
        }

        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "registationupdate")
    public ResponseEntity<JSONObject> registationupdate(@RequestBody RegistrationMedicalrecordlist entitys) {
        entitys.getRegistration().setUpdateDate(new Date());
        int row = registrationService.registrationupdate(entitys.getRegistration());
        if(entitys.getMedicalRecord()!=null) {
            if (entitys.getMedicalRecord().getPatientTell() != null && entitys.getMedicalRecord().getDoctor() != null) {
                entitys.getMedicalRecord().setRegistration(entitys.getRegistration());
                row = registrationService.medicalRecordInserts(entitys.getMedicalRecord());
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(row));
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
            RecipelDetail recipelDetail = recipelDetailService.get(id);
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
        registrationService.updateExpiredStatusFromPage(result, row -> row.getCompany().getId());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    //获取费用信息
    @PostMapping("/wx/list")
    public ResponseEntity<JSONObject> wxListPages(@RequestBody PageRegistration pageRegistration) {
        Page<ReceptionEvt> result = registrationService.wxListPages(pageRegistration);
        registrationService.updateExpiredStatusFromPage(result, row -> row.getRegistration().getCompany().getId());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    //获取发药记录
    @PostMapping("/wx/dispensingList")
    public ResponseEntity<JSONObject> wxDispensingListPages(@RequestBody PageRegistration pageRegistration) {
        Page<ReceptionEvt> result = registrationService.wxDispensingListPages(pageRegistration);
        registrationService.updateExpiredStatusFromPage(result, row -> row.getRegistration().getCompany().getId());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    //微信端通过openid获取就诊信息
    @PostMapping("getRegistrationByOpenId")
    public ResponseEntity<JSONObject> getRegistrationByOpenId(@RequestBody SearchParams searchParams) {
        Page<Registration> registrationByOpenId = registrationService.getRegistrationByOpenId(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(registrationByOpenId));
    }
}
