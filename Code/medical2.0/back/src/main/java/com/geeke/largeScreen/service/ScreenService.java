package com.geeke.largeScreen.service;


import com.geeke.largeScreen.dao.ScreenDao;
import com.geeke.largeScreen.entity.Basics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据大屏Service
 * @author zhanghan
 * @version 2021-10-12
 */
 
@Service("ScreenService")
@Transactional(readOnly = false)
public class ScreenService{
    @Autowired
    private ScreenDao screenDao;

    /** 获取大屏基础数据 */
    public Basics getBasics(String parentId) {
        Basics basics = new Basics();
        basics.setQuest(screenDao.getQuest(parentId));
        basics.setRegister(screenDao.getRegister(parentId));
        basics.setReceivePatients(screenDao.getReceivePatients(parentId));
        basics.setIncome(screenDao.getIncome(parentId));
        // basics.setSigning(screenDao.getSigning(parentId));

        return basics;
    }


}