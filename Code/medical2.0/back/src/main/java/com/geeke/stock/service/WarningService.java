package com.geeke.stock.service;

import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.Stuff;
import com.geeke.stock.entity.SupplierStock;
import com.geeke.utils.excel.ExcelExportBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("warningService")
@Transactional(readOnly = true)
public class WarningService {

    @Autowired
    private SupplierStockService supplierStockService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private StuffService stuffService;


    @Transactional(readOnly = true)
    public Page<SupplierStock> getDrugIndateWarning(List<Parameter> parameters, int offset, int limit, String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        Page<SupplierStock> indateWarning = supplierStockService.getDrugIndateWarning(parameters, offset, limit, orderby);
        return indateWarning;


    }

    @Transactional(readOnly = true)
    public Page<Drug> getDrugInventoryWarning(SearchParams searchParams) {
        Page<Drug> drugInventoryWarning = drugService.getDrugInventoryWarning(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());

        return drugInventoryWarning;
    }

    @Transactional(readOnly = true)
    public Page<SupplierStock> getStuffIndateWarning(List<Parameter> parameters, int offset, int limit, String orderby) {
        Page<SupplierStock> stuffIndateWarning = supplierStockService.getStuffIndateWarning(parameters, offset, limit, orderby);
        return stuffIndateWarning;
    }


    public Page<Stuff> getStuffInventoryWarning(List<Parameter> params, int offset, int limit, String orderby) {
        Page<Stuff> stuffInventoryWarning = stuffService.getStuffInventoryWarning(params, offset, limit, orderby);
        return stuffInventoryWarning;
    }

    //预警导出
    @Transactional
    public void exportTable(SearchParams searchParams, HttpServletResponse response) throws IOException {
        if(Objects.equals(searchParams.getColumnName(),"0")){
            //药品有效期导出
            exportDrugIndate(searchParams,response);
        }else if(Objects.equals("1",searchParams.getColumnName())){
            exportDrugInventory(searchParams,response);
        }else if(Objects.equals("2",searchParams.getColumnName())){
            exportStuffIndate(searchParams,response);
        }else {
            exportStuffInventory(searchParams,response);
        }
    }

    //材料库存预警导出
    private void exportStuffInventory(SearchParams searchParams, HttpServletResponse response) throws IOException {
        Page<Stuff> stuffInventoryWarning = stuffService.getStuffInventoryWarning(searchParams.getParams(), 0, 1000000, searchParams.getOrder());
        List<Map<String, Object>> rows = new ArrayList<>();
        if (!CollectionUtils.isEmpty(stuffInventoryWarning.getRows())) {
            for (Stuff stuff : stuffInventoryWarning.getRows()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("name", stuff.getName());
                row.put("typeName", stuff.getType().getName());
                // 规格
                String specification = stuff.getPackNumber() + stuff.getMinUnit().getName() + "/" + stuff.getPackUnit().getName();
                row.put("specification", specification);
                row.put("factoryName", stuff.getFactory().getName());
                // 剩余数量
                String inventory = stuff.getStock().getStorageStock().subtract(stuff.getStock().getUsedStock().add(stuff.getStock().getReimburseStock())).stripTrailingZeros().toPlainString();
                stuff.setInventory(Integer.parseInt(inventory));
                String number = formatStuffNumber(stuff);
                row.put("number", number);
                rows.add(row);
            }
        }
        new ExcelExportBuilder("材料库存预警导出")
                .addColumns(
                        new ExcelExportBuilder.Column("材料名称", "name"),
                        new ExcelExportBuilder.Column("材料分类", "typeName"),
                        new ExcelExportBuilder.Column("规格", "specification"),
                        new ExcelExportBuilder.Column("生产厂商", "factoryName"),
                        new ExcelExportBuilder.Column("剩余数量", "number")
                )
                .data(rows)
                .write(response, "材料库存预警导出.xlsx");
    }

    //材料有效期预警导出
    private void exportStuffIndate(SearchParams searchParams, HttpServletResponse response) throws IOException {
        Page<SupplierStock> stuffIndateWarning = supplierStockService.getStuffIndateWarning(searchParams.getParams(), 0, 1000000, searchParams.getOrder());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Map<String, Object>> rows = new ArrayList<>();
        if (!CollectionUtils.isEmpty(stuffIndateWarning.getRows())) {
            for (SupplierStock supplierStock : stuffIndateWarning.getRows()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("stuffName", supplierStock.getStuff().getName());
                row.put("typeName", supplierStock.getStuff().getType().getName());
                row.put("norms", supplierStock.getNorms());
                row.put("factoryName", supplierStock.getFactory().getName());
                row.put("supplierName", supplierStock.getSupplierId().getName());
                row.put("batchNo", supplierStock.getBatchNo());
                // 剩余数量
                if (!Objects.isNull(supplierStock.getMedicinalStorageControl()) && !Objects.isNull(supplierStock.getMedicinalStorageControl().getStorageStock())) {
                    String inventory = supplierStock.getMedicinalStorageControl().getStorageStock().subtract(supplierStock.getMedicinalStorageControl().getUsedStock().add(supplierStock.getMedicinalStorageControl().getReimburseStock())).stripTrailingZeros().toPlainString();
                    supplierStock.getStuff().setInventory(Integer.parseInt(inventory));
                } else {
                    supplierStock.getStuff().setInventory(0);
                }
                row.put("number", formatStuffNumber(supplierStock.getStuff()));
                // 有效期
                row.put("endDate", sdf.format(supplierStock.getEndDate()));
                // 状态
                row.put("status", Objects.equals("0", supplierStock.getCancellation()) ? "即将过期" : "已过期");
                rows.add(row);
            }
        }
        new ExcelExportBuilder("材料有效期预警导出")
                .addColumns(
                        new ExcelExportBuilder.Column("材料名称", "stuffName"),
                        new ExcelExportBuilder.Column("材料分类", "typeName"),
                        new ExcelExportBuilder.Column("规格", "norms"),
                        new ExcelExportBuilder.Column("生产厂商", "factoryName"),
                        new ExcelExportBuilder.Column("供应商", "supplierName"),
                        new ExcelExportBuilder.Column("批号", "batchNo"),
                        new ExcelExportBuilder.Column("剩余数量", "number"),
                        new ExcelExportBuilder.Column("有效期", "endDate"),
                        new ExcelExportBuilder.Column("状态", "status")
                )
                .data(rows)
                .write(response, "材料有效期预警导出.xlsx");
    }

    //药品库存预警导出
    private void exportDrugInventory(SearchParams searchParams, HttpServletResponse response) throws IOException {
        Page<Drug> drugInventoryWarning = drugService.getDrugInventoryWarning(searchParams.getParams(), 0, 1000000, searchParams.getOrder());
        List<Map<String, Object>> rows = new ArrayList<>();
        if (!CollectionUtils.isEmpty(drugInventoryWarning.getRows())) {
            for (Drug drug : drugInventoryWarning.getRows()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("goodsName", drug.getGoodsName());
                row.put("typeName", drug.getType().getName());
                // 规格
                String specification = drug.getDosis() + drug.getDosisUnit().getName() + "*" + drug.getPreparation() + drug.getPreparationUnit().getName() + "/" + drug.getPack().getName();
                row.put("specification", specification);
                row.put("factoryName", drug.getFactory().getName());
                // 剩余数量
                String inventory = drug.getStock().getStorageStock().subtract(drug.getStock().getUsedStock().add(drug.getStock().getReimburseStock())).stripTrailingZeros().toPlainString();
                drug.setInventory(Integer.parseInt(inventory));
                row.put("number", formatDrugNumber(drug));
                rows.add(row);
            }
        }
        new ExcelExportBuilder("药品库存预警导出")
                .addColumns(
                        new ExcelExportBuilder.Column("药品名称", "goodsName"),
                        new ExcelExportBuilder.Column("药品分类", "typeName"),
                        new ExcelExportBuilder.Column("规格", "specification"),
                        new ExcelExportBuilder.Column("生产厂商", "factoryName"),
                        new ExcelExportBuilder.Column("剩余数量", "number")
                )
                .data(rows)
                .write(response, "药品库存预警导出.xlsx");
    }

    //药品有效期预警导出
    private void exportDrugIndate(SearchParams searchParams, HttpServletResponse response) throws IOException {
        Page<SupplierStock> drugIndateWarning = supplierStockService.getDrugIndateWarning(searchParams.getParams(), 0, 1000000, searchParams.getOrder());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Map<String, Object>> rows = new ArrayList<>();
        if (!CollectionUtils.isEmpty(drugIndateWarning.getRows())) {
            for (SupplierStock supplierStock : drugIndateWarning.getRows()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("drugName", supplierStock.getDrug().getGoodsName());
                row.put("typeName", supplierStock.getDrug().getType().getName());
                row.put("norms", supplierStock.getNorms());
                row.put("factoryName", supplierStock.getFactory().getName());
                row.put("supplierName", supplierStock.getSupplierId().getName());
                row.put("batchNo", supplierStock.getBatchNo());
                // 剩余数量
                if (!Objects.isNull(supplierStock.getMedicinalStorageControl()) && !Objects.isNull(supplierStock.getMedicinalStorageControl().getStorageStock())) {
                    String inventory = supplierStock.getMedicinalStorageControl().getStorageStock().subtract(supplierStock.getMedicinalStorageControl().getUsedStock().add(supplierStock.getMedicinalStorageControl().getReimburseStock())).stripTrailingZeros().toPlainString();
                    supplierStock.getDrug().setInventory(Integer.parseInt(inventory));
                } else {
                    supplierStock.getDrug().setInventory(0);
                }
                row.put("number", formatDrugNumber(supplierStock.getDrug()));
                // 有效期
                row.put("endDate", sdf.format(supplierStock.getEndDate()));
                // 状态
                row.put("status", Objects.equals("0", supplierStock.getCancellation()) ? "即将过期" : "已过期");
                rows.add(row);
            }
        }
        new ExcelExportBuilder("药品有效期预警导出")
                .addColumns(
                        new ExcelExportBuilder.Column("药品名称", "drugName"),
                        new ExcelExportBuilder.Column("药品分类", "typeName"),
                        new ExcelExportBuilder.Column("规格", "norms"),
                        new ExcelExportBuilder.Column("生产厂商", "factoryName"),
                        new ExcelExportBuilder.Column("供应商", "supplierName"),
                        new ExcelExportBuilder.Column("批号", "batchNo"),
                        new ExcelExportBuilder.Column("剩余数量", "number"),
                        new ExcelExportBuilder.Column("有效期", "endDate"),
                        new ExcelExportBuilder.Column("状态", "status")
                )
                .data(rows)
                .write(response, "药品有效期预警导出.xlsx");
    }

    /**
     * 格式化药品剩余数量，按包装单位换算显示（如"2盒3片"）
     */
    private String formatDrugNumber(Drug drug) {
        int inventory = drug.getInventory();
        int preparation = Integer.parseInt(drug.getPreparation());
        int wholePacks = inventory / preparation;
        int remainder = inventory % preparation;
        if (wholePacks >= 0) {
            return wholePacks + drug.getPack().getName()
                    + (remainder > 0 ? remainder + drug.getPreparationUnit().getName() : "");
        } else {
            return inventory + drug.getPreparationUnit().getName();
        }
    }

    /**
     * 格式化材料剩余数量，按包装单位换算显示（如"2包3个"）
     */
    private String formatStuffNumber(Stuff stuff) {
        int inventory = stuff.getInventory();
        int packNumber = stuff.getPackNumber();
        int wholePacks = inventory / packNumber;
        int remainder = inventory % packNumber;
        if (wholePacks >= 0) {
            return wholePacks + stuff.getPackUnit().getName()
                    + (remainder > 0 ? remainder + stuff.getMinUnit().getName() : "");
        } else {
            return inventory + stuff.getMinUnit().getName();
        }
    }
}
