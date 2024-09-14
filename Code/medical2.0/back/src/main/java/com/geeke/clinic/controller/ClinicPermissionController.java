package com.geeke.clinic.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.common.entity.Permission;
import com.geeke.clinic.service.ClinicPermissionService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 资源控制器
 *
 * @author Lining
 * @date 2018/2/11
 */
@RestController("ClinicPermissionController")
@RequestMapping("/clinic")
public class ClinicPermissionController extends BaseController {

    @Autowired
    private ClinicPermissionService permissionService;

    @PostMapping("/{roleId}/savePermission")
    public ResponseEntity<JSONObject> savePermission(@PathVariable("roleId") String roleId, @RequestBody Permission permission) {
        int rows = permissionService.savePermission(roleId, permission);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

}
