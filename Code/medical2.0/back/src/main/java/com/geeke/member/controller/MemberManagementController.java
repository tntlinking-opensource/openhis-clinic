package com.geeke.member.controller;

import java.util.List;

import com.geeke.member.entity.MemberItem;
import com.geeke.member.entity.MemberManagementDetail;
import com.geeke.member.entity.MemberSet;
import com.geeke.member.service.MemberItemService;
import com.geeke.member.service.MemberManagementDetailService;
import com.geeke.member.service.MemberSetService;
import com.geeke.outpatient.entity.RecipelInfoEvt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.geeke.member.entity.MemberManagement;
import com.geeke.member.service.MemberManagementService;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.utils.ResultUtil;

/**
 * 会员卡管理Controller
 * @author rys
 * @version 2022-10-26
 */
@RestController
@RequestMapping(value = "/member/memberManagement")
public class MemberManagementController extends CrudController<MemberManagementService, MemberManagement> {

	@Autowired
	protected MemberManagementService memberManagementService;

	@Autowired
    private MemberSetService memberSetService;

	@Autowired
    private MemberItemService memberItemService;

	@Autowired
    private MemberManagementDetailService memberManagementDetailService;

    @Override
    protected MemberManagementService getService() {
        return memberManagementService;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        MemberManagement entity = memberManagementService.get(id);
        MemberSet memberSet = memberSetService.get(entity.getMemberSet().getId());
        List<MemberItem> byMemberSetId = memberItemService.getByMemberSetId(memberSet.getId());
        memberSet.setMemberItem(byMemberSetId);
        entity.setMemberSet(memberSet);
        //根据管理id获取详情id
        List<MemberManagementDetail> memberManagementDetails = memberManagementDetailService.getByManagementId(id);
        entity.setMemberManagementDetails(memberManagementDetails);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @Override
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody MemberManagement entity) {
        int rows = memberManagementService.delete(entity);
        //禁用卡成功后需要将卡还回会员卡
        if(rows>0){
            memberSetService.addNumber(entity.getMemberSet().getId());
        }
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    //根据处方明细和处方详情获取会员卡信息
    @PostMapping(value = "getMember")
    public ResponseEntity<JSONObject> getMember(@RequestBody RecipelInfoEvt recipelInfoEvt){
        List<MemberManagement> member = memberManagementService.getMember(recipelInfoEvt);
        return ResponseEntity.ok(ResultUtil.successJson(member));
    }

    @GetMapping(value = "getByPatientId/{patientId}")
    public ResponseEntity<JSONObject> getByPatientId(@PathVariable("patientId") String patientId){
        List<MemberManagement> memberManagements=memberManagementService.getByPatientId(patientId);
        return ResponseEntity.ok(ResultUtil.successJson(memberManagements));
    }
}
