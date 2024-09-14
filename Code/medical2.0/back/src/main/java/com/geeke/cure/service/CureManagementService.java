package com.geeke.cure.service;

import com.geeke.common.service.CrudService;
import com.geeke.cure.dao.CureManagementDao;
import com.geeke.cure.entity.CureManagement;

import com.geeke.org.entity.Company;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("cureManagementService")
@Transactional(readOnly = true)
public class CureManagementService extends CrudService<CureManagementDao, CureManagement> {


}
