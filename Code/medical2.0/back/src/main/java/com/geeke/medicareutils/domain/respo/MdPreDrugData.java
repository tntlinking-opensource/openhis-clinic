package com.geeke.medicareutils.domain.respo;

import lombok.Data;

import java.util.List;

/**
 * @Description 电子处方取药结果返回
 * @Author Hzx
 * @Date 2024/11/13
 */
@Data
public class MdPreDrugData {
    private String hiRxno;
    private String setlTime;
    private List<MdMedicineInfo> eltdelts;

    @Data
    public static class MdMedicineInfo {
        // 医药机构药品编号
        private String medinsListCodg; // 字符型 20
        // 通用名
        private String drugGenname; // 字符型 50
        // 药品商品名
        private String drugProdname; // 字符型 50
        // 药品剂型
        private String drugDosform; // 字符型 20
        // 药品规格
        private String drugSpec; // 字符型 20
        // 数量
        private Integer ent; // 数值型 16,3
        // 批准文号
        private String aprvno; // 字符型 20
        // 批次号
        private String bchno; // 字符型 20
        // 生产批号
        private String manuLotnum; // 字符型 20
        // 生产厂家
        private String prdrName; // 字符型 50

    }

}
