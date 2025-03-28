package com.geeke.medicareutils.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.outpatient.entity.RecipelInfoReview;

/**
 * @Description 电子处方
 * @Author Hzx
 * @Date 2024/10/30 11:45
 */
public interface MdMedicationOrderService {

    // 【Ld7801】电子处方上传预核验
    JSONObject validateElectronicPrescriptionUpload_Ld7801(RecipelInfoReview recipelInfoReview);

    // 【Ld7802】电子处方医保电子签名
    JSONObject signElectronicPrescriptionWithInsurance_Ld7802(RecipelInfoReview recipelInfoReview, String ysId);

    // 【Ld7101】电子处方上传
    JSONObject uploadElectronicPrescription_Ld7101(RecipelInfoReview recipelInfoReview,String ysId);


    JSONObject queryElectronicPrescriptionReviewResult_Ld7805(RecipelInfoReview recipelInfoReview);

    // 【Ld7804】电子处方取药结果查询
    JSONObject queryPrescriptionDispensingResult_Ld7804(RecipelInfoReview recipelInfoReview);

    //电子处方撤销
    JSONObject cancelElectronicPrescription_Ld7104(RecipelInfoReview recipelInfoReview,String undoRea);

    // 【Ld7202】电子处方信息查询
    JSONObject queryElectronicPrescriptionInfo_Ld7202();


    // 【Ld7102】电子处方审核结果反馈
    JSONObject feedbackPrescriptionReviewResult_Ld7102();


    // 【Ld7806】电子处方药品目录查询
    Boolean queryPrescriptionDrugDirectory_Ld7806();


}
