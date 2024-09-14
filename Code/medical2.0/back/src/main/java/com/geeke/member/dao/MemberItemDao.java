package com.geeke.member.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.member.entity.MemberItem;

import java.util.List;

/**
 * 会员卡详情DAO接口
 * @author rys
 * @version 2022-10-25
 */
@Mapper
public interface MemberItemDao extends CrudDao<MemberItem> {
    List<MemberItem> getByMemberSetId(String memberSetId);

    int deleteByMemberSet(String memberSetId);

}