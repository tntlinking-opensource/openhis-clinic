package com.geeke.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.medicareutils.config.MedicareConfigProperties;
import com.geeke.medicareutils.service.MdInventoryService;
import com.geeke.stock.entity.DispensingEvt;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.StorageEvt;
import com.geeke.stock.entity.SupplierStock;
import com.geeke.stock.service.DrugService;
import com.geeke.stock.service.SupplierStockService;
import com.geeke.stock.service.SupplierStorageService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 供应商库存Controller
 * @author txl
 * @version 2022-06-09
 */
@RestController
@RequestMapping(value = "/stock/supplierStock")
@RequiredArgsConstructor
public class SupplierStockController extends BaseController {

	@Autowired
	private SupplierStockService supplierStockService;

	@Autowired
	private SupplierStorageService supplierStorageService;

    @Autowired
    @Lazy
    private DrugService drugService;

    private  final MedicareConfigProperties medicareConfigProperties;

    @Resource
    @Lazy
    private  final MdInventoryService mdInventoryService;

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        SupplierStock entity = supplierStockService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @GetMapping("/stock/{sid}")
    public ResponseEntity<JSONObject> getByStorageId(@PathVariable("sid") String sid) {
        List<SupplierStock> byStorageId = supplierStockService.getByStorageId(sid);
        return ResponseEntity.ok(ResultUtil.successJson(byStorageId));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<SupplierStock> result = supplierStockService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<SupplierStock> result = supplierStockService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody SupplierStock entity) {
        logger.info("dayin:", entity);
        String id = supplierStockService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody SupplierStock entity) {
        int rows = supplierStockService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<SupplierStock> entitys) {
        List<String> ids = supplierStockService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<SupplierStock> entitys) {
        List<String> ids = supplierStockService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<SupplierStock> entitys) {
        int rows = supplierStockService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "inStorage")
    public ResponseEntity<JSONObject> inStorage(@RequestBody StorageEvt storageEvt) {
        //批量入库时无药品信息添加药品信息
        storageEvt.getSupplierStockList().forEach(item -> {
            Drug drug = drugService.getByNameAndPrice(item.getDrug().getGoodsName(),item.getRetailPrice());
            item.setDrug(drug);
          }
         );
        supplierStockService.saves(storageEvt);
        supplierStockService.savesTo(storageEvt);
        if("true".equals(medicareConfigProperties.getCheck())){
            //开启医保调用 药品 暂且默认 调拨入库
            if(storageEvt.getSupplierStorage().getBreed().equals(1)){
                mdInventoryService.updateInventoryList(storageEvt,"101");
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson());
    }

    @PostMapping(value = "cancel")
    public ResponseEntity<JSONObject> cancel(@RequestBody String id) {
        supplierStorageService.cancel(id);
        return ResponseEntity.ok(ResultUtil.successJson());
    }

    @PostMapping(value = "updateStock")
    public ResponseEntity<JSONObject> updateStock(@RequestBody DispensingEvt dispensingEvt) {
        //发药、退药
        if (null != dispensingEvt.getDispensingDetailEvtList() && !dispensingEvt.getDispensingDetailEvtList().isEmpty()) {
            supplierStockService.updateStock(dispensingEvt);
        }
        if("true".equals(medicareConfigProperties.getCheck())){
            //开启医保调用 发药退药修改库存
            mdInventoryService.updateInventoryList(dispensingEvt);

        }
        return ResponseEntity.ok(ResultUtil.successJson());
    }


    /**
     * 给指定的诊所进行发药
     *
     * @param storageEvt
     * @return
     */
    @PostMapping(value = "inStorageByCompany")
    public ResponseEntity<JSONObject> inStorageByCompany(@RequestBody StorageEvt storageEvt) {
        supplierStockService.saves(storageEvt);
        supplierStockService.savesToAudit(storageEvt);
        return ResponseEntity.ok(ResultUtil.successJson());
    }


    /**
     * 诊所审核入库
     *
     * @return
     */
    @PostMapping(value = "auditStorage")
    public ResponseEntity<JSONObject> auditStorage(@RequestBody StorageEvt storageEvt) {
        supplierStockService.auditStorage(storageEvt);
        return ResponseEntity.ok(ResultUtil.successJson());
    }

}