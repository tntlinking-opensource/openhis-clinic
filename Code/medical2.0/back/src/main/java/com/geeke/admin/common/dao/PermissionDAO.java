package com.geeke.admin.common.dao;

import com.geeke.admin.common.entity.DataPermission;
import com.geeke.admin.entity.Resource;
import com.geeke.admin.entity.Router;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

//import com.geeke.sys.entity.Resource;


/**
 * 权限管理DAO接口
 * 
 * @author lys
 * @date 2019/7/26
 */
@Mapper
public interface PermissionDAO {

	/**
	 * 根据用户id得到资源权限
	 * 
	 * @param userId
	 *            用户Id
	 * @return
	 */
	//List<Resource> listResourceByUserId(String userId);
	
	/**
	 * 根据路由id得到资源权限，不含禁用的
	 * @param userId
	 * @param routerId
	 * @return
	 */
	List<Resource> listResourcePermission(@Param("userId")String userId, @Param("routerId")String routerId);
	
	
	/**
	 * 获取用户有权限的路由，不含禁用的
	 * @param userId
	 * @return
	 */
	List<Router> listRouterPermission(@Param("userId")String userId,@Param("companyId")String companyId);

	/**
	 * 获取用户有权限的路由，不含禁用的
	 * @param roleId
	 * @return
	 */
	List<Router> listRouterPermissionByRoleId(@Param("roleId")String roleId);
	
	/**
	 * 根据用户id得到资源权限，不含禁用的
	 * @param userId
	 * @return
	 */
	List<String> listResourcePermissionByUserId(@Param("userId")String userId,@Param("companyId")String companyId);
	
	
	/**
	 * 根据角色id得到资源权限
	 * @param roleId
	 * @return
	 */
	List<String> listResourcePermissionByRoleId(String roleId);
	
	
    /**
     * 根据角色id删除路由权限
     * @param roleId
     */
    void removeRouterByRoleId(String roleId);
	
	/**
	 * 根据角色id删除资源权限
	 * @param roleId
	 * @return
	 */
	int removeResourceByRoleId(String roleId);
    

    /**
     * 保存路由权限
     * @param roleId
     * @param routerIds
     * @return
     */
    int saveRouterPermission(@Param("roleId")String roleId,  @Param("map")Map<String, String> map);
	

	/**
	 * 保存资源权限
	 * @param roleId
	 * @param resourceIds
	 * @return
	 */
	int saveResourcePermission(@Param("roleId")String roleId, @Param("map")Map<String, String> map);

	
	/**
	 * 根据角色id得到数据权限
	 * @param roleId
	 * @return
	 */
	List<DataPermission> listDataPermissionByRoleId(String roleId);
	
	
	/**
	 * 删除角色数据权限
	 * @param roleId
	 * @return
	 */
	int removeDataPermssionByRoleId(@Param("roleId") String roleId);
	
	
	int saveDataPermission(@Param("dataPermissions") List<DataPermission> dataPermissions, @Param("ids") String[] ids);
	
	
	/**
	 * 根据用户id得到数据权限
	 * @param userId
	 * @return
	 */
	List<DataPermission> listDataPermissionByUserId(@Param("userId") String userId,@Param("companyId")String companyId);
	
	
	/**
	 * 获取路由的默认资源
	 * @param roleId
	 * @return
	 */
	List<String> listDefaultResourceByRouters(@Param("routers") List<Router> routers);
}
