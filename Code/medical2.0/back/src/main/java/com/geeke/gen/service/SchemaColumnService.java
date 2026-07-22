package com.geeke.gen.service;

import com.geeke.common.service.CrudService;
import com.geeke.gen.dao.SchemaColumnDao;
import com.geeke.gen.entity.GenTableColumn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统表字段控件Service
 * @author lys
 * @version 2019-08-31
 */
 
@Service("schemaColumnService")
@Transactional(readOnly = true)
public class SchemaColumnService extends CrudService<SchemaColumnDao, GenTableColumn>{


}