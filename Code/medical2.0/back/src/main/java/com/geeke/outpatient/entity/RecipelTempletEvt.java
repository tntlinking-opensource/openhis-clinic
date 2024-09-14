package com.geeke.outpatient.entity;

import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.Stuff;
import com.geeke.sys.entity.DictItem;
import com.geeke.treatment.entity.CostItem;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 处方详情Entity
 * @author txl
 * @version 2022-06-07
 */
@Data
public class RecipelTempletEvt implements Serializable {

	private static final long serialVersionUID = 1014474470772900077L;
	//医院名称
	private String clinicName;
	//处方签
    private String recipelSign = "处方笺";
	//处方小类
    private String smallType;
    //科室
    private String office;
    //处方号
    private String code;
	//姓名
	private String name;
	//性别
	private String gender;
    //开单日期
    private String billDate;
	//年龄
	private String age;
	//费别
	private String feeType;
	//发药日期
    private String dispenseDate;
    //发药
    private String dispense = "发药：";
    //身份证号
    private String idCard;
    //临床诊断
    private String diagnose;
	//药品标识
	private String stuffSign = " RP:";
	//医嘱
	private String entrust;
	//中药配置
    private String chineseMedicineUse;
    //医师
    private String doctor;
    //金额
    private String fee;
    //药师
    private String apothecary;
    //审核
    private String check;
    //核对
    private String checkAgain ="核对：";
    //发药
    private String dispensing;
    //药师/士（调配）
    private String deploy;
    //注意事项
    private String medicalRemark;
    //black
    private String black;
    //初诊/复诊
    private String treatType;
}