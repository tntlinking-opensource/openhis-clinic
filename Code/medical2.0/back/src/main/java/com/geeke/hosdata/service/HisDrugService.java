package com.geeke.hosdata.service;


import com.alibaba.fastjson.JSONObject;
import com.geeke.common.service.CrudService;
import com.geeke.hosdata.dao.HisDrugDao;
import com.geeke.hosdata.entity.DistDto;
import com.geeke.hosdata.entity.HisDrugs;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.service.DrugService;
import com.geeke.stock.service.MedicinalStockControlService;
import com.geeke.sys.utils.SessionUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HisDrugService  extends CrudService<HisDrugDao, HisDrugs> {

    private  final  HisDrugDao hisDrugDao;

    private  final  MedicinalStockControlService medicinalStockControlService;

    private final DrugService drugService;

    @Override
    public List<String> bulkInsert(List<HisDrugs> entitys) {
        //院版诊所对应关系/
        Map<String, String> typeMap = getTypeMap("1004078055755374603");
        Map<String, String> dosMap = getTypeMap("1004406758192578588");
        Map<String, String> preMap = getTypeMap("1004406758192578593");
        Map<String, String> packMap = getTypeMap("1004406758192578597");
        JSONObject userObj = SessionUtils.getUserJson();
        List<String> ids = Lists.newArrayList();
        for(HisDrugs entity: entitys) {
            Drug drug = drugService.getByCode(entity.getStandardCode(),userObj.getString("companyId"));
            if(drug!=null){
                continue;
            }
            entity.setCompanyId(userObj.getString("companyId"));
            entity.setType(typeMap.getOrDefault(entity.getType(),entity.getType()));
            entity.setDosisUnit(dosMap.getOrDefault(entity.getDosisUnit(),entity.getDosisUnit()));
            entity.setPreparationUnit(preMap.getOrDefault(entity.getPreparationUnit(),entity.getPreparationUnit()));
            entity.setPack(packMap.getOrDefault(entity.getPack(),entity.getPack()));
           entity.preInsert();
           ids.add(entity.getId());
        }
        dao.bulkInsert(entitys);
        ids.forEach(id->{
            Drug drug = drugService.get(id);
            medicinalStockControlService.initStock(drug);
        });
        return ids;
    }

    private Map<String, String> getTypeMap(String type) {
        List<DistDto> dist = hisDrugDao.getDist(type);
        return  dist.stream().collect(Collectors.toMap(DistDto::getDistName,DistDto::getDistValue,(value1, value2) -> value1));
    }

    public List<String> getDrugList(String companyId) {
        return hisDrugDao.getDrugCode(companyId);
    }
}
