package com.geeke.outpatient.controller;


import com.alibaba.fastjson.JSONObject;
import com.geeke.common.data.Page;
import com.geeke.outpatient.entity.Blmb;
import com.geeke.outpatient.entity.Blmcxrc;
import com.geeke.outpatient.service.BlmbService;
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
@RequestMapping(value = "/outpatient/blmb")
public class BlmbController extends BaseController {

    @Autowired
    private BlmbService blmbService;

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody Blmb blmb){
        int result=blmbService.insert(blmb);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "getblmblist")
    public ResponseEntity<JSONObject> getblmblist(@RequestBody Blmcxrc blmcxrc){
        Page<Blmb> result=blmbService.getblmblist(blmcxrc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "deletembbm")
    public ResponseEntity<JSONObject> deletembbm(@RequestBody Blmcxrc blmcxrc){
        int result=blmbService.deletembbm(blmcxrc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "selectmbbm")
    public ResponseEntity<JSONObject> selectmbbm(@RequestBody Blmcxrc blmcxrc){
        List<Blmb> result=blmbService.selectmbbm(blmcxrc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "updatembbm")
    public ResponseEntity<JSONObject> updatembbm(@RequestBody Blmb blmb){
        int result=blmbService.updatembbm(blmb);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

}
