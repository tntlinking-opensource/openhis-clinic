package com.geeke.sys.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.sys.dao.CodeRuleDao;
import com.geeke.sys.entity.CodeRule;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 系统编码规则Service
 * @author lys
 * @version 2021-05-17
 */
 
@Service("codeRuleService")
@Transactional(readOnly = true)
public class CodeRuleService extends CrudService<CodeRuleDao, CodeRule>{

    @Override
    @Transactional(readOnly = false)
    public CodeRule save(CodeRule codeRule) {
        Map<String, String> colMaps = Maps.newHashMap();
        // code
        colMaps.clear();
        colMaps.put("code", "code");
        
        if(exists(dao, codeRule, colMaps)) {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "已存在相同编码"));
        }

        CodeRule codeRuleTemp = super.save(codeRule);
        return codeRuleTemp;
    }

    
    public List<CodeRule> listNotInCompany(String companyId) {
    	return dao.listNotInCompany(companyId);
    }
}