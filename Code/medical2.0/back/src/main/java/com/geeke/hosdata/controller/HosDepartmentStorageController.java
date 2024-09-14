package com.geeke.hosdata.controller;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Parameter;
import com.geeke.hosdata.constant.ApiUrl;
import com.geeke.hosdata.entity.HisDrugs;
import com.geeke.hosdata.entity.HisDrugsStorage;
import com.geeke.hosdata.util.HttpUtil;
import com.geeke.org.entity.Company;
import com.geeke.stock.entity.*;
import com.geeke.stock.service.DrugService;
import com.geeke.stock.service.SupplierStockService;
import com.geeke.sys.entity.DictItem;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.utils.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 院版科室发药
 */
@RestController
@RequestMapping("/hosdata/HosDepartmentStorage")
@RequiredArgsConstructor
public class HosDepartmentStorageController {

    private final HttpUtil httpUtil;
    
    private final DrugService drugService;

    private  final HosCollectDataController hosCollectDataController;

    private final SupplierStockService supplierStockService ;


    /**
     * 查询发药信息时就同步药品信息
     * @param searchParams
     * @return
     */
    @PostMapping(value = "getHosDrugsStorage")
    public ResponseEntity<JSONObject> getHosMaterials(@RequestBody SearchParams searchParams) {
        List<Parameter> params = searchParams.getNewParams();
        List<Parameter> newParams = new ArrayList<>();
        params.forEach(item ->{
            // 限定本科室
            if(item.getColumnName().equals("kscode") &&item.getValue().equals("")){
                item.setValue(SessionUtils.getUserJson().getString("companyId"));
            }
            newParams.add(new Parameter(item.getColumnName(),item.getQueryType(),item.getValue()));
        });
        JSONObject hosData = httpUtil.getHosData(searchParams, ApiUrl.HOS_GET_DRUGS_STORAGE_PAGE);
        JSONArray jsonArray = hosData.getJSONObject("BusData").getJSONObject("data").getJSONArray("rows");
        if(!jsonArray.isEmpty()){
            List<HisDrugsStorage> hisDrugsStorages = new ArrayList<>();
            List<HisDrugs> hisDrugs = new ArrayList<>();
            jsonArray.forEach(item->{
                HisDrugsStorage hisDrugsStorage = BeanUtil.copyProperties(item, HisDrugsStorage.class);
                hisDrugsStorages.add(hisDrugsStorage);
            });
            for (HisDrugsStorage hisDrugsStorage : hisDrugsStorages) {
                String ypdm = hisDrugsStorage.getYpdm();
                Drug drug = drugService.getByCode(ypdm,SessionUtils.getUserJson().getString("companyId"));
                if (drug == null) {
                    newParams.forEach(item-> {
                        if("ypdm".equals(item.getColumnName())){
                            item.setValue(ypdm);
                        }
                    });
                    searchParams.setParams(newParams);
                    JSONArray rows = hosCollectDataController.getJsonObject(searchParams).getJSONArray("rows");
                    rows.forEach(item->{
                        HisDrugs hisDrug = BeanUtil.copyProperties(item, HisDrugs.class);
                        hisDrugs.add(hisDrug);
                    });
                }
            }
            if(!hisDrugs.isEmpty()){
                //插入未同步的药品信息
                hosCollectDataController.getStrings(hisDrugs.stream()
                        .collect(Collectors.collectingAndThen(
                                Collectors.groupingBy(HisDrugs::getCode), // 根据code分组
                                map -> map.values().stream()            // 取出每个code对应的值（即对象列表）
                                        .map(list -> list.get(0))           // 从每个列表中取第一个元素作为代表
                                        .collect(Collectors.toList())      // 收集到一个新的列表
                        )));

            }
           hosData.getJSONObject("BusData").getJSONObject("data").put("rows", hisDrugsStorages);
        }
        return ResponseEntity.ok(ResultUtil.successJson(hosData.getJSONObject("BusData").getJSONObject("data")));
    }


    /**
     * 审核入库本诊所院版发药列表
     * @param hisDrugsStorages
     * @return
     */
    @PostMapping(value = "auditHosDrugsStorage")
    public ResponseEntity<JSONObject> auditHosMaterials(@RequestBody List<HisDrugsStorage> hisDrugsStorages) {
        StorageEvt storageEvt = new StorageEvt();
        SupplierStorage supplierStorage = new SupplierStorage();
        List<SupplierStock> supplierStockList = new ArrayList<>();
        int totalNumber = 0; // 初始化总数为0
        for (int i = 0; i < hisDrugsStorages.size(); i++){
            Drug drug = drugService.getByCode(hisDrugsStorages.get(i).getYpdm(),SessionUtils.getUserJson().getString("companyId"));
            SupplierStock supplierStock = new SupplierStock();
            //生产批次
            supplierStock.setBatchNo(hisDrugsStorages.get(i).getPc());
            //药品信息
            supplierStock.setDrug(drug);
            //材料默认为null
            supplierStock.setStuff(new Stuff());
            //数量判断院版是最小单位还是标准单位
            if(Integer.parseInt(hisDrugsStorages.get(i).getCkzhyz()) > 1){
                //标准单位
                supplierStock.setNumber(hisDrugsStorages.get(i).getSl() * Integer.parseInt( (drug.getPreparation().substring(0,drug.getPreparation().indexOf(".")))));
                supplierStock.setLeastBid(hisDrugsStorages.get(i).getPfj().divide(new BigDecimal(Integer.parseInt(hisDrugsStorages.get(i).getCkzhyz())),2,BigDecimal.ROUND_HALF_UP));
            }else {
                supplierStock.setNumber(hisDrugsStorages.get(i).getSl());
                supplierStock.setLeastBid(hisDrugsStorages.get(i).getPfj());
            }
            //诊所id
            Company company = new Company();
            company.setId(SessionUtils.getUserJson().getString("companyId"));
            supplierStock.setCompany(company);
            supplierStorage.setCompany(company);
            //生产日期
            if(hisDrugsStorages.get(i).getScrq() == null){
                //院版为空则设置为当前时间避免报错
                supplierStock.setProduceDate(new Date());
            }else{
                supplierStock.setProduceDate(hisDrugsStorages.get(i).getScrq());
            }

            //有效期
            supplierStock.setEndDate(hisDrugsStorages.get(i).getYxq());
            Supplier supplier = new Supplier();
            //TODO 默认设置暂且写死为院版供应商，后续根据需求优化
            supplier.setId("2098053487889309725");
            supplierStock.setSupplierId(supplier);
            supplierStorage.setSupplier(supplier);
            //进价
            supplierStock.setBid(hisDrugsStorages.get(i).getPfj());
            supplierStockList.add(supplierStock);
            //发票号
            supplierStorage.setInvoiceNumber(hisDrugsStorages.get(i).getFPh());
            totalNumber += supplierStock.getNumber();
        }
        //开始添加库存信息
        supplierStorage.setNumber(totalNumber); // 将累加的结果赋值给supplierStorage
        //药品 2
        supplierStorage.setBreed(2);
        supplierStorage.setExamineDate(new Date());
        DictItem dictItem = new DictItem();
        //TODO 字典值默认写死 入库通过
        dictItem.setId("1005787933775863930");
        dictItem.setValue("supplierStorageExamineStatus_0");
        supplierStorage.setExamine(dictItem);
        supplierStorage.setType("1");
        supplierStorage.setCode("");


        storageEvt.setSupplierStockList(supplierStockList);
        storageEvt.setSupplierStorage(supplierStorage);

        supplierStockService.saves(storageEvt);
        supplierStockService.savesTo(storageEvt);
        // 回调院版接口修改已入库状态
        hisDrugsStorages.forEach(item-> httpUtil.UpdateHosData(ApiUrl.HOS_UPDATE_DRUGS_STORAGE_STATUS, item.getCrkmxId()));
        return ResponseEntity.ok(ResultUtil.successJson());
    }



}
