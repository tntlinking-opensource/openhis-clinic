package com.geeke.sys.service;

import cn.hutool.core.util.ObjectUtil;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.sys.dao.PersonalThemeDao;
import com.geeke.sys.dao.SysParamConfigDao;
import com.geeke.sys.entity.PersonalTheme;
import com.geeke.sys.entity.SysParamConfig;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 系统参数配置Service
 * @author lys
 * @version 2021-07-16
 */
 
@Service("sysParamConfigService")
@Transactional(readOnly = true)
public class SysParamConfigService extends CrudService<SysParamConfigDao, SysParamConfig>{

}