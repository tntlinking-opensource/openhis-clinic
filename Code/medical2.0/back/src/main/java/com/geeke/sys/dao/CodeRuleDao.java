package com.geeke.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geeke.common.persistence.CrudDao;
import com.geeke.sys.entity.CodeRule;

/**
 * 系统编码规则DAO接口
 * @author lys
 * @version 2021-05-17
 */
@Mapper
public interface CodeRuleDao extends CrudDao<CodeRule> {
	
	/**
	 * 不在公司的规则
	 * @param companyId
	 * @return
	 */
	public List<CodeRule> listNotInCompany(String companyId);
	
	/**
	 * 获取编码规则
	 * @param code
	 * @return
	 */
	public String getRuleDefByCode( @Param("code") String code);
}