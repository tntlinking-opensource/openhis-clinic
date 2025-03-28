package com.geeke.medicareutils.util;

import cn.hutool.http.HttpUtil;
import com.geeke.medicareutils.config.MedicareConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @Description 医保中间件访问接口
 * @Author
 * @Date 2025/3/26
 */
@Component
@RequiredArgsConstructor
public class YbWebApiUtil {

    private final MedicareConfigProperties medicareConfigProperties;

    private static  final  String YB_URL = "/getMedicareData";

    private static  final  String DZ_CF_URL = "/getDzcfData";

    public String getMedicareData(String infoNo, String jsonObject){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("infoNo", infoNo);
        paramMap.put("jsonObject", jsonObject);
        return HttpUtil.post(medicareConfigProperties.getWebUrl() + YB_URL, paramMap);
    }

    public String getDzcfData(String infoNo, String jsonObject){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("infoNo", infoNo);
        paramMap.put("jsonObject", jsonObject);
        return HttpUtil.post(medicareConfigProperties.getWebUrl()+DZ_CF_URL,paramMap);

    }






}
