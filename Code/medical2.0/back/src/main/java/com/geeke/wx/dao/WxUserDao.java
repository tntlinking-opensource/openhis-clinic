package com.geeke.wx.dao;

import com.geeke.wx.entity.WxUser;
import com.geeke.common.persistence.CrudDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface WxUserDao extends CrudDao<WxUser> {

    void save(WxUser wxUser);

    WxUser getByOpenId(String openId);

    void update(@Param("phone") String phone,@Param("openId") String openId);
}
