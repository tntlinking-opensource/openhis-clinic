package com.geeke.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.CrudController;
import com.geeke.stock.entity.SupplierOutboundDetail;
import com.geeke.stock.service.SupplierOutboundDetailService;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 出库单Controller
 *
 * @author txl
 * @version 2023-07-14/
 */

@RestController
@RequestMapping(value = "/stock/supplierOutboundDetail")
public class SupplierOutboundDetailController extends CrudController<SupplierOutboundDetailService, SupplierOutboundDetail> {

    @Autowired
    protected SupplierOutboundDetailService supplierOutboundDetailService;

    @Override
    protected SupplierOutboundDetailService getService() {
        return supplierOutboundDetailService;
    }

    @GetMapping("/listByBoutbound/{outboundId}")
    public ResponseEntity<JSONObject> listByBoutbound(@PathVariable("outboundId")String outboundId) {
        List<SupplierOutboundDetail> result = supplierOutboundDetailService.getByOutboundId(outboundId);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
}
