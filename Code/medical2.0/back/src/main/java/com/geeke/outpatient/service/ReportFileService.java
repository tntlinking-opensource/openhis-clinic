package com.geeke.outpatient.service;

import com.geeke.common.service.CrudService;
import com.geeke.outpatient.dao.UReportFileDao;
import com.geeke.outpatient.entity.UReportFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("ReportFileService")
@Transactional(readOnly = true)
public class ReportFileService extends CrudService<UReportFileDao, UReportFile> {

    public UReportFile getReportFileByName(String name) {
        return this.dao.getReportFileByName(name);
    }

    public List<UReportFile> getListAll() {
        return this.dao.getListAll();
    }
}
