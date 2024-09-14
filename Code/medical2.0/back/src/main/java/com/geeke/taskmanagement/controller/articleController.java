package com.geeke.taskmanagement.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.outpatient.entity.RecipelInfo;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.taskmanagement.entity.article;
import com.geeke.taskmanagement.service.ArticleService;

import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

/**
 * 文章管理Controller
 * @author txl
 * @version 2022-06-13
 */
@RestController
@RequestMapping(value = "/taskmanagement/article")
public class articleController extends BaseController{
    @Autowired
    private ArticleService articleService;

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody article entity) {
        entity.setIsLocked("0");
        entity.setAricledate(new Date());
        String refdata = articleService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(refdata));
    }
    @PostMapping(value = "update")
    public ResponseEntity<JSONObject> update(@RequestBody article entity) {
        JSONObject userObj = SessionUtils.getUserJson();
        if (userObj != null && StringUtils.isNotBlank(userObj.getString("id"))) {
            entity.setUpdateBy(userObj.getString("name") + "(" + userObj.getString("loginName") + ")");
            entity.setUpdateDate(new Date());
        }
        entity.setIsLocked("0");
        entity.setAricledate(new Date());
        int refdata = articleService.update(entity);
        return ResponseEntity.ok(ResultUtil.successJson(refdata));
    }
    @PostMapping(value = "audit")
    public ResponseEntity<JSONObject> audit(@RequestBody article entity) {
        JSONObject userObj = SessionUtils.getUserJson();
        if (userObj != null && StringUtils.isNotBlank(userObj.getString("id"))) {
            entity.setAuditor(userObj.getString("name") + "(" + userObj.getString("loginName") + ")");
            entity.setAuditordate(new Date());
        }
        int refdata = articleService.update(entity);
        return ResponseEntity.ok(ResultUtil.successJson(refdata));
    }
    @PostMapping(value = "list")
    public ResponseEntity<JSONObject> list(@RequestBody SearchParams searchParams) {
        Page<article> result = articleService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "lists")
    public ResponseEntity<JSONObject> lists(@RequestBody SearchParams searchParams) {
        Page<article> result = articleService.listPages(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listall(@RequestBody SearchParams searchParams){
        List<article> result = articleService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    @PostMapping(value = "listgetid")
    public ResponseEntity<JSONObject> listgetid(@RequestBody article entity){
        article result = articleService.listgetid(entity);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

}
