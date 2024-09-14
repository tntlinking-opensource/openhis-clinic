package com.geeke.cure.entity;

import com.geeke.org.entity.Company;
import com.geeke.outpatient.entity.Patient;
import com.geeke.treatment.entity.CostItems;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InspectionCheckInfoCostItem {
    private String id;
    private Company company;      // 诊所id
    private InspectionCheck inspectionCheck;      // 主表id
    private String fileId;		// 文件id
    private Patient patient;      // 患者id
    private Date samplingDate;		// 采样时间
    private String samplingNum;		// 样本号
    private String instrument;		// 检验仪器
    private String checkoutNum;		// 检验号
    private Date checkoutDate;		// 检验时间
    private String checkoutOffice;		// 检验科室
    private String reportConclusion;		// 报告结论
    private String checkType;          //检查类型
    private String checkPart;         //检查部位
    private String checkAdvise;      //检查所见
    private String opinion;      //意见
    private String doctor;      //医师
    private String checkProject;   //检查项目
    private List<CostItems> costItemsList;  //诊疗项目
    private List<InspectionCheckDetailCostItem> inspectionCheckDetails;
}
