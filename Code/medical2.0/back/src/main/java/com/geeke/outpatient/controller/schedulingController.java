package com.geeke.outpatient.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.outpatient.entity.*;
import com.geeke.outpatient.service.schedulingService;
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
public class schedulingController extends BaseController {

    @Autowired
    private schedulingService schedulingService;

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody schedulingDTO strEntity) {
        //schedulingDTO scheduling = strEntity;//JSONObject.parseObject(strEntity, schedulingDTO.class);
        List<schedulingDTO> result = schedulingService.getHisuserpblist(strEntity);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listzl")
    public ResponseEntity<JSONObject> listzl(@RequestBody schedulingDTO strEntity) {
        //schedulingDTO scheduling = strEntity;//JSONObject.parseObject(strEntity, schedulingDTO.class);
        List<schedulingDTO> result = schedulingService.getlistzl(strEntity);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listzlrday")
    public ResponseEntity<JSONObject> listzlrday(@RequestBody schedulingDTO strEntity) {
        List<schedulingDTO> result = schedulingService.listzlr(strEntity);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listmx")
    public ResponseEntity<JSONObject> listmx(@RequestBody schedulingDTO strEntity) {
        //schedulingDTO scheduling = strEntity;//JSONObject.parseObject(strEntity, schedulingDTO.class);
        List<schedulingmxDTO> result = schedulingService.getlistmx(strEntity);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody scheduling entity) {
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
    public ResponseEntity<JSONObject>delete(@RequestBody scheduling entity){
        Date cs= entity.getSchedulingtime();
//        String data = "2021-02-24 24:21:00";
//        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
//        Date date = sdf.format (cs);

        int refdata = schedulingService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(refdata));
    }

}
