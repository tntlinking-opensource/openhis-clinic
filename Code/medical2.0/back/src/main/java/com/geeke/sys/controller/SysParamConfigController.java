package com.geeke.sys.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.entity.PersonalTheme;
import com.geeke.sys.entity.SysParamConfig;
import com.geeke.sys.service.PersonalThemeService;
import com.geeke.sys.service.SysParamConfigService;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统配置Controller
 * @author lc
 */
@RestController
@RequestMapping(value = "/sys/paramConfig")
public class SysParamConfigController extends BaseController {

    @Resource
    private SysParamConfigService sysParamConfigService;

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        SysParamConfig entity = sysParamConfigService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<SysParamConfig> result = sysParamConfigService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<SysParamConfig> result = sysParamConfigService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody SysParamConfig entity) {
    	String id = sysParamConfigService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "save/list")
    public ResponseEntity<JSONObject> saveList(@RequestBody List<SysParamConfig> entitys) {
        if (CollectionUtil.isNotEmpty(entitys)) {
            entitys.forEach(sysParamConfigService::save);
        }
        return ResponseEntity.ok(ResultUtil.successJson("操作成功"));
    }


    @DeleteMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody SysParamConfig entity) {
        int rows = sysParamConfigService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

}