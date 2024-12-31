package com.geeke.medicareutils.service;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


public interface MdDirService {

    /**
     * 目录对照上传
     * @return
     */
    JSONObject upDirData();

    /**
     * 目录对照撤销
     * @return
     */
    JSONObject delDirData();


}
