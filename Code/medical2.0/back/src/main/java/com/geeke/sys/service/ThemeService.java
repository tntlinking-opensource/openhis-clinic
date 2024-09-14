package com.geeke.sys.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.sys.dao.ThemeDao;
import com.geeke.sys.entity.Theme;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 系统主题Service
 * @author lys
 * @version 2021-09-19
 */
 
@Service("themeService")
@Transactional(readOnly = true)
public class ThemeService extends CrudService<ThemeDao, Theme>{


}