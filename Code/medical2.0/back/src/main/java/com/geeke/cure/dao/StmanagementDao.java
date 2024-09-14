package com.geeke.cure.dao;

import com.geeke.cure.entity.Stmanagement;
import com.geeke.cure.entity.Stparameter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StmanagementDao {

    List<Stmanagement> stmanagementtotal(Stparameter stparameter);

    List<Stmanagement> stmanagementlist(Stparameter stparameter);

    int updatestresult(Stparameter stparameter);

    int updatesttime(Stparameter stparameter);

    List<Stmanagement> selectdetailid(Stparameter stparameter);


}
