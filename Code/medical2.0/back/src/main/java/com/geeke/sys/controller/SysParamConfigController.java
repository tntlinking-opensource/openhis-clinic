package com.geeke.sys.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.CrudController;
import com.geeke.sys.entity.SysParamConfig;
import com.geeke.sys.service.SysParamConfigService;
import com.geeke.utils.ResultUtil;
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
public class SysParamConfigController extends CrudController<SysParamConfigService, SysParamConfig> {

    @Resource
    protected SysParamConfigService sysParamConfigService;

    @Override
    protected SysParamConfigService getService() {
        return sysParamConfigService;
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