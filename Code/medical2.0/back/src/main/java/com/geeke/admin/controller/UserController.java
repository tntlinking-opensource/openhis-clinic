package com.geeke.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.entity.User;
import com.geeke.admin.entity.UserExt;
import com.geeke.admin.service.UserExtService;
import com.geeke.admin.service.UserService;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.common.data.Parameter;
import com.geeke.common.data.SearchParamsBuilder;
import com.geeke.common.service.ServiceException;
import com.geeke.medicareutils.config.MedicareConfigProperties;
import com.geeke.medicareutils.service.MdPsnDataService;
import com.geeke.org.entity.ClinicOffice;
import com.geeke.org.entity.Department;
import com.geeke.org.service.ClinicOfficeService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理Controller
 * @author lys
 * @version 2021-08-25
 */
@RestController
@RequestMapping(value = "/admin/user")
@RequiredArgsConstructor
public class UserController extends CrudController<UserService, User> {

    protected final UserService service;

    private final UserExtService userExtService;

    private final ClinicOfficeService clinicOfficeService;

    private final MedicareConfigProperties medicareConfigProperties;

    private final MdPsnDataService mdPsnDataService;

    @Override
    protected UserService getService() {
        return service;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        User entity = service.get(id);
        UserExt extByUserId = userExtService.getExtByUserId(id);
        entity.setUserExt(extByUserId);
        // 不返回密码
        entity.setLoginPassword("");
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<JSONObject> getByPhone(@PathVariable("phone") String phone) {
        User entity = service.CheckNewUserByPhone(phone);
        if(null!=entity)
        {
            UserExt oldExtInfo = userExtService.getOldExtByUserId(entity.getId());
            if(null != oldExtInfo)
            {
                oldExtInfo.setCompanyId(SessionUtils.getLoginTenantId());
                entity.setUserExt(oldExtInfo);
            }

            // 不返回密码
            entity.setLoginPassword("");
        }
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @Override
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        User user = SessionUtils.getUser();
        //查询所有数据
        Page<User> userPage;
        //供应商人员走旧逻辑
        if("1000".equals(user.getId()) || "1001".equals(user.getId())) {
            userPage = service.listPage(searchParams.getParams(),
                    searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        }
        else
        {

            //诊所管理员查询全部数据
            List<Parameter> params = searchParams.getParams();
            params.set(0,new Parameter());
            userPage = service.listPageForNoAdmin(params,
            searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        }

        // 不返回密码
        if(userPage.getRows() != null) {
            for(User entity: userPage.getRows()){
                entity.setLoginPassword("");
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(userPage));
    }

    @Override
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<User> result = service.listAll(searchParams.getParams(), searchParams.getOrderby());
        // 不返回密码
        if(result != null) {
            for(User entity: result){
                entity.setLoginPassword("");
            }
        }
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "saveWithFile")
    public ResponseEntity<JSONObject> saveWithFile(@RequestParam("entity") String strUser,
                                           @RequestParam("fileIdUploads") MultipartFile[] fileIdUploads,  // 文件: 用户图片
                                           @RequestParam(value = "deleteIds",required = false)String strDeleteIds) throws java.io.IOException {
        User user = JSONObject.parseObject(strUser, User.class);
        user.setLoginName(user.getPhone());
        // test
        // 设置部门id
        List<Parameter> list = SearchParamsBuilder.create()
                .eq("company_id", user.getCompany().getId())
                .eq("name", user.getUserExt().getOffice())
                .build();
        String departmentId = clinicOfficeService.listAll(list, "").get(0).getId();
        Department department = new Department();
        department.setId(departmentId);
        user.setDepartment(department);
        user.getUserExt().setOfficeCode(clinicOfficeService.listAll(list, "").get(0).getCode());
        String[] deleteIds = JSONObject.parseObject(strDeleteIds, String[].class);
        String id = service.save(user,fileIdUploads,deleteIds).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "update")
    public ResponseEntity<JSONObject> update(@RequestParam("entity") String strUser,
                                             @RequestParam("fileIdUploads") MultipartFile[] fileIdUploads,  // 文件: 用户图片
                                             @RequestParam("deleteIds")String strDeleteIds) throws java.io.IOException  {

        User user = JSONObject.parseObject(strUser, User.class);
        List<Parameter> list = SearchParamsBuilder.create()
                .eq("company_id", user.getCompany().getId())
                .eq("name", user.getUserExt().getOffice())
                .build();
        ClinicOffice clinicOffice = clinicOfficeService.listAll(list, "").get(0);
        user.getUserExt().setOfficeCode(clinicOffice.getCode());
        //user.setLoginName(user.getPhone());
        String[] deleteIds = JSONObject.parseObject(strDeleteIds, String[].class);
        String id = service.update(user,fileIdUploads,deleteIds).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @Override
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody User entity) {
        if(medicareConfigProperties.getIsDemo().equals("true")){
            throw new ServiceException("演示系统请勿删除用户！");
        }
        int all = service.countClinicIdByPhone(entity.getPhone());
        service.delete(entity,all);
        return ResponseEntity.ok(ResultUtil.successJson(all));
    }


    /**
     * 修改 密码
     */
    @PutMapping("/{id}/loginPassword")
    public ResponseEntity<JSONObject> changeLoginPassword(@PathVariable("id") String id, String password) {
        int rows = 0;
         if(medicareConfigProperties.getIsDemo().equals("true")){
             throw new ServiceException("演示系统请勿修改密码！");
         }
        if(!StringUtils.isBlank(id)) {
            rows = service.changeLoginPassword(id, password);
        }
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

}