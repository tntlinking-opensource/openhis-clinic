package com.geeke.hosdata.entity;

import com.geeke.common.persistence.DataEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class HisMaterials extends DataEntity<HisMaterials> implements Serializable {
    private String companyId;      // 诊所id
    private String  sfxmmc ; //材料名称
    private String  py ;//拼音码
    private String  sfxmCode; //唯一材料编码
    private String  status; //状态
    private String  dwjls ;//单位计量数
    private String  dw; //最小单位
    private String  gg; //包装规格
    private String  dj; //对外销售价格
}
