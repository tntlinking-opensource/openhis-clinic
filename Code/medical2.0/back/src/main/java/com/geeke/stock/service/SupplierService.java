package com.geeke.stock.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.stock.dao.SupplierDao;
import com.geeke.stock.entity.Supplier;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 供应商管理Service
 * @author txl
 * @version 2022-06-22
 */
 
@Service("supplierService")
@Transactional(readOnly = true)
public class SupplierService extends CrudService<SupplierDao, Supplier>{
     @Autowired
     private SupplierDao supplierDao;
    @Override
    @Transactional(readOnly = false)
    public Supplier save(Supplier supplier) {
        Map<String, String> colMaps = Maps.newHashMap();
        // 供应商已存在
        colMaps.clear();
        colMaps.put("name", "name");

        /*if(exists(dao, supplier, colMaps)) {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "供应商已存在"));
        }*/
        //校验供应商是否存在
       if(supplier.getId()==null){
           String by = supplierDao.findBy(supplier.getName(), supplier.getCompany().getId());
           if(by!=null){
               throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "供应商已存在"));

           }
       }
        Supplier supplierTemp = super.save(supplier);
        return supplierTemp;
    }
    

}