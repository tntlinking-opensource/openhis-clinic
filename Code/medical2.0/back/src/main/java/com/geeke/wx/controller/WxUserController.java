package com.geeke.wx.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.wx.entity.WxUser;
import com.geeke.wx.service.WxUserService;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wxUser")
public class WxUserController {
    @Autowired
    private WxUserService wxUserService;

    @PostMapping ("getCode")
    public ResponseEntity<JSONObject> getCode(@RequestBody WxUser wxUser1){
        WxUser wxUser = wxUserService.getCode(wxUser1);
        return ResponseEntity.ok(ResultUtil.successJson(wxUser));
    }

    @PostMapping ("getPhone")
    public ResponseEntity<JSONObject> getPhone(@RequestBody WxUser wxUser){
        WxUser wxUser1 = wxUserService.getPhone(wxUser);
        System.out.println(wxUser1);
        return ResponseEntity.ok(ResultUtil.successJson(wxUser1));
    }

    @PostMapping("initLogin")
    public ResponseEntity<JSONObject> initLogin(@RequestBody WxUser wxUser){
        WxUser user = wxUserService.initLogin(wxUser);
        return ResponseEntity.ok(ResultUtil.successJson(user));
    }
}
