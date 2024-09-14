package com.geeke.outpatient.dao;


import com.geeke.outpatient.entity.Blmb;
import com.geeke.outpatient.entity.Blmcxrc;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlmbDao {

    /**
     * 添加病历模板数据
     * @param blmb
     * @return
     */
    int insert(Blmb blmb);

    List<Blmb> getblmblisttotal(Blmcxrc blmcxrc);
    List<Blmb> getblmblist(Blmcxrc blmcxrc);

    int deletembbm(Blmcxrc blmcxrc);

    List<Blmb> selectmbbm(Blmcxrc blmcxrc);
    int updatembbm(Blmb blmb);
}
