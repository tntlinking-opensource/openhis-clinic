//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.geeke.org.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.org.entity.OrgDictItem;
import com.geeke.org.service.OrgDictItemService;
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
@RequestMapping({"/org/dictItem"})
public class OrgDictItemController extends BaseController {
    @Autowired
    private OrgDictItemService dictItemService;

    public OrgDictItemController() {
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        OrgDictItem entity = (OrgDictItem)this.dictItemService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @PostMapping({"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<OrgDictItem> result = this.dictItemService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping({"listAll"})
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<OrgDictItem> result = this.dictItemService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping({"save"})
    public ResponseEntity<JSONObject> save(@RequestBody OrgDictItem entity) {
        String id = ((OrgDictItem)this.dictItemService.save(entity)).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping({"delete"})
    public ResponseEntity<JSONObject> delete(@RequestBody OrgDictItem entity) {
        int rows = this.dictItemService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }
}
