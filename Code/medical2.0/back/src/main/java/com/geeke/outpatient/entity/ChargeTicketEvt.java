package com.geeke.outpatient.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 处方详情Entity
 * @author txl
 * @version 2022-06-07
 */
@Data
public class ChargeTicketEvt implements Serializable {

	private static final long serialVersionUID = 1014474470772900060L;
	//诊所名称
	private String clinicName;
	//姓名
	private String name;
	//性别
	private String gender;
	//年龄
	private String age;
	//收费单号
	private String code;
	//收费类别
    private String type;
	//收费项目
	private String project;
	//收费人
    private String people;
    //就诊日期
    private String date;
	//就诊科室
	private String office;
	//收费金额
	private String fee;
	//支付方式金额
	private String feeType;
	//温馨提示
	private String tip;
	//挂号序号
	private String num;
}