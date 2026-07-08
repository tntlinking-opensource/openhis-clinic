package com.geeke.outpatient.controller;

import java.util.List;

import com.geeke.outpatient.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.service.RecipelDetailService;
import com.geeke.common.controller.CrudController;
import com.geeke.utils.ResultUtil;

/**
 * 处方详情Controller
 * @author txl
 * @version 2022-06-07
 */
@RestController
@RequestMapping(value = "/outpatient/recipelDetail")
public class RecipelDetailController extends CrudController<RecipelDetailService, RecipelDetail> {

	@Autowired
	private RecipelDetailService recipelDetailService;

	@Override
	protected RecipelDetailService getService() {
		return recipelDetailService;
	}

	@Autowired
	private MedicalRecordService medicalRecordService;

	@GetMapping(value = "/getByRecipelInfoId/{recipelInfoId}")
	public ResponseEntity<JSONObject> getByRecipelInfoId(@PathVariable("recipelInfoId") String recipelInfoId){
		List<RecipelDetail> byRecipelInfoId = recipelDetailService.getByRecipelInfoId(recipelInfoId);
		for (RecipelDetail recipelDetail:
		byRecipelInfoId) {
			{
				recipelDetail.setDrugStuffId(medicalRecordService.getDrugStuffEvt(recipelDetail));
			}
		}
		return ResponseEntity.ok(ResultUtil.successJson(byRecipelInfoId));
	}
}
