package com.geeke.workbench.controller;


import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.member.entity.MemberManagement;
import com.geeke.member.service.MemberManagementService;
import com.geeke.outpatient.entity.PageRegistration;
import com.geeke.outpatient.entity.ReceptionEvt;
import com.geeke.outpatient.entity.Registration;
import com.geeke.outpatient.service.MedicalRecordService;
import com.geeke.outpatient.service.RegistrationService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.workbench.entity.VisitProgress;
import com.geeke.workbench.entity.Visitprogresspara;
import com.geeke.workbench.service.SchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 工作台
 */
@RestController
@RequestMapping(value = "/workbench/Schedules")
public class SchedulesController extends BaseController {
    @Autowired
    private SchedulesService schedulesService;

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private MedicalRecordService medicalRecordService;
    @Autowired
    private MemberManagementService memberManagementService;
    /**
     * 获取待就诊信息
     * @param searchParams
     * @return
     */
    @PostMapping(value = {"tobeseelist", ""})
    public ResponseEntity<JSONObject> schedules(@RequestBody SearchParams searchParams) {
        Page<Registration> result = registrationService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 待发药
     * @param pageRegistration
     * @return
     */
    @PostMapping(value = "/dispensing")
    public ResponseEntity<JSONObject> allNewQuery(@RequestBody PageRegistration pageRegistration,@RequestParam(name = "recipelInfoIds",required=false) String strRecipelInfoIds) {
        Page<Registration> result = registrationService.listPages(pageRegistration);
        ArrayList<ReceptionEvt> registrations=new ArrayList<ReceptionEvt>();
        if(!Objects.isNull(result.getRows())){
            for (Registration item:result.getRows()) {
                String[] recipelInfoIds = StringUtils.commaDelimitedListToStringArray(strRecipelInfoIds);
                registrations.add(medicalRecordService.allNewQuery(item.getId(), recipelInfoIds));
            }
        }
        Page<ReceptionEvt>  data= new Page((long)result.getTotal(), registrations);
        return ResponseEntity.ok(ResultUtil.successJson(data));
    }

    @PostMapping(value = "/patidwsflist")
    public ResponseEntity<JSONObject> patidwsflist(@RequestBody PageRegistration pageRegistration) {
        Page<Registration> result = registrationService.listPages(pageRegistration);
        ArrayList<List<MemberManagement>> memberManagements=new ArrayList<>();
        for (Registration item:result.getRows()) {
            memberManagements.add(memberManagementService.getByPatientId(item.getId()));
        }
        return ResponseEntity.ok(ResultUtil.successJson(memberManagements));
    }

    /**
     * 通过ID进行修改是否忽略状态
     * @param strRecipelInfoIds
     * @return
     */
    @GetMapping("/{strRecipelInfoIds}")
    public ResponseEntity<JSONObject> updateoverlookid(@PathVariable("strRecipelInfoIds") String strRecipelInfoIds){
        int data=schedulesService.updateoverlookid(strRecipelInfoIds);
        return ResponseEntity.ok(ResultUtil.successJson(data));
    }

//    @PostMapping("/updateoverlookidlist")
//    public ResponseEntity<JSONObject> updateoverlookidlist(@RequestBody List<String> idlist){
//        for(String item:idlist){
//            schedulesService.updateoverlookid(item);
//        }
//        return ResponseEntity.ok(ResultUtil.successJson(""));
//    }
        @PostMapping("/updateoverlookidlist")
    public ResponseEntity<JSONObject> updateoverlookidlist(@RequestBody PageRegistration pageRegistration){
        if(pageRegistration.getStatus().equals("0")){
            List<String> result=schedulesService.listPagesjyjc(pageRegistration);
            for(String item:result){
                schedulesService.updateoverlookid(item);
            }
        }else{
            List<String> result = schedulesService.listPages(pageRegistration);
            for(String item:result){
                schedulesService.updateoverlookid(item);
            }
        }

        return ResponseEntity.ok(ResultUtil.successJson(""));
    }


//    @PostMapping("/schedulelists")
//    public ResponseEntity<JSONObject> visitprogresslist(@RequestBody Visitprogresspara visitprogresspara){
//        Page<VisitProgress> result=schedulesService.visitProgressPage(visitprogresspara);
//        return ResponseEntity.ok(ResultUtil.successJson(result));
//    }

    @PostMapping("/schedulelists")
    public ResponseEntity<JSONObject> visitprogresslist(@RequestBody Visitprogresspara visitprogresspara){
        Page<VisitProgress> result=schedulesService.visitProgressPage(visitprogresspara);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

}
