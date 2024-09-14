package com.geeke.admin.common.controller;


import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.User;
import com.geeke.admin.service.UserService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.JwtUtils;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController()
@RequestMapping("/person")
public class PersonController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    
    @GetMapping("/me")
    public ResponseEntity<JSONObject> getCurrentUser() {
    	User user = SessionUtils.getUser();
    	String currentUserId = user.getId();
        User dto = this.userService.get(currentUserId);
        dto.setLoginPassword("");
        return ResponseEntity.ok(ResultUtil.successJson(dto));
    }
    
    @PutMapping("/me")
    public ResponseEntity<JSONObject> updateCurrentUser(@RequestBody User userDetail) {
    	String id = this.userService.updatePersonAndPass(userDetail);
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

}
