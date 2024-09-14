package com.geeke.stock.service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
import com.geeke.stock.entity.*;
import com.geeke.utils.IdGen;
import com.geeke.utils.SessionUtils;
import com.sun.prism.paint.Color;
import io.swagger.models.auth.In;
import org.apache.commons.lang.ObjectUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.stock.dao.InventoryVerificationDetailDao;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * 库存盘点详情Service
 * @author 超级管理员
 * @version 2022-11-02
 */
 
@Service("inventoryVerificationDetailService")
@Transactional(readOnly = false)
public class InventoryVerificationDetailService extends CrudService<InventoryVerificationDetailDao, InventoryVerificationDetail>{

    @Autowired
    private InventoryVerificationService inventoryVerificationService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private StuffService stuffService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private MedicinalStockControlService medicinalStockControlService;

    @Transactional
    public Page<InventoryVerificationDetail> listPage(List<Parameter> parameters, int offset, int limit, String orderby,String type) {
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        int total = this.dao.count(pageRequest);
        List<InventoryVerificationDetail> list = null;
        if (total > 0) {
            list = this.dao.listPage(pageRequest,type);
        }

        return new Page((long)total, list);
    }

    public Page<InventoryVerificationDetail> listPages(List<Parameter> parameters, int offset, int limit, String orderby) {
/*        Optional<Parameter> cartOptional = parameters.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        parameters.remove(0);
        String id = (String) cartOptional.get().getValue();
        Company company = companyService.get(id);
        String institution = companyService.getInstitution(company.getParent().getId());
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby, id, institution);*/
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        int total = dao.count(pageRequest);
        List<InventoryVerificationDetail> list = null;
        if(total > 0) {
            list = dao.listPage(pageRequest);
        }
        return new Page<InventoryVerificationDetail>(total, list);
    }

    @Transactional(readOnly = false)
    public void save(InventoryVerification inventoryVerification, String variety, List<MedicinalStorageControl> medicinalStorageControls) {
        if(ObjectUtils.equals("0",variety)){
            //保存药品
            for (MedicinalStorageControl medicinalStorageControl : medicinalStorageControls) {
                medicinalStorageControl.setInventoryVerification(inventoryVerification);
                medicinalStorageControl.setInventoryVerificationDetailId(IdGen.uuid());
                medicinalStorageControl.setStorageStock(medicinalStorageControl.getStorageStock().subtract((medicinalStorageControl.getUsedStock().add(medicinalStorageControl.getReimburseStock()))));
            }
            this.dao.saveDrug(medicinalStorageControls);
        }
    }

    @Transactional
    public String saveAll(List<List<InventoryVerificationDetail>> inventoryVerificationDetailDTOS) {
        BigDecimal allPrice=new BigDecimal("0");
        int i=-1;
        if(!CollectionUtils.isEmpty(inventoryVerificationDetailDTOS)){
            for (List<InventoryVerificationDetail> inventoryVerificationDetailDTO : inventoryVerificationDetailDTOS) {
                if(!CollectionUtils.isEmpty(inventoryVerificationDetailDTO)){
                    for (InventoryVerificationDetail inventoryVerificationDetail : inventoryVerificationDetailDTO) {
                        inventoryVerificationDetail.preUpdate();
                        allPrice=allPrice.add(inventoryVerificationDetail.getProfitAndLossPrice());
                    }
                    i = this.dao.bulkUpdate(inventoryVerificationDetailDTO);

                }
            }
            //循环执行成功后将总金额保存到主表中
            if(i>0){
                inventoryVerificationService.updateAllPrice(inventoryVerificationDetailDTOS.get(0).get(0).getInventoryVerification().getId(),allPrice);
            }
            return "执行成功";
        }else {
            return "数据为空";
        }
    }

    @Transactional
    public void saveStuff(InventoryVerification inventoryVerification, String variety, List<MedicinalStorageControl> medicinalStorageControls) {
        if(ObjectUtils.equals("1",variety)){
            //保存材料
            for (MedicinalStorageControl medicinalStorageControl : medicinalStorageControls) {
                medicinalStorageControl.setInventoryVerification(inventoryVerification);
                medicinalStorageControl.setInventoryVerificationDetailId(IdGen.uuid());
                medicinalStorageControl.setStorageStock(medicinalStorageControl.getStorageStock().subtract((medicinalStorageControl.getUsedStock().add(medicinalStorageControl.getReimburseStock()))));
            }
            this.dao.saveStuff(medicinalStorageControls);
        }
    }

    /*导出库存盘点*/
    @Transactional
    public void exportExcel(InventoryVerification inventoryVerification, HttpServletResponse response) throws IOException {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("库存盘点表");
            String fileName = "库存盘点"+".xls";
            //设置样式
            CellStyle blackStyle = workbook.createCellStyle();
            //自动换行*重要*
            blackStyle.setWrapText(true);
            blackStyle.setAlignment(HorizontalAlignment.CENTER);
            blackStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            //新增数据行，并且设置单元格数据
            int rowNum=1;

            // headers表示excel表中第一行的表头 在excel表中添加表头
            String[] headers = {"药品编码","药品名称","类型","规格","生产厂家","供应商","批号","有效期","当前库存数量","盘点库存","盘盈盘亏","备注","盈亏金额(元)"};

            HSSFRow row = sheet.createRow(0);


            //设置字体样式
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);
            HSSFFont font1 = workbook.createFont();
            font1.setColor(IndexedColors.BLACK.getIndex());
            font1.setBold(false);
            font1.setFontHeightInPoints((short) 11);
            cellStyle.setFont(font1);

            //设置第十列的字体颜色
            CellStyle cellStyle101 = workbook.createCellStyle();
            cellStyle101.setWrapText(true);
            HSSFFont font101 = workbook.createFont();
            font101.setColor(IndexedColors.GREEN.getIndex());
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
            //获取数据库数据
            List<Parameter> parameters = new ArrayList<>();
            parameters.add(new Parameter("inventory_verification_id", "=", inventoryVerification.getId()));
            parameters.add(new Parameter("company_id", "=", inventoryVerification.getCompany().getId()));
            Page<InventoryVerificationDetail> inventoryVerificationDetailPage=null;
            if("0".equals(inventoryVerification.getType())){
                inventoryVerificationDetailPage = super.listPage(parameters, 0, 10000000, "");
            }else {
                inventoryVerificationDetailPage = super.listPage(parameters, 0, 10000000, "stuff");
            }
            List<InventoryVerificationDetail> rows = inventoryVerificationDetailPage.getRows();
            for (InventoryVerificationDetail inventoryVerificationDetail : rows) {
                if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                    Drug drug = drugService.get(inventoryVerificationDetail.getDrug().getId());
                    inventoryVerificationDetail.setDrug(drug);
                }else {
                    Stuff stuff = stuffService.get(inventoryVerificationDetail.getStuff().getId());
                    inventoryVerificationDetail.setStuff(stuff);
                }
            }
            Map<Integer,Integer> map=new HashMap<>();
            // 初始化标题的列宽,字体
            for (int i= 0; i<13;i++) {
                map.put(i, row.getCell(i).getStringCellValue().getBytes().length * 256 + 200);
               // row.getCell(i).setCellStyle(blackStyle);//设置自动换行
            }



                for (InventoryVerificationDetail inventoryVerificationDetail : rows) {
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.setHeightInPoints(20);
                if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                    ArrayList<Integer> list = new ArrayList<>();
                    row1.createCell(0).setCellValue(inventoryVerificationDetail.getDrug().getCode());
                    map.put(0,Math.max(inventoryVerificationDetail.getDrug().getCode().getBytes().length* 256 + 200,map.get(0)));

                    row1.createCell(1).setCellValue(inventoryVerificationDetail.getDrug().getGoodsName());
                    map.put(1,Math.max(inventoryVerificationDetail.getDrug().getGoodsName().getBytes().length*250+200,map.get(1)));

//                    row1.createCell(2).setCellValue(inventoryVerificationDetail.getDrug().getLocationNumber());
//                    map.put(2,Math.max((inventoryVerificationDetail.getDrug().getLocationNumber()==null?0:inventoryVerificationDetail.getDrug().getLocationNumber().getBytes().length)*250+200,map.get(2)));

                    row1.createCell(2).setCellValue(inventoryVerificationDetail.getDrug().getType().getName());
                    map.put(2,Math.max(inventoryVerificationDetail.getDrug().getType().getName().getBytes().length*250+200,map.get(2)));

                    //计算规格
                    String specification = inventoryVerificationDetail.getDrug().getDosis()+inventoryVerificationDetail.getDrug().getDosisUnit().getName()+"/"+inventoryVerificationDetail.getDrug().getPreparation()+inventoryVerificationDetail.getDrug().getPreparationUnit().getName()+"/"+inventoryVerificationDetail.getDrug().getPack().getName();
                    row1.createCell(3).setCellValue(specification);
                    map.put(3,Math.max(specification.getBytes().length*250+200,map.get(3)));

                    row1.createCell(4).setCellValue(inventoryVerificationDetail.getDrug().getFactory().getName());
                    map.put(4,Math.max((inventoryVerificationDetail.getDrug().getFactory().getName()==null?0:inventoryVerificationDetail.getDrug().getFactory().getName().getBytes().length)*250+200,map.get(4)));

                    //供应商名称
                    row1.createCell(5).setCellValue(inventoryVerificationDetail.getSupplierStock().getSupplierId().getName());
                    map.put(5,Math.max((inventoryVerificationDetail.getSupplierStock().getSupplierId().getName()==null?0:inventoryVerificationDetail.getSupplierStock().getSupplierId().getName().getBytes().length)*250+200,map.get(5)));

                    //批号
                    row1.createCell(6).setCellValue(inventoryVerificationDetail.getSupplierStock().getNorms());
                    map.put(6,Math.max((inventoryVerificationDetail.getSupplierStock().getNorms().getBytes().length)*250+200,map.get(6)));

                    //有效期
                    row1.createCell(7).setCellValue(inventoryVerificationDetail.getSupplierStock().getEndDate());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String endDate = simpleDateFormat.format(inventoryVerificationDetail.getSupplierStock().getEndDate());
                    map.put(7,Math.max((endDate.getBytes().length)*250+200,map.get(7)));

                    //计算当前库存
                    String currentInventory = getCurrentInventory(inventoryVerificationDetail);
                    row1.createCell(8).setCellValue(currentInventory);
                    map.put(8,Math.max(currentInventory.getBytes().length*250+200,map.get(8)));

                    //盘点库存
                    String checkInventory = getCheckInventory(inventoryVerificationDetail);
                    row1.createCell(9).setCellValue(checkInventory);
                    map.put(9,Math.max(checkInventory.getBytes().length*250+200,map.get(9)));

                    //盘盈盘亏
                    String profitAndLoss = getProfitAndLoss(inventoryVerificationDetail);
                    row1.createCell(10).setCellValue(profitAndLoss);
                    map.put(10,Math.max(profitAndLoss.getBytes().length*250+200,map.get(10)));

                    row1.createCell(11).setCellValue(inventoryVerificationDetail.getRemarks());
                    map.put(11,Math.max((inventoryVerificationDetail.getRemarks()==null?0:inventoryVerificationDetail.getRemarks().getBytes().length)*250+200,map.get(11)));


                    row1.createCell(12).setCellValue(inventoryVerificationDetail.getProfitAndLossPrice().doubleValue());
                    map.put(12,Math.max(((inventoryVerificationDetail.getProfitAndLossPrice()+"").getBytes().length)*250+200,map.get(12)));

                    rowNum++;

                    for (int i = 1; i < 12; i++) {
                        row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                    }
                    //单独设置第十列的字体颜色
                    BigDecimal bigDecimal = new BigDecimal(0);
                    if(bigDecimal.compareTo(inventoryVerificationDetail.getProfitAndLossPrice())<=0){
                        row1.getCell(12).setCellStyle(cellStyle101);
                    }else {
                        row1.getCell(12).setCellStyle(cellStyle102);
                    }

                }else {
                    row1.createCell(0).setCellValue(inventoryVerificationDetail.getStuff().getCode());
                    map.put(0,Math.max(inventoryVerificationDetail.getStuff().getCode().getBytes().length* 256 + 200,map.get(0)));

                    row1.createCell(1).setCellValue(inventoryVerificationDetail.getStuff().getName());
                    map.put(1,Math.max(inventoryVerificationDetail.getStuff().getName().getBytes().length* 256 + 200,map.get(1)));

//                    row1.createCell(2).setCellValue(inventoryVerificationDetail.getStuff().getLocationNumber());
//                    map.put(2,Math.max((inventoryVerificationDetail.getStuff().getLocationNumber()==null?"":inventoryVerificationDetail.getStuff().getLocationNumber()).getBytes().length* 256 + 200,map.get(2)));

                    row1.createCell(2).setCellValue(inventoryVerificationDetail.getStuff().getType().getName());
                    map.put(2,Math.max(inventoryVerificationDetail.getStuff().getType().getName().getBytes().length* 256 + 200,map.get(2)));

                    //计算规格
                    String specification = inventoryVerificationDetail.getStuff().getSpecifications();
                    row1.createCell(3).setCellValue(specification);
                    map.put(3,Math.max(specification.getBytes().length* 256 + 200,map.get(3)));

                    row1.createCell(4).setCellValue(inventoryVerificationDetail.getStuff().getFactory().getName());
                    map.put(4,Math.max((inventoryVerificationDetail.getStuff().getFactory().getName()==null?"":inventoryVerificationDetail.getStuff().getFactory().getName()).getBytes().length* 256 + 200,map.get(4)));

                    //供应商名称
                    row1.createCell(5).setCellValue(inventoryVerificationDetail.getSupplierStock().getSupplierId().getName());
                    map.put(5,Math.max((inventoryVerificationDetail.getSupplierStock().getSupplierId().getName()==null?0:inventoryVerificationDetail.getSupplierStock().getSupplierId().getName().getBytes().length)*250+200,map.get(5)));

                    //批号
                    row1.createCell(6).setCellValue(inventoryVerificationDetail.getSupplierStock().getNorms());
                    map.put(6,Math.max((inventoryVerificationDetail.getSupplierStock().getNorms().getBytes().length)*250+200,map.get(6)));

                    //有效期
                    row1.createCell(7).setCellValue(inventoryVerificationDetail.getSupplierStock().getEndDate());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String endDate = simpleDateFormat.format(inventoryVerificationDetail.getSupplierStock().getEndDate());
                    map.put(7,Math.max((endDate.getBytes().length)*250+200,map.get(7)));

                    //计算当前库存
                    String currentInventory = getCurrentInventory(inventoryVerificationDetail);
                    row1.createCell(8).setCellValue(currentInventory);
                    map.put(8,Math.max(currentInventory.getBytes().length* 256 + 200,map.get(8)));

                    //盘点库存
                    String checkInventory = getCheckInventory(inventoryVerificationDetail);
                    row1.createCell(9).setCellValue(checkInventory);
                    map.put(9,Math.max(checkInventory.getBytes().length* 256 + 200,map.get(9)));

                    //盘盈盘亏
                    String profitAndLoss = getProfitAndLoss(inventoryVerificationDetail);
                    row1.createCell(10).setCellValue(profitAndLoss);
                    map.put(10,Math.max(profitAndLoss.getBytes().length* 256 + 200,map.get(10)));

                    row1.createCell(11).setCellValue(inventoryVerificationDetail.getRemarks());
                    map.put(11,Math.max((inventoryVerificationDetail.getRemarks()==null?"":inventoryVerificationDetail.getRemarks()).getBytes().length* 256 + 200,map.get(11)));

                    row1.createCell(12).setCellValue(inventoryVerificationDetail.getProfitAndLossPrice().doubleValue());
                    map.put(12,Math.max((inventoryVerificationDetail.getProfitAndLossPrice()+"").getBytes().length* 256 + 200,map.get(12)));

                    rowNum++;

                    for (int i = 1; i < 12; i++) {
                        row1.getCell(i).setCellStyle(cellStyle);//设置文本的样式
                    }
                    //单独设置第十列的字体颜色
                    BigDecimal bigDecimal = new BigDecimal(0);
                    if(bigDecimal.compareTo(inventoryVerificationDetail.getProfitAndLossPrice())<=0){
                        row1.getCell(12).setCellStyle(cellStyle101);
                    }else {
                        row1.getCell(12).setCellStyle(cellStyle102);
                    }
                }
            }
            for (int i= 0; i<13;i++){
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

    //计算当前库存
    public String getCurrentInventory(InventoryVerificationDetail inventoryVerificationDetail){
        String inventory = "";
        BigDecimal currentInventory = new BigDecimal(inventoryVerificationDetail.getCurrentInventory().toString());
        BigDecimal preparation = null;
        if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
            preparation = new BigDecimal(inventoryVerificationDetail.getDrug().getPreparation().toString());
        }else {
            preparation = new BigDecimal(inventoryVerificationDetail.getStuff().getPackNumber().toString());
        }
        BigDecimal divide = currentInventory.divide(preparation,BigDecimal.ROUND_CEILING);
        BigDecimal bigDecimal = divide.setScale(0, BigDecimal.ROUND_DOWN);
        BigDecimal subtract = divide.subtract(bigDecimal);
        int i = bigDecimal.intValue();
        int i1 = subtract.multiply(preparation).intValue();
        if((i-i1)>0){
            if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                inventory=(i+inventoryVerificationDetail.getDrug().getPack().getName())+(i1+inventoryVerificationDetail.getDrug().getPreparationUnit().getName());
            }else {
                inventory=(i+inventoryVerificationDetail.getStuff().getPackUnit().getName())+(i1+inventoryVerificationDetail.getStuff().getMinUnit().getName());
            }
            return inventory;
        }else {
            if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                inventory=i+inventoryVerificationDetail.getDrug().getPack().getName();
            }else {
                inventory=i+inventoryVerificationDetail.getStuff().getPackUnit().getName();
            }

            return inventory;
        }
    }

    //盘点库存
    public String getCheckInventory(InventoryVerificationDetail inventoryVerificationDetail){
        String inventory = "";
        BigDecimal currentInventory = new BigDecimal(inventoryVerificationDetail.getCheckInventory().toString());
        BigDecimal preparation = null;
        if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
            preparation = new BigDecimal(inventoryVerificationDetail.getDrug().getPreparation().toString());
        }else {
            preparation = new BigDecimal(inventoryVerificationDetail.getStuff().getPackNumber().toString());
        }

        BigDecimal divide = currentInventory.divide(preparation,BigDecimal.ROUND_CEILING);
        BigDecimal bigDecimal = divide.setScale(0, BigDecimal.ROUND_DOWN);
        BigDecimal subtract = divide.subtract(bigDecimal);
        int i = bigDecimal.intValue();
        int i1 = subtract.multiply(preparation).intValue();
        if((i-i1)>0){
            if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                inventory=(i+inventoryVerificationDetail.getDrug().getPack().getName())+(i1+inventoryVerificationDetail.getDrug().getPreparationUnit().getName());
            }else {
                inventory=(i+inventoryVerificationDetail.getStuff().getPackUnit().getName())+(i1+inventoryVerificationDetail.getStuff().getMinUnit().getName());
            }
            return inventory;
        }else {
            if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                inventory=i+inventoryVerificationDetail.getDrug().getPack().getName();
            }else {
                inventory=i+inventoryVerificationDetail.getStuff().getPackUnit().getName();
            }

            return inventory;
        }
    }

    //盘盈盘亏
    public String getProfitAndLoss(InventoryVerificationDetail inventoryVerificationDetail){
        String checkInventory="";
        if(inventoryVerificationDetail.getCheckInventory()>0){

            BigDecimal bigDecimal = new BigDecimal(inventoryVerificationDetail.getProfitAndLoss().toString());
            BigDecimal bigDecimal1 = null;
            if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                bigDecimal1 = new BigDecimal(inventoryVerificationDetail.getDrug().getPreparation().toString());
            }else {
                bigDecimal1 = new BigDecimal(inventoryVerificationDetail.getStuff().getPackNumber().toString());
            }
            BigDecimal divide = bigDecimal.divide(bigDecimal1,BigDecimal.ROUND_CEILING);
            BigDecimal bigDecimal2 = divide.setScale(0, BigDecimal.ROUND_DOWN);
            BigDecimal subtract = divide.subtract(bigDecimal2);
            int i = bigDecimal2.intValue();
            int i1 = subtract.multiply(bigDecimal1).intValue();
            if(i1>0){
                if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                    checkInventory=(i+inventoryVerificationDetail.getDrug().getPack().getName())+(i1+inventoryVerificationDetail.getDrug().getPreparationUnit().getName());
                }else {
                    checkInventory=(i+inventoryVerificationDetail.getStuff().getPackUnit().getName())+(i1+inventoryVerificationDetail.getStuff().getMinUnit().getName());
                }
              //  checkInventory=(i+inventoryVerificationDetail.getDrug().getPack().getName())+(i1+inventoryVerificationDetail.getDrug().getPreparationUnit().getName());
                return checkInventory;
            }else {
                if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                    checkInventory=i+inventoryVerificationDetail.getDrug().getPack().getName();
                }else {
                    checkInventory=i+inventoryVerificationDetail.getStuff().getPackUnit().getName();
                }
               // checkInventory=i+inventoryVerificationDetail.getDrug().getPack().getName();
                return checkInventory;
            }
        }else if(inventoryVerificationDetail.getCheckInventory()<0){
            //先转换为正数
            Integer i=~(inventoryVerificationDetail.getProfitAndLoss()-1);
            BigDecimal bigDecimal = new BigDecimal(i.toString());
           // BigDecimal bigDecimal1 = new BigDecimal(inventoryVerificationDetail.getDrug().getPreparation().toString());
            BigDecimal bigDecimal1 = null;
            if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                bigDecimal1 = new BigDecimal(inventoryVerificationDetail.getDrug().getPreparation().toString());
            }else {
                bigDecimal1 = new BigDecimal(inventoryVerificationDetail.getStuff().getPackNumber().toString());
            }
            BigDecimal divide = bigDecimal.divide(bigDecimal1,BigDecimal.ROUND_CEILING);
            BigDecimal bigDecimal2 = divide.setScale(0, BigDecimal.ROUND_DOWN);
            BigDecimal subtract = divide.subtract(bigDecimal2);
            int i2 = bigDecimal2.intValue();
            int i3 = subtract.multiply(bigDecimal1).intValue();
            if(i3>0){
                if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                    checkInventory="-"+(i2+inventoryVerificationDetail.getDrug().getPack().getName())+(i3+inventoryVerificationDetail.getDrug().getPreparationUnit().getName());
                }else {
                    checkInventory="-"+(i2+inventoryVerificationDetail.getStuff().getPackUnit().getName())+(i3+inventoryVerificationDetail.getStuff().getMinUnit().getName());
                }
               //checkInventory="-"+(i2+inventoryVerificationDetail.getDrug().getPack().getName())+(i3+inventoryVerificationDetail.getDrug().getPreparationUnit().getName());
                return checkInventory;
            }else {
                if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                    checkInventory="-"+i2+inventoryVerificationDetail.getDrug().getPack().getName();
                }else {
                    checkInventory="-"+i2+inventoryVerificationDetail.getStuff().getPackUnit().getName();
                }
                //checkInventory="-"+i2+inventoryVerificationDetail.getDrug().getPack().getName();
                return checkInventory;
            }
        } else {
            if(!StringUtils.isNullOrEmpty(inventoryVerificationDetail.getDrug())){
                return 0+inventoryVerificationDetail.getDrug().getPack().getName();
            }else {
                return 0+inventoryVerificationDetail.getStuff().getPackUnit().getName();
            }

        }
    }
    @Transactional(readOnly = false)
    public List<InventoryVerificationDetail> getByInfoId(String infoId) {
        List<InventoryVerificationDetail> list = this.dao.getByInfoId(infoId);
        return list;
    }

    @Transactional
    public void deleteByInventoryVerificationId(String inventoryVerificationId) {
        this.dao.deleteByInventoryVerificationId(inventoryVerificationId);
    }
}