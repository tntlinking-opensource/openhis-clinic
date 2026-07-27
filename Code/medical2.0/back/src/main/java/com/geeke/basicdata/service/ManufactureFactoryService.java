package com.geeke.basicdata.service;

import com.geeke.basicdata.dao.ManufactureFactoryDao;
import com.geeke.basicdata.entity.ManufactureFactory;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.common.service.ServiceException;
import com.geeke.org.service.CompanyService;


import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 生产厂家Service
 * @author txl
 * @version 2022-06-22
 */
 
@Service("manufactureFactoryService")
@Transactional(readOnly = true)
public class ManufactureFactoryService extends CrudService<ManufactureFactoryDao, ManufactureFactory>{
    @Autowired
    private ManufactureFactoryDao manufactureFactoryDao;

    /** 生产厂家为机构共享字典：允许同机构下诊所访问机构诊所的数据（与列表 #{institution} SQL 一致） */
    @Override
    protected boolean isInstitutionShared() {
        return true;
    }
    @Autowired
    private CompanyService companyService;
    @Override
    @Transactional(readOnly = false)
    public ManufactureFactory save(ManufactureFactory manufactureFactory) {
        Map<String, String> colMaps = Maps.newHashMap();
        // 生产厂家已存在
        colMaps.clear();
        colMaps.put("name", "name");
        
        // duplicate check handled below by findBy
        //校验生产厂家是否存在
       if(manufactureFactory.getId()==null){
           String by = manufactureFactoryDao.findBy(manufactureFactory.getName(), manufactureFactory.getCompany().getId());
           if(by!=null){
               throw new ServiceException("生产厂家已存在");

           }
       }

        ManufactureFactory manufactureFactoryTemp = super.save(manufactureFactory);
        return manufactureFactoryTemp;
    }

    /**
     * 查询生产厂家列表
     * @param parameters 查询参数列表
     * @param orderby 排序条件
     * @return 生产厂家列表
     */
public List<ManufactureFactory> listAlls(List<Parameter> parameters, String orderby) {
    PageRequest pageRequest = buildTenantPageRequest(parameters, orderby);
    return dao.listAlls(pageRequest);
}


    public String repeatBy(ManufactureFactory manufactureFactory){
      return  manufactureFactoryDao.findBy(manufactureFactory.getName(), manufactureFactory.getCompany().getId());
    }
    

}