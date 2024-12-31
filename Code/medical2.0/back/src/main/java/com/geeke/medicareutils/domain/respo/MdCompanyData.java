package com.geeke.medicareutils.domain.respo;

import com.geeke.org.entity.ClinicOffice;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 科室信息
 */
@Data
public class MdCompanyData {
    private String hospDeptCodg;//医院科室编码
    private String hospDeptName;//医院科室名称
    private LocalDateTime begntime;//开始时间
    private LocalDateTime endtime;//结束时间
    private String itro;         //简介
    private String deptResperName;//科室负责人
    private String deptResperTel;//科室负责人电话
    private String deptMedServScp;
    private String caty;//科别
    private LocalDateTime deptEstbdat;//科室成立日期
    private String aprvBedCnt;//批准床位数量
    private String hiCrtfBedCnt;//医保认可床位数
    private String poolareaNo;//统筹区编号
    private String drPsncnt;//医师人数
    private String pharPsncnt;//药师人数
    private String nursPsncnt;//护士人数
    private String tecnPsncnt;//技师人数
    private String memo;//备注
}
