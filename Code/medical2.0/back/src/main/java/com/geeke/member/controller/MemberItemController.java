package com.geeke.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geeke.common.controller.CrudController;
import com.geeke.member.entity.MemberItem;
import com.geeke.member.service.MemberItemService;

/**
 * 会员卡详情Controller
 * @author rys
 * @version 2022-10-25
 */
@RestController
@RequestMapping(value = "/member/memberItem")
public class MemberItemController extends CrudController<MemberItemService, MemberItem> {

	@Autowired
	protected MemberItemService memberItemService;

	@Override
	protected MemberItemService getService() {
		return memberItemService;
	}

}
