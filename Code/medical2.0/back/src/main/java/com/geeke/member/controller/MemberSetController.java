package com.geeke.member.controller;

import java.util.List;

import com.geeke.member.entity.MemberItem;
import com.geeke.member.service.MemberItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.member.entity.MemberSet;
import com.geeke.member.service.MemberSetService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;

/**
 * 会员卡设置Controller
 * @author rys
 * @version 2022-10-25
 */
@RestController
@RequestMapping(value = "/member/memberSet")
public class MemberSetController extends BaseController {

	@Autowired
	private MemberSetService memberSetService;

	@Autowired
    private MemberItemService memberItemService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        MemberSet entity = memberSetService.get(id);
        //根据会员卡获取对应的项目详情
        List<MemberItem> memberItemList=memberItemService.getByMemberSetId(id);
        entity.setMemberItem(memberItemList);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<MemberSet> result = memberSetService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
       if(!CollectionUtils.isEmpty(result.getRows())){
           List<MemberSet> memberSets = result.getRows();
           for (MemberSet memberSet : memberSets) {
               //获取对应的详情
               List<MemberItem> byMemberSetId = memberItemService.getByMemberSetId(memberSet.getId());
               memberSet.setMemberItem(byMemberSetId);
           }
           Page<MemberSet> memberSetPage = new Page<>(result.getTotal(), memberSets);
           return ResponseEntity.ok(ResultUtil.successJson(memberSetPage));
       }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<MemberSet> result = memberSetService.listAll(searchParams.getParams(), searchParams.getOrderby());
        for (MemberSet memberSet : result) {
            //获取对应的详情
            List<MemberItem> byMemberSetId = memberItemService.getByMemberSetId(memberSet.getId());
            memberSet.setMemberItem(byMemberSetId);
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody MemberSet entity) {
        String id = memberSetService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody MemberSet entity) {
        int rows = memberSetService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<MemberSet> entitys) {
        List<String> ids = memberSetService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<MemberSet> entitys) {
        List<String> ids = memberSetService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<MemberSet> entitys) {
        int rows = memberSetService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

}