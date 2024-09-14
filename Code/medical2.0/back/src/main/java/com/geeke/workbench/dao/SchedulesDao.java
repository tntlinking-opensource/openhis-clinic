package com.geeke.workbench.dao;

import com.geeke.common.data.Page;
import com.geeke.outpatient.entity.PageRegistration;
import com.geeke.outpatient.entity.Registration;
import com.geeke.workbench.entity.VisitProgress;
import com.geeke.workbench.entity.Visitprogresspara;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;


@Mapper
public interface SchedulesDao  {

    int updateoverlookid(@Param("recipelInfoIds") String recipelInfoIds);

//    List<VisitProgress> visitprogresspage(Visitprogresspara visitprogresspara);

      List<VisitProgress> visitprogresspage(Visitprogresspara visitprogresspara);

    List<VisitProgress> visitprogresspagecounts(Visitprogresspara visitprogresspara);
     List<String> listPages(PageRegistration pageRegistration);
     List<String> listPagejyjc(PageRegistration pageRegistration);
}
