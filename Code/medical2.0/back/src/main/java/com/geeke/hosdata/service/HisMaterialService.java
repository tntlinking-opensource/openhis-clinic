package com.geeke.hosdata.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.service.CrudService;
import com.geeke.hosdata.dao.HisDrugDao;
import com.geeke.hosdata.dao.HisInstitutionDao;
import com.geeke.hosdata.dao.HisMaterialDao;
import com.geeke.hosdata.entity.DistDto;
import com.geeke.hosdata.entity.HisDrugs;
import com.geeke.hosdata.entity.HisMaterials;
import com.geeke.stock.entity.Stuff;
import com.geeke.stock.service.MedicinalStockControlService;
import com.geeke.stock.service.StuffService;
import com.geeke.sys.utils.SessionUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HisMaterialService  extends CrudService<HisMaterialDao, HisMaterials> {


    private  final HisDrugDao hisDrugDao;

    private  final HisMaterialDao hisMaterialDao;

    private final StuffService stuffService;

    private  final MedicinalStockControlService medicinalStockControlService;



    @Override
    public List<String> bulkInsert(List<HisMaterials> entitys) {
        //院版诊所对应关系/
        Map<String, String> preMap = getTypeMap("1004406758192578593");
        Map<String, String> packMap = getTypeMap("1004406758192578597");
        List<String> ids = Lists.newArrayList();
        JSONObject userObj = SessionUtils.getUserJson();
        for(HisMaterials entity : entitys) {
            entity.setCompanyId(userObj.getString("companyId"));
            entity.setDw(preMap.getOrDefault(entity.getDw(), preMap.get("个")));
            entity.setGg(packMap.getOrDefault(entity.getGg(), preMap.get("个")));
            entity.preInsert();
            ids.add(entity.getId());
        }
        dao.bulkInsert(entitys);
        ids.forEach(id -> {
            Stuff stuff =  stuffService.get(id);
            medicinalStockControlService.initStock(stuff);
        });
        return ids;
    }

    private Map<String, String> getTypeMap(String type) {
        List<DistDto> dist = hisDrugDao.getDist(type);
        return  dist.stream().collect(Collectors.toMap(DistDto::getDistName,DistDto::getDistValue,(value1, value2) -> value1));
    }

    public List<String> getcCodeList(String companyId) {
        return hisMaterialDao.getMaterialCode(companyId);
    }

}
