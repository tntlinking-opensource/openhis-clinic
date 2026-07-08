package com.geeke.stock.service;

import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.common.data.Parameter;

import java.util.ArrayList;
import com.geeke.stock.entity.*;
import com.geeke.utils.excel.ExcelExportBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service("currentInventoryService")
@Transactional(readOnly = true)
public class CurrentInventoryService {
    @Autowired
    private DrugService drugService;

    @Autowired
    private SupplierStockService supplierStockService;

    @Autowired
    private StuffService stuffService;

    @Autowired
    private MedicinalStockControlService medicinalStockControlService;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    @Transactional(readOnly = true)
    public HashMap<String, BigDecimal> getAllDrugs(List<Parameter> params, String orderby) {
        List<Drug> drugs = drugService.listAllDrug(params, orderby);
        BigDecimal totalPrice = new BigDecimal("0");
        BigDecimal totalSellingPrice = new BigDecimal("0");
        for (Drug drug : drugs) {
            //获取每个药品的进价合计
            //进行售价计算
            BigDecimal leastBid = drug.getPrice().divide(new BigDecimal(drug.getPreparation()),4,BigDecimal.ROUND_HALF_UP);
            BigDecimal multiply = leastBid.multiply(drug.getStock().getStorageStock().subtract((drug.getStock().getUsedStock().add(drug.getStock().getReimburseStock()))));
            totalSellingPrice = totalSellingPrice.add(multiply);
        }

        //获取每个药品的进价合计
        List<String> drugIds = new ArrayList<>();
        for (Drug d : drugs) { drugIds.add(d.getId()); }
        BigDecimal byDrugOrStuffIds = medicinalStorageControlService.getByDrugOrStuffIds(drugIds);
        totalPrice = totalPrice.add(byDrugOrStuffIds);

        HashMap<String, BigDecimal> totalMap = new HashMap<>();
        totalMap.put("totalPrice",totalPrice);
        totalMap.put("totalSellingPrice",totalSellingPrice);
        return totalMap;
    }

    @Transactional(readOnly = true)
    public Page<MedicinalStorageControl> getBatchNumberDrug(SearchParams searchParams) {
        Page<MedicinalStorageControl> medicinalStorageControlPage = medicinalStorageControlService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());

        return medicinalStorageControlPage;
    }
    @Transactional(readOnly = true)
    public HashMap<String, BigDecimal> getgetStuffSalesStat(List<Parameter> params, String orderby) {
        List<Stuff> stuffs = stuffService.listAlls(params, orderby);
        BigDecimal totalPrice = new BigDecimal("0");
        BigDecimal totalSellingPrice = new BigDecimal("0");
        for (Stuff stuff : stuffs) {
            //获取材料的总进价
            //获取材料的总售价
            BigDecimal divide = stuff.getPriceOutSell().divide(new BigDecimal(stuff.getPackNumber() + ""), 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal multiply = divide.multiply(stuff.getStock().getStorageStock().subtract((stuff.getStock().getUsedStock().add(stuff.getStock().getReimburseStock()))));
            totalSellingPrice = totalSellingPrice.add(multiply);
        }

        //获取材料的总进价
        List<String> stuffIds = new ArrayList<>();
        for (Stuff s : stuffs) { stuffIds.add(s.getId()); }
        BigDecimal byDrugOrStuffIds = medicinalStorageControlService.getByDrugOrStuffIds(stuffIds);
        totalPrice = totalPrice.add(byDrugOrStuffIds);

        HashMap<String, BigDecimal> totalMap = new HashMap<>();
        totalMap.put("totalPrice",totalPrice);
        totalMap.put("totalSellingPrice",totalSellingPrice);
        return totalMap;
    }
    @Transactional(readOnly = true)
    public Page<MedicinalStorageControl> getBatchNumberStuff(SearchParams searchParams) {
        Page<MedicinalStorageControl> medicinalStorageControlPage = medicinalStorageControlService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return medicinalStorageControlPage;
    }
    @Transactional(readOnly = true)
    public HashMap<String, BigDecimal> getDrugSalesStatByNumber(SearchParams searchParams) {
        List<MedicinalStorageControl> medicinalStorageControls = medicinalStorageControlService.listAll(searchParams.getParams(), searchParams.getOrder());
        BigDecimal totalPrice = new BigDecimal("0");
        BigDecimal totalSellingPrice = new BigDecimal("0");
        if(!CollectionUtils.isEmpty(medicinalStorageControls)){
            for (MedicinalStorageControl medicinalStorageControl : medicinalStorageControls) {

                //获取所有的进价合计
                BigDecimal purchasing = new BigDecimal(medicinalStorageControl.getSupplierStock().getLeastBid() + "").multiply(medicinalStorageControl.getStorageStock().subtract(medicinalStorageControl.getUsedStock().add(medicinalStorageControl.getReimburseStock())));
                totalPrice = totalPrice.add(purchasing);

                //获取所有的售价
                BigDecimal divide = new BigDecimal((medicinalStorageControl.getDrug().getPrice()==null?0:medicinalStorageControl.getDrug().getPrice()) + "").divide(new BigDecimal((medicinalStorageControl.getDrug().getPreparation()==null?"0":medicinalStorageControl.getDrug().getPreparation())), 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal selling = (medicinalStorageControl.getStorageStock().subtract(medicinalStorageControl.getUsedStock().add(medicinalStorageControl.getReimburseStock()))).multiply(divide);
                totalSellingPrice = totalSellingPrice.add(selling);
            }
        }
        HashMap<String, BigDecimal> totalMap = new HashMap<>();
        totalMap.put("totalPrice",totalPrice);
        totalMap.put("totalSellingPrice",totalSellingPrice);
        return totalMap;
    }

    @Transactional(readOnly = true)
    public HashMap<String, BigDecimal> getStuffSalesStatNumber(SearchParams searchParams) {
        List<MedicinalStorageControl> medicinalStorageControls = medicinalStorageControlService.listAll(searchParams.getParams(), searchParams.getOrder());
        BigDecimal totalPrice = new BigDecimal("0");
        BigDecimal totalSellingPrice = new BigDecimal("0");
        if(!CollectionUtils.isEmpty(medicinalStorageControls)){
            for (MedicinalStorageControl medicinalStorageControl : medicinalStorageControls) {
                //获取进价合计
                BigDecimal purchasing = new BigDecimal(medicinalStorageControl.getSupplierStock().getLeastBid() + "").multiply(medicinalStorageControl.getStorageStock().subtract(medicinalStorageControl.getUsedStock().add(medicinalStorageControl.getReimburseStock())));
                totalPrice = totalPrice.add(purchasing);

                //售价合计
                BigDecimal divide = new BigDecimal(medicinalStorageControl.getStuff().getPriceOutSell() + "").divide(new BigDecimal(medicinalStorageControl.getStuff().getPackNumber()),4, BigDecimal.ROUND_HALF_UP);
                BigDecimal selling = (medicinalStorageControl.getStorageStock().subtract(medicinalStorageControl.getUsedStock().add(medicinalStorageControl.getReimburseStock()))).multiply(divide);
                totalSellingPrice = totalSellingPrice.add(selling);
            }
        }
        HashMap<String, BigDecimal> totalMap = new HashMap<>();
        totalMap.put("totalPrice",totalPrice);
        totalMap.put("totalSellingPrice",totalSellingPrice);
        return totalMap;
    }

    @Transactional
    public void exportTable(SearchParams searchParams, HttpServletResponse response) throws IOException {
        if(Objects.equals("1",searchParams.getColumnName())){
            exportDrug(searchParams,response);
        }else if(Objects.equals("2",searchParams.getColumnName())){
            exportDrugByBatchNo(searchParams,response);
        }else if(Objects.equals("3",searchParams.getColumnName())){
            exportStuff(searchParams,response);
        }else {
            exportStuffByBatchNo(searchParams,response);
        }
    }

    //按批号导出材料
    private void exportStuffByBatchNo(SearchParams searchParams, HttpServletResponse response) throws IOException {
        List<MedicinalStorageControl> medicinalStorageControls = medicinalStorageControlService.listAll(searchParams.getParams(), searchParams.getOrder());

        // 预计算衍生字段
        if (!CollectionUtils.isEmpty(medicinalStorageControls)) {
            for (MedicinalStorageControl msc : medicinalStorageControls) {
                // 剩余数量（原始数值）
                String inventory = msc.getStorageStock().subtract(msc.getUsedStock().add(msc.getReimburseStock())).stripTrailingZeros().toPlainString();
                msc.getStuff().setInventory(Integer.parseInt(inventory));

                // 售价合计
                BigDecimal selling = new BigDecimal(msc.getStuff().getPriceOutSell() + "").divide(new BigDecimal(msc.getStuff().getPackNumber()), 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal sellingTotal = new BigDecimal(msc.getStuff().getInventory()).multiply(selling);
                msc.getStuff().setRetailPrice(sellingTotal);

                // 成本合计
                BigDecimal allBid = msc.getSupplierStock().getLeastBid().multiply(new BigDecimal(msc.getStuff().getInventory()));
                msc.getStuff().setBid(allBid);

                // 剩余数量（格式化字符串）
                int inv = msc.getStuff().getInventory();
                int packNum = msc.getStuff().getPackNumber();
                String number = Math.floor(inv / packNum) >= 0
                        ? (int) Math.floor(inv / packNum) + msc.getStuff().getPackUnit().getName()
                        + ((inv % packNum > 0) ? (inv % packNum) + msc.getStuff().getMinUnit().getName() : "")
                        : inv + msc.getStuff().getMinUnit().getName();
                msc.getStuff().setStockNumber(number);
            }
        }

        new ExcelExportBuilder("材料当前库存-按批号")
                .addColumns(
                        new ExcelExportBuilder.Column("材料名称", "stuff.name"),
                        new ExcelExportBuilder.Column("材料分类", "stuff.type.name"),
                        new ExcelExportBuilder.Column("规格", "supplierStock.norms"),
                        new ExcelExportBuilder.Column("生产厂商", "stuff.factory.name"),
                        new ExcelExportBuilder.Column("供应商", "supplierStock.supplierId.name"),
                        new ExcelExportBuilder.Column("批号", "supplierStock.batchNo"),
                        new ExcelExportBuilder.Column("售价(元)", "stuff.priceOutSell"),
                        new ExcelExportBuilder.Column("售价合计(元)", "stuff.retailPrice"),
                        new ExcelExportBuilder.Column("成本价(元)", "supplierStock.bid"),
                        new ExcelExportBuilder.Column("成本合计(元)", "stuff.bid"),
                        new ExcelExportBuilder.Column("剩余数量", "stuff.stockNumber")
                )
                .data(medicinalStorageControls)
                .write(response, "材料当前库存-按批号.xlsx");
    }

    //按商品导出材料
    private void exportStuff(SearchParams searchParams, HttpServletResponse response) throws IOException {
        List<Stuff> stuffs = stuffService.listAlls(searchParams.getParams(), searchParams.getOrder());

        // 预计算衍生字段
        if (!CollectionUtils.isEmpty(stuffs)) {
            for (Stuff stuff : stuffs) {
                // 剩余数量（原始数值）
                if (!Objects.isNull(stuff.getStock()) && !Objects.isNull(stuff.getStock().getStorageStock())) {
                    String inventory = stuff.getStock().getStorageStock().subtract(stuff.getStock().getUsedStock().add(stuff.getStock().getReimburseStock())).stripTrailingZeros().toPlainString();
                    stuff.setInventory(Integer.parseInt(inventory));
                } else {
                    stuff.setInventory(0);
                }

                // 规格
                String specification = stuff.getPackNumber() + stuff.getMinUnit().getName() + "/" + stuff.getPackUnit().getName();
                stuff.setSpecifications(specification);

                // 售价合计
                BigDecimal selling = new BigDecimal(stuff.getPriceOutSell() + "").divide(new BigDecimal(stuff.getPackNumber()), 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal sellingTotal = new BigDecimal(stuff.getInventory()).multiply(selling);
                stuff.setRetailPrice(sellingTotal);

                // 成本合计
                BigDecimal totalCost = medicinalStorageControlService.getByDrugOrStuffId(stuff.getId());
                stuff.setBid(totalCost);

                // 剩余数量（格式化字符串）
                int inv = stuff.getInventory();
                int packNum = stuff.getPackNumber();
                String number = Math.floor(inv / packNum) >= 0
                        ? (int) Math.floor(inv / packNum) + stuff.getPackUnit().getName()
                        + ((inv % packNum > 0) ? (inv % packNum) + stuff.getMinUnit().getName() : "")
                        : inv + stuff.getMinUnit().getName();
                stuff.setStockNumber(number);
            }
        }

        new ExcelExportBuilder("材料当前库存-按商品")
                .addColumns(
                        new ExcelExportBuilder.Column("材料名称", "name"),
                        new ExcelExportBuilder.Column("材料分类", "type.name"),
                        new ExcelExportBuilder.Column("规格", "specifications"),
                        new ExcelExportBuilder.Column("生产厂商", "factory.name"),
                        new ExcelExportBuilder.Column("售价(元)", "priceOutSell"),
                        new ExcelExportBuilder.Column("售价合计(元)", "retailPrice"),
                        new ExcelExportBuilder.Column("成本合计(元)", "bid"),
                        new ExcelExportBuilder.Column("剩余数量", "stockNumber")
                )
                .data(stuffs)
                .write(response, "材料当前库存-按商品.xlsx");
    }

    //按批号导出药品
    private void exportDrugByBatchNo(SearchParams searchParams, HttpServletResponse response) throws IOException {
        List<MedicinalStorageControl> medicinalStorageControls = medicinalStorageControlService.listAll(searchParams.getParams(), searchParams.getOrder());

        // 预计算衍生字段
        if (!CollectionUtils.isEmpty(medicinalStorageControls)) {
            for (MedicinalStorageControl msc : medicinalStorageControls) {
                // 剩余数量（原始数值）
                String inventory = msc.getStorageStock().subtract(msc.getUsedStock().add(msc.getReimburseStock())).stripTrailingZeros().toPlainString();
                msc.getDrug().setInventory(Integer.parseInt(inventory));

                // 售价合计
                BigDecimal selling = new BigDecimal(msc.getDrug().getPrice() + "").divide(new BigDecimal(msc.getDrug().getPreparation()), 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal sellingTotal = msc.getStorageStock().subtract(msc.getUsedStock().add(msc.getReimburseStock())).multiply(selling);
                msc.getDrug().setRetailPrice(sellingTotal);

                // 成本合计
                BigDecimal allBid = msc.getSupplierStock().getLeastBid().multiply(msc.getStorageStock().subtract(msc.getUsedStock().add(msc.getReimburseStock())));
                msc.getDrug().setBid(allBid);

                // 剩余数量（格式化字符串）
                int inv = msc.getDrug().getInventory();
                int prep = Integer.parseInt(msc.getDrug().getPreparation());
                String number = Math.floor(inv / prep) >= 0
                        ? (int) Math.floor(inv / prep) + msc.getDrug().getPack().getName()
                        + ((inv % prep > 0) ? (inv % prep) + msc.getDrug().getPreparationUnit().getName() : "")
                        : inv + msc.getDrug().getPreparationUnit().getName();
                msc.getDrug().setStockNumber(number);
            }
        }

        new ExcelExportBuilder("药品当前库存-按批号")
                .addColumns(
                        new ExcelExportBuilder.Column("药品名称", "drug.goodsName"),
                        new ExcelExportBuilder.Column("药品分类", "drug.type.name"),
                        new ExcelExportBuilder.Column("规格", "supplierStock.norms"),
                        new ExcelExportBuilder.Column("生产厂商", "drug.factory.name"),
                        new ExcelExportBuilder.Column("供应商", "supplierStock.supplierId.name"),
                        new ExcelExportBuilder.Column("批号", "supplierStock.batchNo"),
                        new ExcelExportBuilder.Column("售价(元)", "drug.price"),
                        new ExcelExportBuilder.Column("售价合计(元)", "drug.retailPrice"),
                        new ExcelExportBuilder.Column("成本价(元)", "supplierStock.bid"),
                        new ExcelExportBuilder.Column("成本合计(元)", "drug.bid"),
                        new ExcelExportBuilder.Column("剩余数量", "drug.stockNumber")
                )
                .data(medicinalStorageControls)
                .write(response, "药品当前库存-按批号.xlsx");
    }

    //按商品导出药品
    @Transactional
    public void exportDrug(SearchParams searchParams, HttpServletResponse response) throws IOException {
        List<Drug> drugs = drugService.listAlls(searchParams.getParams(), searchParams.getOrder());

        // 预计算衍生字段
        if (!CollectionUtils.isEmpty(drugs)) {
            for (Drug drug : drugs) {
                // 规格
                String specification = drug.getDosis() + drug.getDosisUnit().getName() + "*"
                        + drug.getPreparation() + drug.getPreparationUnit().getName() + "/" + drug.getPack().getName();
                drug.setYpgg(specification);

                // 剩余数量（原始数值）
                if (!Objects.isNull(drug.getStock()) && !Objects.isNull(drug.getStock().getStorageStock())) {
                    String inventory = drug.getStock().getStorageStock().subtract(drug.getStock().getUsedStock().add(drug.getStock().getReimburseStock())).stripTrailingZeros().toPlainString();
                    drug.setInventory(Integer.parseInt(inventory));
                } else {
                    drug.setInventory(0);
                }

                // 售价合计
                BigDecimal selling = new BigDecimal(drug.getPrice() + "").divide(new BigDecimal(drug.getPreparation()), 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal sellingTotal = drug.getStock().getStorageStock().subtract(drug.getStock().getUsedStock().add(drug.getStock().getReimburseStock())).multiply(selling);
                drug.setRetailPrice(sellingTotal);

                // 成本合计
                BigDecimal totalCost = medicinalStorageControlService.getByDrugOrStuffId(drug.getId());
                drug.setBid(totalCost);

                // 剩余数量（格式化字符串）
                int inv = drug.getInventory();
                int prep = Integer.parseInt(drug.getPreparation());
                String number = Math.floor(inv / prep) >= 0
                        ? (int) Math.floor(inv / prep) + drug.getPack().getName()
                        + ((inv % prep > 0) ? (inv % prep) + drug.getPreparationUnit().getName() : "")
                        : inv + drug.getPreparationUnit().getName();
                drug.setStockNumber(number);
            }
        }

        new ExcelExportBuilder("药品当前库存-按商品")
                .addColumns(
                        new ExcelExportBuilder.Column("药品名称", "goodsName"),
                        new ExcelExportBuilder.Column("药品分类", "type.name"),
                        new ExcelExportBuilder.Column("规格", "ypgg"),
                        new ExcelExportBuilder.Column("生产厂商", "factory.name"),
                        new ExcelExportBuilder.Column("售价(元)", "price"),
                        new ExcelExportBuilder.Column("售价合计(元)", "retailPrice"),
                        new ExcelExportBuilder.Column("成本合计(元)", "bid"),
                        new ExcelExportBuilder.Column("剩余数量", "stockNumber")
                )
                .data(drugs)
                .write(response, "药品当前库存-按商品.xlsx");
    }


}
