//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.geeke.org.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.org.entity.OrgDictType;
import com.geeke.org.service.OrgDictTypeService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/org/dictType"})
public class OrgDictTypeController extends BaseController {
    @Autowired
    private OrgDictTypeService dictTypeService;

    public OrgDictTypeController() {
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        OrgDictType entity = this.dictTypeService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @PostMapping({"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<OrgDictType> result = this.dictTypeService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping({"listAll"})
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<OrgDictType> result = this.dictTypeService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping({"save"})
    public ResponseEntity<JSONObject> save(@RequestBody OrgDictType entity) {
        String id = this.dictTypeService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping({"delete"})
    public ResponseEntity<JSONObject> delete(@RequestBody OrgDictType entity) {
        int rows = this.dictTypeService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping({"bulkInsert"})
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<OrgDictType> entitys) {
        List<String> ids = this.dictTypeService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping({"bulkUpdate"})
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<OrgDictType> entitys) {
        List<String> ids = this.dictTypeService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    @PostMapping({"bulkDelete"})
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<OrgDictType> entitys) {
        int rows = this.dictTypeService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping({"importJson"})
    public ResponseEntity<JSONObject> importJson(@RequestBody OrgDictType entity) {
        String id = this.dictTypeService.importJson(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
}
