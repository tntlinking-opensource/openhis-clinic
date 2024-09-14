package com.geeke.member.service;

import java.util.List;
import java.util.Map;

import com.geeke.member.entity.MemberItem;
import com.geeke.member.entity.MemberManagement;
import com.geeke.treatment.entity.CostItem;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.member.dao.MemberSetDao;
import com.geeke.member.entity.MemberSet;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

import javax.swing.text.html.parser.Entity;

/**
 * 会员卡设置Service
 * @author rys
 * @version 2022-10-25
 */
 
@Service("memberSetService")
@Transactional(readOnly = false)
public class MemberSetService extends CrudService<MemberSetDao, MemberSet>{
    @Autowired
    private MemberItemService memberItemService;

    @Autowired
    private MemberManagementService memberManagementService;

    @Transactional(readOnly = false)
    public MemberSet save(MemberSet memberSet){
       if(StringUtils.isBlank(memberSet.getId())){
           memberSet.preInsert();
           int i = this.dao.insert(memberSet);
           if(i>0){
               List<MemberItem> memberItemList = memberSet.getMemberItem();
               for (MemberItem memberItem : memberItemList) {
                   CostItem costItem = new CostItem();
                   costItem.setId(memberItem.getName());
                   memberItem.setCostItem(costItem);
                   memberItem.setMember(memberSet);
                   memberItemService.save(memberItem);
               }
           }
       }else {
           memberSet.preUpdate();
           //修改会员卡数量需要去判断会员卡发放数量是否大于发放数量
           List<MemberManagement> memberManagements = memberManagementService.getByMemberSetId(memberSet.getId());

           if(memberManagements.size()>memberSet.getAmount()){
               throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "修改的会员卡数量不能低于已发放会员卡数量！"));
           }
           int i = this.dao.update(memberSet);
           if(i>0){
               //先将原来的项目进行删除在添加
               int delete=memberItemService.deleteByMemberSet(memberSet.getId());

                   List<MemberItem> memberItemList = memberSet.getMemberItem();
                   for (MemberItem memberItem : memberItemList) {
                       memberItem.setId(null);
                       CostItem costItem = new CostItem();
                       costItem.setId(memberItem.getName());
                       memberItem.setCostItem(costItem);
                       memberItem.setMember(memberSet);
                       memberItemService.save(memberItem);

               }
           }
       }
        return memberSet;
    }

    @Transactional
    public int updateNumber(String id) {
        //获取现有会员卡数量
        MemberSet memberSet = this.dao.get(id);
        if(memberSet.getNumber()==0){
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "会员卡剩余数量为0，不能再添加会员！"));

        }else {
            int newNumber=memberSet.getNumber()-1;
            int i = this.dao.updateNumber(id,newNumber);
            return i;
        }

    }
    @Transactional
    public int addNumber(String id) {
        //获取现有会员卡数量
        MemberSet memberSet = this.dao.get(id);
        int newNumber = memberSet.getNumber()+1;
        int i = this.dao.updateNumber(id, newNumber);
        return i;
    }
}