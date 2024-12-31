package com.geeke.medicareutils.domain.respo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 执业人员信息
 */
@Data
public class MdStaffData {
    private String psnCertType;//人员证件类型
    private String certno;//证件号码
    private String pracPsnNo;//执业人员编号
    private String pracPsnCode;//执业人员代码
    private String pracPsnName;//执业人员姓名
    private String pracPsnType;//执业人员分类
    private String pracPsnCert;//执业人员资格证书编码
    private String pracCertNo;//执业证书编号
    private String hiDrFlag;//医保医师标志
    private LocalDateTime begntime;//开始时间
    private LocalDateTime endtime;//结束时间
    private String chgRea;//变更原因

}
