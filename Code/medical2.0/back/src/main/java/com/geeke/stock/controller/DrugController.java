package com.geeke.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.service.DrugService;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;




/**
 * 药品信息Controller
 * @author txl
 * @version 2022-06-07
 */
@RestController
@RequestMapping(value = "/stock/drug")
public class DrugController extends CrudController<DrugService, Drug> {

    @Autowired
    protected DrugService drugService;

    @Override
    protected DrugService getService() {
        return drugService;
    }

    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<Drug> result = drugService.listPages(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        if (result.getRows() != null) {
            //循环设置单位，前端要控制可以拆零销售的药品单位显示零售单位，不允许拆零的显示包装单位
            result.getRows().forEach(x -> {
                if (x.getIsUnpackSell().equals("1")) {
                    x.setStockUnit(x.getPreparationUnit().getName());
                } else {
                    x.setStockUnit(x.getPack().getName());
                }
            });
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }


    /**
     * 只查询租户的药品
     *
     * @param searchParams
     * @return
     */
    @PostMapping(value = {"listByInstitution"})
    public ResponseEntity<JSONObject> listByInstitution(@RequestBody SearchParams searchParams) {
        Page<Drug> result = drugService.listByInstitution(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        if (result.getRows() != null) {
            //循环设置单位，前端要控制可以拆零销售的药品单位显示零售单位，不允许拆零的显示包装单位
            result.getRows().forEach(x -> {
                if (x.getIsUnpackSell().equals("1")) {
                    x.setStockUnit(x.getPreparationUnit().getName());
                } else {
                    x.setStockUnit(x.getPack().getName());
                }
            });
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 只查询本公司的药品
     *
     * @param searchParams
     * @return
     */
    @PostMapping(value = {"listByCompany"})
    public ResponseEntity<JSONObject> listByCompany(@RequestBody SearchParams searchParams) {
        Page<Drug> result = drugService.listByCompany(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        if (result.getRows() != null) {
            //循环设置单位，前端要控制可以拆零销售的药品单位显示零售单位，不允许拆零的显示包装单位
            result.getRows().forEach(x -> {
                if (x.getIsUnpackSell().equals("1")) {
                    x.setStockUnit(x.getPreparationUnit().getName());
                } else {
                    x.setStockUnit(x.getPack().getName());
                }
            });
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }


    /**
     * 只查询自己诊所下的药品
     * @param searchParams
     * @return
     */
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<Drug> result = drugService.listAllDrug(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listAllStock")
    public ResponseEntity<JSONObject> listAllStock(@RequestBody SearchParams searchParams) {
        List<Drug> result = drugService.listAllStock(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listAllStock2")
    public ResponseEntity<JSONObject> listAllStock2(@RequestBody SearchParams searchParams) {
        List<Drug> result = drugService.listAllStock2(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody Drug entity) {
        String id = null;
        try {
            id = drugService.save(entity).getId();
        } catch (Exception e) {
            return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_50001, "药品信息重复"));
        }
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "updateAllIndate")
    public ResponseEntity<JSONObject> updateAllIndate(@RequestBody Drug drug) {
        int i = drugService.updateAllIndate(drug.getIndate(), drug.getCompany().getId());
        return ResponseEntity.ok(ResultUtil.successJson(i));
    }

    @PostMapping(value = "updateAllInventory")
    public ResponseEntity<JSONObject> updateAllInventory(@RequestBody Drug drug) {
        int i = drugService.updateAllInventory(drug.getInventoryFloor(), drug.getCompany().getId());
        return ResponseEntity.ok(ResultUtil.successJson(i));
    }

    @PostMapping(value = "inventory")
    public ResponseEntity<JSONObject> inventory(@RequestBody SearchParams searchParams) {
        List<Drug> result = drugService.inventory(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping("/uploadExcel")
    @ApiOperation("excel")
    public ResponseEntity taskUploadExcel(@RequestParam(value = "file") MultipartFile file) {
        try {
            List<String> error = drugService.excel(file);
            return ResponseEntity.ok(ResultUtil.successJson(error));
        } catch (Exception e) {
            return ResponseEntity.ok(ResultUtil.warningJson(ErrorEnum.E_50001, e.getMessage()));
        }
    }

    /**
     * 租户药品同步至诊所
     *
     * @param entitys
     * @return
     */
    @PostMapping(value = "syncToClinic")
    public ResponseEntity<JSONObject> syncToClinic(@RequestBody List<Drug> entitys) {
        List<String> ids = drugService.syncToClinic(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }


}
