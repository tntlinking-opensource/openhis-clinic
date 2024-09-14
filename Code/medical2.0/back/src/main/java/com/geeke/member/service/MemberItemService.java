package com.geeke.member.service;

import java.util.List;
import java.util.Map;

import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.service.CostItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.member.dao.MemberItemDao;
import com.geeke.member.entity.MemberItem;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 会员卡详情Service
 * @author rys
 * @version 2022-10-25
 */
 
@Service("memberItemService")
@Transactional(readOnly = true)
public class MemberItemService extends CrudService<MemberItemDao, MemberItem>{
    @Autowired
    private CostItemService costItemService;

    @Transactional(readOnly = false)
    public List<MemberItem> getByMemberSetId(String memberSetId) {
        List<MemberItem> memberItemList=this.dao.getByMemberSetId(memberSetId);
        for (MemberItem memberItem : memberItemList) {
            memberItem.setName(memberItem.getCostItem().getId());
            //根据诊疗项目id获取诊疗项目
            CostItem costItem = costItemService.get(memberItem.getCostItem().getId());
            memberItem.setCostItem(costItem);
        }
        return memberItemList;
    }
    @Transactional(readOnly = false)
    public int deleteByMemberSet(String memberSetId) {
        int i=this.dao.deleteByMemberSet(memberSetId);
        return i;
    }
}