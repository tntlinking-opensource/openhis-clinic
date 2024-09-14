package com.geeke.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.MedicinalStorageControl;
import com.geeke.stock.entity.Stuff;
import com.geeke.stock.entity.SupplierStock;
import com.geeke.stock.service.CurrentInventoryService;
import com.geeke.stock.service.DrugService;
import com.geeke.stock.service.StuffService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 当前库存Controller
 * @author txl
 * @version 2022-06-07
 */
@RestController
@RequestMapping(value = "/stock/currentInventory")
public class CurrentInventoryController extends BaseController {

	@Autowired
	private DrugService drugService;

	@Autowired
    private CurrentInventoryService currentInventoryService;

	@Autowired
    private StuffService stuffService;
	//获取药品
    @PostMapping("getDrug")
    public ResponseEntity<JSONObject> getDrug(@RequestBody SearchParams searchParams){
        Page<Drug> drugList = drugService.getDrug(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(drugList));
    }
    //获取材料
    @PostMapping("getStuff")
    public ResponseEntity<JSONObject> getStuff(@RequestBody SearchParams searchParams){
        Page<Stuff> stuffPage = stuffService.getStuff(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(stuffPage));
    }

    //按药品商品计算价格
    @PostMapping("getDrugSalesStat")
    public ResponseEntity<JSONObject> getDrugSalesStat(@RequestBody SearchParams searchParams){
        HashMap<String, BigDecimal> allDrugs = currentInventoryService.getAllDrugs(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(allDrugs));
    }
    //按材料商品计算价格
    @PostMapping("getStuffSalesStat")
    public ResponseEntity<JSONObject> getStuffSalesStat(@RequestBody SearchParams searchParams){
        HashMap<String, BigDecimal> stringBigDecimalHashMap = currentInventoryService.getgetStuffSalesStat(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(stringBigDecimalHashMap));
    }
    //按批号获取药品
    @PostMapping("getBatchNumberDrug")
    public ResponseEntity<JSONObject> getBatchNumberDrug(@RequestBody SearchParams searchParams){
        Page<MedicinalStorageControl> batchNumberDrug = currentInventoryService.getBatchNumberDrug(searchParams);
        return ResponseEntity.ok(ResultUtil.successJson(batchNumberDrug));
    }

    //按批号获取材料
    @PostMapping("getBatchNumberStuff")
    public ResponseEntity<JSONObject> getBatchNumberStuff(@RequestBody SearchParams searchParams){
        Page<MedicinalStorageControl> medicinalStorageControlPage = currentInventoryService.getBatchNumberStuff(searchParams);
        return ResponseEntity.ok(ResultUtil.successJson(medicinalStorageControlPage));
    }
    //计算药品批号价格
    @PostMapping("getDrugSalesStatByNumber")
    public ResponseEntity<JSONObject> getDrugSalesStatByNumber(@RequestBody SearchParams searchParams){
        HashMap<String, BigDecimal> drugSalesStatByNumber = currentInventoryService.getDrugSalesStatByNumber(searchParams);
        return ResponseEntity.ok(ResultUtil.successJson(drugSalesStatByNumber));
    }
    //计算材料批号价格
    @PostMapping("getStuffSalesStatNumber")
    public ResponseEntity<JSONObject> getStuffSalesStatNumber(@RequestBody SearchParams searchParams){
        HashMap<String, BigDecimal> stuffSalesStatNumber = currentInventoryService.getStuffSalesStatNumber(searchParams);
        return ResponseEntity.ok(ResultUtil.successJson(stuffSalesStatNumber));
    }

    //表格导出
    @PostMapping("exportTable")
    public void exportTable(@RequestBody SearchParams searchParams, HttpServletRequest request, HttpServletResponse response) throws IOException {
        currentInventoryService.exportTable(searchParams,response);
    }
}