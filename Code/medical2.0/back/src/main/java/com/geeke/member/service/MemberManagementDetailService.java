package com.geeke.member.service;

import com.geeke.common.service.CrudService;
import com.geeke.member.dao.MemberManagementDao;
import com.geeke.member.dao.MemberManagementDetailDao;
import com.geeke.member.entity.MemberItem;
import com.geeke.member.entity.MemberManagement;
import com.geeke.member.entity.MemberManagementDetail;
import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.service.CostItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 会员卡管理Service
 * @author rys
 * @version 2022-10-26
 */
 
@Service("memberManagementDetailService")
@Transactional(readOnly = false)
public class MemberManagementDetailService extends CrudService<MemberManagementDetailDao, MemberManagementDetail>{
  @Autowired
  private MemberItemService memberItemService;

  @Autowired
  private CostItemService costItemService;

  @Autowired
  private MemberManagementService memberManagementService;

    @Transactional(readOnly = false)
    public void updateUseNumber(MemberManagement memberManagement) {

        List<MemberManagementDetail> memberManagementDetails = memberManagement.getMemberManagementDetails();

        for (MemberManagementDetail memberManagementDetail : memberManagementDetails) {
            this.dao.updateUseNumber(memberManagementDetail);
        }

        //修改次数后，需要修改会员卡状态
        memberManagementService.updaStatus(memberManagement);
    }

    @Transactional(readOnly = false)
    public List<MemberManagementDetail> getByManagementId(String managementId) {
        List<MemberManagementDetail> memberManagementDetails = this.dao.getByManagementId(managementId);
        for (MemberManagementDetail memberManagementDetail : memberManagementDetails) {
            MemberItem memberItem = memberItemService.get(memberManagementDetail.getMemberItem().getId());
            //根据会员卡详情获取项目内容
            CostItem costItem = costItemService.get(memberItem.getCostItem().getId());
            memberItem.setCostItem(costItem);
            memberManagementDetail.setMemberItem(memberItem);
        }
        return memberManagementDetails;
    }


    public MemberManagementDetail getByManagementIdAndMemberItemId(String managementId, String memberItemId) {
       MemberManagementDetail memberManagementDetail = this.dao.getByManagementIdAndMemberItemId(managementId,memberItemId);
       return memberManagementDetail;
    }
}