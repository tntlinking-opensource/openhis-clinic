package com.geeke.member.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.member.entity.MemberManagement;
import com.geeke.member.entity.MemberManagementDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员卡管理DAO接口
 * @author rys
 * @version 2022-10-26
 */
@Mapper
public interface MemberManagementDetailDao extends CrudDao<MemberManagementDetail> {
    List<MemberManagementDetail> getByManagementId(String managementId);

    MemberManagementDetail getByManagementIdAndMemberItemId(@Param("managementId") String managementId, @Param("memberItemId") String memberItemId);

    void updateUseNumber(MemberManagementDetail memberManagementDetail);

}