package com.geeke.collect.service;

import java.util.*;

import com.geeke.admin.common.dao.PermissionDAO;
import com.geeke.admin.entity.Router;
import com.geeke.common.service.ServiceException;
import com.geeke.utils.StringUtils;
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
            throw new ServiceException("参数用户id不能为空。");
        }

        List<SysCollect> sysCollectList = sysCollectDao.listAllByUserId(userId);
        return sysCollectList != null ? sysCollectList : new ArrayList<>();
    }
}