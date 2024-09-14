package com.geeke.workbench.service;

import com.geeke.common.data.Page;
import com.geeke.outpatient.entity.PageRegistration;
import com.geeke.outpatient.entity.Registration;
import com.geeke.workbench.dao.SchedulesDao;
import com.geeke.workbench.entity.VisitProgress;
import com.geeke.workbench.entity.Visitprogresspara;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("schedulesService")
@Transactional(readOnly = false)
public  class SchedulesService  {
    @Autowired
    private SchedulesDao schedulesDao;

    public int updateoverlookid(String strRecipelInfoIds){
        return schedulesDao.updateoverlookid(strRecipelInfoIds);
    }

//    public Page<VisitProgress> visitProgressPage(Visitprogresspara visitprogresspara){
//        List<VisitProgress> list = schedulesDao.visitprogresspage(visitprogresspara);
//        int total=list.size();
//        return new Page((long)total, list);
//    }

        public Page<VisitProgress> visitProgressPage(Visitprogresspara visitprogresspara){
        List<VisitProgress> list = schedulesDao.visitprogresspagecounts(visitprogresspara);
        int total=list.size();
            List<VisitProgress> list2 = null;
            if(total>0){
                list2 = schedulesDao.visitprogresspage(visitprogresspara);
            }
        return new Page((long)total, list2);
    }

    public List<String> listPages(PageRegistration pageRegistration){
        List<String> list = schedulesDao.listPages(pageRegistration);
        int total=list.size();
        return  list;
    }
    public List<String> listPagesjyjc(PageRegistration pageRegistration){
        List<String> list = schedulesDao.listPagejyjc(pageRegistration);
        int total=list.size();
        return  list;
    }

}
