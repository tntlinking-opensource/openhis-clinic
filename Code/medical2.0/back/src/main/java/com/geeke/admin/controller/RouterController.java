package com.geeke.admin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.Router;
import com.geeke.admin.service.RouterService;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;

/**
 * 路由管理Controller
 * @author lys
 * @version 2021-11-18
 */
@RestController
@RequestMapping(value = "/admin/router")
public class RouterController extends CrudController<RouterService, Router> {

    @Autowired
    protected RouterService routerService;

    @Override
    protected RouterService getService() {
        return routerService;
    }

    @PostMapping(value = "tree")
    public ResponseEntity<JSONObject> tree(@RequestBody SearchParams searchParams) {
        List<Router> result = routerService.tree(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    //获取用户是否拥有有效期预警权限
    @GetMapping("getUserIndateWarning/{userId}")
    public ResponseEntity<JSONObject> getUserIndateWarning(@PathVariable("userId") String userId){
        List<String> userIndateWarning = routerService.getUserIndateWarning(userId);
        return ResponseEntity.ok(ResultUtil.successJson(userIndateWarning));
    }
}