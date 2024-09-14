package com.geeke.outpatient.service;


import com.geeke.common.service.CrudService;
import com.geeke.outpatient.dao.SchedulingDao;
import com.geeke.outpatient.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;

import java.util.*;

@Service("schedulingService")
@Transactional(readOnly = true)
public class schedulingService extends CrudService<SchedulingDao, scheduling>{

    @Autowired
    private SchedulingDao schedulingDao;

    public List<schedulingDTO> getHisuserpblist(schedulingDTO pageRequest) {
        List<schedulingDTO> buserlist= schedulingDao.getpbuserlist(pageRequest);
        if (Objects.nonNull(buserlist)){
            for (schedulingDTO buser : buserlist ){
                //String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

                    List<weekdayDTO> weeks = new ArrayList<>();
                    if (buser.getPbsl() > 0) {
                        buser.setCompanyid(pageRequest.getCompanyid());
                        buser.setStratime(pageRequest.getStratime());
                        buser.setEndtime(pageRequest.getEndtime());
                        List<scheduling> schedulingList=schedulingDao.listscheduling(buser);
                        weekdayDTO week=new weekdayDTO();
                        for (scheduling list:schedulingList)
                        {
                            if(list.getDelFlag().equals("1")){
                                continue;
                            }

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(list.getSchedulingtime());

                            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
                            Date schedulingtime = list.getSchedulingtime();
                            List<scheduling> list1 =  schedulingDao.getList(schedulingtime);
                            switch (w) {
                                case 0:
                                    for (scheduling ls : list1){
                                        week.setSunday(ls.getTimeid());
                                    }
                                    break;
                                case 1:
                                    for (scheduling ls : list1){
                                        week.setMonday(ls.getTimeid());
                                    }
                                    break;
                                case 2:
                                    for (scheduling ls : list1){
                                        week.setTuesday(ls.getTimeid());
                                    }
                                    break;
                                case 3:
                                    for (scheduling ls : list1){
                                        week.setWednesday(ls.getTimeid());
                                    }
                                    break;
                                case 4:
                                    for (scheduling ls : list1){
                                        week.setThursday(ls.getTimeid());
                                    }
                                    break;
                                case 5:
                                    for (scheduling ls : list1){
                                        week.setFriday(ls.getTimeid());
                                    }
                                    break;
                                case 6:
                                    for (scheduling ls : list1){
                                        week.setSaturday(ls.getTimeid());
                                    }
                                    break;
                            }
                        }
                        weeks.add(week);
                    }else {
                        weekdayDTO week=new weekdayDTO();
                        weeks.add(week);
                    }
                    buser.setDates(weeks);

            }

        }
        return buserlist;
    }
    public List<schedulingDTO> getlistzl(schedulingDTO pageRequest) {
        List<schedulingDTO> buserlist= schedulingDao.getpbuserlist(pageRequest);
        if (Objects.nonNull(buserlist)){
            for (schedulingDTO buser : buserlist ){
                //String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

                weekdayDTO week=new weekdayDTO();
                List<String> dates=new ArrayList<>();
                if (buser.getPbsl() > 0) {
                    buser.setCompanyid(pageRequest.getCompanyid());
                    buser.setStratime(pageRequest.getStratime());
                    buser.setEndtime(pageRequest.getEndtime());
                    List<scheduling> schedulingList=schedulingDao.listscheduling(buser);


                    for (scheduling list:schedulingList)
                    {
                        if(list.getDelFlag().equals("1")){
                            continue;
                        }

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(list.getSchedulingtime());
                        //这个是你要转成后的时间的格式
                        SimpleDateFormat sdff=new SimpleDateFormat("yyyy-MM-dd");
                        // 时间戳转换成时间
                        String datetieme = sdff.format(list.getSchedulingtime());
                        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
                        switch (w) {
                            case 0:
                                week.setSunday(datetieme);
                                break;
                            case 1:
                                week.setMonday(datetieme);
                                break;
                            case 2:
                                week.setTuesday(datetieme);
                                break;
                            case 3:
                                week.setWednesday(datetieme);
                                break;
                            case 4:
                                week.setThursday(datetieme);
                                break;
                            case 5:
                                week.setFriday(datetieme);
                                break;
                            case 6:
                                week.setSaturday(datetieme);
                                break;
                        }
                    }

                    if (week.getMonday()!=null){dates.add(week.getMonday());}else dates.add(null);
                    if (week.getTuesday()!=null){dates.add(week.getTuesday());}else dates.add(null);
                    if (week.getWednesday()!=null){dates.add(week.getWednesday());}else dates.add(null);
                    if (week.getThursday()!=null){dates.add(week.getThursday());}else dates.add(null);
                    if (week.getFriday()!=null){dates.add(week.getFriday());}else dates.add(null);
                    if (week.getSaturday()!=null){dates.add(week.getSaturday());}else dates.add(null);
                    if (week.getSunday()!=null){dates.add(week.getSunday());}else dates.add(null);
                    buser.setDatelist(dates);
                }

                buser.setDatelist(dates);

            }

        }
        return buserlist;
    }

    public List<schedulingDTO> listzlr(schedulingDTO pageRequest) {
        List<schedulingDTO> buserlist= schedulingDao.getpbuserlist(pageRequest);
        if (Objects.nonNull(buserlist)){
            for (schedulingDTO buser : buserlist ){
                //String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

                List<Integer> house=new ArrayList<>();
                if (buser.getPbsl() > 0) {
                    buser.setCompanyid(pageRequest.getCompanyid());
                    buser.setDatestra(pageRequest.getDatestra());
                    buser.setDateend(pageRequest.getDateend());
                    List<schedulingmxDTO> schedulingList=schedulingDao.listschedulingmx(buser);

                    for (schedulingmxDTO list:schedulingList)
                    {
                        if (list.getHouser()!=null){
                            house.add(list.getHouser());
                        }
                    }

                    //buser.setDatehouse(house);
                }

                buser.setDatehouse(house);

            }

        }
        return buserlist;
    }
    public List<schedulingmxDTO> getlistmx(schedulingDTO pageRequest)
    {
        return schedulingDao.listschedulingmx(pageRequest);
    }


    public int listcount(scheduling scheduling){
        List<scheduling> schedulingList=schedulingDao.listcount(scheduling);
        int count=0;
        if(schedulingList == null){
            return count;
        }else{
            count=schedulingList.size();
        }
        return count;
    }


}
