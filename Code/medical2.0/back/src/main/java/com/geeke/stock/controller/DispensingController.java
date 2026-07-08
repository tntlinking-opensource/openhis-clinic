package com.geeke.stock.controller;

import com.geeke.stock.entity.DispensingReportEvt;
import com.geeke.stock.entity.DispensingReportTotalEvt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.data.Page;
import com.geeke.stock.entity.Dispensing;
import com.geeke.stock.service.DispensingService;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.utils.ResultUtil;

/**
 * 发药明细Controller
 * @author txl
 * @version 2022-08-11
 */
@RestController
@RequestMapping(value = "/stock/dispensing")
public class DispensingController extends CrudController<DispensingService, Dispensing> {

	@Autowired
	protected DispensingService dispensingService;

	@Override
	protected DispensingService getService() {
		return dispensingService;
	}

    /**
     * 发药报表
     * @param searchParams
     * @return
     */
    @PostMapping(value = "reportList")
    public ResponseEntity<JSONObject> reportList(@RequestBody SearchParams searchParams) {
        Page<DispensingReportEvt> result = dispensingService.reportList(searchParams);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "getOrganizationList")
    public ResponseEntity<JSONObject> getOrganizationList(@RequestBody SearchParams searchParams) {
        Page<DispensingReportEvt> result = dispensingService.getOrganizationList(searchParams);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 发药报表
     * @param searchParams
     * @return
     */
    @PostMapping(value = "reportAmount")
    public ResponseEntity<JSONObject> reportAmount(@RequestBody SearchParams searchParams) {
        DispensingReportTotalEvt dispensingReportTotalEvt = dispensingService.reportAmount(searchParams);
        return ResponseEntity.ok(ResultUtil.successJson(dispensingReportTotalEvt));
    }

}
