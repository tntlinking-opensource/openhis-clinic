package com.geeke.outpatient.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 远程诊断管理
 * @author zh
 * @version 2023-12-06
 */

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemoteDiagnosisTreatment extends DataEntity<RemoteDiagnosisTreatment> {
    private static final long serialVersionUID = -7912591825814208871L;
    private Company company;        // 诊所id
    private String applyId;  // 远程诊疗流水号（由his系统生成）
    private String registrationId;  // 挂号id
    private String nameId;      // 患者id
    private String name;        // 患者姓名
	private String gender;      // 性别
	private String card;		// 身份证号
	private String hospital;	// 申请医院
	private String hospitalId;	// 申请医院
	private String department;	// 申请科室
	private String departmentId;	// 申请科室
	private String medic;		// 申请医生
	private String medicId;		// 申请医生
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime diagnosisTime; // 诊疗时间
    private String status;		// 状态
    private String medicationStatus;	// 发药收费状态（0：代收费 1：已收费待发药 2：已发药）
    private String applicationResults;	// 申请结果
    private String recipientName;		// 收件人姓名
    private String recipientPhone;		// 收件人电话
    private String province;		// 收件人省
    private String market;		// 收件人市
    private String distinguish;		// 收件人区县
    private String recipientAddress;	// 收件人地址
    private String conferenceId;		// 会议号
    private String cardNo;		// his就诊卡号
    private String orderNo;		// 费用订单号
    private String payFee;		// 费用

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "remote_diagnosis";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "-7912591825814208871L";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}