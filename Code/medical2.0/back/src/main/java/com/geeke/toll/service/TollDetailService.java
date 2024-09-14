package com.geeke.toll.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.toll.dao.TollDetailDao;
import com.geeke.toll.entity.TollDetail;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 收费管理Service
 * @author lc
 * @version 2022-06-15
 */
 
@Service("tollDetailService")
@Transactional(readOnly = true)
public class TollDetailService extends CrudService<TollDetailDao, TollDetail>{

    

}