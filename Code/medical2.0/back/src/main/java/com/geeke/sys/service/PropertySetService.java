package com.geeke.sys.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.sys.dao.PropertySetDao;
import com.geeke.sys.entity.PropertySet;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 属性集管理Service
 * @author lys
 * @version 2021-12-26
 */
 
@Service("propertySetService")
@Transactional(readOnly = true)
public class PropertySetService extends CrudService<PropertySetDao, PropertySet>{


}