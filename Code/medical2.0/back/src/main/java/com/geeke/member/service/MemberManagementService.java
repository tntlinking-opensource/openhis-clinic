package com.geeke.member.service;

import java.util.*;

import com.geeke.member.entity.MemberItem;
import com.geeke.member.entity.MemberManagementDetail;
import com.geeke.member.entity.MemberSet;
import com.geeke.outpatient.entity.RecipelDetail;
import com.geeke.outpatient.entity.RecipelInfoEvt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.member.dao.MemberManagementDao;
import com.geeke.member.entity.MemberManagement;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

/**
 * 会员卡管理Service
 * @author rys
 * @version 2022-10-26
 */
 
@Service("memberManagementService")
@Transactional(readOnly = false)
public class MemberManagementService extends CrudService<MemberManagementDao, MemberManagement>{
        @Autowired
        private MemberManagementDetailService memberManagementDetailService;

        @Autowired
        private MemberSetService memberSetService;

        @Autowired
        private MemberItemService memberItemService;

        @Transactional(readOnly = false)
        public MemberManagement save(MemberManagement memberManagement){
            memberManagement.preInsert();
            String id = memberManagement.getId();
            String card = new Date().getTime() + "";
            card = card.substring(card.length()-8);
            memberManagement.setCard(card);
            //绑卡时进行用户校验是否已经办卡了
            List<MemberManagement> memberManagements = this.dao.getByPatientAndMember(memberManagement.getPatient().getId(),memberManagement.getMemberSet().getId());
            if(!CollectionUtils.isEmpty(memberManagements)){
                throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "该用户已经绑定了该会员卡，不能重复绑定"));
            }
            int insert = this.dao.insert(memberManagement);
            //保存好后将数据保存到详情表中

            List<MemberItem> memberItem = memberManagement.getMemberSet().getMemberItem();
            for (MemberItem item : memberItem) {
                MemberManagementDetail memberManagementDetail = new MemberManagementDetail();
                memberManagementDetail.setMemberManagement(memberManagement);
                memberManagementDetail.setMemberItem(item);
                memberManagementDetail.setNumber(item.getNumber());
                memberManagementDetail.setUseNumber(0);
                memberManagementDetail.setCompany(memberManagement.getCompany());
                memberManagementDetailService.save(memberManagementDetail);
            }
            //绑卡成功需要去扣减会员卡数量
            if(insert>0){
                int i = memberSetService.updateNumber(memberManagement.getMemberSet().getId());
            }
            return memberManagement;
        }
    @Transactional(readOnly = false)
    public List<MemberManagement> getByMemberSetId(String memberSetId) {
        List<MemberManagement> memberManagements = this.dao.getByMemberSetId(memberSetId);
        return memberManagements;
    }
    
    @Transactional(readOnly = false)
    public List<MemberManagement> getMember(RecipelInfoEvt recipelInfoEvt) {
            //获取诊疗项目id
        List<RecipelDetail> recipelDetailEvtList = recipelInfoEvt.getRecipelDetailEvtList();

        //根据患者id获取其是否绑定了体验卡
        List<MemberManagement> memberManagements = this.dao.getByPatientId(recipelInfoEvt.getRecipelInfo().getRegistration().getPatientId().getId());
        List<MemberManagement> memberManagementList = new ArrayList<>();

        //根据用户绑定卡，去获取其绑定的项目
        if(!CollectionUtils.isEmpty(memberManagements)){
            for (MemberManagement memberManagement : memberManagements) {
                List<MemberManagementDetail> memberManagementDetails = memberManagementDetailService.getByManagementId(memberManagement.getId());

                int count=0;
                for (MemberManagementDetail memberManagementDetail : memberManagementDetails) {
                    if(memberManagementDetail.getNumber()==memberManagementDetail.getUseNumber()){
                        count++;
                    }
                }
                //如果该会员卡使用次数都使用完毕，结束本次循环
                if(count==memberManagementDetails.size()){
                    continue;
                }

                //存在未使用完的会员卡时，进行判断
                boolean flage = false;
                MemberSet memberSet = memberSetService.get(memberManagement.getMemberSet().getId());
                //获取体验卡详情
                List<MemberItem> memberItems = memberItemService.getByMemberSetId(memberSet.getId());
                for (MemberItem memberItem : memberItems) {
                    for (RecipelDetail recipelDetail : recipelDetailEvtList) {
                            if(Objects.equals(recipelDetail.getDrugStuffId().getCostItem().getId(),memberItem.getCostItem().getId())){
                                //如果相等再去获取该用户是否使用完
                                MemberManagementDetail memberManagementDetail = memberManagementDetailService.getByManagementIdAndMemberItemId(memberManagement.getId(),memberItem.getId());
                                if(memberManagementDetail.getNumber()>memberManagementDetail.getUseNumber()) {
                                    flage = true;
                                    break;
                                }
                            }
                    }
                    if(flage){
                        break;
                    }
                }
                if(flage){
                    memberManagement.setMemberSet(memberSet);
                    memberManagement.setMemberItems(memberItems);
                    memberManagement.setMemberManagementDetails(memberManagementDetails);
                    memberManagementList.add(memberManagement);
                }
            }

            return memberManagementList;
        }else {
            return new ArrayList<MemberManagement>();
        }

    }

    @Transactional(readOnly = false)
    public void updaStatus(MemberManagement memberManagement) {
            this.dao.updaStatus(memberManagement);
    }

    public List<MemberManagement> getByPatientId(String patientId) {
        List<MemberManagement> memberManagements=this.dao.getByPatientId(patientId);
        List<MemberManagement> memberManagementList = new ArrayList<>();
        for (MemberManagement memberManagement : memberManagements) {
            List<MemberManagementDetail> memberManagementDetails = memberManagementDetailService.getByManagementId(memberManagement.getId());
            int count=0;
            for (MemberManagementDetail memberManagementDetail : memberManagementDetails) {
                if(memberManagementDetail.getNumber()==memberManagementDetail.getUseNumber()){
                    count++;
                }
            }
            if(count<memberManagementDetails.size()){
                memberManagementList.add(memberManagement);
            }
        }
        return memberManagementList;
    }
}