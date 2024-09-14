package com.geeke.stock.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.geeke.basicdata.dao.ManufactureFactoryDao;
import com.geeke.basicdata.entity.ManufactureFactory;
import com.geeke.basicdata.service.ManufactureFactoryService;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.sequence.service.SequenceService;
import com.geeke.common.service.CrudService;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
import com.geeke.stock.dao.DrugDao;
import com.geeke.stock.entity.Drug;
import com.geeke.sys.entity.DictItem;
import com.geeke.sys.service.DictTypeService;
import com.geeke.sys.utils.SessionUserDto;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 药品信息Service
 *
 * @author txl
 * @version 2022-06-07
 */

@Service("drugService")
@Transactional(readOnly = true)
public class DrugService extends CrudService<DrugDao, Drug> {
    @Autowired
    SequenceService sequenceService;
    @Autowired
    DrugDao drugDao;
    @Autowired
    private MedicinalStockControlService medicinalStockControlService;
    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;
    @Autowired
    private DictTypeService dictTypeService;
    @Autowired
    private ManufactureFactoryService manufactureFactoryService;
    @Autowired
    private CompanyService companyService;

    @Autowired
    private ManufactureFactoryDao manufactureFactoryDao;

    @Override
    @Transactional(readOnly = false)
    public Drug save(Drug drug) {
        // 判断药品是否重复
        int repeat = this.repeat(drug);
        if (repeat!=0){
            throw new RuntimeException("药品信息重复");

        }
        // 新增时, 处理自动编号字段
        if (StringUtils.isBlank(drug.getId())){
            String sn = sequenceService.generate(SessionUtils.getUser().getCompanyId(), "medical_code", drug);
            drug.setCode(sn);
        }

        Drug drugTemp = super.save(drug);
        this.medicinalStockControlService.initStock(drugTemp);
        return drugTemp;
    }


    public int addDurg(Drug drug) {
        return dao.insert(drug);
    }

    public Page<Drug> listPages(List<Parameter> parameters, int offset, int limit, String order) {
        Optional<Parameter> cartOptional = parameters.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        parameters.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        // String parentId = company.get(id);
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, order, id, institution);
        int total = dao.count(pageRequest);
        List<Drug> list = null;
        if (total > 0) {
            list = drugDao.listPages(pageRequest);
            // list = drugDao.listPage(pageRequest);
        }
        return new Page<>(total, list);
    }



    public List<Drug> listAlls(List<Parameter> parameters, String orderby) {
        Optional<Parameter> cartOptional = parameters.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        parameters.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        PageRequest pageRequest = new PageRequest(parameters, orderby, id, institution);
        return dao.listAlls(pageRequest);
    }

    public List<Drug> listAllStock(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = new PageRequest(parameters, orderby);
        return this.dao.listAllStock(pageRequest);
    }

    public List<Drug> listAllStock2(List<Parameter> parameters, String orderby) {

        Optional<Parameter> cartOptional = parameters.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        parameters.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        PageRequest pageRequest = new PageRequest(parameters, orderby, id, institution);
        return this.dao.listAllStock2(pageRequest);
    }

    //修改库存
    @Transactional(readOnly = false)
    public void updateInventory(int inventory, String id) {
        //根据id获取现有库存
        Drug drug = this.dao.get(id);

        //获取现有库存加上新增库存进行修改库存
        //判读是否允许拆零
        // if("0".equals(drug.getIsUnpackSell())){
        drug.setInventory(drug.getInventory() + inventory);
      /*  }else {
            drug.setInventory(drug.getInventory()+(inventory*Integer.parseInt(drug.getPreparation())));
        }*/

        drugDao.updateInventory(drug.getId(), drug.getInventory());
    }

    public List<Drug> getAll(String companyId, String type) {
        List<Drug> drugs = this.dao.getAll(companyId, type);
        return drugs;
    }


    public Page<Drug> getDrug(List<Parameter> params, int offset, int limit, String orderby) {
        Optional<Parameter> cartOptional = params.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        params.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby, id, institution);
        int total = this.dao.count(pageRequest);
        List<Drug> list = null;
        if (total > 0) {
            list = this.dao.listPages(pageRequest);
        }
        //获取到当前药品后去库存明细中获取其进价并算出其成本合计
        if (!CollectionUtils.isEmpty(list)) {
            for (Drug drug : list) {
                //根据动态库存获取成本合计
                BigDecimal totalCost = medicinalStorageControlService.getByDrugOrStuffId(drug.getId());
                if (!Objects.isNull(drug.getStock()) && !Objects.isNull(drug.getStock().getStorageStock())) {
                    String inventory =
                            drug.getStock().getStorageStock().subtract((drug.getStock().getUsedStock().add(drug.getStock().getReimburseStock()))).stripTrailingZeros().toPlainString();
                    drug.setInventory(Integer.parseInt(inventory));
                } else {
                    drug.setInventory(0);
                }
                if (!Objects.isNull(totalCost)) {
                    drug.setBid(totalCost);
                } else {
                    drug.setBid(new BigDecimal("0"));
                }
            }
        }

        return new Page((long) total, list);
    }

    @Transactional(readOnly = false)
    public int updateAllIndate(String indate, String companyId) {
        int i = this.dao.updateAllIndate(indate, companyId);
        return i;
    }

    @Transactional(readOnly = false)
    public int updateAllInventory(String inventoryFloor, String companyId) {
        int i = this.dao.updateAllInventory(inventoryFloor, companyId);
        return i;
    }

    public Page<Drug> getDrugInventoryWarning(List<Parameter> parameters, int offset, int limit, String orderby) {
        Optional<Parameter> cartOptional = parameters.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        parameters.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby, id, institution);
        int total = this.dao.countInventory(pageRequest);
        List<Drug> list = null;
        if (total > 0) {
            List<Drug> drugInventoryWarning = this.dao.getDrugInventoryWarning(pageRequest);
            if (!CollectionUtils.isEmpty(drugInventoryWarning)) {
                list = drugInventoryWarning;
            }
        }
        return new Page((long) total, list);
    }

    public List<Drug> inventory(List<Parameter> parameters, String orderby) {
        Optional<Parameter> cartOptional = parameters.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        parameters.remove(0);
        String id = (String) cartOptional.get().getValue();
        Company company = companyService.get(id);
        String institution = company.getParent().getId();
        PageRequest pageRequest = new PageRequest(parameters, orderby, id, institution);
        return dao.inventory(pageRequest);
    }

    @Transactional(readOnly = false)
    public List excel(MultipartFile file) throws Exception{
        String companyId = com.geeke.sys.utils.SessionUtils.getUserJson().getString("companyId");
        Company company = new Company();
        company.setId(companyId);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 判断文件格式 我需要的是EXCEL表
        String fileName = file.getOriginalFilename();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            throw new Exception("上传文件格式不正确");
        }
        List<Drug> list = new ArrayList<>();
        List<String> error = new ArrayList<>();
        int num = 0;
        int chengGong = 0;
        int siBai = 0;
        String mistake = "";
        String drugName = "";
            InputStream is = file.getInputStream();
            // 读取工作簿
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            // 读取工作表
            XSSFSheet sheet = workbook.getSheetAt(0);
            //获取多少行
            int rows = sheet.getPhysicalNumberOfRows();
            Drug task = null;

            for (int j = 2; j < rows; j++) {
                try {
                    num = j + 1;
                    task = new Drug();
                    //获得第 j 行
                    XSSFRow row = sheet.getRow(j);
                    // row.getCell(j).setCellType(CellType.STRING);
                    if (row.getCell(0) != null){
                        task.setGoodsName(row.getCell(0).getStringCellValue());
                        drugName = row.getCell(0).getStringCellValue();
                    }
                    if (row.getCell(1) != null){
                        task.setBrandName(row.getCell(1).getStringCellValue());
                    }
                    if (row.getCell(2) != null){
                        task.setPinyinCode(row.getCell(2).getStringCellValue());
                    }
                    if (row.getCell(3) != null){
                        DictItem dictItem = new DictItem();
                        dictItem.setName(row.getCell(3).getStringCellValue());
                        dictItem.setValue(dictTypeService.getValue(row.getCell(3).getStringCellValue(),"1004078055755374603"));
                        task.setType(dictItem);
                    }

                    if (row.getCell(4) != null){
                        task.setSource(row.getCell(4).getStringCellValue());
                    }
                    if (row.getCell(5) != null){
                        DictItem dictItem = new DictItem();
                        dictItem.setName(row.getCell(5).getStringCellValue());
                        dictItem.setValue(dictTypeService.getValue(row.getCell(5).getStringCellValue(), "1004078055755374607"));
                        task.setNature(dictItem);
                    }
                    if (row.getCell(6) != null){
                        ManufactureFactory manufactureFactory = new ManufactureFactory();
                        final String name = row.getCell(6).getStringCellValue();
                        String by = manufactureFactoryDao.findBy(name, companyId);
                        if(by!=null){
                            manufactureFactory.setId(by);
                        }else {
                            manufactureFactory.setName(name);
                            manufactureFactory.setType("1");
                            manufactureFactory.setCompany(company);
                            ManufactureFactory manufactureFactoryTemp = manufactureFactoryService.save(manufactureFactory);
                            manufactureFactory.setId(manufactureFactoryTemp.getId());
                        }
                        task.setFactory(manufactureFactory);
                    }
                    if (row.getCell(7) != null){
                        task.setStandardCode(row.getCell(7).getStringCellValue());
                    }
                    if (row.getCell(8) != null){
                        task.setBitCode(row.getCell(8).getStringCellValue());
                    }
                    if (row.getCell(9) != null){
                        task.setBarCode(row.getCell(9).getStringCellValue());
                    }
                    if (row.getCell(10) != null){
                        task.setInsuranceCode(row.getCell(10).getStringCellValue());
                    }
                    if (row.getCell(11) != null){
                        final double numericCellValue = row.getCell(11).getNumericCellValue();
                        task.setDosis(String.valueOf(numericCellValue));
                    }
                    if (row.getCell(12) != null){
                        DictItem dictItem = new DictItem();
                        dictItem.setName(row.getCell(12).getStringCellValue());
                        dictItem.setValue(dictTypeService.getValue(row.getCell(12).getStringCellValue(), "1004406758192578588"));
                        task.setDosisUnit(dictItem);
                    }
                    if (row.getCell(13) != null){
                        final double numericCellValue = row.getCell(13).getNumericCellValue();
                        task.setPreparation(String.valueOf(numericCellValue));
                    }
                    if (row.getCell(14) != null){
                        DictItem dictItem = new DictItem();
                        dictItem.setName(row.getCell(14).getStringCellValue());
                        dictItem.setValue(dictTypeService.getValue(row.getCell(14).getStringCellValue(), "1004406758192578593"));
                        task.setPreparationUnit(dictItem);
                    }
                    if (row.getCell(15) != null){
                        DictItem dictItem = new DictItem();
                        String stringCellValue = row.getCell(15).getStringCellValue();
                        dictItem.setName(row.getCell(15).getStringCellValue());
                        dictItem.setValue(dictTypeService.getValue(row.getCell(15).getStringCellValue(),"1004406758192578597"));
                        task.setPack(dictItem);
                    }
                    if (row.getCell(16) != null){
                        double numericCellValue = row.getCell(16).getNumericCellValue();
                        BigDecimal price = new BigDecimal(numericCellValue);
                        task.setPrice(price);
                    }
                    if (row.getCell(17) != null){
                        task.setIsUnpackSell(row.getCell(17).getStringCellValue().equals("是") ? "1" : "0");
                    }
                    if (row.getCell(18) != null){
                        double numericCellValue = row.getCell(18).getNumericCellValue();
                        BigDecimal retailPrice = new BigDecimal(numericCellValue);
                        task.setRetailPrice(retailPrice);
                    }
                    if (row.getCell(19) != null){
                        task.setStatus(row.getCell(19).getStringCellValue().equals("是") ? "1" : "0");
                    }
                    if (row.getCell(20) != null){
                        task.setRemarks(row.getCell(20).getStringCellValue());
                    }
                    if (row.getCell(21) != null){
                        task.setTotal(Integer.parseInt(row.getCell(21).getStringCellValue()));
                    }
                    task.setCompany(company);
                    this.save(task).getId();
                    chengGong+=1;
                } catch (Exception e) {
                    String a = "";
                    if (e.getMessage()!=null) {
                        a = e.getMessage().equals("药品信息重复") ? "药品信息重复," : "";
                    }
                    siBai+=1;
                    mistake += "表格第"+num+"行["+drugName+"]药品信息异常，"+a+"请核对后重新导入\n";
                }
            }
        error.add(0,String.valueOf(chengGong));
        error.add(1,String.valueOf(siBai));
        error.add(2,mistake);
        return error;
    }

    // 药品新增查重
    public int repeat(Drug drug) {

        String companyId = com.geeke.sys.utils.SessionUtils.getUserJson().getString("companyId");
        Company company = companyService.get(companyId);
        drug.setCompany(company);
        List<Drug> drugList = this.dao.repeat(drug);

        //如果新增则为0，修改条数是1但是ID不一样则不重复
        if (ObjUtil.isNull(drug.getId()) && drugList.size() == 1 && drugList.get(0).getId().equals(drug.getId())) {
            return 0;
        } else {
            return drugList.size();
        }

    }

    // 药品名称获取数据
    public String getId(String name,String company) {
        String drugs = this.dao.getId(name,company);
        return drugs;
    }


    public Page<Drug> listByCompany(List<Parameter> params, int offset, int limit, String orderby) {

        Optional<Parameter> cartOptional = params.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        params.remove(0);
        String id = (String) cartOptional.get().getValue();
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby, id);
        int total = dao.count(pageRequest);
        List<Drug> list = null;
        if (total > 0) {
            list = dao.listPages(pageRequest);
        }
        return new Page<>(total, list);
    }

    public Page<Drug> listByInstitution(List<Parameter> params, int offset, int limit, String orderby) {
        Optional<Parameter> cartOptional = params.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        params.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby, id, institution);
        int total = dao.countByInstitution(pageRequest);
        List<Drug> list = null;
        if (total > 0) {
            list = dao.listByInstitution(pageRequest);

        }
        return new Page<>(total, list);
    }


    @Transactional(readOnly = false)
    public List<String> syncToClinic(List<Drug> entitys) {
        SessionUserDto userDto = SessionUtils.getUserDto();
        SessionUserDto.CompanyDTO companyDto = userDto.getCompany();
        Company company = BeanUtil.copyProperties(companyDto, Company.class);

        List<String> list = new ArrayList<>();

        entitys.forEach(drug -> {
            drug.setCompany(company);
            String oldId = drug.getId();
            int repeat = repeatByCode(drug);
            if (repeat == 0) {
                drug.setId(null);
                drug.setSyncId(oldId);
                super.save(drug);
                this.medicinalStockControlService.initStock(drug);
                list.add(drug.getId());
            }

        });

        return list;
    }

    private int repeatByCode(Drug drug) {
        String companyId = SessionUtils.getUserDto().getCompanyId();
        String code = drug.getCode();
        return this.dao.checkCompanyAndCode(companyId, code);

    }

    public Drug getByCode(String ypdm, String companyId) {
        return  drugDao.getByCode(ypdm,companyId);
    }
}