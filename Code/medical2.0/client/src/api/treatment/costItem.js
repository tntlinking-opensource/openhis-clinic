import request from "@/utils/request";

export const getCostItemById = id =>
  request({
    url: "/treatment/costItem/" + id,
    method: "get"
  });

export const listCostItemPage = search =>
  request({
    url: "/treatment/costItem/list",
    method: "post",
    data: search
  });
// 上级租户的诊疗项目列表信息
export const listCostItemPageByParent = search =>
  request({
    url: "/treatment/costItem/listByInstitution",
    method: "post",
    data: search
  });
  // 获取院版所有诊疗项目
export const listCostItemPageByInstitutionAll = search =>
  request({
    url: "/hosdata/HosCollectData/getHosInstitutions",
    method: "post",
    data: search
  });

// 选择租户的诊疗项目同步至诊所
export const listCostItemPageByInstitution = search =>
  request({
    url: "/treatment/costItem/syncToClinic",
    method: "post",
    data: search
  });

  // 选择院版诊疗项目同步
export const listCostItemPageByInstitutionSync = search =>
  request({
    url: "/hosdata/HosCollectData/HisInstitutionsToClinic",
    method: "post",
    data: search
  });

export const listCostItemAll = search =>
  request({
    url: "/treatment/costItem/listAll",
    method: "post",
    data: search
  });

export const saveCostItem = costItem =>
  request({
    url: "/treatment/costItem/save",
    method: "post",
    data: costItem
  });

export const deleteCostItem = costItem =>
  request({
    url: "/treatment/costItem/delete",
    method: "post",
    data: costItem
  });

export const bulkInsertCostItem = costItems =>
  request({
    url: "/treatment/costItem/bulkInsert",
    method: "post",
    data: costItems
  });

export const bulkUpdateCostItem = costItems =>
  request({
    url: "/treatment/costItem/bulkUpdate",
    method: "post",
    data: costItems
  });

export const bulkDeleteCostItem = costItems =>
  request({
    url: "/treatment/costItem/bulkDelete",
    method: "post",
    data: costItems
  });
