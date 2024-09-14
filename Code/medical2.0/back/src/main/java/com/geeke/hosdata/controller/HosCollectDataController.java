package com.geeke.hosdata.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geeke.basicdata.entity.ManufactureFactory;
import com.geeke.basicdata.service.ManufactureFactoryService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Parameter;
import com.geeke.hosdata.constant.ApiUrl;
import com.geeke.hosdata.entity.HisDrugs;
import com.geeke.hosdata.entity.HisInstitutions;
import com.geeke.hosdata.entity.HisMaterials;
import com.geeke.hosdata.service.HisDrugService;
import com.geeke.hosdata.service.HisInstitutionService;
import com.geeke.hosdata.service.HisMaterialService;
import com.geeke.hosdata.util.HttpUtil;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.service.DrugService;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.utils.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.geeke.hosdata.config.FieldMappingConfig.readFieldMappingFromXML;

@RestController
@RequestMapping("/hosdata/HosCollectData")
@RequiredArgsConstructor
public class HosCollectDataController {

    private final CompanyService companyService;

    private final HttpUtil httpUtil;

    private  final HisDrugService hisDrugService;

    private  final HisMaterialService hisMaterialService;
    //生产厂家
    private final ManufactureFactoryService manufactureService;

    private final HisInstitutionService hisInstitutionService;

    //所有诊所
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<Company> companies = companyService.listAll(searchParams.getParams(), searchParams.getOrder());
        return ResponseEntity.ok(ResultUtil.successJson(companies));
    }

    /**
     * 获取院版药品列表
     * @param searchParams
     * @return
     */
    @PostMapping(value = "getHosDrugs")
    public ResponseEntity<JSONObject> getHosDrugs(@RequestBody SearchParams searchParams) {
        JSONObject result = getJsonObject(searchParams);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 获取院版材料列表
     * @param searchParams
     * @return
     */
    @PostMapping(value = "getHosMaterials")
    public ResponseEntity<JSONObject> getHosMaterials(@RequestBody SearchParams searchParams) {
        JSONObject hosData = httpUtil.getHosData(searchParams, ApiUrl.HOS_GET_MATERIAL_ITEM_PAGE);
        List<String> codeList = hisMaterialService.getcCodeList(SessionUtils.getUserJson().getString("companyId"));
        JSONArray jsonArray = hosData.getJSONObject("BusData").getJSONObject("data").getJSONArray("rows");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(codeList.contains(jsonObject.getString("sfxmCode"))){
                jsonObject.put("syncNum",1);
            }

        }
        hosData.getJSONObject("BusData").getJSONObject("data").put("rows",jsonArray);
        return ResponseEntity.ok(ResultUtil.successJson(hosData.getJSONObject("BusData").getJSONObject("data")));
    }

    /**
     * 获取院版诊疗项目列表
     * @param searchParams
     * @return
     */
    @PostMapping(value = "getHosInstitutions")
    public ResponseEntity<JSONObject> getHosInstitutions(@RequestBody SearchParams searchParams) {
        JSONObject hosData = httpUtil.getHosData(searchParams, ApiUrl.HOS_GET_CHARGE_ITEM_PAGE);
        JSONArray jsonArray = hosData.getJSONObject("BusData").getJSONObject("data").getJSONArray("rows");
        List<String> codeList = hisInstitutionService.getcCodeList(SessionUtils.getUserJson().getString("companyId"));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(codeList.contains(jsonObject.getString("sfxmCode"))){
                    jsonObject.put("syncNum",1);
                }

        }
        hosData.getJSONObject("BusData").getJSONObject("data").put("rows",jsonArray);
        return ResponseEntity.ok(ResultUtil.successJson(hosData.getJSONObject("BusData").getJSONObject("data")));
    }



    @PostMapping(value = "HisInstitutionsToClinic")
    public ResponseEntity<JSONObject> HisHisInstitutionsToClinic( @RequestBody  List<HisInstitutions> entitys) {
        List<String> ids = hisInstitutionService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));

    }
    @PostMapping(value = "HisMaterialsToClinic")
    public ResponseEntity<JSONObject> HisMaterialsToClinic( @RequestBody  List<HisMaterials> entitys) {
        List<String> ids = hisMaterialService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));

    }
    @PostMapping(value = "HisDrugsToClinic")
    public ResponseEntity<JSONObject> HisDrugToClinic( @RequestBody  List<HisDrugs> entitys) {
        List<String> ids =  getStrings(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));

    }

  public  List<String> getStrings(List<HisDrugs> entitys) {
        for (HisDrugs drug : entitys){
            ManufactureFactory manufactureFactory = getManufactureFactory(drug);
            String factory = manufactureService.repeatBy(manufactureFactory);
            if( factory == null){
                drug.setFactory(manufactureService.save(manufactureFactory).getId());
            }else{
                drug.setFactory(factory);
            }
        }
        List<String> ids = hisDrugService.bulkInsert(entitys);
        return ids;
    }

    //同步插入院版生产厂家
    private  ManufactureFactory getManufactureFactory(HisDrugs drug) {
        ManufactureFactory manufactureFactory = new ManufactureFactory();
        Company company = new Company();
        company.setId(SessionUtils.getUserJson().getString("companyId"));
        manufactureFactory.setCompany(company);
        manufactureFactory.setCreateBy(drug.getCreateBy());
        manufactureFactory.setCreateDate(drug.getCreateDate());
        manufactureFactory.setUpdateBy(drug.getUpdateBy());
        manufactureFactory.setUpdateDate(drug.getUpdateDate());
        manufactureFactory.setName(drug.getFactory());
        manufactureFactory.setType("1");
        manufactureFactory.setStatus("1");
        return manufactureFactory;
    }


    public JSONObject getJsonObject(SearchParams searchParams) {

        JSONObject hosData = httpUtil.getHosData(searchParams, ApiUrl.HOS_GET_MEDICINE_PAGE);
        JSONArray dataArray =  hosData.getJSONObject("BusData").getJSONObject("data").getJSONArray("rows");
        JSONObject fieldMapping = readFieldMappingFromXML();
        ExecutorService executor = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors());
        List<JSONObject> processedDataArray = new ArrayList<>();
        List<String> codeList = hisDrugService.getDrugList(SessionUtils.getUserJson().getString("companyId"));
        for (int i = 0; i < dataArray.size(); i++) {
            final int index = i;
            executor.submit(() -> {
                JSONObject dataObject = dataArray.getJSONObject(index);
                // 遍历有序的 fieldMapping 中的每个映射，并将字段名转换
                for (Map.Entry<String, Object> entry : fieldMapping.entrySet()) {
                    String oldFieldName = entry.getKey();
                    String newFieldName = (String) entry.getValue();
                    if (dataObject.containsKey(oldFieldName)) {
                        Object value = dataObject.remove(oldFieldName); // 移除旧字段名
                        dataObject.put(newFieldName, value); // 添加新字段名
                    }
                }
                String code = dataObject.getString("code");
                if(codeList.contains(code)){
                    dataObject.put("syncNum",1);
                }
                synchronized (processedDataArray) {
                    processedDataArray.add(dataObject); // 将处理后的数据添加到结果列表
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject result = new JSONObject();
        result.put("rows",processedDataArray);
        result.put("total",hosData.getJSONObject("BusData").getJSONObject("data").getInteger("total"));
        return result;
    }


//    @PostMapping(value = "test")
//    public ResponseEntity<JSONObject> getHosDrugs1(@RequestBody SearchParams searchParams) {
//      JSONObject hosData = httpUtil.getHosData(httpUtil.getHosToken(),searchParams);
//      return ResponseEntity.ok(ResultUtil.successJson(hosData.getJSONObject("BusData").getJSONObject("data")));
//
//    }



}