package com.geeke.medicareutils.service;

import com.alibaba.fastjson.JSONObject;


public interface MdDirService {

    /**
     * 目录对照上传
     * @return
     */
    JSONObject upDirData_3301();

    /**
     * 目录对照撤销
     * @return
     */
    JSONObject delDirData_3302();


}
