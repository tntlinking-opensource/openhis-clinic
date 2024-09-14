import request from "@/utils/request";

export const getStuffById = id =>
  request({
    url: "/stock/stuff/" + id,
    method: "get"
  });

export const listStuffPage = search =>
  request({
    url: "/stock/stuff/list",
    method: "post",
    data: search
  });

export const listSyncStuffPage = search =>
  request({
    url: "/stock/stuff/listByCompany",
    method: "post",
    data: search
  });
// 获取上级租户材料列表
export const listParentStuffPage = search =>
  request({
    url: "/stock/stuff/listByInstitution",
    method: "post",
    data: search
  });
  // 获取院版材料列表
export const listInstitutionStuffPage = search =>
  request({
    url: "/hosdata/HosCollectData/getHosMaterials",
    method: "post",
    data: search
  });

// 选择租户的材料同步至诊所
export const syncStuff = search =>
  request({
    url: "/stock/stuff/syncToClinic",
    method: "post",
    data: search
  });

// 选择院办材料同步
export const syncInstitutionStuff = search =>
  request({
    url: "/hosdata/HosCollectData/HisMaterialsToClinic",
    method: "post",
    data: search
  });

export const listDrug = search =>
  request({
    url: "/stock/drug/list",
    method: "post",
    data: search
  });

export const listStuffAll = search =>
  request({
    url: "/stock/stuff/listAll",
    method: "post",
    data: search
  });

export const inventory = search =>
  request({
    url: "/stock/stuff/inventory",
    method: "post",
    data: search
  });

export const saveStuff = stuff =>
  request({
    url: "/stock/stuff/save",
    method: "post",
    data: stuff
  });

export const deleteStuff = stuff =>
  request({
    url: "/stock/stuff/delete",
    method: "post",
    data: stuff
  });

export const bulkInsertStuff = stuffs =>
  request({
    url: "/stock/stuff/bulkInsert",
    method: "post",
    data: stuffs
  });

export const bulkUpdateStuff = stuffs =>
  request({
    url: "/stock/stuff/bulkUpdate",
    method: "post",
    data: stuffs
  });

export const bulkDeleteStuff = stuffs =>
  request({
    url: "/stock/stuff/bulkDelete",
    method: "post",
    data: stuffs
  });

export const listStuffPageForMedical = search =>
  request({
    url: "/stock/stuff/listForMedical",
    method: "post",
    data: search
  });

export const updateAllIndate = stuff =>
  request({
    url: "/stock/stuff/updateAllIndate",
    method: "post",
    data: stuff
  });

export const updateAllInventory = stuff =>
  request({
    url: "/stock/stuff/updateAllInventory",
    method: "post",
    data: stuff
  });

export const uploadExcel = formData =>
  request({
    url: "/stock/stuff/uploadExcel",
    method: "post",
    data: formData
  });

  // 院版发药列表
export const listInstitutionDispensingPage = search =>
  request({
    url: "/hosdata/HosDepartmentStorage/getHosDrugsStorage",
    method: "post",
    data: search
  });

  // 院版发药同步
export const syncInstitutionDispensing = search =>
  request({
    url: "/hosdata/HosDepartmentStorage/auditHosDrugsStorage",
    method: "post",
    data: search
  });

  // 院版发药同步
