package com.geeke.collect.service;

import java.util.*;

import com.geeke.admin.common.dao.PermissionDAO;
import com.geeke.admin.entity.Router;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.service.CrudService;
import com.geeke.collect.dao.SysCollectDao;
import com.geeke.collect.entity.SysCollect;


/**
 * 收藏夹Service
 *
 * @author szy
 * @version 2021-07-28
 */

@Service("sysCollectService")
public class SysCollectService extends CrudService<SysCollectDao, SysCollect> {

    @Autowired
    private SysCollectDao sysCollectDao;

    @Autowired
    private PermissionDAO permissionDAO;


    /**
     * 移动排序批量修改序号，返回用户最新信息
     *
     * @param sysCollectList
     * @param userId
     * @return
     */
    @Transactional
    public List<SysCollect> updateBatch(List<SysCollect> sysCollectList, String userId) {

        if (CollectionUtils.isNotEmpty(sysCollectList)) {

            for (int i = 0; i < sysCollectList.size(); i++) {
                SysCollect sysCollect = sysCollectList.get(i);
                sysCollect.preUpdate();
                sysCollect.setSort((long)(i+1));
            }
            //批量更新
            sysCollectDao.updateBatch(sysCollectList);
        }
        List<SysCollect> sysCollects = sysCollectDao.listAllByUserId(userId);
        //返回最新的收藏数据
        return sysCollects != null ? sysCollects : new ArrayList<>();
    }

    /**
     * 根据用户查询收藏列表
     *
     * @param sysCollect
     * @return
     */
    public List<SysCollect> listAllByUserId(String  userId) {
        if (StringUtils.isBlank(userId)) {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_90003, "参数用户id不能为空。"));
        }

        List<SysCollect> sysCollectList = sysCollectDao.listAllByUserId(userId);

        // List<String>  stringSet=new ArrayList<>();
        // if (CollectionUtils.isNotEmpty(sysCollectList)) {
        //     //获取当前登录人 路由列表
        //     List<Router> routers = permissionDAO.listRouterPermission(userId);
        //     for (Router router : routers) {
        //         stringSet.add(router.getId());
        //     }
        //     Iterator<SysCollect> it = sysCollectList.iterator();
        //     while (it.hasNext()) {
        //         SysCollect sysCollect1 = it.next();
        //         if (!stringSet.contains(sysCollect1.getRouterId().getId())) {
        //             it.remove();
        //         }
        //     }
        // }
        return sysCollectList != null ? sysCollectList : new ArrayList<>();
    }
}