package com.geeke.org.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.org.dao.LesseeDao;
import com.geeke.org.entity.Lessee;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 租户管理Service
 * @author txl
 * @version 2022-05-23
 */
 
@Service("lesseeService")
@Transactional(readOnly = true)
public class LesseeService extends CrudService<LesseeDao, Lessee>{

    

}