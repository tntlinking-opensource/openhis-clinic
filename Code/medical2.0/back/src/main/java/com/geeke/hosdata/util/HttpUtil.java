package com.geeke.hosdata.util;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Parameter;
import com.geeke.hosdata.config.HosConfigProperties;
import com.geeke.hosdata.constant.ApiUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求院版数据封装
 */
@Component
@RequiredArgsConstructor
public class HttpUtil {

    private final HosConfigProperties hosConfigProperties ;

    private  final RestTemplate restTemplate;

    /** 本地Token缓存（替代Redis） */
    private String tokenCache;
    private LocalDateTime tokenExpireTime;


    public  JSONObject getHosData(SearchParams params, String apiUrl){
        Map<String, Object> data = new HashMap<>();
        Map<String, Object>  searchParams = new HashMap<>();
        Map<String, Object>  data2 = new HashMap<>();
        for (Parameter param : params.getNewParams()){
            searchParams.put(param.getColumnName(),param.getValue());
        }
        data2.put("offset",params.getOffset());
        //暂时查询全部
        data2.put("limit",params.getLimit());
        data2.put("queryParams",searchParams);
        data.put("Data",data2);
        data.put("AppId",hosConfigProperties.getAppId());
        data.put("OrganizeId", hosConfigProperties.getOrganizeId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 添加 token 到请求头
        String token = getToken();
        headers.set("Authorization", "Bearer " + token);
        // 创建 HttpEntity，并设置请求体和请求头
        HttpEntity<Map<String,Object>> request = new HttpEntity<>(data, headers);
        String response = restTemplate.postForObject(hosConfigProperties.getHosDataServerUrl()+apiUrl, request,String.class);
        JSONObject jsonObject = JSONObject.parseObject(response);
        return jsonObject;
    }


    /**
     * 修改出入库状态
     * @param apiUrl
     * @param crkmxId
     * @return
     */
     public  Boolean UpdateHosData(String apiUrl,String crkmxId){
         // 构造请求JSON数据
         JSONObject requestData = new JSONObject();
         JSONObject data = new JSONObject();
         data.put("crkmxId",crkmxId);
         requestData.put("Data", data);
         requestData.put("AppId", hosConfigProperties.getAppId());
         requestData.put("OrganizeId", hosConfigProperties.getOrganizeId());

         // 构造HttpEntity对象
         // 设置请求头
         HttpHeaders headers = new HttpHeaders();
         String token = getToken();
         headers.set("Authorization", "Bearer " + token);
         headers.setContentType(MediaType.APPLICATION_JSON);
         HttpEntity<String> requestEntity = new HttpEntity<>(requestData.toString(), headers);
         ResponseEntity<String> responseEntity = restTemplate.postForEntity(hosConfigProperties.getHosDataServerUrl()+apiUrl, requestEntity, String.class);
         return responseEntity.getStatusCodeValue() == 200;
     }



    public  String getHosToken(){
        Map<String, Object> data = new HashMap<>();
        data.put("AppId",hosConfigProperties.getAppId());
        data.put("Data", "API.Manage");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 创建 HttpEntity，并设置请求体和请求头
        HttpEntity<Map<String,Object>> request = new HttpEntity<>(data, headers);
        String response = restTemplate.postForObject(hosConfigProperties.getHosTokenUrl()+ApiUrl.HOS_GET_TOKEN, request,String.class);
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONObject busData = jsonObject.getJSONObject("BusData").getJSONObject("data");
        String token = busData.getString("Token");
        // 缓存Token 20分钟过期
        tokenCache = token;
        tokenExpireTime = LocalDateTime.now().plusMinutes(20);
        return token;
    }

    /** 获取Token，缓存有效直接返回，否则重新获取 */
    private String getToken() {
        if (tokenCache != null && tokenExpireTime != null && LocalDateTime.now().isBefore(tokenExpireTime)) {
            return tokenCache;
        }
        return getHosToken();
    }


}
