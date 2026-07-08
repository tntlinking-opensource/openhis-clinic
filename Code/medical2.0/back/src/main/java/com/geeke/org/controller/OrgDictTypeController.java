package com.geeke.org.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.CrudController;
import com.geeke.org.entity.OrgDictType;
import com.geeke.org.service.OrgDictTypeService;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/org/dictType"})
public class OrgDictTypeController extends CrudController<OrgDictTypeService, OrgDictType> {

    @Autowired
    private OrgDictTypeService dictTypeService;

    @Override
    protected OrgDictTypeService getService() {
        return dictTypeService;
    }

    @PostMapping({"importJson"})
    public ResponseEntity<JSONObject> importJson(@RequestBody OrgDictType entity) {
        String id = this.dictTypeService.importJson(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
}
