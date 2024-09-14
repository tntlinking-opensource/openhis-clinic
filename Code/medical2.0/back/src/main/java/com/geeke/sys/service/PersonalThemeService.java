package com.geeke.sys.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.sys.dao.PersonalThemeDao;
import com.geeke.sys.entity.PersonalTheme;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 系统主题Service
 * @author lys
 * @version 2021-07-16
 */
 
@Service("personalThemeService")
@Transactional(readOnly = true)
public class PersonalThemeService extends CrudService<PersonalThemeDao, PersonalTheme>{

}