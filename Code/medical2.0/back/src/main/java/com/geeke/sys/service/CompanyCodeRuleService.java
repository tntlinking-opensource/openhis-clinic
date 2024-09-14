package com.geeke.sys.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.sys.dao.CompanyCodeRuleDao;
import com.geeke.sys.entity.CompanyCodeRule;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 公司编码规则Service
 * @author lys
 * @version 2021-05-17
 */
 
@Service("companyCodeRuleService")
@Transactional(readOnly = true)
public class CompanyCodeRuleService extends CrudService<CompanyCodeRuleDao, CompanyCodeRule>{

    @Override
    @Transactional(readOnly = false)
    public CompanyCodeRule save(CompanyCodeRule companyCodeRule) {
        Map<String, String> colMaps = Maps.newHashMap();
        // company_id
        colMaps.clear();
        colMaps.put("company_id", "companyId");
        colMaps.put("code", "code");
        
        if(exists(dao, companyCodeRule, colMaps)) {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "已存在相同的数据(公司,编码)"));
        }

        CompanyCodeRule companyCodeRuleTemp = super.save(companyCodeRule);
        return companyCodeRuleTemp;
    }

}