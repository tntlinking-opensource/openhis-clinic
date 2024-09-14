package com.geeke.org.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.service.TreeService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import com.geeke.org.dao.DepartmentDao;
import com.geeke.org.entity.Department;
import com.google.common.collect.Maps;

/**
 * 部门管理Service
 * @author lys
 * @version 2021-07-13
 */
 
@Service("departmentService")
@Transactional(readOnly = true)
public class DepartmentService extends TreeService<DepartmentDao, Department> {

	@Override
	@Transactional(readOnly = false)
	public Department save(Department department) {
        Map<String, String> colMaps = Maps.newHashMap();
        // code
        colMaps.clear();
        colMaps.put("company_id", "company.id");
        colMaps.put("code", "code");
        
        if(exists(dao, department, colMaps)) {
            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "同一个公司下已存在相同的部门编码"));
        }

        Department departmentTemp = super.save(department);
        return departmentTemp;
	}

}