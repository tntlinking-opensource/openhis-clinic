package com.geeke.sys.service;

import com.geeke.common.service.CrudService;
import com.geeke.sys.dao.SysFileContentDao;
import com.geeke.sys.dao.SysFileDao;
import com.geeke.sys.entity.SysFile;
import com.geeke.sys.entity.SysFileContent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统附件字节Service
 * @author szy
 * @version 2021-08-26
 */
 
@Service("sysFileContentService")
@Transactional(readOnly = true)
public class SysFileContentService extends CrudService<SysFileContentDao, SysFileContent>{

}