package com.geeke.utils;

import com.geeke.admin.entity.User;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Parameter;

import java.util.List;
import java.util.stream.Collectors;

public class CheckUtils {

    /**
     * 判断是否是超级管理员
     * @param user
     * @return
     */
    public static boolean isAdmin(User user){
        String id = user.getId();
        boolean result = false;
        if(id.equals("2000") || id.equals("2001") || id.equals("1001"))
        {
            result = true;
        }
        return result;
    }

    /**
     * 判断是否是超级管理员
     * @param params
     * @return
     */
    public static boolean isTenantQuery(SearchParams params){
        List<Parameter> list = params.getParams();
        List<String> collect = list.stream().map(p -> p.getColumnName()).collect(Collectors.toList());
        if(collect.contains("tenant_id"))
        {
            return true;
        }
        return false;
    }
}
