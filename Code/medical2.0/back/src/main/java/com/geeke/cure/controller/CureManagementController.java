package com.geeke.cure.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.cure.entity.CureManagement;
import com.geeke.cure.service.CureManagementService;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.service.RecipelDetailService;
import com.geeke.outpatient.service.RecipelInfoService;
import com.geeke.stock.entity.Drug;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cure/cureManagement")
public class CureManagementController {
    @Autowired
    private CureManagementService cureManagementService;
    @Autowired
    private RecipelDetailService recipelDetailService;
    @Autowired
    private RecipelInfoService recipelInfoService;
    @PostMapping("save")
    public ResponseEntity<JSONObject> save(@RequestBody CureManagement entity){
        System.out.println(entity.getId());
        String crueId=entity.getId();
        String id="";
        if("3".equals(entity.getRecipelDetail().getStuffType())){
            id = cureManagementService.save(entity).getId();
        }else {
            if(null==entity.getId()||entity.getId().isEmpty()){
                entity.setRecipelInfoId(entity.getRecipelDetail().getRecipelInfo().getId());
                entity.setInfuseGroup(entity.getRecipelDetail().getInfuseGroup());
            }
            id = cureManagementService.save(entity).getId();
        }
        if(id!=null){
            recipelDetailService.updateById(entity.getRecipelDetail().getExecutions(),entity.getRecipelDetail().getId(),entity.getRecipelDetail().getStuffType(),entity.getRecipelDetail().getInfuseType(),crueId);
        }else {
            recipelDetailService.updateById(entity.getRecipelDetail().getExecutions(),entity.getRecipelDetail().getId(),entity.getRecipelDetail().getStuffType(),entity.getRecipelDetail().getInfuseType(),crueId);
        }

        final String prescriptionId = entity.getRecipelDetail().getRecipelInfo().getId();
        int schedule = recipelDetailService.schedule(prescriptionId);
        if (schedule == 0){
            int cureType = 1;
            recipelInfoService.updateSchedule(prescriptionId,cureType);
        }

        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<CureManagement> result = cureManagementService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
}
