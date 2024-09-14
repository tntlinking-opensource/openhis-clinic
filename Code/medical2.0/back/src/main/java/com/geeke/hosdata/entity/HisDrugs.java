package com.geeke.hosdata.entity;

import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.sys.entity.DictItem;
import lombok.Data;

import java.io.Serializable;

//院版his药品
@Data
public class HisDrugs extends DataEntity<HisDrugs> implements Serializable {

    private String goodsName;		// 药品名称
    private String companyId;      // 诊所id
    private String brandName;		// 商品别名
    private String type;      // 药品类型  01：西药  03：中药  0002000066 :中成药
    private String code;		// 药品编码
    private String source;		// 药品来源
    private String factory;		// 生产单位
    private String pinyinCode; // 拼音
    private String price;      //价格
    private String dosis;     // 剂量
    private String dosisUnit; //剂量单位
    private String preparation; //制剂
    private String preparationUnit; //制剂单位
    private String pack; //包装单位
    private String standardCode; //国药准字
    private String insuranceCode; //医保编码
    private String status; //状态






}
