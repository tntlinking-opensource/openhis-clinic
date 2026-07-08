package com.geeke.medicareutils.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geeke.medicareutils.service.MdInventoryService;
import com.geeke.medicareutils.util.MdRequestUtil;
import com.geeke.outpatient.entity.RecipelInfoEvt;
import com.geeke.stock.entity.*;
import com.geeke.stock.service.DrugService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 医保进销存控制实现类
 */
@RequiredArgsConstructor
@Service
public class MdInventoryServiceImpl implements MdInventoryService {


    private final MdRequestUtil mdRequestUtil;

    private final DrugService drugService;
    /**
     * 商品盘存上传  3501
     *
     * @return
     */
    @Override
    public JSONObject uploadInventory_3501() {
        JSONObject invinfo = new JSONObject();
        invinfo.put("med_list_codg", ""); // 医疗目录编码
        invinfo.put("fixmedins_hist_id", ""); // 定点医药机构目录编号
        invinfo.put("fixmedins_hist_name", ""); // 定点医药机构目录名称
        invinfo.put("rx_flag", ""); // 处方药标志
        invinfo.put("invdate", ""); // 盘存日期
        invinfo.put("inv_cnt", ""); // 库存数量
        invinfo.put("manu_lotnum", ""); // 生产批号
        invinfo.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        invinfo.put("manu_date", ""); // 生产日期
        invinfo.put("expy_end", ""); // 有效期止
        invinfo.put("memo", ""); // 备注
        invinfo.put("drugtracinfo", ""); // 溯源码节点信息
        JSONObject drugtracinfo = new JSONObject();
        drugtracinfo.put("drug_trac_codg","");//药品追溯码
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("invinfo",jsonObject);
        jsonObject.put("drugtracinfo",drugtracinfo);

        return mdRequestUtil.getMedicareData("3501",jsonObject);
    }

    /**
     * 商品盘存上传 3501A
     * 参数多行构造
     * @return
     */
    @Override
    public JSONObject uploadInventoryList_3501A() {
        JSONArray array = new  JSONArray();
        JSONObject invinfo = new JSONObject();
        invinfo.put("med_list_codg", ""); // 医疗目录编码
        invinfo.put("fixmedins_hist_id", ""); // 定点医药机构目录编号
        invinfo.put("fixmedins_hist_name", ""); // 定点医药机构目录名称
        invinfo.put("rx_flag", ""); // 处方药标志
        invinfo.put("invdate", ""); // 盘存日期
        invinfo.put("inv_cnt", ""); // 库存数量
        invinfo.put("manu_lotnum", ""); // 生产批号
        invinfo.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        invinfo.put("manu_date", ""); // 生产日期
        invinfo.put("expy_end", ""); // 有效期止
        invinfo.put("memo", ""); // 备注
        invinfo.put("drugtracinfo", ""); // 溯源码节点信息
        //明细追溯码
        JSONObject drugtracinfo = new JSONObject();
        drugtracinfo.put("drug_trac_codg","");//药品追溯码
        array.add(invinfo);
        JSONArray array1 = new JSONArray();
        array1.add(drugtracinfo);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("invinfoDetail",array);
        jsonObject.put("drugtracinfo",array1);

        return mdRequestUtil.getMedicareData("3501A",jsonObject);
    }

    /**
     * 商品库存变更 3502
     *
     * @return
     */
    @Override
    public JSONObject updateInventory_3502(StorageEvt storageEvt, String invChgType) {
        JSONObject invinfo = new JSONObject();
        SupplierStock supplierStock = storageEvt.getSupplierStockList().get(0);
        //以下字段需根据实际业务补充
        invinfo.put("med_list_codg", ""); // 医疗目录编码
        invinfo.put("inv_chg_type", invChgType); // 库存变更类型
        invinfo.put("fixmedins_hist_id", ""); // 定点医药机构目录编号
        invinfo.put("fixmedins_hist_name", ""); // 定点医药机构目录名称
        invinfo.put("fixmedins_bchno", supplierStock.getSupplierStorage().getCode()); // 定点医药机构批次流水号
        invinfo.put("pric", supplierStock.getRetailPrice()); // 单价
        invinfo.put("cnt", supplierStock.getCnt()); // 医保数量
        Drug drug = drugService.get(supplierStock.getDrug().getId());
        invinfo.put("rx_flag", drug.getRxFlag() == null ? "0" : drug.getRxFlag()); // 处方药标志 0 否 1是
        invinfo.put("inv_chg_time", supplierStock.getCreateDate()); // 库存变更时间
        invinfo.put("inv_chg_opter_name", supplierStock.getCreateBy().substring(0, supplierStock.getCreateBy().indexOf("("))); // 库存变更经办人姓名
        invinfo.put("memo", ""); // 备注
        invinfo.put("trdn_flag", ""); // 拆零标志
        // 存储该药品的追溯码数组
        JSONArray drugTracInfoArray = new JSONArray();
        // 检查是否有多个追溯码，逗号分隔
        if (supplierStock.getDrugTracCodg().contains(",")) {
            Arrays.asList(supplierStock.getDrugTracCodg().split(",")).forEach(
                    drugTracCodg -> {
                        JSONObject drugtracinfo = new JSONObject();
                        drugtracinfo.put("drug_trac_codg", drugTracCodg); // 药品追溯码
                        drugTracInfoArray.add(drugtracinfo);
                    }
            );
        } else {
            JSONObject drugtracinfo = new JSONObject();
            drugtracinfo.put("drug_trac_codg", supplierStock.getDrugTracCodg()); // 药品追溯码
            drugTracInfoArray.add(drugtracinfo);
        }
        // 将追溯码数组添加到 invinfo 对象中
        invinfo.put("drugtracinfo", drugTracInfoArray);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("invinfo", invinfo);
        return mdRequestUtil.getMedicareData("3502",jsonObject);
    }

    /**
     * 3502A  诊所库存入库
     *
     * @return result节点：retRslt 1：成功，0：失败  msgRslt：失败原因
     */
    @Override
    public JSONObject updateInventoryList_3502A(StorageEvt storageEvt, String invChgType) {
        JSONArray array = new JSONArray();
        storageEvt.getSupplierStockList().forEach(supplierStock -> {
            Drug drug = drugService.get(supplierStock.getDrug().getId());
            JSONObject invinfo = new JSONObject();
            invinfo.put("med_list_codg", ""); // 医疗目录编码
            invinfo.put("inv_chg_type", invChgType); // 库存变更类型
            invinfo.put("fixmedins_hist_id", drug.getCode()); // 定点医药机构目录编号
            invinfo.put("fixmedins_hist_name", drug.getName()); // 定点医药机构目录名称
            invinfo.put("fixmedins_bchno", supplierStock.getSupplierStorage().getCode()); // 定点医药机构批次流水号
            invinfo.put("pric", supplierStock.getRetailPrice()); // 单价
            invinfo.put("cnt", supplierStock.getCnt()); // 医保数量
            invinfo.put("rx_flag", drug.getRxFlag() == null ? "0" : drug.getRxFlag()); // 处方药标志 0 否 1是
            invinfo.put("inv_chg_time", supplierStock.getCreateDate()); // 库存变更时间
            invinfo.put("inv_chg_opter_name", supplierStock.getCreateBy().substring(0, supplierStock.getCreateBy().indexOf("("))); // 库存变更经办人姓名
            invinfo.put("memo", ""); // 备注
            invinfo.put("trdn_flag", ""); // 拆零标志
            // 存储该药品的追溯码数组
            JSONArray drugTracInfoArray = new JSONArray();
            // 检查是否有多个追溯码，逗号分隔
            if (supplierStock.getDrugTracCodg().contains(",")) {
                Arrays.asList(supplierStock.getDrugTracCodg().split(",")).forEach(
                        drugTracCodg -> {
                            JSONObject drugtracinfo = new JSONObject();
                            drugtracinfo.put("drug_trac_codg", drugTracCodg); // 药品追溯码
                            drugTracInfoArray.add(drugtracinfo);
                        }
                );
            } else {
                JSONObject drugtracinfo = new JSONObject();
                drugtracinfo.put("drug_trac_codg", supplierStock.getDrugTracCodg()); // 药品追溯码
                drugTracInfoArray.add(drugtracinfo);
            }
            // 将追溯码数组添加到 invinfo 对象中
            invinfo.put("drugtracinfo", drugTracInfoArray);
            // 将 invinfo 添加到主数组中
            array.add(invinfo);
        });
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("invinfoDetail", array);
        return mdRequestUtil.getMedicareData("3502A", jsonObject);
    }

    /**
     * 发药退药  3502A
     * @param dispensingEvt
     * @return
     */
    @Override
    public JSONObject updateInventoryList_3502A(DispensingEvt dispensingEvt) {
        JSONArray array = new JSONArray();
        dispensingEvt.getDispensingDetailEvtList().forEach(detailEvt -> {
            JSONObject invinfo = new JSONObject();
            Drug drug = drugService.get(detailEvt.getId());
            invinfo.put("med_list_codg", ""); // 医疗目录编码
            if("0".equals(dispensingEvt.getDispensingStatus())){
                //发药  出库
                invinfo.put("inv_chg_type", "101");
            }else if("1".equals(dispensingEvt.getDispensingStatus())){
                //退药  入库
                invinfo.put("inv_chg_type", "102");
            }
            invinfo.put("fixmedins_hist_id", drug.getCode()); // 定点医药机构目录编号
            invinfo.put("fixmedins_hist_name", drug.getName()); // 定点医药机构目录名称
            invinfo.put("fixmedins_bchno", dispensingEvt.getRegistrationId()); // 定点医药机构批次流水号
            invinfo.put("pric",drug.getPrice()); // 单价
            invinfo.put("cnt",""); // 数量
            invinfo.put("rx_flag", ""); // 处方药标志
            invinfo.put("inv_chg_time", ""); // 库存变更时间
            invinfo.put("inv_chg_opter_name", ""); // 库存变更经办人姓名
            invinfo.put("memo", ""); // 备注
            invinfo.put("trdn_flag", ""); // 拆零标志
            invinfo.put("drugtracinfo", ""); // 溯源码节点信息
            // 存储该药品的追溯码数组
            JSONArray drugTracInfoArray = new JSONArray();
            // 检查是否有多个追溯码，逗号分隔
            if (detailEvt.getDrugTracCodg().contains(",")) {
                Arrays.asList(detailEvt.getDrugTracCodg().split(",")).forEach(
                        drugTracCodg -> {
                            JSONObject drugtracinfo = new JSONObject();
                            drugtracinfo.put("drug_trac_codg", drugTracCodg); // 药品追溯码
                            drugTracInfoArray.add(drugtracinfo);
                        }
                );
            } else {
                JSONObject drugtracinfo = new JSONObject();
                drugtracinfo.put("drug_trac_codg", detailEvt.getDrugTracCodg()); // 药品追溯码
                drugTracInfoArray.add(drugtracinfo);
            }
            // 将追溯码数组添加到 invinfo 对象中
            invinfo.put("drugtracinfo", drugTracInfoArray);
            // 将 invinfo 添加到主数组中
            array.add(invinfo);
        });
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("invinfoDetail", array);
        return mdRequestUtil.getMedicareData("3502A", jsonObject);
    }

    /**
     * 出库 3502A
     *
     * @param outboundEvt 出库单
     * @return
     */
    @Override
    public JSONObject updateInventoryList_3502A(OutboundEvt outboundEvt) {
        JSONArray array = new JSONArray();
        outboundEvt.getSupplierOutboundDetailList().forEach(supplierStock -> {
            Drug drug = drugService.get(supplierStock.getDrug().getId());
            JSONObject invinfo = new JSONObject();
            invinfo.put("med_list_codg", ""); // 医疗目录编码
            invinfo.put("inv_chg_type", "101"); // 库存变更类型
            invinfo.put("fixmedins_hist_id", drug.getCode()); // 定点医药机构目录编号
            invinfo.put("fixmedins_hist_name", drug.getName()); // 定点医药机构目录名称
            invinfo.put("fixmedins_bchno", supplierStock.getMedicinalStorage().getId()); // 定点医药机构批次流水号
            invinfo.put("pric", drug.getPrice()); // 单价
            invinfo.put("cnt", supplierStock.getCnt()); // 医保数量
            invinfo.put("rx_flag", drug.getRxFlag() == null ? "0" : drug.getRxFlag()); // 处方药标志 0 否 1是
            invinfo.put("inv_chg_time", supplierStock.getCreateDate()); // 库存变更时间
            invinfo.put("inv_chg_opter_name", supplierStock.getCreateBy().substring(0, supplierStock.getCreateBy().indexOf("("))); // 库存变更经办人姓名
            invinfo.put("memo", ""); // 备注
            invinfo.put("trdn_flag", ""); // 拆零标志
            // 存储该药品的追溯码数组
            JSONArray drugTracInfoArray = new JSONArray();
            // 检查是否有多个追溯码，逗号分隔
            if (supplierStock.getDrugTracCodg().contains(",")) {
                Arrays.asList(supplierStock.getDrugTracCodg().split(",")).forEach(
                        drugTracCodg -> {
                            JSONObject drugtracinfo = new JSONObject();
                            drugtracinfo.put("drug_trac_codg", drugTracCodg); // 药品追溯码
                            drugTracInfoArray.add(drugtracinfo);
                        }
                );
            } else {
                JSONObject drugtracinfo = new JSONObject();
                drugtracinfo.put("drug_trac_codg", supplierStock.getDrugTracCodg()); // 药品追溯码
                drugTracInfoArray.add(drugtracinfo);
            }
            // 将追溯码数组添加到 invinfo 对象中
            invinfo.put("drugtracinfo", drugTracInfoArray);
            // 将 invinfo 添加到主数组中
            array.add(invinfo);
        });
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("invinfoDetail", array);
       return mdRequestUtil.getMedicareData("3502A", jsonObject);
    }

    /**
     * 零售出库 3502A
     *
     * @param recipelInfoEvt@return
     */
    @Override
    public JSONObject updateInventoryList_3502A(RecipelInfoEvt recipelInfoEvt) {
        JSONArray array = new JSONArray();
        recipelInfoEvt.getRecipelDetailEvtList().forEach(supplierStock -> {
            Drug drug = drugService.get(supplierStock.getDrugStuffId().getDrug().getId());
            JSONObject invinfo = new JSONObject();
            invinfo.put("med_list_codg", ""); // 医疗目录编码
            invinfo.put("inv_chg_type", "101"); // 库存变更类型
            invinfo.put("fixmedins_hist_id", drug.getCode()); // 定点医药机构目录编号
            invinfo.put("fixmedins_hist_name", drug.getName()); // 定点医药机构目录名称
            invinfo.put("fixmedins_bchno", recipelInfoEvt.getRecipelInfo().getId()); // 定点医药机构批次流水号
            invinfo.put("pric", drug.getPrice()); // 单价
            invinfo.put("cnt", supplierStock.getTotal()); // 医保数量
            invinfo.put("rx_flag", drug.getRxFlag() == null ? "0" : drug.getRxFlag()); // 处方药标志 0 否 1是
            invinfo.put("inv_chg_time", supplierStock.getCreateDate()); // 库存变更时间
            invinfo.put("inv_chg_opter_name", supplierStock.getCreateBy().substring(0, supplierStock.getCreateBy().indexOf("("))); // 库存变更经办人姓名
            invinfo.put("memo", ""); // 备注
            invinfo.put("trdn_flag", ""); // 拆零标志
            // 存储该药品的追溯码数组
            JSONArray drugTracInfoArray = new JSONArray();
            // 检查是否有多个追溯码，逗号分隔
            if (supplierStock.getDrugTracCodg().contains(",")) {
                Arrays.asList(supplierStock.getDrugTracCodg().split(",")).forEach(
                        drugTracCodg -> {
                            JSONObject drugtracinfo = new JSONObject();
                            drugtracinfo.put("drug_trac_codg", drugTracCodg); // 药品追溯码
                            drugTracInfoArray.add(drugtracinfo);
                        }
                );
            } else {
                JSONObject drugtracinfo = new JSONObject();
                drugtracinfo.put("drug_trac_codg", supplierStock.getDrugTracCodg()); // 药品追溯码
                drugTracInfoArray.add(drugtracinfo);
            }
            // 将追溯码数组添加到 invinfo 对象中
            invinfo.put("drugtracinfo", drugTracInfoArray);
            // 将 invinfo 添加到主数组中
            array.add(invinfo);
        });
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("invinfoDetail", array);
        return mdRequestUtil.getMedicareData("3502A", jsonObject);
    }

    /**
     * 商品采购 3503
     *
     * @return result节点：retRslt 1：成功，0：失败  msgRslt：失败原因
     */
    @Override
    public JSONObject createPurchaseOrder_3503() {
        JSONObject purcinfo = new JSONObject();
        purcinfo.put("med_list_codg", ""); // 医疗目录编码
        purcinfo.put("fixmedins_hist_id", ""); // 定点医药机构目录编号
        purcinfo.put("fixmedins_hist_name", ""); // 定点医药机构目录名称
        purcinfo.put("dynt_no", ""); // 随货单号
        purcinfo.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        purcinfo.put("spler_name", ""); // 供应商名称
        purcinfo.put("spler_pmtno", ""); // 供应商许可证号
        purcinfo.put("manu_lotnum", ""); // 生产批号
        purcinfo.put("prodentp_name", ""); // 生产厂家名称
        purcinfo.put("aprvno", ""); // 批准文号
        purcinfo.put("manu_date", ""); // 生产日期
        purcinfo.put("expy_end", ""); // 有效期止
        purcinfo.put("finl_trns_pric", 0.0); // 最终成交单价
        purcinfo.put("purc_retn_cnt", 0); // 采购/退货数量
        purcinfo.put("purc_invo_codg", ""); // 采购发票编码
        purcinfo.put("purc_invo_no", ""); // 采购发票号
        purcinfo.put("rx_flag", ""); // 处方药标志
        purcinfo.put("purc_retn_stoin_time", ""); // 采购/退货入库时间
        purcinfo.put("purc_retn_opter_name", ""); // 采购/退货经办人姓名
        purcinfo.put("prod_geay_flag", "0"); // 商品赠送标志
        purcinfo.put("memo", ""); // 备注
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("purcinfo", purcinfo);
        return mdRequestUtil.getMedicareData("3503", jsonObject);
    }

    /**
     * 商品采购 3503A
     *
     * @return result节点：retRslt 1：成功，0：失败  msgRslt：失败原因
     */
    @Override
    public JSONObject createPurchaseOrderList_3503A() {
        JSONObject purcinfo = new JSONObject();
        purcinfo.put("med_list_codg", ""); // 医疗目录编码
        purcinfo.put("fixmedins_hist_id", ""); // 定点医药机构目录编号
        purcinfo.put("fixmedins_hist_name", ""); // 定点医药机构目录名称
        purcinfo.put("dynt_no", ""); // 随货单号
        purcinfo.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        purcinfo.put("spler_name", ""); // 供应商名称
        purcinfo.put("spler_pmtno", ""); // 供应商许可证号
        purcinfo.put("manu_lotnum", ""); // 生产批号
        purcinfo.put("prodentp_name", ""); // 生产厂家名称
        purcinfo.put("aprvno", ""); // 批准文号
        purcinfo.put("manu_date", ""); // 生产日期
        purcinfo.put("expy_end", ""); // 有效期止
        purcinfo.put("finl_trns_pric", 0.0); // 最终成交单价
        purcinfo.put("purc_retn_cnt", 0); // 采购/退货数量
        purcinfo.put("purc_invo_codg", ""); // 采购发票编码
        purcinfo.put("purc_invo_no", ""); // 采购发票号
        purcinfo.put("rx_flag", ""); // 处方药标志
        purcinfo.put("purc_retn_stoin_time", ""); // 采购/退货入库时间
        purcinfo.put("purc_retn_opter_name", ""); // 采购/退货经办人姓名
        purcinfo.put("prod_geay_flag", "0"); // 商品赠送标志
        purcinfo.put("memo", ""); // 备注
        JSONArray array = new JSONArray();
        array.add(purcinfo);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("purcinfoDetail", array);
        return mdRequestUtil.getMedicareData("3503A", jsonObject);
    }

    /**
     * 商品采购退货 3504
     *
     * @return result节点：retRslt 1：成功，0：失败  msgRslt：失败原因
     */
    @Override
    public JSONObject createPurchaseReturnOrder_3504() {
        JSONObject purcinfo = new JSONObject();
        purcinfo.put("med_list_codg", ""); // 医疗目录编码
        purcinfo.put("fixmedins_hist_id", ""); // 定点医药机构目录编号
        purcinfo.put("fixmedins_hist_name", ""); // 定点医药机构目录名称
        purcinfo.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        purcinfo.put("spler_name", ""); // 供应商名称
        purcinfo.put("spler_pmtno", ""); // 供应商许可证号
        purcinfo.put("manu_date", ""); // 生产日期 (格式: yyyy-MM-dd)
        purcinfo.put("expy_end", ""); // 有效期止 (格式: yyyy-MM-dd)
        purcinfo.put("finl_trns_pric", 0.0); // 最终成交单价
        purcinfo.put("purc_retn_cnt", 0); // 采购/退货数量
        purcinfo.put("purc_invo_codg", ""); // 采购发票编码
        purcinfo.put("purc_invo_no", ""); // 采购发票号
        purcinfo.put("rx_flag", ""); // 处方药标志
        purcinfo.put("purc_retn_stoin_time", ""); // 采购/退货入库时间 (格式: yyyy-MM-dd HH:mm:ss)
        purcinfo.put("purc_retn_opter_name", ""); // 采购/退货经办人姓名
        purcinfo.put("memo", ""); // 备注
        purcinfo.put("medins_prod_purc_no", ""); // 商品采购流水号
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("purcinfo", purcinfo);
        return mdRequestUtil.getMedicareData("3504", jsonObject);
    }

    /**
     * 商品采购退货 3504A
     *
     * @return 输出节点 purcinfoErrDetail
     */
    @Override
    public JSONObject createPurchaseReturnOrderList_3504A() {
        JSONObject purcinfo = new JSONObject();
        purcinfo.put("med_list_codg", ""); // 医疗目录编码
        purcinfo.put("fixmedins_hist_id", ""); // 定点医药机构目录编号
        purcinfo.put("fixmedins_hist_name", ""); // 定点医药机构目录名称
        purcinfo.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        purcinfo.put("spler_name", ""); // 供应商名称
        purcinfo.put("spler_pmtno", ""); // 供应商许可证号
        purcinfo.put("manu_date", ""); // 生产日期 (格式: yyyy-MM-dd)
        purcinfo.put("expy_end", ""); // 有效期止 (格式: yyyy-MM-dd)
        purcinfo.put("finl_trns_pric", 0.0); // 最终成交单价
        purcinfo.put("purc_retn_cnt", 0); // 采购/退货数量
        purcinfo.put("purc_invo_codg", ""); // 采购发票编码
        purcinfo.put("purc_invo_no", ""); // 采购发票号
        purcinfo.put("rx_flag", ""); // 处方药标志
        purcinfo.put("purc_retn_stoin_time", ""); // 采购/退货入库时间 (格式: yyyy-MM-dd HH:mm:ss)
        purcinfo.put("purc_retn_opter_name", ""); // 采购/退货经办人姓名
        purcinfo.put("memo", ""); // 备注
        purcinfo.put("medins_prod_purc_no", ""); // 商品采购流水号
        JSONArray array = new JSONArray();
        array.add(purcinfo);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("purcinfoDetail", array);
        return mdRequestUtil.getMedicareData("3504A", jsonObject);
    }

    /**
     * 商品销售 3505
     *
     * @return
     */
    @Override
    public JSONObject createSaleOrder_3505() {
        JSONObject selinfo = new JSONObject();

        selinfo.put("med_list_codg", "");          // 医疗目录编码
        selinfo.put("fixmedins_hilist_id", "");    // 定点医药机构目录编号
        selinfo.put("fixmedins_hilist_name", "");  // 定点医药机构目录名称
        selinfo.put("fixmedins_bchno", "");        // 定点医药机构批次流水号
        selinfo.put("prsc_dr_cert_type", "");      // 开方医师证件类型
        selinfo.put("prsc_dr_certno", "");         // 开方医师证件号码
        selinfo.put("prsc_dr_name", "");            // 开方医师姓名
        selinfo.put("phar_cert_type", "");         // 药师证件类型
        selinfo.put("phar_certno", "");            // 药师证件号码
        selinfo.put("phar_name", "");               // 药师姓名
        selinfo.put("phar_prac_cert_no", "");      // 药师执业资格证号
        selinfo.put("hi_feesetl_type", "");        // 医保费用结算类型
        selinfo.put("setl_id", "");                 // 结算 ID
        selinfo.put("mdtrt_sn", "");                // 就医流水号
        selinfo.put("psn_no", "");                  // 人员编号
        selinfo.put("psn_cert_type", "");          // 人员证件类型
        selinfo.put("certno", "");                  // 证件号码
        selinfo.put("psn_name", "");                // 人员姓名
        selinfo.put("manu_lotnum", "");            // 生产批号
        selinfo.put("manu_date", "");              // 生产日期
        selinfo.put("expy_end", "");                // 有效期止
        selinfo.put("rx_flag", "");                 // 处方药标志
        selinfo.put("trdn_flag", "");               // 拆零标志
        selinfo.put("finl_trns_pric", 0.0);        // 最终成交单价
        selinfo.put("rxno", "");                    // 处方号
        selinfo.put("rx_circ_flag", "");           // 外购处方标志
        selinfo.put("rtal_docno", "");              // 零售单据号
        selinfo.put("stoout_no", "");               // 销售出库单据号
        selinfo.put("bchno", "");                   // 批次号
        selinfo.put("drug_trac_codg", "");         // 药品追溯码
        selinfo.put("drug_prod_barc", "");         // 药品条形码
        selinfo.put("shelf_posi", "");              // 货架位
        selinfo.put("sel_retn_cnt", 0);             // 销售/退货数量
        selinfo.put("sel_retn_time", "");           // 销售/退货时间
        selinfo.put("sel_retn_opter_name", "");     // 销售/退货经办人姓名
        selinfo.put("memo", "");// 备注
        selinfo.put("MDTRT_SETL_TYPE", "");        // 就诊结算类型
        selinfo.put("drugtracinfo", "");            // 溯源码节点信息

        JSONObject drugtracinfo = new JSONObject();
        drugtracinfo.put("drug_trac_codg","");//药品追溯码

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("selinfo", selinfo);
        jsonObject.put("drugtracinfo", drugtracinfo);
        return mdRequestUtil.getMedicareData("3505", jsonObject);
    }

    /**
     * 商品销售 3505A
     *
     * @return
     */
    @Override
    public JSONObject createSaleOrderList_3505A() {
        JSONObject selinfo = new JSONObject();
        JSONArray array = new JSONArray();
        selinfo.put("med_list_codg", "");          // 医疗目录编码
        selinfo.put("fixmedins_hilist_id", "");    // 定点医药机构目录编号
        selinfo.put("fixmedins_hilist_name", "");  // 定点医药机构目录名称
        selinfo.put("fixmedins_bchno", "");        // 定点医药机构批次流水号
        selinfo.put("prsc_dr_cert_type", "");      // 开方医师证件类型
        selinfo.put("prsc_dr_certno", "");         // 开方医师证件号码
        selinfo.put("prsc_dr_name", "");            // 开方医师姓名
        selinfo.put("phar_cert_type", "");         // 药师证件类型
        selinfo.put("phar_certno", "");            // 药师证件号码
        selinfo.put("phar_name", "");               // 药师姓名
        selinfo.put("phar_prac_cert_no", "");      // 药师执业资格证号
        selinfo.put("hi_feesetl_type", "");        // 医保费用结算类型
        selinfo.put("setl_id", "");                 // 结算 ID
        selinfo.put("mdtrt_sn", "");                // 就医流水号
        selinfo.put("psn_no", "");                  // 人员编号
        selinfo.put("psn_cert_type", "");          // 人员证件类型
        selinfo.put("certno", "");                  // 证件号码
        selinfo.put("psn_name", "");                // 人员姓名
        selinfo.put("manu_lotnum", "");            // 生产批号
        selinfo.put("manu_date", "");              // 生产日期
        selinfo.put("expy_end", "");                // 有效期止
        selinfo.put("rx_flag", "");                 // 处方药标志
        selinfo.put("trdn_flag", "");               // 拆零标志
        selinfo.put("finl_trns_pric", 0.0);        // 最终成交单价
        selinfo.put("rxno", "");                    // 处方号
        selinfo.put("rx_circ_flag", "");           // 外购处方标志
        selinfo.put("rtal_docno", "");              // 零售单据号
        selinfo.put("stoout_no", "");               // 销售出库单据号
        selinfo.put("bchno", "");                   // 批次号
        selinfo.put("drug_trac_codg", "");         // 药品追溯码
        selinfo.put("drug_prod_barc", "");         // 药品条形码
        selinfo.put("shelf_posi", "");              // 货架位
        selinfo.put("sel_retn_cnt", 0);             // 销售/退货数量
        selinfo.put("sel_retn_time", "");           // 销售/退货时间
        selinfo.put("sel_retn_opter_name", "");     // 销售/退货经办人姓名
        selinfo.put("memo", "");// 备注
        selinfo.put("MDTRT_SETL_TYPE", "");        // 就诊结算类型
        selinfo.put("drugtracinfo", "");            // 溯源码节点信息
        array.add(selinfo);

        JSONObject drugtracinfo = new JSONObject();
        drugtracinfo.put("drug_trac_codg","");//药品追溯码
        JSONArray array1 = new JSONArray();
        array1.add(drugtracinfo);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("selinfoDetail", array);
        jsonObject.put("drugtracinfo", array1);

        return mdRequestUtil.getMedicareData("3505A", jsonObject);
    }

    /**
     * 销售退货 3506
     *
     * @return
     */
    @Override
    public JSONObject createSaleReturnOrder_3506() {
        JSONObject selinfo = new JSONObject();
        selinfo.put("med_list_codg", ""); // 医疗目录编码
        selinfo.put("fixmedins_hilist_id", ""); // 定点医药机构目录编号
        selinfo.put("fixmedins_hilist_name", ""); // 定点医药机构目录名称
        selinfo.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        selinfo.put("setl_id", ""); // 结算 ID
        selinfo.put("psn_no", ""); // 人员编号
        selinfo.put("psn_cert_type", ""); // 人员证件类型
        selinfo.put("certno", ""); // 证件号码
        selinfo.put("psn_name", ""); // 人员姓名
        selinfo.put("manu_lotnum", ""); // 生产批号
        selinfo.put("manu_date", ""); // 生产日期
        selinfo.put("expy_end", ""); // 有效期止
        selinfo.put("rx_flag", ""); // 处方药标志
        selinfo.put("trdn_flag", ""); // 拆零标志
        selinfo.put("finl_trns_pric", ""); // 最终成交单价
        selinfo.put("sel_retn_cnt", ""); // 销售/退货数量
        selinfo.put("sel_retn_time", ""); // 销售/退货时间
        selinfo.put("sel_retn_opter_name", ""); // 销售/退货经办人姓名
        selinfo.put("memo", ""); // 备注
        selinfo.put("medins_prod_sel_no", ""); // 商品销售流水号
        selinfo.put("mdtrt_sn", ""); // 就医流水号
        selinfo.put("drugtracinfo", ""); // 溯源码节点信息
        JSONObject drugtracinfo = new JSONObject();
        drugtracinfo.put("drug_trac_codg","");//药品追溯码
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("selinfo", selinfo);
        jsonObject.put("drugtracinfo",drugtracinfo);
        return mdRequestUtil.getMedicareData("3506", jsonObject);
    }

    /**
     * 销售退货 3506A
     *
     * @return
     */
    @Override
    public JSONObject createSaleReturnOrderList_3506A() {
        JSONObject selinfo = new JSONObject();
        JSONArray array  = new JSONArray();
        selinfo.put("med_list_codg", ""); // 医疗目录编码
        selinfo.put("fixmedins_hilist_id", ""); // 定点医药机构目录编号
        selinfo.put("fixmedins_hilist_name", ""); // 定点医药机构目录名称
        selinfo.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        selinfo.put("setl_id", ""); // 结算 ID
        selinfo.put("psn_no", ""); // 人员编号
        selinfo.put("psn_cert_type", ""); // 人员证件类型
        selinfo.put("certno", ""); // 证件号码
        selinfo.put("psn_name", ""); // 人员姓名
        selinfo.put("manu_lotnum", ""); // 生产批号
        selinfo.put("manu_date", ""); // 生产日期
        selinfo.put("expy_end", ""); // 有效期止
        selinfo.put("rx_flag", ""); // 处方药标志
        selinfo.put("trdn_flag", ""); // 拆零标志
        selinfo.put("finl_trns_pric", ""); // 最终成交单价
        selinfo.put("sel_retn_cnt", ""); // 销售/退货数量
        selinfo.put("sel_retn_time", ""); // 销售/退货时间
        selinfo.put("sel_retn_opter_name", ""); // 销售/退货经办人姓名
        selinfo.put("memo", ""); // 备注
        selinfo.put("medins_prod_sel_no", ""); // 商品销售流水号
        selinfo.put("mdtrt_sn", ""); // 就医流水号
        selinfo.put("drugtracinfo", ""); // 溯源码节点信息
        array.add(selinfo);
        JSONObject drugtracinfo = new JSONObject();
        drugtracinfo.put("drug_trac_codg","");//药品追溯码
        JSONArray array1 = new JSONArray();
        array1.add(drugtracinfo);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("selinfoDetail", array);
        jsonObject.put("drugtracinfo",array1);
        return mdRequestUtil.getMedicareData("3506A", jsonObject);
    }

    /**
     * 商品信息删除 3507
     * 通过此交易删除某一批次商品信息
     *
     * @return
     */
    @Override
    public JSONObject removeProductRecord_3507() {
        JSONObject data =  new JSONObject();
        data.put("fixmedins_bchno","");
        data.put("inv_data_type","");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
        return mdRequestUtil.getMedicareData("3507", jsonObject);
    }

    /**
     * 商品信息删除 3507A
     * 通过此交易删除某一批次商品信息
     *
     * @return
     */
    @Override
    public JSONObject removeProductRecordList() {
        JSONObject data =  new JSONObject();
        data.put("fixmedins_bchno","");
        data.put("inv_data_type","");
        JSONArray array = new JSONArray();
        array.add(data);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("goodDetail", array);
        return mdRequestUtil.getMedicareData("3507", jsonObject);

    }

    /**
     * 商品库存信息查询 3508
     *
     * @return
     */
    @Override
    public JSONObject getInventoryInfo_3508() {
        JSONObject data = new JSONObject();
        data.put("fixmedins_code", ""); // 定点医药机构编号
        data.put("medins_list_codg", ""); // 医药机构目录编码
        data.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        data.put("begndate", ""); // 开始日期
        data.put("enddate", ""); // 结束日期
        data.put("medins_prod_inv_no", ""); // 定点医药机构商品库存流水号
        data.put("med_list_codg", ""); // 医疗目录编码
        data.put("medins_list_name", ""); // 医药机构目录名称
        data.put("rx_flag", ""); // 处方药标志
        data.put("list_sp_item_flag", ""); // 目录特项标志
        data.put("trdn_flag", ""); // 拆零标志
        data.put("invdate", ""); // 库存日期
        data.put("manu_lotnum", ""); // 生产批号
        data.put("manu_date", ""); // 生产日期
        data.put("expy_end", ""); // 有效期止
        data.put("memo", ""); // 备注
        data.put("vali_flag", ""); // 有效标志
        data.put("rid", ""); // 数据唯一记录号
        data.put("crter_id", ""); // 创建人 ID
        data.put("crter_name", ""); // 创建人姓名
        data.put("crte_optins_no", ""); // 创建机构编号
        data.put("opter_id", ""); // 经办人 ID
        data.put("opter_name", ""); // 经办人姓名
        data.put("optins_no", ""); // 经办机构编号
        data.put("poolarea_no", ""); // 统筹区编号
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
        return mdRequestUtil.getMedicareData("3508", jsonObject);
    }

    /**
     * 库存变更记录查询 3509
     *
     * @return
     */
    @Override
    public JSONObject getInventoryChangeRecords_3509() {
        JSONObject data = new JSONObject();
        data.put("fixmedins_code", ""); // 定点医药机构编号
        data.put("medins_list_codg", ""); // 医药机构目录编码
        data.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        data.put("begndate", ""); // 开始日期
        data.put("enddate", ""); // 结束日期
        data.put("medins_prod_inv_chg_no", ""); // 定点医药机构商品库存变更流水号
        data.put("med_list_codg", ""); // 医疗目录编码
        data.put("inv_chg_type", ""); // 库存变更类型
        data.put("medins_list_name", ""); // 医药机构目录名称
        data.put("rx_flag", ""); // 处方药标志
        data.put("list_sp_item_flag", ""); // 目录特项标志
        data.put("trdn_flag", ""); // 拆零标志
        data.put("inv_chg_time", ""); // 库存变更时间
        data.put("inv_chg_opter_name", ""); // 库存变更经办人姓名
        data.put("memo", ""); // 备注
        data.put("vali_flag", ""); // 有效标志
        data.put("rid", ""); // 数据唯一记录号
        data.put("crter_id", ""); // 创建人 ID
        data.put("crter_name", ""); // 创建人姓名
        data.put("crte_optins_no", ""); // 创建机构编号
        data.put("opter_id", ""); // 经办人 ID
        data.put("opter_name", ""); // 经办人姓名
        data.put("optins_no", ""); // 经办机构编号
        data.put("poolarea_no", ""); // 统筹区编号
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
        return mdRequestUtil.getMedicareData("3509", jsonObject);
    }

    /**
     * 采购记录查询 3510
     *
     * @return
     */
    @Override
    public JSONObject getProcurementRecords_3510() {
        JSONObject data = new JSONObject();
        data.put("fixmedins_code", ""); // 定点医药机构编号
        data.put("medins_list_codg", ""); // 医药机构目录编码
        data.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        data.put("begndate", ""); // 开始日期
        data.put("enddate", ""); // 结束日期
        data.put("medins_prod_purc_no", ""); // 定点医药机构商品采购流水号
        data.put("med_list_codg", ""); // 医疗目录编码
        data.put("medins_list_name", ""); // 医药机构目录名称
        data.put("dynt_no", ""); // 随货单号
        data.put("spler_name", ""); // 供货商名称
        data.put("spler_pmtno", ""); // 供应商许可证号
        data.put("manu_lotnum", ""); // 生产批号
        data.put("prodentp_name", ""); // 生产企业名称
        data.put("aprvno", ""); // 批准文号
        data.put("manu_date", ""); // 生产日期
        data.put("expy_end", ""); // 有效期止
        data.put("purc_invo_codg", ""); // 采购发票编码
        data.put("purc_invo_no", ""); // 采购发票号
        data.put("rx_flag", ""); // 处方药标志
        data.put("list_sp_item_flag", ""); // 目录特项标志
        data.put("purc_retn_stoin_time", ""); // 采购/退货入库时间
        data.put("purc_retn_opter_name", ""); // 采购/退货经办人姓名
        data.put("prod_geay_flag", ""); // 商品赠送标志
        data.put("prod_retn_flag", ""); // 商品退货标志
        data.put("memo", ""); // 备注
        data.put("vali_flag", ""); // 有效标志
        data.put("rid", ""); // 数据唯一记录号
        data.put("crter_id", ""); // 创建人 ID
        data.put("crter_name", ""); // 创建人姓名
        data.put("crte_optins_no", ""); // 创建机构编号
        data.put("opter_id", ""); // 经办人 ID
        data.put("opter_name", ""); // 经办人姓名
        data.put("optins_no", ""); // 经办机构编号
        data.put("poolarea_no", ""); // 统筹区编号
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
        return mdRequestUtil.getMedicareData("3510", jsonObject);
    }

    /**
     * 销售记录查询 3511
     *
     * @return
     */
    @Override
    public JSONObject getSalesRecords_3511() {
        JSONObject data = new JSONObject();
        data.put("fixmedins_code", ""); // 定点医药机构编号
        data.put("medins_list_cod", ""); // 医药机构目录编码
        data.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        data.put("begndate", ""); // 开始日期
        data.put("enddate", ""); // 结束日期
        data.put("medins_prod_sel_no", ""); // 定点医药机构商品销售流水号
        data.put("med_list_codg", ""); // 医疗目录编码
        data.put("medins_list_name", ""); // 医药机构目录名称
        data.put("prsc_dr_cert_type", ""); // 开单医师证件类型
        data.put("prsc_dr_certno", ""); // 开单医师证件号码
        data.put("bilg_dr_name", ""); // 开单医师姓名
        data.put("phar_cert_type", ""); // 药师证件类型
        data.put("phar_certno", ""); // 药师证件号码
        data.put("phar_name", ""); // 药师姓名
        data.put("phar_prac_cert_no", ""); // 药师执业资格证号
        data.put("hi_feesetl_type", ""); // 医保费用结算类型
        data.put("setl_id", ""); // 结算 ID
        data.put("psn_no", ""); // 人员编号
        data.put("psn_cert_type", ""); // 人员证件类型
        data.put("certno", ""); // 证件号码
        data.put("psn_name", ""); // 人员姓名
        data.put("manu_lotnum", ""); // 生产批号
        data.put("manu_date", ""); // 生产日期
        data.put("expy_end", ""); // 有效期止
        data.put("elec_supn_codg", ""); // 电子监管编码
        data.put("rx_flag", ""); // 处方药标志
        data.put("list_sp_item_flag", ""); // 目录特项标志
        data.put("trdn_flag", ""); // 拆零标志
        data.put("sel_retn_time", ""); // 销售/退货时间
        data.put("sel_retn_opter_name", ""); // 销售/退货经办人姓名
        data.put("memo", ""); // 备注
        data.put("vali_flag", ""); // 有效标志
        data.put("rid", ""); // 数据唯一记录号
        data.put("crter_id", ""); // 创建人 ID
        data.put("crter_name", ""); // 创建人姓名
        data.put("crte_optins_no", ""); // 创建机构编号
        data.put("opter_id", ""); // 经办人 ID
        data.put("opter_name", ""); // 经办人姓名
        data.put("optins_no", ""); // 经办机构编号
        data.put("poolarea_no", ""); // 统筹区编号
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
        return mdRequestUtil.getMedicareData("3511", jsonObject);
    }

    /**
     * 入库药品追溯信息查询   3512
     *
     * @return
     */
    @Override
    public JSONObject getInboundDetails_3512() {
        JSONObject data = new JSONObject();
        data.put("fixmedins_code", ""); // 定点医药机构编号
        data.put("medins_list_codg", ""); // 医药机构目录编码
        data.put("fixmedins_bchno", ""); // 定点医药机构批次流水号
        data.put("begndate", ""); // 开始日期，格式: yyyy-MM-dd
        data.put("enddate", ""); // 结束日期，格式: yyyy-MM-dd
        data.put("med_list_codg", ""); // 医疗目录编码
        data.put("drug_trac_codg", ""); // 药品追溯码
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
        return mdRequestUtil.getMedicareData("3512", jsonObject);
    }

    /**
     * 销售药品追溯信息查询   3513
     *
     * @return
     */
    @Override
    public JSONObject getSalesDetails_3513() {
        JSONObject data = new JSONObject();
        data.put("fixmedins_code", "");  // 定点医药机构编号 (必填)
        data.put("medins_list_codg", ""); // 医药机构目录编码 (选填)
        data.put("fixmedins_bchno", "");  // 定点医药机构批次流水号 (选填)
        data.put("begndate", "");          // 开始日期 (选填)
        data.put("enddate", "");            // 结束日期 (选填)
        data.put("med_list_codg", "");     // 医疗目录编码 (选填)
        data.put("mdtrt_id", "");           // 就诊 ID (选填)
        data.put("psn_no", "");             // 人员编号 (选填)
        data.put("psn_cert_type", "");      // 人员证件类型 (必填)
        data.put("certno", "");             // 证件号码 (选填)
        data.put("psn_name", "");           // 人员姓名 (选填)
        data.put("drug_trac_codg", "");     // 药品追溯码 (选填)
        data.put("sel_retn_opter_name", ""); // 销售/退货经办人姓名 (选填)
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", data);
        return mdRequestUtil.getMedicareData("3513", jsonObject);
    }
}
