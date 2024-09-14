package com.geeke.stock.service;

import java.util.List;
import java.util.Map;

import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.stock.entity.DispensingReportEvt;
import com.geeke.stock.entity.DispensingReportTotalEvt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.stock.dao.DispensingDao;
import com.geeke.stock.entity.Dispensing;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;

/**
 * 发药明细Service
 * @author txl
 * @version 2022-08-11
 */
 
@Service("dispensingService")
@Transactional(readOnly = true)
public class DispensingService extends CrudService<DispensingDao, Dispensing>{

    public Page<DispensingReportEvt> reportList(SearchParams searchParams) {
        PageRequest pageRequest = new PageRequest(searchParams.getOffset(),  searchParams.getLimit(),searchParams.getParams(), searchParams.getOrderby());
        int total = this.dao.reportListCount(pageRequest);
        List<DispensingReportEvt> list = null;
        if (total > 0) {
            list = this.dao.reportList(pageRequest);
        }
        return new Page((long)total, list);
    }

    public DispensingReportTotalEvt reportAmount(SearchParams searchParams) {
        PageRequest pageRequest = new PageRequest(searchParams.getOffset(),  searchParams.getLimit(),searchParams.getParams(), searchParams.getOrderby());
        return this.dao.reportAmount(pageRequest);
    }

    public void updateDelFlag(String recipelInfoId) {
        this.dao.updateDelFlag(recipelInfoId);
    }

    @Transactional(readOnly = true)
    public Page<DispensingReportEvt> getOrganizationList(SearchParams searchParams) {
        PageRequest pageRequest = new PageRequest(searchParams.getOffset(),  searchParams.getLimit(),searchParams.getParams(), searchParams.getOrderby());
        int total = this.dao.getOrganizationListCount(pageRequest);
        List<DispensingReportEvt> list = null;
        if (total > 0) {
            list = this.dao.getOrganizationList(pageRequest);
        }
        return new Page((long)total, list);
    }
}