package com.geeke.sys.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.service.CrudService;
import com.geeke.sys.dao.QueryConditionDao;
import com.geeke.sys.entity.QueryCondition;
import com.google.common.collect.Maps;

/**
 * 查询条件Service
 * @author lys
 * @version 2021-07-05
 */
 
@Service("queryConditionService")
public class QueryConditionService extends CrudService<QueryConditionDao, QueryCondition>{


}