package com.geeke.wx.entity;

import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxUser extends DataEntity<WxUser> {
    private String id;
    private Company company;
    private String openId;
    private String phone;
    private String code;
    private String sessionKey;
    private String unionId;
    private String token;
}
