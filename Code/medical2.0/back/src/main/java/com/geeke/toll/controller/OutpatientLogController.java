package com.geeke.toll.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.data.Page;
import com.geeke.outpatient.entity.Patient;
import com.geeke.sys.controller.BaseController;
import com.geeke.toll.entity.*;
import com.geeke.toll.service.OutpatientLogService;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 门诊日志Controller
 *
 * @author zh
 * @version 2022-08-18
 */
@RestController
@RequestMapping(value = "/toll/outpatientLog")
public class OutpatientLogController extends BaseController {

    @Autowired
    private OutpatientLogService outpatientLogService;

    /**
     * id查询日志
     */
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getLogs(@PathVariable("id") String id) {
        OutpatientLog entity = outpatientLogService.getLogs(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    /**
     * 查询所有日志
     */
    @PostMapping(value = "list")
    public ResponseEntity<JSONObject> listLog(@RequestBody OutpatientLogRc outpatientLogRc) {
        Page<OutpatientLog> result = outpatientLogService.listLog(outpatientLogRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 修改日志信息
     */
    @PutMapping(value = "update")
    public int updateLog(@RequestBody OutpatientLog outpatientLogRc) {
        return outpatientLogService.updateLog(outpatientLogRc);

    }

    /**
     * 查询贫困户
     */
    @PostMapping(value = "getPoverty/{patientId}")
    public ResponseEntity<JSONObject> getPoverty(@PathVariable("patientId") String patientId) {
        List<Patient> poverty = outpatientLogService.getPoverty(patientId);
        return ResponseEntity.ok(ResultUtil.successJson(poverty));
    }

    /**
     * 查询所有日志
     */
    @PostMapping(value = "exportExcel")
    public void exportExcel(@RequestBody OutpatientLogRc outpatientLogRc, HttpServletResponse response) {
        try {
            outpatientLogService.exportExcel(outpatientLogRc,response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }


}