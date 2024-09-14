package com.geeke.sys.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.service.CrudService;
import com.geeke.sys.dao.DictItemDao;
import com.geeke.sys.entity.DictItem;

/**
 * 字典项Service
 * @author lys
 * @version 2019-08-23
 */
 
@Service("dictItemService")
@Transactional(readOnly = true)
public class DictItemService extends CrudService<DictItemDao, DictItem>{


}