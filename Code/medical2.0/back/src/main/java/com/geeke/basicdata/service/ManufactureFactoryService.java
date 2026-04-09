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
import java.util.stream.Collectors;

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

    /**
     * 查询生产厂家列表
     * @param parameters 查询参数列表
     * @param orderby 排序条件
     * @return 生产厂家列表
     */
public List<ManufactureFactory> listAlls(List<Parameter> parameters, String orderby) {
    // 从参数列表中筛选出 company_id 参数
    Optional<Parameter> companyOptional = parameters.stream()
            .filter(item -> item.getColumnName().equals("`company_id`"))
            .findFirst();

    // 校验 company_id 参数是否存在
    if (!companyOptional.isPresent()) {
        throw new IllegalArgumentException("Missing required parameter: company_id");
    }

    // 获取 company_id 参数的值
    String id = (String) companyOptional.get().getValue();

    // 创建不包含 company_id 的新参数列表，避免修改原列表
    List<Parameter> filteredParameters = parameters.stream()
            .filter(item -> !item.getColumnName().equals("`company_id`"))
            .collect(Collectors.toList());

    // 根据公司 ID 获取机构信息
    String institution = companyService.getInstitution(id);

    // 构建分页请求对象，包含查询参数、排序条件、公司 ID 和机构信息
    PageRequest pageRequest = new PageRequest(filteredParameters, orderby, id, institution);

    // 调用 DAO 层方法查询生产厂家列表
    return dao.listAlls(pageRequest);
}


    public String repeatBy(ManufactureFactory manufactureFactory){
      return  manufactureFactoryDao.findBy(manufactureFactory.getName(), manufactureFactory.getCompany().getId());
    }
    

}