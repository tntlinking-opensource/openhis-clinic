package com.geeke.common.constants;

/**
 * 业务状态/类型常量定义
 * 与 sys_dict_item 表中的 value 字段一致
 *
 * 消除代码中散布的魔法字符串（如 "amountStatus_1"、"tollType_0" 等）
 * 如果字典值发生变更，只需修改此文件
 */
public final class BizConstants {

    private BizConstants() {}

    // ========== 挂号状态 registrationStatus ==========
    /** 待就诊 */
    public static final String REG_STATUS_PENDING = "registrationStatus_0";
    /** 已就诊 */
    public static final String REG_STATUS_VISITED = "registrationStatus_1";
    /** 退号 */
    public static final String REG_STATUS_REFUNDED = "registrationStatus_2";
    /** 待签到 */
    public static final String REG_STATUS_PENDING_CHECKIN = "registrationStatus_3";
    /** 已失效 */
    public static final String REG_STATUS_FEE_REFUNDED = "registrationStatus_4";
    /** 待分诊 */
    public static final String REG_STATUS_PENDING_TRIAGE = "registrationStatus_5";

    // ========== 收费状态 amountStatus ==========
    /** 未收费 */
    public static final String AMOUNT_STATUS_UNPAID = "amountStatus_0";
    /** 已收费 */
    public static final String AMOUNT_STATUS_PAID = "amountStatus_1";
    /** 已退费 */
    public static final String AMOUNT_STATUS_REFUNDED = "amountStatus_2";

    // ========== 收费类型 tollType ==========
    /** 西药处方 */
    public static final String TOLL_TYPE_WESTERN = "tollType_0";
    /** 中药处方 */
    public static final String TOLL_TYPE_CHINESE = "tollType_1";
    /** 输液 */
    public static final String TOLL_TYPE_INFUSION = "tollType_2";
    /** 治疗 */
    public static final String TOLL_TYPE_TREATMENT = "tollType_3";
    /** 检验检查 */
    public static final String TOLL_TYPE_EXAMINATION = "tollType_4";
    /** 挂号费 */
    public static final String TOLL_TYPE_REGISTRATION = "tollType_5";

    // ========== 处方类型 recipelType ==========
    /** 西药处方 */
    public static final String RECIPEL_TYPE_WESTERN = "recipelType_0";
    /** 中药处方 */
    public static final String RECIPEL_TYPE_CHINESE = "recipelType_1";
    /** 输液处方 */
    public static final String RECIPEL_TYPE_INFUSION = "recipelType_2";
    /** 其他处方 */
    public static final String RECIPEL_TYPE_OTHER = "recipelType_3";
    /** 中成药处方 */
    public static final String RECIPEL_TYPE_PATENT = "recipelType_4";
    /** 外配处方 */
    public static final String RECIPEL_TYPE_EXTERNAL = "recipelType_5";

    // ========== 就诊类型 treatType ==========
    /** 普通门诊 */
    public static final String TREAT_TYPE_NORMAL = "treatType_0";
    /** 复诊 */
    public static final String TREAT_TYPE_FOLLOWUP = "treatType_1";

    // ========== 药品/材料类型 stuffType ==========
    /** 药品 */
    public static final String STUFF_TYPE_DRUG = "stuffType_0";
    /** 材料 */
    public static final String STUFF_TYPE_MATERIAL = "stuffType_1";
    /** 诊疗项目 */
    public static final String STUFF_TYPE_COST_ITEM = "stuffType_2";

    // ========== 门诊类型 medicalType ==========
    /** 西药 */
    public static final String MEDICAL_TYPE_WESTERN = "medicalType_0";
    /** 中草药 */
    public static final String MEDICAL_TYPE_HERBAL = "medicalType_1";
    /** 中成药 */
    public static final String MEDICAL_TYPE_PATENT = "medicalType_2";

    // ========== 治疗项目类型 treatmentItemType ==========
    /** 检验 */
    public static final String TREATMENT_ITEM_TYPE_LAB = "treatmentItemType_0";
    /** 检查 */
    public static final String TREATMENT_ITEM_TYPE_EXAM = "treatmentItemType_1";

    // ========== 性别 gender ==========
    /** 男 */
    public static final String GENDER_MALE = "gender_0";
    /** 女 */
    public static final String GENDER_FEMALE = "gender_1";

    // ========== 角色编码 ==========
    /** 管理员角色编码 */
    public static final String ROLE_ADMIN = "admin";

    // ========== 支付方式 payType ==========
    /** 现金支付 */
    public static final String PAY_TYPE_CASH = "payType_0";
    /** 支付宝支付 */
    public static final String PAY_TYPE_ALIPAY = "payType_1";
    /** 微信支付 */
    public static final String PAY_TYPE_WECHAT = "payType_2";
    /** 银行卡支付 */
    public static final String PAY_TYPE_BANK_CARD = "payType_3";
    /** 医保支付 */
    public static final String PAY_TYPE_MEDICAL = "payType_4";

    // ========== 传染病类型 infectType ==========
    /** 无传染病 */
    public static final String INFECT_TYPE_NONE = "infectType_0";

    // ========== 挂号来源 registrationSource ==========
    /** 初诊 */
    public static final String REG_SOURCE_NORMAL = "registrationSource_0";
    /** 快速接诊 */
    public static final String REG_SOURCE_QUICK = "registrationSource_2";
    /** 零售收费 */
    public static final String REG_SOURCE_RETAIL = "registrationSource_3";

    // ========== 供应商入库审核状态 supplierStorageExamineStatus ==========
    /** 审核通过 */
    public static final String SUPPLIER_STORAGE_EXAMINE_PASS = "supplierStorageExamineStatus_0";
    /** 已作废 */
    public static final String SUPPLIER_STORAGE_EXAMINE_VOIDED = "supplierStorageExamineStatus_2";
    /** 待审核 */
    public static final String SUPPLIER_STORAGE_EXAMINE_PENDING = "supplierStorageExamineStatus_3";

    // ========== 字典类型ID（sys_dict_type.id） ==========
    /** 药品类型 */
    public static final String DICT_TYPE_DRUG_TYPE = "1004078055755374603";
    /** 药品性质 */
    public static final String DICT_TYPE_DRUG_NATURE = "1004078055755374607";
    /** 剂量单位 */
    public static final String DICT_TYPE_DOSAGE_UNIT = "1004406758192578588";
    /** 制剂单位 */
    public static final String DICT_TYPE_PREPARATION_UNIT = "1004406758192578593";
    /** 包装规格 */
    public static final String DICT_TYPE_PACK_SPEC = "1004406758192578597";
    /** 材料类型 */
    public static final String DICT_TYPE_STUFF_TYPE = "1004462867645374476";
    /** 机构类型 */
    public static final String DICT_TYPE_INSTITUTION_TYPE = "998465736089977631";
    /** 机构单位 */
    public static final String DICT_TYPE_INSTITUTION_UNIT = "999976636865404934";

    // ========== 业务实体ID（需要后续改为动态配置） ==========
    /** 默认供应商ID（院版供应商） */
    public static final String DEFAULT_SUPPLIER_ID = "2098053487889309725";
    /** 入库审核通过字典项ID */
    public static final String DICT_ITEM_STORAGE_EXAMINE_PASS = "1005787933775863930";
}
