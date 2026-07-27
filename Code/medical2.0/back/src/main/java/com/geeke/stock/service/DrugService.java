package com.geeke.stock.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.geeke.common.constants.BizConstants;
import com.geeke.basicdata.dao.ManufactureFactoryDao;
import com.geeke.basicdata.entity.ManufactureFactory;
import com.geeke.basicdata.service.ManufactureFactoryService;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.sequence.service.SequenceService;
import com.geeke.common.service.CrudService;
import com.geeke.common.service.ServiceException;
import com.geeke.org.entity.Company;
import com.geeke.org.service.CompanyService;
import com.geeke.stock.dao.DrugDao;
import com.geeke.stock.entity.Drug;
import com.geeke.sys.entity.DictItem;
import com.geeke.sys.service.DictTypeService;
import com.geeke.sys.utils.SessionUserDto;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import com.geeke.utils.excel.ExcelImportBuilder;
import com.geeke.utils.excel.ExcelImportResult;
import com.geeke.utils.excel.ImportError;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 药品信息Service
 *
 * @author txl
 * @version 2022-06-07
 */

@Service("drugService")
@Transactional(readOnly = true)
public class DrugService extends CrudService<DrugDao, Drug>  {
    private static final Logger logger = LoggerFactory.getLogger(DrugService.class);

    /** 药品为机构共享字典：允许同机构下诊所访问机构诊所的数据（与列表 #{institution} SQL 一致） */
    @Override
    protected boolean isInstitutionShared() {
        return true;
    }

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
            throw new ServiceException("药品信息重复");

        }
        // 新增时, 处理自动编号字段
        if (StringUtils.isBlank(drug.getId())){
            String sn = sequenceService.generate(SessionUtils.getUser().getCompanyId(), "medical_code", drug);
            drug.setCode(sn);
            //添加药品gg字段
        }
        String ypgg = drug.getDosis() + drug.getDosisUnit().getName()+ "*" + drug.getPreparation() + drug.getPreparationUnit().getName()+"/" + drug.getPack().getName();
        drug.setYpgg(ypgg);
        Drug drugTemp = super.save(drug);
        this.medicinalStockControlService.initStock(drugTemp);
        return drugTemp;
    }


    public int addDurg(Drug drug) {
        return dao.insert(drug);
    }

    public Page<Drug> listPages(List<Parameter> parameters, int offset, int limit, String order) {
        PageRequest pageRequest = buildTenantPageRequest(parameters, offset, limit, order);
        return paginate(
            () -> dao.count(pageRequest),
            () -> drugDao.listPages(pageRequest)
        );
    }

    /**
     * 入库时查询自己诊所所有药品信息 2025.4.2
     * @param parameters
     * @param orderby
     * @return
     */
    public List<Drug> listAllDrug(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = new PageRequest(parameters, orderby);

        return dao.listAlls(pageRequest);
    }


    public List<Drug> listAlls(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = buildTenantPageRequest(parameters, orderby);
        return dao.listAlls(pageRequest);
    }

    public List<Drug> listAllStock(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = new PageRequest(parameters, orderby);
        return this.dao.listAllStock(pageRequest);
    }

    public List<Drug> listAllStock2(List<Parameter> parameters, String orderby) {
        PageRequest pageRequest = buildTenantPageRequest(parameters, orderby);
        return this.dao.listAllStock2(pageRequest);
    }

    public List<Drug> getAll(String companyId, String type) {
        List<Drug> drugs = this.dao.getAll(companyId, type);
        return drugs;
    }

    /**
     * 2025.4.2 @update
     * 获取本诊所下所有库存
     * @return
     */
    public Page<Drug> getDrugNew(List<Parameter> params, int offset, int limit, String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby,SessionUtils.getLoginTenantId());
        return paginate(
            () -> this.dao.count(pageRequest),
            () -> {
                List<Drug> list = this.dao.listPages(pageRequest);
                //获取到当前药品后去库存明细中获取其进价并算出其成本合计
                for (Drug drug : list) {
                    //根据动态库存获取成本合计
                    BigDecimal totalCost = medicinalStorageControlService.getByDrugOrStuffId(drug.getId());
                    if (!Objects.isNull(drug.getStock()) && !Objects.isNull(drug.getStock().getStorageStock())) {
                        BigDecimal inventory =
                                drug.getStock().getStorageStock().subtract(drug.getStock().getUsedStock().add(drug.getStock().getReimburseStock()));
                        drug.setInventory(inventory.intValue());
                    } else {
                        drug.setInventory(0);
                    }
                    if (!Objects.isNull(totalCost)) {
                        drug.setBid(totalCost);
                    } else {
                        drug.setBid(new BigDecimal("0"));
                    }
                }
                return list;
            }
        );
    }

    /**
     * 获取租户所有库存 诊所共用
     * @param params
     * @param offset
     * @param limit
     * @param orderby
     * @return
     */
    public Page<Drug> getDrug(List<Parameter> params, int offset, int limit, String orderby) {
        PageRequest pageRequest = buildTenantPageRequest(params, offset, limit, orderby);
        return paginate(
            () -> this.dao.count(pageRequest),
            () -> {
                List<Drug> list = this.dao.listPages(pageRequest);
                //获取到当前药品后去库存明细中获取其进价并算出其成本合计
                for (Drug drug : list) {
                    //根据动态库存获取成本合计
                    BigDecimal totalCost = medicinalStorageControlService.getByDrugOrStuffId(drug.getId());
                    if (!Objects.isNull(drug.getStock()) && !Objects.isNull(drug.getStock().getStorageStock())) {
                        BigDecimal inventory =
                                drug.getStock().getStorageStock().subtract(drug.getStock().getUsedStock().add(drug.getStock().getReimburseStock()));
                        drug.setInventory(inventory.intValue());
                    } else {
                        drug.setInventory(0);
                    }
                    if (!Objects.isNull(totalCost)) {
                        drug.setBid(totalCost);
                    } else {
                        drug.setBid(new BigDecimal("0"));
                    }
                }
                return list;
            }
        );
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
        PageRequest pageRequest = buildTenantPageRequest(parameters, offset, limit, orderby);
        return paginate(
            () -> this.dao.countInventory(pageRequest),
            () -> this.dao.getDrugInventoryWarning(pageRequest)
        );
    }

    public List<Drug> inventory(List<Parameter> parameters, String orderby) {
        // 移除前端 company_id（防止越权），统一以会话登录租户作为隔离边界
        Parameter.extractAndRemoveCompanyId(parameters);
        String id = SessionUtils.getLoginTenantId();
        if ("null".equals(id)) {
            id = null;
        }
        String institution = null;
        if (id != null) {
            Company company = companyService.get(id);
            institution = company.getParent().getId();
        }
        PageRequest pageRequest = new PageRequest(parameters, orderby, id, institution);
        return dao.inventory(pageRequest);
    }

    @Transactional(readOnly = false)
    public List<String> excel(MultipartFile file) throws Exception {
        String companyId = com.geeke.sys.utils.SessionUtils.getUserJson().getString("companyId");
        Company company = new Company();
        company.setId(companyId);

        // 判断文件格式
        String fileName = file.getOriginalFilename();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            throw new Exception("上传文件格式不正确");
        }

        int chengGong = 0;
        int siBai = 0;
        StringBuilder mistake = new StringBuilder();

        // Step 1: 使用 ExcelImportBuilder 读取并校验 Excel 数据
        ExcelImportResult<Drug> result;
        try (InputStream is = file.getInputStream()) {
            result = new ExcelImportBuilder<>(Drug.class, is)
                    .dictTypeService(dictTypeService)
                    .skipRows(2)
                    .read();
        }

        // Step 2: 重新打开工作簿，读取生产厂家名称（列6）和备注（列20）
        List<String> manufacturerNames = new ArrayList<>();
        List<String> remarksList = new ArrayList<>();
        try (InputStream is2 = file.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(is2)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();
            for (int i = 2; i < rows; i++) {
                XSSFRow row = sheet.getRow(i);
                String mfgName = null;
                if (row != null && row.getCell(6) != null) {
                    mfgName = row.getCell(6).getStringCellValue();
                }
                manufacturerNames.add(mfgName);

                String remark = null;
                if (row != null && row.getCell(20) != null) {
                    remark = row.getCell(20).getStringCellValue();
                }
                remarksList.add(remark);
            }
        }

        // Step 3: 处理校验失败的行（ExcelImportBuilder 发现的字段级错误）
        java.util.Map<Integer, java.util.List<ImportError>> errorsByRow = new java.util.LinkedHashMap<>();
        for (ImportError err : result.getErrors()) {
            errorsByRow.computeIfAbsent(err.getRowNum(), k -> new java.util.ArrayList<>()).add(err);
        }
        for (java.util.Map.Entry<Integer, java.util.List<ImportError>> entry : errorsByRow.entrySet()) {
            siBai++;
            StringBuilder rowMsg = new StringBuilder();
            rowMsg.append("表格第").append(entry.getKey()).append("行");
            for (ImportError err : entry.getValue()) {
                if (err.getFieldName() != null) {
                    rowMsg.append("[").append(err.getFieldName()).append("]");
                }
            }
            rowMsg.append("数据校验失败：");
            for (ImportError err : entry.getValue()) {
                rowMsg.append(err.getMessage()).append("；");
            }
            rowMsg.append("请核对后重新导入\n");
            mistake.append(rowMsg);
        }

        // Step 4: 处理校验通过的行，逐行保存
        List<Drug> dataList = result.getDataList();
        for (int i = 0; i < dataList.size(); i++) {
            Drug drug = dataList.get(i);
            int rowNum = i + 3; // Excel 行号（跳过2行表头，行号从1开始）
            String drugName = drug.getGoodsName();
            try {
                // 设置诊所
                drug.setCompany(company);

                // 处理生产厂家（列6）：按名称查找，不存在则创建
                if (i < manufacturerNames.size() && StringUtils.isNotBlank(manufacturerNames.get(i))) {
                    String mfgName = manufacturerNames.get(i);
                    ManufactureFactory manufactureFactory = new ManufactureFactory();
                    String existingId = manufactureFactoryDao.findBy(mfgName, companyId);
                    if (existingId != null) {
                        manufactureFactory.setId(existingId);
                    } else {
                        manufactureFactory.setName(mfgName);
                        manufactureFactory.setType("1");
                        manufactureFactory.setCompany(company);
                        ManufactureFactory savedFactory = manufactureFactoryService.save(manufactureFactory);
                        manufactureFactory.setId(savedFactory.getId());
                    }
                    drug.setFactory(manufactureFactory);
                }

                // 处理备注（列20）
                if (i < remarksList.size() && remarksList.get(i) != null) {
                    drug.setRemarks(remarksList.get(i));
                }

                // 处理是否拆零销售："是" -> "1", 其他 -> "0"
                if ("是".equals(drug.getIsUnpackSell())) {
                    drug.setIsUnpackSell("1");
                } else {
                    drug.setIsUnpackSell("0");
                }

                // 处理状态："是" -> "1", 其他 -> "0"
                if ("是".equals(drug.getStatus())) {
                    drug.setStatus("1");
                } else {
                    drug.setStatus("0");
                }

                this.save(drug).getId();
                chengGong++;
            } catch (Exception e) {
                logger.warn("导入第{}行失败", rowNum, e);
                String a = "";
                if (e.getMessage() != null) {
                    a = e.getMessage().equals("药品信息重复") ? "药品信息重复," : "";
                }
                siBai++;
                mistake.append("表格第").append(rowNum).append("行[").append(drugName).append("]药品信息异常，").append(a).append("请核对后重新导入\n");
            }
        }

        List<String> error = new ArrayList<>();
        error.add(0, String.valueOf(chengGong));
        error.add(1, String.valueOf(siBai));
        error.add(2, mistake.toString());
        return error;
    }

    // 药品新增查重
    public int repeat(Drug drug) {

        String companyId = com.geeke.sys.utils.SessionUtils.getUserJson().getString("companyId");
        Company company = companyService.get(companyId);
        drug.setCompany(company);
        List<Drug> drugList = this.dao.repeat(drug);

        //如果新增则为0，修改条数是1但是ID不一样则不重复
        if (drugList.size() == 1 && drugList.get(0).getId().equals(drug.getId())) {
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
        PageRequest pageRequest = new PageRequest(offset, limit, params, orderby,SessionUtils.getLoginTenantId());
        return paginate(
            () -> dao.count(pageRequest),
            () -> {
                List<Drug> list = dao.listPages(pageRequest);
                //获取到当前药品后去库存明细中获取其进价并算出其成本合计
                for (Drug drug : list) {
                    //根据动态库存获取成本合计
                    BigDecimal totalCost = medicinalStorageControlService.getByDrugOrStuffId(drug.getId());
                    if (!Objects.isNull(drug.getStock()) && !Objects.isNull(drug.getStock().getStorageStock())) {
                        BigDecimal inventory =
                                drug.getStock().getStorageStock().subtract(drug.getStock().getUsedStock().add(drug.getStock().getReimburseStock()));
                        drug.setInventory(inventory.intValue());
                    } else {
                        drug.setInventory(0);
                    }
                    if (!Objects.isNull(totalCost)) {
                        drug.setBid(totalCost);
                    } else {
                        drug.setBid(new BigDecimal("0"));
                    }
                }
                return list;
            }
        );
    }

    public Page<Drug> listByInstitution(List<Parameter> params, int offset, int limit, String orderby) {
        PageRequest pageRequest = buildTenantPageRequest(params, offset, limit, orderby);
        return paginate(
            () -> dao.countByInstitution(pageRequest),
            () -> dao.listByInstitution(pageRequest)
        );
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


    /**
     * 根据名称和零售价查询药品导入
     * @param name
     * @param retailPrice
     * @return
     */
    public Drug getByNameAndPrice(String name, BigDecimal retailPrice) {
        return  drugDao.getByNameAndPrice(name,retailPrice, SessionUtils.getUserJson().getString("companyId"));
    }
}