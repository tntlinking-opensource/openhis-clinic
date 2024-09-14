package com.geeke.grid.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.grid.dao.UserGridDao;
import com.geeke.grid.entity.UserGrid;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 自定义布局Service
 * @author ycy
 * @version 2021-10-12
 */
 
@Service("userGridService")
@Transactional(readOnly = true)
public class UserGridService extends CrudService<UserGridDao, UserGrid>{

    @Override
    @Transactional(readOnly = false)
    public UserGrid save(UserGrid userGrid) {
        Map<String, String> colMaps = Maps.newHashMap();
        // 用户布局唯一
        colMaps.clear();
        colMaps.put("user_id", "userId");
        colMaps.put("router_id", "routerId");
        
        if(exists(dao, userGrid, colMaps)) { // 一个用户在一个路由下 只能有一个布局
            this.doUpdate(userGrid);
            return userGrid;
        }

        UserGrid userGridTemp = super.save(userGrid);
        return userGridTemp;
    }

}