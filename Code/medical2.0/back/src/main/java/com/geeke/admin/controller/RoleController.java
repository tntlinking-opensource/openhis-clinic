package com.geeke.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.Role;
import com.geeke.admin.service.RoleService;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理Controller
 * @author lys
 * @version 2022-05-24
 */
@RestController
@RequestMapping(value = "/admin/role")
public class RoleController extends CrudController<RoleService, Role> {

	@Autowired
	protected RoleService roleService;

	@Override
	protected RoleService getService() {
		return roleService;
	}

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<Role> result = roleService.listAllByTenantId(SessionUtils.getLoginTenantId());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

}