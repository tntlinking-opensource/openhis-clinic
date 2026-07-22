package com.geeke.stock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.geeke.stock.entity.MedicinalStockControl;
import com.geeke.stock.service.MedicinalStockControlService;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.utils.ResultUtil;


/**
 * 药品/材料库存总控制Controller
 * @author hl
 * @version 2022-09-26
 */
@RestController
@RequestMapping(value = "/stock/medicinalStockControl")
public class MedicinalStockControlController extends CrudController<MedicinalStockControlService, MedicinalStockControl> {

	@Autowired
	protected MedicinalStockControlService medicinalStockControlService;

	@Override
	protected MedicinalStockControlService getService() {
		return medicinalStockControlService;
	}

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<MedicinalStockControl> result = null;
        if("false".equals(searchParams.getIsPre()) || "0".equals(searchParams.getIsPre()) || searchParams.getIsPre() == null) {
           result = medicinalStockControlService.listAllByCompany(searchParams.getParams(), searchParams.getOrderby());
        }else{
         result =    medicinalStockControlService.listPreAll(searchParams.getParams(), searchParams.getOrderby());
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

}
