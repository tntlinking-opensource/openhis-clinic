package com.geeke.outpatient.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.outpatient.entity.*;
import com.geeke.outpatient.service.SchedulingService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 排班Controller
 * @author txl
 * @version 2022-06-13
 */
@RestController
@RequestMapping(value = "/outpatient/scheduling")
public class SchedulingController extends BaseController {

    @Autowired
    private SchedulingService schedulingService;

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SchedulingDTO strEntity) {
        //SchedulingDTO scheduling = strEntity;//JSONObject.parseObject(strEntity, SchedulingDTO.class);
        List<SchedulingDTO> result = schedulingService.getHisuserpblist(strEntity);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listzl")
    public ResponseEntity<JSONObject> listzl(@RequestBody SchedulingDTO strEntity) {
        //SchedulingDTO scheduling = strEntity;//JSONObject.parseObject(strEntity, SchedulingDTO.class);
        List<SchedulingDTO> result = schedulingService.getlistzl(strEntity);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listzlrday")
    public ResponseEntity<JSONObject> listzlrday(@RequestBody SchedulingDTO strEntity) {
        List<SchedulingDTO> result = schedulingService.listzlr(strEntity);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listmx")
    public ResponseEntity<JSONObject> listmx(@RequestBody SchedulingDTO strEntity) {
        //SchedulingDTO scheduling = strEntity;//JSONObject.parseObject(strEntity, SchedulingDTO.class);
        List<SchedulingmxDTO> result = schedulingService.getlistmx(strEntity);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody Scheduling entity) {
        entity.setIsLocked("0");
        int countlist= schedulingService.listcount(entity);
        String refdata=null;
        if (countlist>0){
            return ResponseEntity.ok(ResultUtil.successJson(refdata));
        }else {
            refdata = schedulingService.save(entity).getId();
        }
        return ResponseEntity.ok(ResultUtil.successJson(refdata));
    }
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject>delete(@RequestBody Scheduling entity){
        Date cs= entity.getSchedulingtime();
//        String data = "2021-02-24 24:21:00";
//        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
//        Date date = sdf.format (cs);

        int refdata = schedulingService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(refdata));
    }

}
