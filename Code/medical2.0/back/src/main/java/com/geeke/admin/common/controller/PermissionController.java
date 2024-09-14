package com.geeke.admin.common.controller;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson.JSONObject;
import com.geeke.admin.common.entity.DataPermission;
import com.geeke.admin.common.entity.Permission;
import com.geeke.admin.common.service.PermissionService;
import com.geeke.admin.entity.Resource;
import com.geeke.admin.entity.Router;
import com.geeke.admin.entity.User;
import com.geeke.admin.service.ResourceService;
import com.geeke.admin.service.RouterService;
import com.geeke.clinic.entity.ClinicVersion;
import com.geeke.common.data.Parameter;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源控制器
 * 
 * @author Lining
 * @date 2018/2/11
 */
@RestController("PermissionController")
@RequestMapping("/permission")
public class PermissionController extends BaseController {
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private RouterService routerService;
    
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private CompanyService companyService;
    
    
    @GetMapping("/resources")
    public ResponseEntity<JSONObject> listResourcePermission(@RequestParam(required = false) String routerId) {
    	User user = SessionUtils.getUser();
    	
    	List<Resource> list = this.permissionService.listResourcePermission(user.getId(), routerId);
        return ResponseEntity.ok(ResultUtil.successJson(list));
    }
    
    @GetMapping("/permissions")
    public ResponseEntity<JSONObject> treePermission() {
    	List<Router> listRouter = this.listUnlockedRouter();

    	List<Resource> listResource = this.listUnlockedResource();
    	List<Permission> listPermission = Lists.newArrayList();
    	for(Router router: listRouter ){
    		Permission permission = new Permission();
    		permission.setId(router.getId());
    		permission.setType(Permission.ROUTER_TYPE);
    		permission.setName(router.getName());
    		permission.setCode(router.getCode());
    		permission.setParentId(router.getParent() == null? "": router.getParent().getId());
    		listPermission.add(permission);
    	}
    	
    	for(Resource resource: listResource) {
    		if(resource.getRouter() != null) {
    			Permission permission = new Permission();
    			permission.setId(resource.getId());
    			permission.setType(Permission.RESOURCE_TYPE);
    			permission.setName(resource.getName());
    			permission.setParentId(resource.getRouter().getId());
    			permission.setKey(resource.getId());
    			listPermission.add(permission);
    		}   		    		
    	}
    			
    	List<Permission> trees = this.bulid(listPermission);
    	return ResponseEntity.ok(ResultUtil.successJson(trees));
    }
    
    @GetMapping("/{roleId}/resourcePermissions")
    public ResponseEntity<JSONObject> listResourcePermissionByRoleId(@PathVariable("roleId") String roleId) {
    	List<String> list = this.permissionService.listResourcePermission(roleId);
    	return ResponseEntity.ok(ResultUtil.successJson(list));
    }
    
    @PostMapping("/{roleId}/savePermission")
    public ResponseEntity<JSONObject> savePermission(@PathVariable("roleId") String roleId, @RequestBody Permission permission) {
        int rows = this.permissionService.savePermission(roleId, permission);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }
    
    @GetMapping("/{roleId}/dataPermissions")
    public ResponseEntity<JSONObject> listDataPermissionByRoleId(@PathVariable("roleId") String roleId) {
    	List<DataPermission> list = this.permissionService.listDataPermissionByRoleId(roleId);
    	return ResponseEntity.ok(ResultUtil.successJson(list));
    }
    
    
    
    /**
     * 两层循环实现建树
     * @param list 传入的树节点列表
     * 把list数据通过 id，和parentId够造成一个树
     * @return
     */
    private List<Permission> bulid(List<Permission> list) {
        List<Permission> trees = Lists.newArrayList();
        for (Permission entity : list) {
        	//增加了调试代码
        	if(Permission.ROUTER_TYPE.equals(entity.getType()) && (StringUtils.isBlank(entity.getParentId()) ||
        			"0".equals(entity.getParentId()))) {
                trees.add(entity);
            }

            for (Permission it : list) {
                if (entity.getId().equals(it.getParentId())) {
                    if (entity.getChildren() == null) {
                    	entity.setChildren(Lists.newArrayList());
                    }                	
                	entity.getChildren().add(it);
                }
            }
        }
        return trees;
    }
    
    /**
     * 获取未禁用的路由
     * @return
     */
    private List<Router> listUnlockedRouter() {
        List<Parameter> parms = Lists.newArrayList();
        parms.add(new Parameter("is_locked", "=", "0"));

        
        // 系统级功能路由不能被分配： 3006  代码管理;	3007  路由管理;   3003   资源管理 ;  3011  字典数据;  3009    操作日志; 3024  系统编号规则; 3026 系统主题;
        // parms.add(new Parameter("id", "not in", "(3006, 3007, 3003, 3011, 3009, 3024, 3026)", true));
        
        parms.add(new Parameter("can_permission", "=", "1"));

        Company company = companyService.get(SessionUtils.getLoginTenantId());
        String parentId = Opt.ofNullable(company).map(Company::getParent).map(Company::getId).get();
        if (ObjUtil.isNotNull(company) && ObjUtil.notEqual(parentId, 0) && ObjUtil.notEqual("供应商", company.getName())) {
            //对诊所限定
            ClinicVersion version = company.getVersion();
            String roleId = version.getRoleId();
            return routerService.getAllRoutersByVersion(roleId);
        }
        return this.routerService.listAll(parms, "sort");
    }
    
    
    /**
     * 获取未禁用的资源
     * @return
     */
    private List<Resource> listUnlockedResource() {       
        List<Parameter> parms = Lists.newArrayList();
        parms.add(new Parameter("is_locked", "=", "0"));            
        parms.add(new Parameter("is_default", "=", "0"));		// 默认的不用分配
        /* 不参与分配的资源
         * 公司管理：41105517082353672 添加公司 、54172422505193473 删除公司 
         */
        // parms.add(new Parameter("id", "not in", "(41105517082353672, 54172422505193473)", true));
        
        parms.add(new Parameter("can_permission", "=", "1"));
        Company company = companyService.get(SessionUtils.getLoginTenantId());
        if (ObjUtil.isNotNull(company) && !"0".equals(company.getParent().getId()) && !"供应商".equals(company.getName())) {
            //对诊所限定
            ClinicVersion version = company.getVersion();
            String roleId = version.getRoleId();
            return resourceService.listAllByVersion(roleId);
        }
        return this.resourceService.listAll(parms, null);
    }
}
