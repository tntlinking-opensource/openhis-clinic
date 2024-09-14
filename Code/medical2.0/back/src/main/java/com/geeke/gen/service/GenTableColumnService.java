package com.geeke.gen.service;

import com.geeke.common.service.CrudService;
import com.geeke.gen.dao.GenTableColumnDao;
import com.geeke.gen.entity.GenTableColumn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 业务字段表Service
 * @author lys
 * @version 2019-08-31
 */
 
@Service("genTableColumnService")
@Transactional(readOnly = true)
public class GenTableColumnService extends CrudService<GenTableColumnDao, GenTableColumn>{


}