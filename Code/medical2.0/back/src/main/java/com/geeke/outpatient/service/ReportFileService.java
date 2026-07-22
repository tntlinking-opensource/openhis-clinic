package com.geeke.outpatient.service;

import com.geeke.common.service.CrudService;
import com.geeke.outpatient.dao.UReportFileDao;
import com.geeke.outpatient.entity.UReportFile;
import com.geeke.utils.SessionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("ReportFileService")
public class ReportFileService extends CrudService<UReportFileDao, UReportFile> {

    public UReportFile getReportFileByName(String name) {
        String companyId = SessionUtils.getLoginTenantId();
        return this.dao.getReportFileByName(name, companyId);
    }

    public List<UReportFile> getListAll() {
        String companyId = SessionUtils.getLoginTenantId();
        return this.dao.getListAll(companyId);
    }
}
