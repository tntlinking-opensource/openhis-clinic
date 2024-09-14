package com.geeke.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.stock.entity.MedicalProject;
import com.geeke.stock.entity.Stuff;
import com.geeke.stock.service.StuffService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 材料信息Controller
 * @author txl
 * @version 2022-06-22
 */
@RestController
@RequestMapping(value = "/stock/stuff")
public class StuffController extends BaseController {

	@Autowired
	private StuffService stuffService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        Stuff entity = stuffService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<Stuff> result = stuffService.listPages(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = {"listForMedical", ""})
    public ResponseEntity<JSONObject> listForMedical(@RequestBody SearchParams searchParams) {
        List<MedicalProject> result = stuffService.listPageForMedical(searchParams.getParams(),searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<Stuff> result = stuffService.listAlls(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody Stuff entity) {
        String id = null;
        System.out.println(entity.getPriceOutSell());
        System.out.println(entity.getRetailPrice());
        try {
            id = stuffService.save(entity).getId();
        } catch (Exception e) {
            return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_50001, "材料信息重复"));
        }
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody Stuff entity) {
        int rows = stuffService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<Stuff> entitys) {
        List<String> ids = stuffService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<Stuff> entitys) {
        List<String> ids = stuffService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<Stuff> entitys) {
        int rows = stuffService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "updateAllIndate")
    public ResponseEntity<JSONObject> updateAllIndate(@RequestBody Stuff stuff){
        int i =stuffService.updateAllIndate(stuff.getIndate(),stuff.getCompany().getId());
        return ResponseEntity.ok(ResultUtil.successJson(i));
    }

    @PostMapping(value = "updateAllInventory")
    public ResponseEntity<JSONObject> updateAllInventory(@RequestBody Stuff stuff){
        int i =stuffService.updateAllInventory(stuff.getInventoryFloor(),stuff.getCompany().getId());
        return ResponseEntity.ok(ResultUtil.successJson(i));
    }

    @PostMapping(value = "inventory")
    public ResponseEntity<JSONObject> inventory(@RequestBody SearchParams searchParams) {
        List<Stuff> result = stuffService.inventory(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping("/uploadExcel")
    @ApiOperation("excel")
    public ResponseEntity taskUploadExcel(@RequestParam(value = "file") MultipartFile file) {
        List excel = null;
        try {
            excel = stuffService.excel(file);
        } catch (Exception e) {
            return ResponseEntity.ok(new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, e.getMessage())));
        }
        return ResponseEntity.ok(ResultUtil.successJson(excel));
    }

    /**
     * 只查询租户的材料
     *
     * @param searchParams
     * @return
     */
    @PostMapping(value = {"listByInstitution"})
    public ResponseEntity<JSONObject> listByInstitution(@RequestBody SearchParams searchParams) {
        Page<Stuff> result = stuffService.listByInstitution(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 只查询本公司的材料
     *
     * @param searchParams
     * @return
     */
    @PostMapping(value = {"listByCompany"})
    public ResponseEntity<JSONObject> listByCompany(@RequestBody SearchParams searchParams) {
        Page<Stuff> result = stuffService.listByCompany(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 租户材料同步至诊所
     *
     * @param entitys
     * @return
     */
    @PostMapping(value = "syncToClinic")
    public ResponseEntity<JSONObject> syncToClinic(@RequestBody List<Stuff> entitys) {
        List<String> ids = stuffService.syncToClinic(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
}