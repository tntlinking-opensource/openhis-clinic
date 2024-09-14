package com.geeke.member.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.member.entity.MemberManagement;

import java.util.List;

/**
 * 会员卡管理DAO接口
 * @author rys
 * @version 2022-10-26
 */
@Mapper
public interface MemberManagementDao extends CrudDao<MemberManagement> {
    List<MemberManagement> getByMemberSetId(String memberSetId);

    List<MemberManagement> getByPatientAndMember(@Param("patientId") String patientId, @Param("memberSetId") String memberSetId);

    List<MemberManagement> getByPatientId(String patientId);

    void updaStatus(MemberManagement memberManagement);

}