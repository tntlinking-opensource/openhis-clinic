package com.geeke.medicareutils.domain.reqpo;

import lombok.Data;

/**
 * 医保接口主请求体
 */
@Data
public class RequestData {
    private String infno;
    private String msgid;
    private String mdtrtarea_admvs;
    private String insuplc_admdvs;
    private String recer_sys_code;
    private String dev_no;
    private String dev_safe_info;
    private String cainfo;
    private String signtype;
    private String infver;
    private String opter_type;
    private String opter;
    private String opter_name;
    private String inf_time;
    private String fixmedins_code;
    private String fixmedins_name;
    private String sign_no;
}
