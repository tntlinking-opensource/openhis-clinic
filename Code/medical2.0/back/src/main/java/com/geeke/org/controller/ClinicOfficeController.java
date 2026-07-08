package com.geeke.org.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.medicareutils.config.MedicareConfigProperties;
import com.geeke.medicareutils.service.MdCompanyService;
import com.geeke.org.entity.ClinicOffice;
import com.geeke.org.service.ClinicOfficeService;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 科室信息Controller
 * @author ch
 * @version 2022-06-14
 */
@RestController
@RequestMapping(value = "/org/clinicOffice")
public class ClinicOfficeController extends CrudController<ClinicOfficeService, ClinicOffice> {

    @Autowired
    protected ClinicOfficeService clinicOfficeService;

    @Autowired
    private MedicareConfigProperties properties;

    @Autowired
    private MdCompanyService mdcompanyService;

    @Override
    protected ClinicOfficeService getService() {
        return clinicOfficeService;
    }

    @Override
    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody ClinicOffice entity) {
        entity.setCompany(SessionUtils.getLoginTenant());
        if("1".equals(entity.getIsDefault())){
            clinicOfficeService.updateDefault(entity.getCompany().getId());
        }
        //同步至开启医保接口
        if (properties.getCheck().equals("true")) {
            //添加
            if (StringUtils.isBlank(entity.getId())) {
              mdcompanyService.upCompanyData_3401_3402(entity,"3401");
            } else {
                //修改
                mdcompanyService.upCompanyData_3401_3402(entity,"3402");
            }
        }
        String id = clinicOfficeService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

}
