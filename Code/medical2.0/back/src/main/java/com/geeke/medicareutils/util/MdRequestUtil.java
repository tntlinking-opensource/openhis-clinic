package com.geeke.medicareutils.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.geeke.admin.entity.User;
import com.geeke.medicareutils.config.MedicareConfigProperties;
import com.geeke.medicareutils.domain.reqpo.RequestData;
import com.geeke.org.entity.ClinicOffice;
import com.geeke.outpatient.entity.MedicalRecord;
import com.geeke.outpatient.entity.PatientMdData;
import com.geeke.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 医保请求封装
 */
@Component
@RequiredArgsConstructor
public class MdRequestUtil {
    //医保配置参数
    private final MedicareConfigProperties medicareConfigProperties;
    //请求封装
    private  final RestTemplate restTemplate;
    //采用redis存储签到流水号
    private final StringRedisTemplate stringRedisTemplate;


    /**
     * 获取医保信息
     * @param infoNo
     * @return
     */
    public JSONObject getMedicareData(String infoNo,JSONObject jsonObject) {
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestData = createRequestData(infoNo,jsonObject);
        HttpEntity<String> request = new HttpEntity<>(JSON.toJSONString(requestData) ,headers);
        String response = restTemplate.postForObject(medicareConfigProperties.getUrl(), request,String.class);
        return JSONObject.parseObject(response).getJSONObject("output");
    }


    /**
     * 获取医保文件信息
     * @param infoNo
     * @return
     */
    public File getMedicareFile(String infoNo, JSONObject object,String fileName) {
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        String requestData = createRequestData(infoNo,object);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestData, headers);
        File file = new File(fileName);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                medicareConfigProperties.getUrl(),
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );

        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throw new RuntimeException("RestTemplate error status code: " + statusCode);
        }
        String contentType = Objects.requireNonNull(responseEntity.getHeaders().getContentType()).toString();
        if (contentType.contains("application/octet-stream")) {
            byte[] content = responseEntity.getBody();
            if (content != null) {

                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(content);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return file;
    }



    /**
     * 构建主体请求参数
     * @param infoNo
     * @param
     * @return
     */
    private String createRequestData(String infoNo,JSONObject jsonObject) {
        RequestData requestData = new RequestData();
        //获取定点医疗机构代码与名称
        String FixmedinsCode = SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode");
        String FixmedinsName = SessionUtils.getUserJson().getJSONObject("company").getString("name");
        requestData.setInfno("5101");
        //顺序号(4)
        String orderNum =  "1234";
        //发送方id
        String MsgId =  FixmedinsCode + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+ orderNum;
        requestData.setMsgid(MsgId);
        //就医医保区划
        requestData.setMdtrtarea_admvs("value4");
        //参保地医保区划
        requestData.setInsuplc_admdvs("value3");
        //接收方系统代码
        requestData.setRecer_sys_code("value5");
        //设备编号
        requestData.setDev_no("value6");
        //设备安全信息
        requestData.setDev_safe_info("value7");
        //医保接入安全码数字签名信息
        requestData.setCainfo("value8");
        //签名类型  建议SM2、SM3
        requestData.setSigntype("value9");
        //接口版本号
        requestData.setInfver("value9");
        //1-经办人；2-自助终端；3-移动终端
        requestData.setOpter_type("value10");
        //传入经办人/终端编号
        requestData.setOpter("value11");
        //经办人姓名
        requestData.setOpter_name("value12");
        //接口交易时间
        requestData.setInf_time(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        //设置定点医疗机构代码
        requestData.setFixmedins_code(FixmedinsCode);
        //设置定点医疗机构名称
        requestData.setFixmedins_name(FixmedinsName);
        //签到接口
        //从redis中获取签到流水号
        requestData.setSign_no(stringRedisTemplate.opsForValue().get("sign_no"));
        if(infoNo.equals("9001")){
            requestData.setSign_no("");
        }
        // 设置 input.data数据
        JSONObject json = (JSONObject) JSON.toJSON(requestData);
        json.put("input",jsonObject);
        return json.toJSONString();
    }



    //签到或签退
    public JSONObject getMedicareSignNo(String infoNo,JSONObject jsonObject) {
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestData = createRequestData(infoNo,jsonObject);
        HttpEntity<String> request = new HttpEntity<>(requestData ,headers);
        String response = restTemplate.postForObject(medicareConfigProperties.getUrl(), request,String.class);
        if(infoNo.equals("9001")){
            //保存8小时签到码
            stringRedisTemplate.opsForValue().set("sign_no",JSONObject.parseObject(response).getString("sign_no"),8, TimeUnit.HOURS);
        }
        if(infoNo.equals("9002")){
            //删除签到码
            stringRedisTemplate.delete("sign_no");
        }
        return JSONObject.parseObject(response);
    }





















    public static void main(String[] args) {
RequestData requestData = new RequestData();
    //获取定点医疗机构代码与名称
    //String FixmedinsCode = SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode");
    //String FixmedinsName = SessionUtils.getUserJson().getJSONObject("company").getString("name");
        requestData.setInfno("5101");
    //发送方id
   // String MsgId =  FixmedinsCode + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+ "1234";
     //   requestData.setMsgid(MsgId);
    //就医医保区划
        requestData.setMdtrtarea_admvs("value4");
    //参保地医保区划
        requestData.setInsuplc_admdvs("value3");
    //接收方系统代码
        requestData.setRecer_sys_code("value5");
    //设备编号
        requestData.setDev_no("value6");
    //设备安全信息
        requestData.setDev_safe_info("value7");
    //医保接入安全码数字签名信息
        requestData.setCainfo("value8");
    //签名类型  建议SM2、SM3
        requestData.setSigntype("value9");
    //接口版本号
        requestData.setInfver("value9");
    //1-经办人；2-自助终端；3-移动终端
        requestData.setOpter_type("value10");
    //传入经办人/终端编号
        requestData.setOpter("value11");
    //经办人姓名
        requestData.setOpter_name("value12");
    //接口交易时间
        requestData.setInf_time("value13");
    //设置定点医疗机构代码与名称
//        requestData.setFixmedins_code(FixmedinsCode);
//        requestData.setFixmedins_name(FixmedinsName);
    //签到接口
    //从redis中获取签到流水号
       // requestData.setSign_no(stringRedisTemplate.opsForValue().get("sign_no"));
//     Map<String, Object> data = BeanUtil.beanToMap(requestData);
//        InputData inputData = new InputData();
//        inputData.setData(data);
//
//        requestData.setInputData(inputData);

        // 解析现有的 JSON 字符串为 JSONObject
        JSONObject jsonObject =  (JSONObject) JSON.toJSON(requestData);

        // 解析额外的 JSON 字符串
        JSONObject additionalData = new JSONObject();
        additionalData.put("data",JSON.toJSON(requestData));

        // 将 additionalData 添加到 jsonObject 中作为 "input" 节点
        jsonObject.put("input", additionalData);

        // 转换为最终的 JSON 字符串
        String finalJsonString = jsonObject.toJSONString();

        System.err.println(finalJsonString);

        JSONObject data = new JSONObject();
        data.put("hosp_dept_codg","222");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(data);

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("data", jsonArray);
        System.err.println(jsonObject1.toJSONString());

        //单行就诊信息
        JSONObject data1 = new JSONObject();
        //诊断多行数据
        JSONArray jsonArray1 = new JSONArray();
        

        //主要病情描述 TODO
        data1.put("main_cond_dscr", "");
        //病种编码    TODO
        data1.put("dise_code", "");
        //病种名称 TODO
        data1.put("dise_name", "");
        data1.put("birctrl_type", "");
        data1.put("bitctrl_matn_date", "");
        data1.put("matn_type", "");
        data1.put("geso_val", "");
        data1.put("expContent", "");

        //诊断参数
        JSONObject array = new JSONObject();
        //诊断类别 1西医诊断 2中医主病诊断 3中医主证诊断 4手术操作 5.安病种付费病种 TODO 只允许一个诊断

        //TODO 默认有效 1, 0无效
        array.put("vali_flag", "1");
        jsonArray1.add(array);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("mdtrtinfo", data);
        jsonObject2.put("diseinfo", jsonArray1);
        System.out.println("jsonObject2 = " + jsonObject2);

        JSONArray array2 = new  JSONArray();
        JSONObject invinfo = new JSONObject();
        invinfo.put("med_list_codg", ""); // 医疗目录编码
        invinfo.put("fixmedins_hist_id", ""); // 定点医药机构目录编号
        invinfo.put("fixmedins_hist_name", ""); // 定点医药机构目录名称
        invinfo.put("rx_flag", ""); // 处方药标志
        invinfo.put("invdate", ""); // 盘存日期
        invinfo.put("inv_cnt", ""); // 库存数量
        invinfo.put("manu_lotnum", ""); // 生产批号
        invinfo.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        invinfo.put("manu_date", ""); // 生产日期
        invinfo.put("expy_end", ""); // 有效期止
        invinfo.put("memo", ""); // 备注
        invinfo.put("drugtracinfo", ""); // 溯源码节点信息
        JSONObject drugtracinfo = new JSONObject();
        drugtracinfo.put("drug_trac_codg","");//药品追溯码
        array2.add(invinfo);
        JSONArray array1 = new JSONArray();
        array1.add(drugtracinfo);
        JSONObject jsonOb = new JSONObject();
        jsonOb.put("invinfo",array2);
        jsonOb.put("drugtracinfo",array1);
        System.out.println("js = " + jsonOb);


    }




}


