package com.geeke.basicdata.service;

import com.geeke.basicdata.dao.ManufactureFactoryDao;
import com.geeke.basicdata.entity.ManufactureFactory;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.org.service.CompanyService;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @Autowired
    private CompanyService companyService;
    @Override
    @Transactional(readOnly = false)
    public ManufactureFactory save(ManufactureFactory manufactureFactory) {
        Map<String, String> colMaps = Maps.newHashMap();
        // 生产厂家已存在
        colMaps.clear();
        colMaps.put("name", "name");
        
       /* if(exists(dao, manufactureFactory, colMaps)) {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "生产厂家已存在"));
        } */
        //校验生产厂家是否存在
       if(manufactureFactory.getId()==null){
           String by = manufactureFactoryDao.findBy(manufactureFactory.getName(), manufactureFactory.getCompany().getId());
           if(by!=null){
               throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "生产厂家已存在"));

           }
       }

        ManufactureFactory manufactureFactoryTemp = super.save(manufactureFactory);
        return manufactureFactoryTemp;
    }

    public List<ManufactureFactory> listAlls(List<Parameter> parameters, String orderby) {
        Optional<Parameter> cartOptional = parameters.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        parameters.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        PageRequest pageRequest = new PageRequest(parameters, orderby, id, institution);
        return dao.listAlls(pageRequest);
    }

    public String repeatBy(ManufactureFactory manufactureFactory){
      return  manufactureFactoryDao.findBy(manufactureFactory.getName(), manufactureFactory.getCompany().getId());
    }
    

}