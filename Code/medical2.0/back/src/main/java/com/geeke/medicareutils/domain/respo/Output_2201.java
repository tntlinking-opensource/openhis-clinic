package com.geeke.medicareutils.domain.respo;

import lombok.Data;

@Data
public class Output_2201 {
    private data2201_out data;
    @Data
    public static class data2201_out {
        /**
         * 就诊 ID 字符型 30 Y 医保返回唯一 流水
         */
        private String mdtrt_id;

        /**
         * 人员编号 字符型 30 Y
         */
        private String psn_no;

        /**
         * 住院/门诊号 字符型 30 Y 院内唯一流水
         */
        private String ipt_otp_no;
    }
}
