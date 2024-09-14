package com.geeke.admin.service;

import com.geeke.admin.dao.ResourceDao;
import com.geeke.admin.entity.Resource;
import com.geeke.common.service.CrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 资源管理Service
 * @author lys
 * @version 2021-11-18
 */
 
@Service("resourceService")
@Transactional(readOnly = true)
public class ResourceService extends CrudService<ResourceDao, Resource>{

    public List<Resource> listAllByVersion(String roleId){
        return this.dao.listAllByVersion(roleId);
    }


}