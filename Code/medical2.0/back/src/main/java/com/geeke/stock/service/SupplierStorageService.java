package com.geeke.stock.service;

import cn.hutool.core.collection.ListUtil;
import com.geeke.common.data.Page;
import com.geeke.common.sequence.service.SequenceService;
import com.geeke.common.service.CrudService;
import com.geeke.stock.dao.SupplierStorageDao;
import com.geeke.stock.entity.SupplierStock;
import com.geeke.stock.entity.SupplierStorage;
import com.geeke.sys.entity.DictItem;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 入库单Service
 * @author txl
 * @version 2022-06-02
 */
 
@Service("supplierStorageService")
@Transactional(readOnly = true)
public class SupplierStorageService extends CrudService<SupplierStorageDao, SupplierStorage>{
    @Autowired
    SequenceService sequenceService;

    @Autowired
    private SupplierStockService supplierStockService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private StuffService stuffService;

    @Autowired
    private SupplierStorageDao supplierStorageDao;

    @Autowired
    private MedicinalStorageControlService medicinalStorageControlService;

    @Override
    @Transactional(readOnly = false)
    public SupplierStorage save(SupplierStorage supplierStorage) {
        // 新增时, 处理自动编号字段
        supplierStorage.setCreateDate(new Date());
        if (StringUtils.isBlank(supplierStorage.getId())){
//            String sn = sequenceService.generate(SessionUtils.getUser().getCompanyId(), "supplier_storage_cod", supplierStorage);
//            supplierStorage.setCode(sn);
            // 获取该诊所最后一次入库的单号
            String oldCode = supplierStorageDao.getCode(supplierStorage.getCompany().getId());
            if(oldCode==""||oldCode==null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String format = simpleDateFormat.format(new Date()).replace("-", "");
                String code=format+"0000001";
                supplierStorage.setCode(code);
            }else {
                String increment=Integer.parseInt(oldCode.substring(8))+1+"";
                String prefix="";
                for (int i = 0; i < 7-increment.length(); i++) {
                    prefix+="0";
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String format = simpleDateFormat.format(new Date()).replace("-", "");
                String code=format+prefix+increment;
                supplierStorage.setCode(code);
            }
        }
        SupplierStorage supplierStorageTemp = super.save(supplierStorage);
        return supplierStorageTemp;
    }

    @Transactional(readOnly = false)
    public void cancel(@RequestBody String id) {
        //作废之前先去判断该药品或者材料是否被使用
        SupplierStorage supplierStorage = this.get(id);
        // 入库单状态->作废
        DictItem dictItem = new DictItem();
        dictItem.setValue("supplierStorageExamineStatus_2");
        supplierStorage.setExamine(dictItem);
        supplierStorage.setExamineDate(new Date());
        this.save(supplierStorage);
        //作废后需要将对应的明细表作废掉
        List<SupplierStock> byStorageId = supplierStockService.getByStorageId(id);

        supplierStockService.cancellation(byStorageId);

        //调用动态库存控制
        this.medicinalStorageControlService.invalidStorageStock(supplierStorage);

        //TODO: 注意后面需要优化    2022-10-09  贺龙

        // 作废减库存
//        List parameters = new ArrayList<Parameter>();
//        parameters.add(new Parameter("supplier_storage_id", "=", id));
//        List<SupplierStock> list = supplierStockService.listAll(parameters, "");
//        if (null != list && !list.isEmpty()) {
//            for (SupplierStock stock : list) {
//                int a=0;
//                if(stock.getDrug().getId()!=null){
//                    //进行删减库存
//
//                    drugService.updateInventory(0-stock.getNumber(),stock.getDrug().getId());
//                }else if (stock.getStuff().getId()!=null){
//                    stuffService.updateInventory(0-stock.getNumber(),stock.getStuff().getId());
//                }
//                stock.setNumber(0);
//                supplierStockService.save(stock);
//            }
//        }
    }


    public Page<SupplierStorage> listByCode(String pinyinCode, String status, String startTime, String endTime, int limit, int offset) {

        String companyId = SessionUtils.getUserDto().getCompanyId();
        List<SupplierStorage> supplierStorages = supplierStorageDao.listByCode(companyId, pinyinCode, status, startTime, endTime);
        int total = supplierStorages.size();
        List<SupplierStorage> list = ListUtil.page(offset, limit, supplierStorages);

        return new Page<>(total, list);
    }
}