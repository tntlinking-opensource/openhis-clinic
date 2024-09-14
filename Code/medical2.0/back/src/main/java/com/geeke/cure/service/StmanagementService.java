package com.geeke.cure.service;

import com.geeke.common.data.Page;
import com.geeke.cure.dao.StmanagementDao;
import com.geeke.cure.entity.Stmanagement;
import com.geeke.cure.entity.Stparameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service("stmanagementService")
@Transactional(readOnly = false)
public class StmanagementService {

    @Autowired
    private StmanagementDao managementdao;

    public Page<Stmanagement> stmanagementlist(Stparameter stparameter){
        List<Stmanagement> list=managementdao.stmanagementtotal(stparameter);
        int total=list.size();
        List<Stmanagement> list2=null;
        if(total>0){
            list2=managementdao.stmanagementlist(stparameter);
        }
        return  new Page<>((long) total,list2);
    }

    public int updatestresult(Stparameter stparameter){
        stparameter.setZxsj(new Date());
        return managementdao.updatestresult(stparameter);
    }
    public int updatesttime(Stparameter stparameter){
        return managementdao.updatesttime(stparameter);
    }
    public List<Stmanagement> selectdetailid(Stparameter stparameter){
        return managementdao.selectdetailid(stparameter);
    }
}
