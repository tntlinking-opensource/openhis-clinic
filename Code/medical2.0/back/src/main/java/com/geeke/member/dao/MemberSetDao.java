package com.geeke.member.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.member.entity.MemberSet;

/**
 * 会员卡设置DAO接口
 * @author rys
 * @version 2022-10-25
 */
@Mapper
public interface MemberSetDao extends CrudDao<MemberSet> {
    int insert(MemberSet memberSet);

    int updateNumber(@Param("id") String id,@Param("number") int number);

}