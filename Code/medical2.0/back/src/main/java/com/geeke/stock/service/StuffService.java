package com.geeke.stock.service;

import cn.hutool.core.bean.BeanUtil;
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
import com.geeke.stock.dao.StuffDao;
import com.geeke.stock.entity.MedicalProject;
import com.geeke.stock.entity.Stuff;
import com.geeke.sys.entity.DictItem;
import com.geeke.sys.service.DictTypeService;
import com.geeke.sys.utils.SessionUserDto;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
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
 * 材料信息Service
 *
 * @author txl
 * @version 2022-06-22
 */

@Service("stuffService")
@Transactional(readOnly = true)
public class StuffService extends CrudService<StuffDao, Stuff> {
    @Autowired
    SequenceService sequenceService;
    @Autowired
    StuffDao stuffDao;
    @Autowired
    private SupplierStockService supplierStockService;
    @Autowired
    private MedicinalStockControlService medicinalStockControlService;
    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;
    @Autowired
    private CompanyService companyService;

    @Autowired
    private DictTypeService dictTypeService;

    @Autowired
    private ManufactureFactoryDao manufactureFactoryDao;

    @Autowired
    private ManufactureFactoryService manufactureFactoryService;

    @Override
    @Transactional(readOnly = false)
    public Stuff save(Stuff stuff) {
        // 判断材料是否重复
        final int repeat = this.repeat(stuff);
        if (repeat!=0){
            throw new NegativeArraySizeException("材料信息重复");

        }
        // 新增时, 处理自动编号字段
        if (StringUtils.isBlank(stuff.getId())) {
            String sn = sequenceService.generate(SessionUtils.getUser().getCompanyId(), "stuff_code", stuff);
            stuff.setCode(sn);
        }

        Stuff stuffTemp = super.save(stuff);

        //保存动态库存
        this.medicinalStockControlService.initStock(stuffTemp);
        return stuffTemp;
    }

    public List<MedicalProject> listPageForMedical(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = new PageRequest(parameters, orderby);
        int total = this.dao.count(pageRequest);
        List<Stuff> lis = null;
        List<MedicalProject> list = new ArrayList<>();
        if (total > 0) {
            lis = this.dao.listAll(pageRequest);
            lis.stream().forEach(stuff -> {
                MedicalProject medicalProject = new MedicalProject();
                BeanUtils.copyProperties(stuff, medicalProject);
                medicalProject.setStuffType("4");
                medicalProject.setDosisUnit(stuff.getMinUnit());
                medicalProject.setPack(stuff.getPackUnit());
                list.add(medicalProject);
            });
        }
        return list;
    }

    @Transactional(readOnly = false)
    public void updateInventory(int inventory, String id) {
        //根据id获取现有库存
        Stuff stuff = this.dao.get(id);
        //获取现有库存加上新增库存进行修改库存
        stuff.setInventory(stuff.getInventory() + inventory);
        System.out.println(stuff.getInventory());
        stuffDao.updateInventory(stuff.getId(), stuff.getInventory());
    }

    @Transactional
    public List<Stuff> getAll(String companyId, String type) {
        List<Stuff> stuffs = this.dao.getAll(companyId, type);
        return stuffs;
    }

    public Page<Stuff> getStuff(List<Parameter> params, int offset, int limit, String orderby) {
        Optional<Parameter> cartOptional =
                params.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        params.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby, id, institution);
        //PageRequest pageRequest = new PageRequest(offset, limit, params, orderby);
        int total = this.dao.count(pageRequest);
        List<Stuff> list = null;
        if (total > 0) {
            list = this.dao.listPages(pageRequest);
        }
        //获取到当前材料后去库存明细中获取其进价并算出其成本合计
        if (!CollectionUtils.isEmpty(list)) {
            for (Stuff stuff : list) {
                //根据动态库存获取成本合计
                BigDecimal totalCost = medicinalStorageControlService.getByDrugOrStuffId(stuff.getId());
                if (!Objects.isNull(stuff.getStock()) && !Objects.isNull(stuff.getStock().getStorageStock())) {
                    String inventory =
                            stuff.getStock().getStorageStock().subtract((stuff.getStock().getUsedStock().add(stuff.getStock().getReimburseStock()))).stripTrailingZeros().toPlainString();
                    stuff.setInventory(Integer.parseInt(inventory));
                } else {
                    stuff.setInventory(0);
                }
                if (!Objects.isNull(totalCost)) {
                    stuff.setBid(totalCost);
                } else {
                    stuff.setBid(new BigDecimal("0"));
                }
            }
        }
        return new Page((long) total, list);
    }

    @Transactional(readOnly = false)
    public int updateAllIndate(String indate, String companyId) {
        return this.dao.updateAllIndate(indate, companyId);
    }

    @Transactional(readOnly = false)
    public int updateAllInventory(String inventoryFloor, String companyId) {
        return this.dao.updateAllInventory(inventoryFloor, companyId);
    }

    public Page<Stuff> getStuffInventoryWarning(List<Parameter> params, int offset, int limit, String orderby) {
        Optional<Parameter> cartOptional =
                params.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        params.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby, id, institution);
        int total = this.dao.getCountWarning(pageRequest);
        List<Stuff> list = null;
        if (total > 0) {
            list = this.dao.getStuffInventoryWarning(pageRequest);
        }
        return new Page((long) total, list);
    }

    public Page<Stuff> listPages(List<Parameter> parameters, int offset, int limit, String order) {
        Optional<Parameter> cartOptional = parameters.stream().filter(item -> item.getColumnName().equals(
                "`company_id`")).findFirst();
        parameters.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        // String parentId = company.get(id);
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, order, id, institution);
        int total = dao.count(pageRequest);
        List<Stuff> list = null;
        if (total > 0) {
            list = stuffDao.listPages(pageRequest);
        }
        return new Page<>(total, list);
    }

    public List<Stuff> listAlls(List<Parameter> parameters, String orderby) {
        Optional<Parameter> cartOptional = parameters.stream().filter(item -> item.getColumnName().equals(
                "`company_id`")).findFirst();
        parameters.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        PageRequest pageRequest = new PageRequest(parameters, orderby, id, institution);
        return dao.listAlls(pageRequest);
    }

    public List<Stuff> inventory(List<Parameter> parameters, String orderby) {
        Optional<Parameter> cartOptional = parameters.stream().filter(item -> item.getColumnName().equals(
                "`company_id`")).findFirst();
        parameters.remove(0);
        String id = (String) cartOptional.get().getValue();
        Company company = companyService.get(id);
        String institution = company.getParent().getId();
        PageRequest pageRequest = new PageRequest(parameters, orderby, id, institution);
        return dao.inventory(pageRequest);
    }

    @Transactional(readOnly = false)
    public List excel(MultipartFile file) throws Exception {
        String companyId = com.geeke.sys.utils.SessionUtils.getUserJson().getString("companyId");
        Company company = new Company();
        company.setId(companyId);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 判断文件格式 我需要的是EXCEL表
        String fileName = file.getOriginalFilename();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            throw new Exception("上传文件格式不正确");
        }
        List<Stuff> list = new ArrayList<>();
        List<String> error = new ArrayList<>();
        int num = 0;
        int chengGong = 0;
        int siBai = 0;
        String mistake = "";
        String stuffName = "";

        InputStream is = file.getInputStream();
        // 读取工作簿
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        // 读取工作表
        XSSFSheet sheet = workbook.getSheetAt(0);
        //获取多少行
        int rows = sheet.getPhysicalNumberOfRows();
        Stuff task = null;

        for (int j = 2; j < rows; j++) {
            try {
                num = j + 1;
                task = new Stuff();
                //获得第 j 行
                XSSFRow row = sheet.getRow(j);
                // row.getCell(j).setCellType(CellType.STRING);
                if (row.getCell(0) != null) {
                    task.setName(row.getCell(0).getStringCellValue());
                    stuffName = row.getCell(0).getStringCellValue();
                }
                if (row.getCell(1) != null) {
                    task.setCommonName(row.getCell(1).getStringCellValue());
                }
                if (row.getCell(2) != null) {
                    // stuffName.toUpperCase();
                    task.setPinyinCode(row.getCell(2).getStringCellValue());
                }
                if (row.getCell(3) != null) {
                    DictItem dictItem = new DictItem();
                    dictItem.setName(row.getCell(3).getStringCellValue());
                    dictItem.setValue(dictTypeService.getValue(row.getCell(3).getStringCellValue(),
                            "1004462867645374476"));
                    task.setType(dictItem);
                }

                if (row.getCell(4) != null) {
                    ManufactureFactory manufactureFactory = new ManufactureFactory();
                    final String name = row.getCell(4).getStringCellValue();
                    String by = manufactureFactoryDao.findBy(name, companyId);
                    if (by != null) {
                        manufactureFactory.setId(by);
                    } else {
                        manufactureFactory.setName(name);
                        manufactureFactory.setType("2");
                        manufactureFactory.setCompany(company);
                        ManufactureFactory manufactureFactoryTemp = manufactureFactoryService.save(manufactureFactory);
                        manufactureFactory.setId(manufactureFactoryTemp.getId());
                    }
                    task.setFactory(manufactureFactory);
                }
                if (row.getCell(5) != null) {
                    DictItem dictItem = new DictItem();
                    dictItem.setName(row.getCell(5).getStringCellValue());
                    dictItem.setValue(dictTypeService.getValue(row.getCell(5).getStringCellValue(),
                            "1004078055755374607"));
                    task.setNature(dictItem);
                }

                if (row.getCell(6) != null) {
                    task.setBarCode(row.getCell(6).getStringCellValue());
                }

                if (row.getCell(7) != null) {
                    task.setRegistrationName(row.getCell(7).getStringCellValue());
                }
                if (row.getCell(8) != null) {
                    task.setRegistrationCode(row.getCell(8).getStringCellValue());
                }
                if (row.getCell(9) != null) {
                    task.setSpecifications(row.getCell(9).getStringCellValue());
                }
                if (row.getCell(10) != null) {
                    task.setPackNumber((int) row.getCell(10).getNumericCellValue());
                }
                if (row.getCell(11) != null) {
                    DictItem dictItem = new DictItem();
                    dictItem.setName(row.getCell(11).getStringCellValue());
                    dictItem.setValue(dictTypeService.getValue(row.getCell(11).getStringCellValue(),
                            "1004406758192578593"));
                    task.setMinUnit(dictItem);
                }
                if (row.getCell(12) != null) {
                    DictItem dictItem = new DictItem();
                    dictItem.setName(row.getCell(12).getStringCellValue());
                    dictItem.setValue(dictTypeService.getValue(row.getCell(12).getStringCellValue(),
                            "1004406758192578597"));
                    task.setPackUnit(dictItem);
                }
                if (row.getCell(13) != null) {
                    task.setIsOutSell(row.getCell(13).getStringCellValue().equals("是") ? "1" : "0");
                }
                if (row.getCell(14) != null) {
                    double numericCellValue = row.getCell(14).getNumericCellValue();
                    BigDecimal price = new BigDecimal(numericCellValue);
                    task.setPriceOutSell(price);
                }
                if (row.getCell(15) != null) {
                    task.setIsUnpackSell(row.getCell(15).getStringCellValue().equals("是") ? "1" : "0");
                }
                if (row.getCell(16) != null) {
                    double numericCellValue = row.getCell(16).getNumericCellValue();
                    BigDecimal retailPrice = new BigDecimal(numericCellValue);
                    task.setRetailPrice(retailPrice);
                }
                if (row.getCell(17) != null) {
                    task.setIsUnpackSell(row.getCell(17).getStringCellValue().equals("是") ? "1" : "0");
                }
                if (row.getCell(18) != null) {
                    double numericCellValue = row.getCell(18).getNumericCellValue();
                    BigDecimal retailPrice = new BigDecimal(numericCellValue);
                    task.setRetailPrice(retailPrice);
                }
                if (row.getCell(19) != null) {
                    task.setStatus(row.getCell(19).getStringCellValue().equals("是") ? "1" : "0");
                }
                task.setCompany(company);
                this.save(task).getId();
                chengGong += 1;
            } catch (Exception e) {
                String a = "";
                if (e.getMessage()!=null) {
                    a = e.getMessage().equals("材料信息重复") ? "材料信息重复," : "";
                }
                siBai += 1;
                mistake += "表格第" + num + "行[" + stuffName + "]材料信息异常，"+a+"请核对后重新导入\n";
            }
        }
        /*if (mistake != null) {
            List<String> error = new ArrayList<>();
            error.add(mistake);
            error.add(String.valueOf(chengGong));
            error.add(String.valueOf(siBai));
            throw new Exception((Throwable) error);
        }*/
        error.add(0,String.valueOf(chengGong));
        error.add(1,String.valueOf(siBai));
        error.add(2,mistake);
        return error;
    }

    // 材料新增查重
    public int repeat(Stuff stuff) {
        String companyId = SessionUtils.getUserDto().getCompanyId();
        Company company = companyService.get(companyId);
        String institution = companyService.getInstitution(companyId);
        company.getParent().setId(institution);
        stuff.setCompany(company);
        return this.dao.repeat(stuff);
    }


    public Page<Stuff> listByCompany(List<Parameter> params, int offset, int limit, String orderby) {

        Optional<Parameter> cartOptional = params.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        params.remove(0);
        String id = (String) cartOptional.get().getValue();
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby, id);
        int total = dao.count(pageRequest);
        List<Stuff> list = null;
        if (total > 0) {
            list = dao.listPages(pageRequest);
        }
        return new Page<>(total, list);
    }

    public Page<Stuff> listByInstitution(List<Parameter> params, int offset, int limit, String orderby) {
        Optional<Parameter> cartOptional = params.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        params.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby, id, institution);
        int total = dao.countByInstitution(pageRequest);
        List<Stuff> list = null;
        if (total > 0) {
            list = dao.listByInstitution(pageRequest);
        }
        return new Page<>(total, list);
    }

    @Transactional(readOnly = false)
    public List<String> syncToClinic(List<Stuff> entitys) {
        SessionUserDto userDto = SessionUtils.getUserDto();
        SessionUserDto.CompanyDTO companyDto = userDto.getCompany();
        Company company = BeanUtil.copyProperties(companyDto, Company.class);

        List<String> list = new ArrayList<>();

        entitys.forEach(stuff -> {
            stuff.setCompany(company);
            String oldId = stuff.getId();
            int repeat = repeatInCompany(stuff);
            if (repeat == 0) {
                stuff.setId(null);
                stuff.setSyncId(oldId);
                String sn = sequenceService.generate(company.getId(), "stuff_code", stuff);
                stuff.setCode(sn);
                super.save(stuff);
                //保存动态库存
                this.medicinalStockControlService.initStock(stuff);
                list.add(stuff.getId());
            }

        });

        return list;
    }

    private int repeatInCompany(Stuff stuff) {
        String companyId = SessionUtils.getUserDto().getCompanyId();
        Company company = companyService.get(companyId);
        String institution = companyService.getInstitution(companyId);
        company.getParent().setId(institution);
        stuff.setCompany(company);
        int i = this.dao.repeatInCompany(stuff);
        return i;
    }


}