package com.geeke.cure.controller;


import com.alibaba.fastjson.JSONObject;
import com.geeke.common.data.Page;
import com.geeke.cure.entity.Stmanagement;
import com.geeke.cure.entity.Stparameter;
import com.geeke.cure.service.StmanagementService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/cure/stmanagement")
public class StmanagementController extends BaseController {

    @Autowired
    private StmanagementService service;


    @PostMapping(value = "stmanagementlist")
    public ResponseEntity<JSONObject> stmanagementlist(@RequestBody Stparameter stparameter){
        Page<Stmanagement> result=service.stmanagementlist(stparameter);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "updatestresult")
    public ResponseEntity<JSONObject> updatestresult(@RequestBody Stparameter stparameter){
        int result=service.updatestresult(stparameter);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "updatesttime")
    public ResponseEntity<JSONObject> updatesttime(@RequestBody Stparameter stparameter){
        int result=service.updatesttime(stparameter);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "selectdetailid")
    public ResponseEntity<JSONObject> selectdetailid(@RequestBody Stparameter stparameter){
        List<Stmanagement> result=service.selectdetailid(stparameter);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
}
