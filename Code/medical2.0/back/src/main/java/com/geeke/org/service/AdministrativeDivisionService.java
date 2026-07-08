package com.geeke.org.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.org.dao.AdministrativeDivisionDao;
import com.geeke.org.entity.AdministrativeDivision;
import com.geeke.utils.StringUtils;
import com.google.common.collect.Maps;

/**
 * 行政区域划分Service
 * @author txl
 * @version 2022-06-21
 */
 
@Service("administrativeDivisionService")
@Transactional(readOnly = true)
public class AdministrativeDivisionService extends CrudService<AdministrativeDivisionDao, AdministrativeDivision>{

    

}