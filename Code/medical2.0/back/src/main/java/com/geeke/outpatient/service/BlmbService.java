package com.geeke.outpatient.service;

import com.geeke.common.data.Page;
import com.geeke.outpatient.dao.BlmbDao;
import com.geeke.outpatient.entity.Blmb;
import com.geeke.outpatient.entity.Blmcxrc;
import com.geeke.utils.IdGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service("blmbService")
@Transactional(readOnly = false)
public class BlmbService {

    @Autowired
    private BlmbDao blmbdao;

    /**
     * 添加病历模板数据
     * @param blmb
     * @return
     */
    public int insert(Blmb blmb){
        blmb.setMbbm(IdGen.uuid());
        blmb.setCreatedTime(new Date());
        return blmbdao.insert(blmb);
    }

    public Page<Blmb> getblmblist(Blmcxrc blmcxrc){
        List<Blmb> listtotal=blmbdao.getblmblisttotal(blmcxrc);
        int total=listtotal.size();
        List<Blmb> list=null;
        if(total>0){
            list=blmbdao.getblmblist(blmcxrc);
        }
        return new Page<>((long) total,list);
    }

    public int deletembbm(Blmcxrc blmcxrc){
        blmcxrc.setUpdatedTime(new Date());
        return blmbdao.deletembbm(blmcxrc);
    }

    public List<Blmb> selectmbbm(Blmcxrc blmcxrc){
        return blmbdao.selectmbbm(blmcxrc);
    }
    public int updatembbm(Blmb blmb){
        blmb.setUpdatedTime(new Date());
        return blmbdao.updatembbm(blmb);
    }
}
