package com.geeke.wx.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.dao.UserDao;
import com.geeke.admin.entity.User;
import com.geeke.admin.service.UserService;
import com.geeke.sys.service.LoginService;
import com.geeke.utils.IdGen;
import com.geeke.utils.JwtUtils;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import com.geeke.wx.dao.WxUserDao;
import com.geeke.wx.entity.WxUser;
import com.geeke.common.service.CrudService;
import com.geeke.utils.WechatUtil;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

@Service
@Transactional(readOnly = false)
public class WxUserService extends CrudService<WxUserDao, WxUser> {

    @Autowired
    private WxUserDao wxUserDao;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional(readOnly = false)
    public WxUser getCode(WxUser wxUser) {
       // WxUser wxUser = new WxUser();
        // 获取code数据
        JSONObject object = WechatUtil.getOpenId(wxUser.getCode());
        // json数据转换成字符串
        String openid = object.get("openid").toString();
        String sessionkey = object.get("session_key").toString();
        // 当主体账户绑定小程序后就可以获取到，未绑定无法获取
        String unionId="";
        if(!ObjectUtils.isEmpty(object.get("unionid"))){
            unionId=object.get("unionid").toString();
        }
        wxUser.setOpenId(openid);
        wxUser.setSessionKey(sessionkey);
        wxUser.setUnionId(unionId);

        //获取是否存在该微信用户
        WxUser user = wxUserDao.getByOpenId(openid);
        if(!ObjectUtils.isEmpty(user)&&!ObjectUtils.isEmpty(user.getPhone())){

            return user;
        }

        //保存用户的openId
        String uuid = IdGen.uuid();
        wxUser.setId(uuid);
        wxUserDao.save(wxUser);
        // 返回参数
        return wxUser;
    }

    @Transactional(readOnly = false)
    public WxUser getPhone(WxUser wxUser) {
        WxUser dto = new WxUser();
        JSONObject token = WechatUtil.getToken();
        if(!ObjectUtils.isEmpty(token.get("access_token"))){
            JSONObject phone = WechatUtil.getPhoneNumber(wxUser.getCode(), token.get("access_token").toString());
            if(Objects.equals(phone.get("errcode").toString(),"0")){
                String phone_info = phone.get("phone_info").toString();
                JSONObject jsonObject = JSONObject.parseObject(phone_info);
                String phoneNumber = jsonObject.get("phoneNumber").toString();
                //通过openId获取是否有该用户信息
                WxUser byOpenId = wxUserDao.getByOpenId(wxUser.getOpenId());
                String tokens="";
                if(!ObjectUtils.isEmpty(byOpenId)){
                    System.out.println(byOpenId);
                    wxUserDao.update(phoneNumber,wxUser.getOpenId());

                    //保存完后进行后端保存微信患者

                    //根据诊所id和openid获取是否有该用户
                    User user1 = userService.getCompanyIdAndOpenId(byOpenId.getCompany().getId(),wxUser.getOpenId());
                    if(!ObjectUtils.isEmpty(user1)){
                        User user = new User();
                        user.setId(IdGen.uuid());
                        user.setCompany(byOpenId.getCompany());
                        user.setIsWxuser("1");
                        user.setIsLocked(0);
                        user.setLoginName(wxUser.getOpenId());
                        int length = wxUser.getOpenId().length();

                        String substring = wxUser.getOpenId().substring(length-6);
                        System.out.println(substring);
                        //进行密码加密
                        Md5Hash md5 = new Md5Hash(substring, user.getId(), 6);
                        String md5Password = md5.toHex();
                        user.setLoginPassword(md5Password);
                        user.setName(byOpenId.getOpenId());
                        user.setPhone(phoneNumber);
                        userService.saveWxUser(user);
                    }else {
                        User user = new User();
                        user.setId(user1.getId());
                        user.setCompany(byOpenId.getCompany());
                        user.setIsWxuser("1");
                        user.setIsLocked(0);
                        user.setLoginName(wxUser.getOpenId());
                        int length = wxUser.getOpenId().length();

                        String substring = wxUser.getOpenId().substring(length-6);
                        System.out.println(substring);
                        //进行密码加密
                        Md5Hash md5 = new Md5Hash(substring, user.getId(), 6);
                        String md5Password = md5.toHex();
                        user.setLoginPassword(md5Password);
                        user.setName(byOpenId.getOpenId());
                        user.setPhone(phoneNumber);
                        userService.updateWxUser(user);
                    }

                }
                dto.setPhone(phoneNumber);
                dto.setToken(tokens);
                return dto;
            }
        }

        return null;
    }

    @Transactional(readOnly = false)
    public WxUser initLogin(WxUser wxUser) {
        JSONObject object = WechatUtil.getOpenId(wxUser.getCode());
        // json数据转换成字符串
        String openid = object.get("openid").toString();
        WxUser user = wxUserDao.getByOpenId(openid);
        if(ObjectUtils.isEmpty(user)){
            wxUser.setOpenId(openid);
            String uuid = IdGen.uuid();
            wxUser.setId(uuid);
            wxUserDao.save(wxUser);
            return wxUser;
        }

        return user;
    }
}
