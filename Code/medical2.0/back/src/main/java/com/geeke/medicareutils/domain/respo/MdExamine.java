package com.geeke.medicareutils.domain.respo;

import lombok.Data;

import java.util.Date;

/**
 * @Description 电子处方审核信息
 * @Author
 * @Date 2025/3/25
 */
@Data
public class MdExamine {
    // 医保处方编号
    private String hiRxno;

    // 医保药师姓名
    private String pharName;

    // 医保药师代码
    private String pharCode;

    // 处方审核状态代码
    private String rxChkStasCodg;

    // 处方审核意见
    private String rxChkOpnn;

    // 处方审核时间
    private Date rxChkTime;
}
