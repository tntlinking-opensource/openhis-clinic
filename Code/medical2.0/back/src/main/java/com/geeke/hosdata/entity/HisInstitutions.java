package com.geeke.hosdata.entity;

import com.geeke.common.persistence.DataEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class HisInstitutions  extends DataEntity<HisInstitutions> implements Serializable {

    private  String companyId;
    private  String sfxmmc; //费用名称
    private  String py; //拼音
    private  String dw; // 单位
    private  String sfxmCode;//唯一项目编码
    private  String dj;//销售单价
    private  String dllx;//收费项目类型


}
