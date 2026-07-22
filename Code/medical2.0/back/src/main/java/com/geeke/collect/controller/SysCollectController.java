package com.geeke.collect.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.geeke.collect.entity.SysCollect;
import com.geeke.collect.service.SysCollectService;
import com.geeke.common.controller.CrudController;
import com.geeke.utils.ResultUtil;

/**
 * 收藏夹Controller
 * @author szy
 * @version 2021-07-28
 */
@RestController
@RequestMapping(value = "/collect/sysCollect")
public class SysCollectController extends CrudController<SysCollectService, SysCollect> {

    @Autowired
    protected SysCollectService sysCollectService;

    @Override
    protected SysCollectService getService() {
        return sysCollectService;
    }

    /**
     * 根据用户id查询收藏列表
     */
    @GetMapping(value = "listAllByUserId/{userId}")
    public ResponseEntity<JSONObject> listAllByUserId(@PathVariable("userId") String userId) {
        List<SysCollect> result = sysCollectService.listAllByUserId(userId);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 移动收藏触发
     */
    @PostMapping(value = "updateBatch/{userId}")
    public ResponseEntity<JSONObject> updateBatch(@RequestBody List<SysCollect> sysCollectList, @PathVariable String userId) {
        List<SysCollect> result = sysCollectService.updateBatch(sysCollectList, userId);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 删除收藏（保留 @DeleteMapping 以兼容前端调用）
     */
    @Override
    @DeleteMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody SysCollect entity) {
        return super.delete(entity);
    }
}