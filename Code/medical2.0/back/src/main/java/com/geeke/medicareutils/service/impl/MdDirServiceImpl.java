package com.geeke.medicareutils.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.geeke.medicareutils.service.MdDirService;
import com.geeke.medicareutils.util.MdRequestUtil;
import com.geeke.sys.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MdDirServiceImpl implements MdDirService {

    private  final MdRequestUtil mdRequestUtil;

    /**
     * 目录对照上传
     *
     * @return
     */
    @Override
    public JSONObject upDirData_3301() {
        JSONObject data = new JSONObject();
        data.put("fixmedins_hilist_id", ""); // 定点医药机构目录编号
        data.put("fixmedins_hilist_name", ""); // 定点医药机构目录名称
        data.put("list_type", ""); // 目录类别
        data.put("med_list_codg", ""); // 医疗目录编码
        data.put("begndate", ""); // 开始日期
        data.put("enddate", ""); // 结束日期
        data.put("aprvno", ""); // 批准文号
        data.put("dosform", ""); // 剂型
        data.put("exct_cont", ""); // 除外内容
        data.put("item_cont", ""); // 项目内涵
        data.put("prcunt", ""); // 计价单位
        data.put("spec", ""); // 规格
        data.put("pacspec", ""); // 包装规格
        data.put("memo", ""); // 备注
        data.put("scdz", ""); // 产地
        data.put("prdr_name", ""); // 生产厂家名称
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",data);
        return mdRequestUtil.getMedicareData("3301",jsonObject);
    }

    /**
     * 目录对照撤销
     *
     * @return
     */
    @Override
    public JSONObject delDirData_3302() {
        JSONObject data = new JSONObject();
        data.put("fixmedins_code", SessionUtils.getUserJson().getJSONObject("company").getString("fixmedinsCode"));
        data.put("fixmedins_hilist_name", "");
        data.put("list_type", "");
        data.put("med_list_codg", "");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",data);
        return mdRequestUtil.getMedicareData("3302",jsonObject);
    }
}
