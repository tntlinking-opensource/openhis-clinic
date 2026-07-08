package com.geeke.admin.common.controller;


import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.User;
import com.geeke.admin.service.UserService;
import com.geeke.common.controller.CrudController;
import com.geeke.common.service.ServiceException;
import com.geeke.medicareutils.config.MedicareConfigProperties;
import com.geeke.utils.JwtUtils;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController()
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController extends CrudController<UserService, User> {

    protected final UserService service;

    private final JwtUtils jwtUtils;

    private final MedicareConfigProperties medicareConfigProperties;

    @Override
    protected UserService getService() {
        return service;
    }

    @GetMapping("/me")
    public ResponseEntity<JSONObject> getCurrentUser() {
    	User user = SessionUtils.getUser();
    	String currentUserId = user.getId();
        User dto = this.service.get(currentUserId);
        dto.setLoginPassword("");
        return ResponseEntity.ok(ResultUtil.successJson(dto));
    }

    @PutMapping("/me")
    public ResponseEntity<JSONObject> updateCurrentUser(@RequestBody User userDetail) {
        if(medicareConfigProperties.getIsDemo().equals("true")){
            throw new ServiceException("演示系统不允许修改密码！");
        }
    	String id = this.service.updatePersonAndPass(userDetail);
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

}
