package com.geeke.hosdata.constant;


/**
 * 院版访问路径
 */
public class ApiUrl {
    //分页药品路径
    public static final String HOS_GET_MEDICINE_PAGE = "/api/BaseData/GetMedicinePage";
    //全部药品路径
    public static final String HOS_GET_MEDICINE_LIST = "/api/BaseData/GetMedicineList";
    //收费诊疗项目
    public static final String HOS_GET_CHARGE_ITEM_PAGE = "/api/BaseData/GetChargeItemPage";
    //收费大类
    public static final String HOS_GET_CHARGE_ITEM = "/api/BaseData/GetChargeCategory";
    //分页材料路径
    public static final String HOS_GET_MATERIAL_ITEM_PAGE = "/api/BaseData/GetMaterialItemPage";
    public static  final String HOS_GET_TOKEN ="/api/Auth/GetAppFrienAuthToken" ;
    //药库发药
    public   static final String HOS_GET_DRUGS_STORAGE_PAGE = "/api/DrugStorage/GetDepartmentDrugsPage";
    //入库后回调审核状态
    public static final String HOS_UPDATE_DRUGS_STORAGE_STATUS = "/api/DrugStorage/UpdateMedicationStatus";


}
