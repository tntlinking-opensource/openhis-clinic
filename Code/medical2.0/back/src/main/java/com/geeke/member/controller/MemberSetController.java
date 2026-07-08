package com.geeke.member.controller;

import java.util.List;

import com.geeke.member.entity.MemberItem;
import com.geeke.member.service.MemberItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.geeke.member.entity.MemberSet;
import com.geeke.member.service.MemberSetService;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.utils.ResultUtil;

/**
 * 会员卡设置Controller
 * @author rys
 * @version 2022-10-25
 */
@RestController
@RequestMapping(value = "/member/memberSet")
public class MemberSetController extends CrudController<MemberSetService, MemberSet> {

	@Autowired
	protected MemberSetService memberSetService;

	@Autowired
    private MemberItemService memberItemService;

    @Override
    protected MemberSetService getService() {
        return memberSetService;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        MemberSet entity = memberSetService.get(id);
        //根据会员卡获取对应的项目详情
        List<MemberItem> memberItemList=memberItemService.getByMemberSetId(id);
        entity.setMemberItem(memberItemList);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @Override
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

    @Override
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

}
