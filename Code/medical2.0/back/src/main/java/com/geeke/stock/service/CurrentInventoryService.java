package com.geeke.stock.service;

import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.common.data.Parameter;
import com.geeke.stock.entity.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.OutputStream;
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

    @Transactional(readOnly = false)
    public HashMap<String, BigDecimal> getAllDrugs(List<Parameter> params, String orderby) {
        List<Drug> drugs = drugService.listAlls(params, orderby);
        BigDecimal totalPrice = new BigDecimal("0");
        BigDecimal totalSellingPrice = new BigDecimal("0");
        for (Drug drug : drugs) {
            //获取每个药品的进价合计
//            BigDecimal bid = medicinalStorageControlService.getByDrugOrStuffId(drug.getId())==null?new BigDecimal("0"):medicinalStorageControlService.getByDrugOrStuffId(drug.getId());
//            totalPrice = totalPrice.add(bid);

            //进行售价计算
            BigDecimal leastBid = drug.getPrice().divide(new BigDecimal(drug.getPreparation()),4,BigDecimal.ROUND_HALF_UP);
            BigDecimal multiply = leastBid.multiply(drug.getStock().getStorageStock().subtract((drug.getStock().getUsedStock().add(drug.getStock().getReimburseStock()))));
            totalSellingPrice = totalSellingPrice.add(multiply);
        }

        //获取每个药品的进价合计
        BigDecimal byDrugOrStuffIds = medicinalStorageControlService.getByDrugOrStuffIds(drugs);
        totalPrice = totalPrice.add(byDrugOrStuffIds);

        HashMap<String, BigDecimal> totalMap = new HashMap<>();
        totalMap.put("totalPrice",totalPrice);
        totalMap.put("totalSellingPrice",totalSellingPrice);
        return totalMap;
    }

    @Transactional
    public Page<MedicinalStorageControl> getBatchNumberDrug(SearchParams searchParams) {
        Page<MedicinalStorageControl> medicinalStorageControlPage = medicinalStorageControlService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());

       // Page<SupplierStock> supplierStockPage = supplierStockService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return medicinalStorageControlPage;
    }
    @Transactional
    public HashMap<String, BigDecimal> getgetStuffSalesStat(List<Parameter> params, String orderby) {
        List<Stuff> stuffs = stuffService.listAlls(params, orderby);
        BigDecimal totalPrice = new BigDecimal("0");
        BigDecimal totalSellingPrice = new BigDecimal("0");
        for (Stuff stuff : stuffs) {
            //获取材料的总进价
//            BigDecimal byDrugOrStuffId = medicinalStorageControlService.getByDrugOrStuffId(stuff.getId());
//            BigDecimal bid = byDrugOrStuffId==null?new BigDecimal("0"):byDrugOrStuffId;
//            totalPrice = totalPrice.add(bid==null?new BigDecimal("0"):bid);

            //获取材料的总售价
            BigDecimal divide = stuff.getPriceOutSell().divide(new BigDecimal(stuff.getPackNumber() + ""), 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal multiply = divide.multiply(stuff.getStock().getStorageStock().subtract((stuff.getStock().getUsedStock().add(stuff.getStock().getReimburseStock()))));
            totalSellingPrice = totalSellingPrice.add(multiply);
        }

        //获取材料的总进价
        BigDecimal byDrugOrStuffIds = medicinalStorageControlService.getByDrugOrStuffIds(stuffs);
        totalPrice = totalPrice.add(byDrugOrStuffIds);

        HashMap<String, BigDecimal> totalMap = new HashMap<>();
        totalMap.put("totalPrice",totalPrice);
        totalMap.put("totalSellingPrice",totalSellingPrice);
        return totalMap;
    }
    @Transactional
    public Page<MedicinalStorageControl> getBatchNumberStuff(SearchParams searchParams) {
        Page<MedicinalStorageControl> medicinalStorageControlPage = medicinalStorageControlService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        //Page<SupplierStock> supplierStockPage = supplierStockService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return medicinalStorageControlPage;
    }
    @Transactional
    public HashMap<String, BigDecimal> getDrugSalesStatByNumber(SearchParams searchParams) {
        //List<SupplierStock> supplierStockList = supplierStockService.listAll(searchParams.getParams(), searchParams.getOrder());
        List<MedicinalStorageControl> medicinalStorageControls = medicinalStorageControlService.listAll(searchParams.getParams(), searchParams.getOrder());
        BigDecimal totalPrice = new BigDecimal("0");
        BigDecimal totalSellingPrice = new BigDecimal("0");
        if(!CollectionUtils.isEmpty(medicinalStorageControls)){
            for (MedicinalStorageControl medicinalStorageControl : medicinalStorageControls) {

                //获取所有的进价合计
                BigDecimal purchasing = new BigDecimal(medicinalStorageControl.getSupplierStock().getLeastBid() + "").multiply(medicinalStorageControl.getStorageStock().subtract(medicinalStorageControl.getUsedStock().add(medicinalStorageControl.getReimburseStock())));
                totalPrice = totalPrice.add(purchasing);

                //获取所有的售价
//                if(medicinalStorageControl.getDrug().getPrice().toString().equals("0")){
//                    System.out.println(supplierStock.getDrug().getPrice());
//                }

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

    @Transactional
    public HashMap<String, BigDecimal> getStuffSalesStatNumber(SearchParams searchParams) {
       // List<SupplierStock> supplierStockList = supplierStockService.listAll(searchParams.getParams(), searchParams.getOrder());
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
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("材料当前库存-按批号");
        String fileName = "材料当前库存-按批号"+".xls";
        //设置样式
        CellStyle blackStyle = workbook.createCellStyle();
        //自动换行*重要*
        blackStyle.setWrapText(true);
        blackStyle.setAlignment(HorizontalAlignment.CENTER);
        blackStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //新增数据行，并且设置单元格数据
        int rowNum=1;

        // headers表示excel表中第一行的表头 在excel表中添加表头
        HSSFRow row = sheet.createRow(0);

        String[] headers={"材料名称","材料分类", "规格", "生产厂商","供应商","批号", "售价(元)","售价合计(元)","成本价(元)","成本合计(元)","剩余数量"};


        //设置字体样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        HSSFFont font1 = workbook.createFont();
        font1.setColor(IndexedColors.BLACK.getIndex());
        font1.setBold(false);
        font1.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font1);

        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString hssfRichTextString = new HSSFRichTextString(headers[i]);
            HSSFFont font = workbook.createFont();
            font.setColor(IndexedColors.BLACK.getIndex());//设置excel数据字体颜色
            font.setFontHeightInPoints((short) 14);//设置excel数据字体大小
            font.setBold(true);

            hssfRichTextString.applyFont(font);

            cell.setCellValue(hssfRichTextString);
            cell.setCellStyle(blackStyle);
        }

        row.setHeightInPoints(30);//目的是想把行高设置成30px

        //List<SupplierStock> supplierStockList = supplierStockService.listAll(searchParams.getParams(), searchParams.getOrder());
        List<MedicinalStorageControl> medicinalStorageControls = medicinalStorageControlService.listAll(searchParams.getParams(), searchParams.getOrder());
        Map<Integer,Integer> map=new HashMap<>();
        // 初始化标题的列宽,字体
        for (int i= 0; i<11;i++) {
            map.put(i, row.getCell(i).getStringCellValue().getBytes().length * 256 + 200);
            // row.getCell(i).setCellStyle(blackStyle);//设置自动换行
        }

        if(!CollectionUtils.isEmpty(medicinalStorageControls)){
            for ( MedicinalStorageControl medicinalStorageControl:
                    medicinalStorageControls) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.setHeightInPoints(20);

                String inventory = medicinalStorageControl.getStorageStock().subtract(medicinalStorageControl.getUsedStock().add(medicinalStorageControl.getReimburseStock())).stripTrailingZeros().toPlainString();
                medicinalStorageControl.getStuff().setInventory(Integer.parseInt(inventory));

                //材料名称
                row1.createCell(0).setCellValue(medicinalStorageControl.getStuff().getName());
                map.put(0,Math.max(medicinalStorageControl.getStuff().getName().getBytes().length* 256 + 200,map.get(0)));

                //材料类型
                row1.createCell(1).setCellValue(medicinalStorageControl.getStuff().getType().getName());
                map.put(1,Math.max(medicinalStorageControl.getStuff().getType().getName().getBytes().length* 256 + 200,map.get(1)));

                //药品规格
                String specification =medicinalStorageControl.getSupplierStock().getNorms();
                row1.createCell(2).setCellValue(specification);
                map.put(2,Math.max(specification.getBytes().length* 256 + 200,map.get(2)));

                //生产厂商
                row1.createCell(3).setCellValue(medicinalStorageControl.getStuff().getFactory().getName());
                map.put(3,Math.max(medicinalStorageControl.getStuff().getFactory().getName().getBytes().length* 256 + 200,map.get(3)));

                //供应商
                row1.createCell(4).setCellValue(medicinalStorageControl.getSupplierStock().getSupplierId().getName());
                map.put(4,Math.max(medicinalStorageControl.getSupplierStock().getSupplierId().getName().getBytes().length* 256 + 200,map.get(4)));

                //批号
                row1.createCell(5).setCellValue(medicinalStorageControl.getSupplierStock().getBatchNo());
                map.put(5,Math.max(medicinalStorageControl.getSupplierStock().getBatchNo().getBytes().length* 256 + 200,map.get(5)));

                //售价
                row1.createCell(6).setCellValue(medicinalStorageControl.getStuff().getPriceOutSell().doubleValue());
                map.put(6,Math.max((medicinalStorageControl.getStuff().getPriceOutSell()+"").getBytes().length* 256 + 200,map.get(6)));

                //售价合计
                BigDecimal selling = new BigDecimal(medicinalStorageControl.getStuff().getPriceOutSell() + "").divide(new BigDecimal(medicinalStorageControl.getStuff().getPackNumber()),4, BigDecimal.ROUND_HALF_UP);
                BigDecimal sellingTotal = new BigDecimal(medicinalStorageControl.getStuff().getInventory()).multiply(selling);
                row1.createCell(7).setCellValue(sellingTotal.doubleValue());
                map.put(7,Math.max(sellingTotal.toString().getBytes().length* 256 + 200,map.get(7)));

                //进价
                row1.createCell(8).setCellValue(medicinalStorageControl.getSupplierStock().getBid().doubleValue());
                map.put(7,Math.max(medicinalStorageControl.getSupplierStock().getBid().toString().getBytes().length* 256 + 200,map.get(7)));

                //进价合计
                BigDecimal allBId = medicinalStorageControl.getSupplierStock().getLeastBid().multiply(new BigDecimal(medicinalStorageControl.getStuff().getInventory()));
                row1.createCell(9).setCellValue(allBId.doubleValue());
                map.put(9,Math.max(allBId.toString().getBytes().length* 256 + 200,map.get(9)));

                //剩余数量
                String number =  Math.floor(medicinalStorageControl.getStuff().getInventory()/medicinalStorageControl.getStuff().getPackNumber()) >= 0 ? (int)Math.floor(medicinalStorageControl.getStuff().getInventory()/medicinalStorageControl.getStuff().getPackNumber())+(medicinalStorageControl.getStuff().getPackUnit().getName())+((medicinalStorageControl.getStuff().getInventory()%medicinalStorageControl.getStuff().getPackNumber() > 0) ? (medicinalStorageControl.getStuff().getInventory()%medicinalStorageControl.getStuff().getPackNumber()) + medicinalStorageControl.getStuff().getMinUnit().getName():""):medicinalStorageControl.getStuff().getInventory()+medicinalStorageControl.getStuff().getMinUnit().getName();
                row1.createCell(10).setCellValue(number);
                map.put(10,Math.max(number.getBytes().length* 256 + 200,map.get(10)));


                rowNum++;

                for (int i = 1; i < 11; i++) {
                    row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                }
            }
        }

        for (int i= 0; i<11;i++){
            //设置列宽
            if(i==5){
                sheet.setColumnWidth(i,map.get(1)+1000);
            }else {
                sheet.setColumnWidth(i,map.get(i));
            }

        }

        response.setContentType("application/octet-stream");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        OutputStream outputStream = response.getOutputStream();
        workbook.write(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }

    //按商品导出材料
    private void exportStuff(SearchParams searchParams, HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("材料当前库存-按商品");
        String fileName = "材料当前库存-按商品"+".xls";
        //设置样式
        CellStyle blackStyle = workbook.createCellStyle();
        //自动换行*重要*
        blackStyle.setWrapText(true);
        blackStyle.setAlignment(HorizontalAlignment.CENTER);
        blackStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //新增数据行，并且设置单元格数据
        int rowNum=1;

        // headers表示excel表中第一行的表头 在excel表中添加表头
        HSSFRow row = sheet.createRow(0);

        String[] headers={"材料名称","材料分类", "规格", "生产厂商", "售价(元)","售价合计(元)","成本合计(元)","剩余数量"};


        //设置字体样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        HSSFFont font1 = workbook.createFont();
        font1.setColor(IndexedColors.BLACK.getIndex());
        font1.setBold(false);
        font1.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font1);

        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString hssfRichTextString = new HSSFRichTextString(headers[i]);
            HSSFFont font = workbook.createFont();
            font.setColor(IndexedColors.BLACK.getIndex());//设置excel数据字体颜色
            font.setFontHeightInPoints((short) 14);//设置excel数据字体大小
            font.setBold(true);

            hssfRichTextString.applyFont(font);

            cell.setCellValue(hssfRichTextString);
            cell.setCellStyle(blackStyle);
        }

        row.setHeightInPoints(30);//目的是想把行高设置成30px

        List<Stuff> stuffs = stuffService.listAlls(searchParams.getParams(), searchParams.getOrder());

        Map<Integer,Integer> map=new HashMap<>();
        // 初始化标题的列宽,字体
        for (int i= 0; i<8;i++) {
            map.put(i, row.getCell(i).getStringCellValue().getBytes().length * 256 + 200);
            // row.getCell(i).setCellStyle(blackStyle);//设置自动换行
        }

        if(!CollectionUtils.isEmpty(stuffs)){
            for ( Stuff stuff:
                    stuffs) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.setHeightInPoints(20);

                if(!Objects.isNull(stuff.getStock())&&!Objects.isNull(stuff.getStock().getStorageStock())){
                    String inventory = stuff.getStock().getStorageStock().subtract((stuff.getStock().getUsedStock().add(stuff.getStock().getReimburseStock()))).stripTrailingZeros().toPlainString();
                    stuff.setInventory(Integer.parseInt(inventory));
                }else {
                    stuff.setInventory(0);
                }

                //材料名称
                row1.createCell(0).setCellValue(stuff.getName());
                map.put(0,Math.max(stuff.getName().getBytes().length* 256 + 200,map.get(0)));

                //材料类型
                row1.createCell(1).setCellValue(stuff.getType().getName());
                map.put(1,Math.max(stuff.getType().getName().getBytes().length* 256 + 200,map.get(1)));

                //材料规格
                String specification =stuff.getPackNumber()+stuff.getMinUnit().getName()+"/"+stuff.getPackUnit().getName();
                row1.createCell(2).setCellValue(specification);
                map.put(2,Math.max(specification.getBytes().length* 256 + 200,map.get(2)));

                //生产厂商
                row1.createCell(3).setCellValue(stuff.getFactory().getName());
                map.put(3,Math.max(stuff.getFactory().getName().getBytes().length* 256 + 200,map.get(3)));

                //售价
                row1.createCell(4).setCellValue(stuff.getPriceOutSell().doubleValue());
                map.put(4,Math.max((stuff.getPriceOutSell()+"").getBytes().length* 256 + 200,map.get(4)));

                //售价合计
                BigDecimal selling = new BigDecimal(stuff.getPriceOutSell() + "").divide(new BigDecimal(stuff.getPackNumber()),4, BigDecimal.ROUND_HALF_UP);
                BigDecimal sellingTotal = new BigDecimal(stuff.getInventory()).multiply(selling);
                row1.createCell(5).setCellValue(sellingTotal.doubleValue());
                map.put(5,Math.max(sellingTotal.toString().getBytes().length* 256 + 200,map.get(5)));

                //进价合计
                //BigDecimal bid = supplierStockService.getByStuffId(stuff.getId());
                BigDecimal totalCost = medicinalStorageControlService.getByDrugOrStuffId(stuff.getId());
                stuff.setBid(totalCost);
                row1.createCell(6).setCellValue((stuff.getBid()==null?new BigDecimal(0).doubleValue():stuff.getBid().doubleValue()));
                map.put(6,Math.max((stuff.getBid()==null?0:stuff.getBid()).toString().getBytes().length* 256 + 200,map.get(6)));

                //剩余数量
                String number =  Math.floor(stuff.getInventory()/stuff.getPackNumber()) >= 0 ? (int)Math.floor(stuff.getInventory()/stuff.getPackNumber())+(stuff.getPackUnit().getName())+((stuff.getInventory()%stuff.getPackNumber() > 0) ? (stuff.getInventory()%stuff.getPackNumber()) + stuff.getMinUnit().getName():""):stuff.getInventory()+stuff.getMinUnit().getName();
                row1.createCell(7).setCellValue(number);
                map.put(7,Math.max(number.getBytes().length* 256 + 200,map.get(7)));


                rowNum++;

                for (int i = 1; i < 7; i++) {
                    row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                }
            }
        }

        for (int i= 0; i<8;i++){
            //设置列宽
            sheet.setColumnWidth(i,map.get(i));
        }

        response.setContentType("application/octet-stream");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        OutputStream outputStream = response.getOutputStream();
        workbook.write(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }

    //按批号导出药品
    private void exportDrugByBatchNo(SearchParams searchParams, HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("药品当前库存-按批号");
        String fileName = "药品当前库存-按批号"+".xls";
        //设置样式
        CellStyle blackStyle = workbook.createCellStyle();
        //自动换行*重要*
        blackStyle.setWrapText(true);
        blackStyle.setAlignment(HorizontalAlignment.CENTER);
        blackStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //新增数据行，并且设置单元格数据
        int rowNum=1;

        // headers表示excel表中第一行的表头 在excel表中添加表头
        HSSFRow row = sheet.createRow(0);

        String[] headers={"药品名称","药品分类", "规格", "生产厂商","供应商","批号", "售价(元)","售价合计(元)","成本价(元)","成本合计(元)","剩余数量"};


        //设置字体样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        HSSFFont font1 = workbook.createFont();
        font1.setColor(IndexedColors.BLACK.getIndex());
        font1.setBold(false);
        font1.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font1);

        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString hssfRichTextString = new HSSFRichTextString(headers[i]);
            HSSFFont font = workbook.createFont();
            font.setColor(IndexedColors.BLACK.getIndex());//设置excel数据字体颜色
            font.setFontHeightInPoints((short) 14);//设置excel数据字体大小
            font.setBold(true);

            hssfRichTextString.applyFont(font);

            cell.setCellValue(hssfRichTextString);
            cell.setCellStyle(blackStyle);
        }

        row.setHeightInPoints(30);//目的是想把行高设置成30px

        //List<SupplierStock> supplierStockList = supplierStockService.listAll(searchParams.getParams(), searchParams.getOrder());
        List<MedicinalStorageControl> medicinalStorageControls = medicinalStorageControlService.listAll(searchParams.getParams(), searchParams.getOrder());
        Map<Integer,Integer> map=new HashMap<>();
        // 初始化标题的列宽,字体
        for (int i= 0; i<11;i++) {
            map.put(i, row.getCell(i).getStringCellValue().getBytes().length * 256 + 200);
            // row.getCell(i).setCellStyle(blackStyle);//设置自动换行
        }

        if(!CollectionUtils.isEmpty(medicinalStorageControls)){
            for ( MedicinalStorageControl medicinalStorageControl:
                    medicinalStorageControls) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.setHeightInPoints(20);

                //药品名称
                row1.createCell(0).setCellValue(medicinalStorageControl.getDrug().getGoodsName());
                map.put(0,Math.max(medicinalStorageControl.getDrug().getGoodsName().getBytes().length* 256 + 200,map.get(0)));

                //药品类型
                row1.createCell(1).setCellValue(medicinalStorageControl.getDrug().getType().getName());
                map.put(1,Math.max(medicinalStorageControl.getDrug().getType().getName().getBytes().length* 256 + 200,map.get(1)));

                //药品规格
                String specification =medicinalStorageControl.getSupplierStock().getNorms();
                row1.createCell(2).setCellValue(specification);
                map.put(2,Math.max(specification.getBytes().length* 256 + 200,map.get(2)));

                //生产厂商
                row1.createCell(3).setCellValue(medicinalStorageControl.getDrug().getFactory().getName());
                map.put(3,Math.max(medicinalStorageControl.getDrug().getFactory().getName().getBytes().length* 256 + 200,map.get(3)));

                //供应商
                row1.createCell(4).setCellValue(medicinalStorageControl.getSupplierStock().getSupplierId().getName());
                map.put(4,Math.max(medicinalStorageControl.getSupplierStock().getSupplierId().getName().getBytes().length* 256 + 200,map.get(4)));

                //批号
                row1.createCell(5).setCellValue(medicinalStorageControl.getSupplierStock().getBatchNo());
                map.put(5,Math.max(medicinalStorageControl.getSupplierStock().getBatchNo().getBytes().length* 256 + 200,map.get(5)));

                //售价
                row1.createCell(6).setCellValue(medicinalStorageControl.getDrug().getPrice().doubleValue());
                map.put(6,Math.max((medicinalStorageControl.getDrug().getPrice()+"").getBytes().length* 256 + 200,map.get(6)));

                //售价合计
                BigDecimal selling = new BigDecimal(medicinalStorageControl.getDrug().getPrice() + "").divide(new BigDecimal(medicinalStorageControl.getDrug().getPreparation()),4, BigDecimal.ROUND_HALF_UP);
                BigDecimal sellingTotal = (medicinalStorageControl.getStorageStock().subtract(medicinalStorageControl.getUsedStock().add(medicinalStorageControl.getReimburseStock()))).multiply(selling);
                row1.createCell(7).setCellValue(sellingTotal.doubleValue());
                map.put(7,Math.max(sellingTotal.toString().getBytes().length* 256 + 200,map.get(7)));

                //进价
                row1.createCell(8).setCellValue(medicinalStorageControl.getSupplierStock().getBid().doubleValue());
                map.put(7,Math.max(medicinalStorageControl.getSupplierStock().getBid().toString().getBytes().length* 256 + 200,map.get(7)));

                //进价合计
                BigDecimal allBId = medicinalStorageControl.getSupplierStock().getLeastBid().multiply(medicinalStorageControl.getStorageStock().subtract(medicinalStorageControl.getUsedStock().add(medicinalStorageControl.getReimburseStock())));
                row1.createCell(9).setCellValue(allBId.doubleValue());
                map.put(9,Math.max(allBId.toString().getBytes().length* 256 + 200,map.get(9)));

                //剩余数量
                String inventory = medicinalStorageControl.getStorageStock().subtract((medicinalStorageControl.getUsedStock().add(medicinalStorageControl.getReimburseStock()))).stripTrailingZeros().toPlainString();
                medicinalStorageControl.getDrug().setInventory(Integer.parseInt(inventory));
                String number =  Math.floor(medicinalStorageControl.getDrug().getInventory()/Integer.parseInt(medicinalStorageControl.getDrug().getPreparation())) >= 0 ? (int)Math.floor(medicinalStorageControl.getDrug().getInventory()/Integer.parseInt(medicinalStorageControl.getDrug().getPreparation()))+(medicinalStorageControl.getDrug().getPack().getName())+((medicinalStorageControl.getDrug().getInventory()%Integer.parseInt(medicinalStorageControl.getDrug().getPreparation()) > 0) ? (medicinalStorageControl.getDrug().getInventory()%Integer.parseInt(medicinalStorageControl.getDrug().getPreparation())) + medicinalStorageControl.getDrug().getPreparationUnit().getName():""):medicinalStorageControl.getDrug().getInventory()+medicinalStorageControl.getDrug().getPreparationUnit().getName();
                row1.createCell(10).setCellValue(number);
                map.put(10,Math.max(number.getBytes().length* 256 + 200,map.get(10)));


                rowNum++;

                for (int i = 1; i < 11; i++) {
                    row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                }
            }
        }

        for (int i= 0; i<11;i++){
            //设置列宽
            if(i==5){
                sheet.setColumnWidth(i,map.get(1)+1000);
            }else {
                sheet.setColumnWidth(i,map.get(i));
            }

        }

        response.setContentType("application/octet-stream");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        OutputStream outputStream = response.getOutputStream();
        workbook.write(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }

    //按商品导出药品
    @Transactional
    public void exportDrug(SearchParams searchParams, HttpServletResponse response) throws IOException {

        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("药品当前库存-按商品");
        String fileName = "药品当前库存-按商品"+".xls";
        //设置样式
        CellStyle blackStyle = workbook.createCellStyle();
        //自动换行*重要*
        blackStyle.setWrapText(true);
        blackStyle.setAlignment(HorizontalAlignment.CENTER);
        blackStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //新增数据行，并且设置单元格数据
        int rowNum=1;

        // headers表示excel表中第一行的表头 在excel表中添加表头
        HSSFRow row = sheet.createRow(0);

        String[] headers={"药品名称","药品分类", "规格", "生产厂商", "售价(元)","售价合计(元)","成本合计(元)","剩余数量"};


        //设置字体样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        HSSFFont font1 = workbook.createFont();
        font1.setColor(IndexedColors.BLACK.getIndex());
        font1.setBold(false);
        font1.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font1);

        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString hssfRichTextString = new HSSFRichTextString(headers[i]);
            HSSFFont font = workbook.createFont();
            font.setColor(IndexedColors.BLACK.getIndex());//设置excel数据字体颜色
            font.setFontHeightInPoints((short) 14);//设置excel数据字体大小
            font.setBold(true);

            hssfRichTextString.applyFont(font);

            cell.setCellValue(hssfRichTextString);
            cell.setCellStyle(blackStyle);
        }

        row.setHeightInPoints(30);//目的是想把行高设置成30px

        List<Drug> drugs = drugService.listAlls(searchParams.getParams(), searchParams.getOrder());

        Map<Integer,Integer> map=new HashMap<>();
        // 初始化标题的列宽,字体
        for (int i= 0; i<8;i++) {
            map.put(i, row.getCell(i).getStringCellValue().getBytes().length * 256 + 200);
            // row.getCell(i).setCellStyle(blackStyle);//设置自动换行
        }

        if(!CollectionUtils.isEmpty(drugs)){
            for ( Drug drug:
                    drugs) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.setHeightInPoints(20);

                //药品名称
                row1.createCell(0).setCellValue(drug.getGoodsName());
                map.put(0,Math.max(drug.getGoodsName().getBytes().length* 256 + 200,map.get(0)));

                //药品类型
                row1.createCell(1).setCellValue(drug.getType().getName());
                map.put(1,Math.max(drug.getType().getName().getBytes().length* 256 + 200,map.get(1)));

                //药品规格
                String specification =drug.getDosis()+drug.getDosisUnit().getName()+"*"+drug.getPreparation()+drug.getPreparationUnit().getName()+"/"+drug.getPack().getName();
                row1.createCell(2).setCellValue(specification);
                map.put(2,Math.max(specification.getBytes().length* 256 + 200,map.get(2)));

                //生产厂商
                row1.createCell(3).setCellValue(drug.getFactory().getName());
                map.put(3,Math.max(drug.getFactory().getName().getBytes().length* 256 + 200,map.get(3)));

                //售价
                row1.createCell(4).setCellValue(drug.getPrice().doubleValue());
                map.put(4,Math.max((drug.getPrice()+"").getBytes().length* 256 + 200,map.get(4)));

                //售价合计
                BigDecimal selling = new BigDecimal(drug.getPrice() + "").divide(new BigDecimal(drug.getPreparation()),4, BigDecimal.ROUND_HALF_UP);
                BigDecimal sellingTotal = (drug.getStock().getStorageStock().subtract(drug.getStock().getUsedStock().add(drug.getStock().getReimburseStock()))).multiply(selling);
                row1.createCell(5).setCellValue(sellingTotal.doubleValue());
                map.put(5,Math.max(sellingTotal.toString().getBytes().length* 256 + 200,map.get(5)));

                //进价合计
               // BigDecimal bid = supplierStockService.getByDrugId(drug.getId());
                BigDecimal totalCost = medicinalStorageControlService.getByDrugOrStuffId(drug.getId());
                drug.setBid(totalCost);
                row1.createCell(6).setCellValue((drug.getBid()==null?new BigDecimal(0).doubleValue():drug.getBid().doubleValue()));
                map.put(6,Math.max((drug.getBid()==null?0:drug.getBid()).toString().getBytes().length* 256 + 200,map.get(6)));

                //剩余数量
               if(!Objects.isNull(drug.getStock())&&!Objects.isNull(drug.getStock().getStorageStock())){
                   String inventory = drug.getStock().getStorageStock().subtract((drug.getStock().getUsedStock().add(drug.getStock().getReimburseStock()))).stripTrailingZeros().toPlainString();
                   drug.setInventory(Integer.parseInt(inventory));
               }else {
                   drug.setInventory(0);
               }
                String number =  Math.floor(drug.getInventory()/Integer.parseInt(drug.getPreparation())) >= 0 ? (int)Math.floor(drug.getInventory()/Integer.parseInt(drug.getPreparation()))+(drug.getPack().getName())+((drug.getInventory()%Integer.parseInt(drug.getPreparation()) > 0) ? (drug.getInventory()%Integer.parseInt(drug.getPreparation())) + drug.getPreparationUnit().getName():""):drug.getInventory()+drug.getPreparationUnit().getName();
                row1.createCell(7).setCellValue(number);
                map.put(7,Math.max(number.getBytes().length* 256 + 200,map.get(7)));


                rowNum++;

                for (int i = 1; i < 7; i++) {
                    row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                }
            }
        }

        for (int i= 0; i<8;i++){
            //设置列宽
            sheet.setColumnWidth(i,map.get(i));
        }

        response.setContentType("application/octet-stream");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        OutputStream outputStream = response.getOutputStream();
        workbook.write(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }


}
