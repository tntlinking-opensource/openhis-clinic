package com.geeke.stock.service;

import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.Stuff;
import com.geeke.stock.entity.SupplierStock;
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
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
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


    @Transactional
    public Page<SupplierStock> getDrugIndateWarning(List<Parameter> parameters, int offset, int limit, String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        Page<SupplierStock> indateWarning = supplierStockService.getDrugIndateWarning(parameters, offset, limit, orderby);
        return indateWarning;


    }

    @Transactional
    public Page<Drug> getDrugInventoryWarning(SearchParams searchParams) {
        Page<Drug> drugInventoryWarning = drugService.getDrugInventoryWarning(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());

        return drugInventoryWarning;
    }

    @Transactional
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
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("材料库存预警导出");
        String fileName = "材料库存预警导出"+".xls";
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

        String[] headers={"材料名称","材料分类", "规格", "生产厂商","剩余数量"};

        //设置第6列的字体颜色
        CellStyle cellStyle101 = workbook.createCellStyle();
        cellStyle101.setWrapText(true);
        HSSFFont font101 = workbook.createFont();
        font101.setColor(IndexedColors.LIGHT_ORANGE.getIndex());
        font101.setBold(false);
        font101.setFontHeightInPoints((short) 11);
        cellStyle101.setFont(font101);


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

        Page<Stuff> stuffInventoryWarning = stuffService.getStuffInventoryWarning(searchParams.getParams(), 0, 1000000, searchParams.getOrder());

        Map<Integer,Integer> map=new HashMap<>();
        // 初始化标题的列宽,字体
        for (int i= 0; i<5;i++) {
            map.put(i, row.getCell(i).getStringCellValue().getBytes().length * 256 + 200);
            // row.getCell(i).setCellStyle(blackStyle);//设置自动换行
        }

        if(!CollectionUtils.isEmpty(stuffInventoryWarning.getRows())){
            for ( Stuff stuff:
                    stuffInventoryWarning.getRows()) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.setHeightInPoints(20);

                //药品名称
                row1.createCell(0).setCellValue(stuff.getName());
                map.put(0,Math.max(stuff.getName().getBytes().length* 256 + 200,map.get(0)));

                //药品类型
                row1.createCell(1).setCellValue(stuff.getType().getName());
                map.put(1,Math.max(stuff.getType().getName().getBytes().length* 256 + 200,map.get(1)));

                //药品规格
                String specification =stuff.getPackNumber()+stuff.getMinUnit().getName()+"/"+stuff.getPackUnit().getName();
                row1.createCell(2).setCellValue(specification);
                map.put(2,Math.max(specification.getBytes().length* 256 + 200,map.get(2)));

                //生产厂商
                row1.createCell(3).setCellValue(stuff.getFactory().getName());
                map.put(3,Math.max(stuff.getFactory().getName().getBytes().length* 256 + 200,map.get(3)));


                //剩余数量
                String inventory = stuff.getStock().getStorageStock().subtract(stuff.getStock().getUsedStock().add(stuff.getStock().getReimburseStock())).stripTrailingZeros().toPlainString();
                stuff.setInventory(Integer.parseInt(inventory));
                String number =  Math.floor(stuff.getInventory()/stuff.getPackNumber()) >= 0 ? (int)Math.floor(stuff.getInventory()/stuff.getPackNumber())+(stuff.getPackUnit().getName())+((stuff.getInventory()%stuff.getPackNumber() > 0) ? (stuff.getInventory()%stuff.getPackNumber()) + stuff.getMinUnit().getName():""):stuff.getInventory()+stuff.getMinUnit().getName();
                row1.createCell(4).setCellValue(number);
                map.put(4,Math.max(number.getBytes().length* 256 + 200,map.get(4)));



                rowNum++;

                for (int i = 1; i < 4; i++) {
                    row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                }
                //给第4列单独设置颜色
                row1.getCell(4).setCellStyle(cellStyle101);
            }
        }

        for (int i= 0; i<5;i++){
            //设置列宽
            sheet.setColumnWidth(i,map.get(i));
        }

        response.setContentType("application/octet-stream");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
        response.flushBuffer();
        OutputStream outputStream = response.getOutputStream();
        workbook.write(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }

    //材料有效期预警导出
    private void exportStuffIndate(SearchParams searchParams, HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("材料有效期预警导出");
        String fileName = "材料有效期预警导出"+".xls";
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

        String[] headers={"材料名称","材料分类", "规格", "生产厂商","供应商","批号", "剩余数量","有效期","状态"};

        //设置第6列的字体颜色
        CellStyle cellStyle101 = workbook.createCellStyle();
        cellStyle101.setWrapText(true);
        HSSFFont font101 = workbook.createFont();
        font101.setColor(IndexedColors.LIGHT_ORANGE.getIndex());
        font101.setBold(false);
        font101.setFontHeightInPoints((short) 11);
        cellStyle101.setFont(font101);

        CellStyle cellStyle102 = workbook.createCellStyle();
        cellStyle102.setWrapText(true);
        HSSFFont font102 = workbook.createFont();
        font102.setColor(IndexedColors.RED.getIndex());
        font102.setBold(false);
        font102.setFontHeightInPoints((short) 11);
        cellStyle102.setFont(font102);


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

        Page<SupplierStock> stuffIndateWarning = supplierStockService.getStuffIndateWarning(searchParams.getParams(), 0, 1000000, searchParams.getOrder());

        Map<Integer,Integer> map=new HashMap<>();
        // 初始化标题的列宽,字体
        for (int i= 0; i<9;i++) {
            map.put(i, row.getCell(i).getStringCellValue().getBytes().length * 256 + 200);
            // row.getCell(i).setCellStyle(blackStyle);//设置自动换行
        }

        if(!CollectionUtils.isEmpty(stuffIndateWarning.getRows())){
            for ( SupplierStock supplierStock:
                    stuffIndateWarning.getRows()) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.setHeightInPoints(20);

                //药品名称
                row1.createCell(0).setCellValue(supplierStock.getStuff().getName());
                map.put(0,Math.max(supplierStock.getStuff().getName().getBytes().length* 256 + 200,map.get(0)));

                //药品类型
                row1.createCell(1).setCellValue(supplierStock.getStuff().getType().getName());
                map.put(1,Math.max(supplierStock.getStuff().getType().getName().getBytes().length* 256 + 200,map.get(1)));

                //药品规格
                String specification =supplierStock.getNorms();
                row1.createCell(2).setCellValue(specification);
                map.put(2,Math.max(specification.getBytes().length* 256 + 200,map.get(2)));

                //生产厂商
                row1.createCell(3).setCellValue(supplierStock.getFactory().getName());
                map.put(3,Math.max(supplierStock.getFactory().getName().getBytes().length* 256 + 200,map.get(3)));

                //供应商
                row1.createCell(4).setCellValue(supplierStock.getSupplierId().getName());
                map.put(4,Math.max(supplierStock.getSupplierId().getName().getBytes().length* 256 + 200,map.get(4)));

                //批号
                row1.createCell(5).setCellValue(supplierStock.getBatchNo());
                map.put(5,Math.max(supplierStock.getBatchNo().getBytes().length* 256 + 1200,map.get(5)));

                //剩余数量
                if(!Objects.isNull(supplierStock.getMedicinalStorageControl()) && !Objects.isNull(supplierStock.getMedicinalStorageControl().getStorageStock())){
                    String inventory = supplierStock.getMedicinalStorageControl().getStorageStock().subtract(supplierStock.getMedicinalStorageControl().getUsedStock().add(supplierStock.getMedicinalStorageControl().getReimburseStock())).stripTrailingZeros().toPlainString();
                    supplierStock.getStuff().setInventory(Integer.parseInt(inventory));
                }else {
                    supplierStock.getStuff().setInventory(0);
                }
                String number =  Math.floor(supplierStock.getStuff().getInventory()/supplierStock.getStuff().getPackNumber()) >= 0 ? (int)Math.floor(supplierStock.getStuff().getInventory()/supplierStock.getStuff().getPackNumber())+(supplierStock.getStuff().getPackUnit().getName())+((supplierStock.getStuff().getInventory()%supplierStock.getStuff().getPackNumber() > 0) ? (supplierStock.getStuff().getInventory()%supplierStock.getStuff().getPackNumber()) + supplierStock.getStuff().getMinUnit().getName():""):supplierStock.getStuff().getInventory()+supplierStock.getStuff().getMinUnit().getName();
                row1.createCell(6).setCellValue(number);
                map.put(6,Math.max(number.getBytes().length* 256 + 200,map.get(6)));

                //有效期
                Date endDate = supplierStock.getEndDate();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String format = simpleDateFormat.format(endDate);
                row1.createCell(7).setCellValue(format);
                map.put(7,Math.max(format.getBytes().length * 256 + 1000,map.get(7)));

                //状态
                if(Objects.equals("0",supplierStock.getCancellation())){
                    row1.createCell(8).setCellValue("即将过期");
                    map.put(8,Math.max("即将过期".getBytes().length*256+200,map.get(8)));

                }else {
                    row1.createCell(8).setCellValue("已过期");
                    map.put(8,Math.max("已过期".getBytes().length*256+200,map.get(8)));

                }


                rowNum++;

                for (int i = 1; i < 8; i++) {
                    row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                }

                if(Objects.equals("0",supplierStock.getCancellation())){
                    row1.getCell(8).setCellStyle(cellStyle101);
                }else {
                    row1.getCell(8).setCellStyle(cellStyle102);
                }
            }
        }

        for (int i= 0; i<9;i++){
            //设置列宽
            sheet.setColumnWidth(i,map.get(i));
        }

        response.setContentType("application/octet-stream");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
        response.flushBuffer();
        OutputStream outputStream = response.getOutputStream();
        workbook.write(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }

    //药品库存预警导出
    private void exportDrugInventory(SearchParams searchParams, HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("药品库存预警导出");
        String fileName = "药品库存预警导出"+".xls";
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

        String[] headers={"药品名称","药品分类", "规格", "生产厂商","剩余数量"};

        //设置第6列的字体颜色
        CellStyle cellStyle101 = workbook.createCellStyle();
        cellStyle101.setWrapText(true);
        HSSFFont font101 = workbook.createFont();
        font101.setColor(IndexedColors.LIGHT_ORANGE.getIndex());
        font101.setBold(false);
        font101.setFontHeightInPoints((short) 11);
        cellStyle101.setFont(font101);


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

        Page<Drug> drugInventoryWarning = drugService.getDrugInventoryWarning(searchParams.getParams(), 0, 1000000, searchParams.getOrder());

        Map<Integer,Integer> map=new HashMap<>();
        // 初始化标题的列宽,字体
        for (int i= 0; i<5;i++) {
            map.put(i, row.getCell(i).getStringCellValue().getBytes().length * 256 + 200);
            // row.getCell(i).setCellStyle(blackStyle);//设置自动换行
        }

        if(!CollectionUtils.isEmpty(drugInventoryWarning.getRows())){
            for ( Drug drug:
                    drugInventoryWarning.getRows()) {
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


                //剩余数量
                String inventoy = drug.getStock().getStorageStock().subtract(drug.getStock().getUsedStock().add(drug.getStock().getReimburseStock())).stripTrailingZeros().toPlainString();
                drug.setInventory(Integer.parseInt(inventoy));
                String number =  Math.floor(drug.getInventory()/Integer.parseInt(drug.getPreparation())) >= 0 ? (int)Math.floor(drug.getInventory()/Integer.parseInt(drug.getPreparation()))+(drug.getPack().getName())+((drug.getInventory()%Integer.parseInt(drug.getPreparation()) > 0) ? (drug.getInventory()%Integer.parseInt(drug.getPreparation())) + drug.getPreparationUnit().getName():""):drug.getInventory()+drug.getPreparationUnit().getName();
                row1.createCell(4).setCellValue(number);
                map.put(4,Math.max(number.getBytes().length* 256 + 200,map.get(4)));



                rowNum++;

                for (int i = 1; i < 4; i++) {
                    row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                }
                //给第4列单独设置颜色
                row1.getCell(4).setCellStyle(cellStyle101);
            }
        }

        for (int i= 0; i<5;i++){
            //设置列宽
            sheet.setColumnWidth(i,map.get(i));
        }

        response.setContentType("application/octet-stream");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
        response.flushBuffer();
        OutputStream outputStream = response.getOutputStream();
        workbook.write(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }

    //药品有效期预警导出
    private void exportDrugIndate(SearchParams searchParams, HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("药品有效期预警导出");
        String fileName = "药品有效期预警导出"+".xls";
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

        String[] headers={"药品名称","药品分类", "规格", "生产厂商","供应商","批号", "剩余数量","有效期","状态"};

        //设置第6列的字体颜色
        CellStyle cellStyle101 = workbook.createCellStyle();
        cellStyle101.setWrapText(true);
        HSSFFont font101 = workbook.createFont();
        font101.setColor(IndexedColors.LIGHT_ORANGE.getIndex());
        font101.setBold(false);
        font101.setFontHeightInPoints((short) 11);
        cellStyle101.setFont(font101);

        CellStyle cellStyle102 = workbook.createCellStyle();
        cellStyle102.setWrapText(true);
        HSSFFont font102 = workbook.createFont();
        font102.setColor(IndexedColors.RED.getIndex());
        font102.setBold(false);
        font102.setFontHeightInPoints((short) 11);
        cellStyle102.setFont(font102);


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

        Page<SupplierStock> drugIndateWarning = supplierStockService.getDrugIndateWarning(searchParams.getParams(), 0, 1000000, searchParams.getOrder());

        Map<Integer,Integer> map=new HashMap<>();
        // 初始化标题的列宽,字体
        for (int i= 0; i<9;i++) {
            map.put(i, row.getCell(i).getStringCellValue().getBytes().length * 256 + 200);
            // row.getCell(i).setCellStyle(blackStyle);//设置自动换行
        }

        if(!CollectionUtils.isEmpty(drugIndateWarning.getRows())){
            for ( SupplierStock supplierStock:
                    drugIndateWarning.getRows()) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.setHeightInPoints(20);

                //药品名称
                row1.createCell(0).setCellValue(supplierStock.getDrug().getGoodsName());
                map.put(0,Math.max(supplierStock.getDrug().getGoodsName().getBytes().length* 256 + 200,map.get(0)));

                //药品类型
                row1.createCell(1).setCellValue(supplierStock.getDrug().getType().getName());
                map.put(1,Math.max(supplierStock.getDrug().getType().getName().getBytes().length* 256 + 200,map.get(1)));

                //药品规格
                String specification =supplierStock.getNorms();
                row1.createCell(2).setCellValue(specification);
                map.put(2,Math.max(specification.getBytes().length* 256 + 200,map.get(2)));

                //生产厂商
                row1.createCell(3).setCellValue(supplierStock.getFactory().getName());
                map.put(3,Math.max(supplierStock.getFactory().getName().getBytes().length* 256 + 200,map.get(3)));

                //供应商
                row1.createCell(4).setCellValue(supplierStock.getSupplierId().getName());
                map.put(4,Math.max(supplierStock.getSupplierId().getName().getBytes().length* 256 + 200,map.get(4)));

                //批号
                row1.createCell(5).setCellValue(supplierStock.getBatchNo());
                map.put(5,Math.max(supplierStock.getBatchNo().getBytes().length* 256 + 1200,map.get(5)));

                //剩余数量
                if(!Objects.isNull(supplierStock.getMedicinalStorageControl()) && !Objects.isNull(supplierStock.getMedicinalStorageControl().getStorageStock())){
                    String inventory = supplierStock.getMedicinalStorageControl().getStorageStock().subtract(supplierStock.getMedicinalStorageControl().getUsedStock().add(supplierStock.getMedicinalStorageControl().getReimburseStock())).stripTrailingZeros().toPlainString();
                    supplierStock.getDrug().setInventory(Integer.parseInt(inventory));
                }else {
                    supplierStock.getDrug().setInventory(0);
                }

                String number =  Math.floor(supplierStock.getDrug().getInventory()/Integer.parseInt(supplierStock.getDrug().getPreparation())) >= 0 ? (int)Math.floor(supplierStock.getDrug().getInventory()/Integer.parseInt(supplierStock.getDrug().getPreparation()))+(supplierStock.getDrug().getPack().getName())+((supplierStock.getDrug().getInventory()%Integer.parseInt(supplierStock.getDrug().getPreparation()) > 0) ? (supplierStock.getDrug().getInventory()%Integer.parseInt(supplierStock.getDrug().getPreparation())) + supplierStock.getDrug().getPreparationUnit().getName():""):supplierStock.getDrug().getInventory()+supplierStock.getDrug().getPreparationUnit().getName();
                row1.createCell(6).setCellValue(number);
                map.put(6,Math.max(number.getBytes().length* 256 + 200,map.get(6)));

                //有效期
                Date endDate = supplierStock.getEndDate();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String format = simpleDateFormat.format(endDate);
                row1.createCell(7).setCellValue(format);
                map.put(7,Math.max(format.getBytes().length * 256 + 1000,map.get(7)));

                //状态
                if(Objects.equals("0",supplierStock.getCancellation())){
                    row1.createCell(8).setCellValue("即将过期");
                    map.put(8,Math.max("即将过期".getBytes().length*256+200,map.get(8)));

                }else {
                    row1.createCell(8).setCellValue("已过期");
                    map.put(8,Math.max("已过期".getBytes().length*256+200,map.get(8)));

                }


                rowNum++;

                for (int i = 1; i < 8; i++) {
                    row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                }

                if(Objects.equals("0",supplierStock.getCancellation())){
                    row1.getCell(8).setCellStyle(cellStyle101);
                }else {
                    row1.getCell(8).setCellStyle(cellStyle102);
                }
            }
        }

        for (int i= 0; i<9;i++){
            //设置列宽
            sheet.setColumnWidth(i,map.get(i));
        }

        response.setContentType("application/octet-stream");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
        response.flushBuffer();
        OutputStream outputStream = response.getOutputStream();
        workbook.write(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }
}
