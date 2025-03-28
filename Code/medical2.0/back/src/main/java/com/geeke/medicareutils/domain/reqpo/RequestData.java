package com.geeke.medicareutils.domain.reqpo;

import lombok.Data;

/**
 * 医保接口主请求体
 */
@Data
public class RequestData {
    // infno: 交易信息编号，用于唯一标识一笔交易
    private String infno;

    // msgid: 消息编号，用于唯一标识每个消息（通常包含机构编号、时间戳和随机数）
    private String msgid;

    // mdtrtarea_admvs: 就医地医保区划，表示就医地点的医保区域代码，6位字符
    private String mdtrtarea_admvs;

    // insuplc_admdvs: 参保地医保区划，表示参保人的医保区域代码，6位字符
    private String insuplc_admdvs;

    // recer_sys_code: 接收方系统代码，用于标识接收方的系统，通常为10位字符
    private String recer_sys_code;

    // dev_no: 设备编号，用于标识设备的唯一编号，最多100字符
    private String dev_no;

    // dev_safe_info: 设备安全信息，用于存储设备的安全相关信息，最多2000字符
    private String dev_safe_info;

    // cainfo: CA证书信息，存储与安全证书相关的字符串，如果没有则为空字符串
    private String cainfo;

    // signtype: 签名类型，指定所使用的签名算法类型，通常为SM2、SM3等
    private String signtype;

    // infver: 接口版本号，表示当前接口版本的字符串，通常是6位字符，例如 "V1.0"
    private String infver;

    // opter_type: 经办人类别，表示操作人员类型的标识，例如"Y1"表示经办人，"Y2"表示终端操作等
    private String opter_type;

    // opter: 经办人或操作员，标识操作该交易的人员编号或终端编号，最多30字符
    private String opter;

    // opter_name: 经办人姓名，表示操作人员的名字
    private String opter_name;

    // inf_time: 交易时间，表示交易发生的具体时间，格式为 "yyyy-MM-dd HH:mm:ss"
    private String inf_time;

    // fixmedins_code: 定点医药机构编号，表示医疗机构的唯一编号，最多30字符
    private String fixmedins_code;

    // fixmedins_name: 定点医药机构名称，表示医疗机构的名称，最多200字符
    private String fixmedins_name;

    // sign_no: 交易签到流水号，唯一标识一次签到交易，通常是通过签到交易获得的，最多30字符
    private String sign_no;

}
