package com.geeke.stock.service;

import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.persistence.DataEntity;
import com.geeke.common.service.CrudService;
import com.geeke.org.service.CompanyService;
import com.geeke.stock.dao.MedicinalStockControlDao;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.MedicinalStockControl;
import com.geeke.stock.entity.Stuff;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 药品/材料库存总控制Service
 * @author hl
 * @version 2022-09-26
 */

@Service("medicinalStockControlService")
@Transactional(readOnly = true)
public class MedicinalStockControlService extends CrudService<MedicinalStockControlDao, MedicinalStockControl>{
    @Autowired
    private MedicinalStockControlDao dao;
    @Autowired
    private DrugService drugService;
    @Autowired
    private StuffService stuffService;

    @Autowired
    private CompanyService companyService;

    public static final Boolean enable = Boolean.TRUE;

    /**
     * 新增药品或材料基础主数据时，初始化库存总控制记录数据
     * @param dataEntity
     */
    @Transactional(readOnly = false)
    public void initStock(DataEntity<?> dataEntity)
    {
        if (!MedicinalStockControlService.enable)
        {
            return;
        }
        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("drug_stuff_id", "=", dataEntity.getId()));
        List<MedicinalStockControl> list = super.listAll(parameters, "");
        MedicinalStockControl medicinalStockControl = CollectionUtils.isEmpty(list) ? new MedicinalStockControl() : list.get(0);

        if (dataEntity instanceof Drug)
        {
            Drug drug = drugService.get(dataEntity.getId());
            if (Objects.isNull(drug) || StringUtils.isBlank(drug.getId()))
            {
                throw new RuntimeException("初始化[药品库存]总控制时,未获取到药品基础维护信息！");
            }

            medicinalStockControl.setDrugStuffName(drug.getGoodsName());
            if (CollectionUtils.isEmpty(list))
            {
                medicinalStockControl.setCompany(drug.getCompany());
                medicinalStockControl.setDrugStuffId(drug.getId());
                medicinalStockControl.setType(1);
                medicinalStockControl.setStorageStock(BigDecimal.ZERO);
                medicinalStockControl.setUsedStock(BigDecimal.ZERO);
                medicinalStockControl.setOccupyStock(BigDecimal.ZERO);
                medicinalStockControl.setSurplusStock(BigDecimal.ZERO);
                medicinalStockControl.setReimburseStock(BigDecimal.ZERO);
            }
        }
        else if (dataEntity instanceof Stuff)
        {
            Stuff stuff = stuffService.get(dataEntity.getId());
            if (Objects.isNull(stuff) || StringUtils.isBlank(stuff.getId()))
            {
                throw new RuntimeException("初始化[材料库存]总控制时,未获取到材料基础维护信息！");
            }

            medicinalStockControl.setDrugStuffName(stuff.getName());
            if (CollectionUtils.isEmpty(list))
            {
                medicinalStockControl.setCompany(stuff.getCompany());
                medicinalStockControl.setDrugStuffId(stuff.getId());
                medicinalStockControl.setType(2);
                medicinalStockControl.setStorageStock(BigDecimal.ZERO);
                medicinalStockControl.setUsedStock(BigDecimal.ZERO);
                medicinalStockControl.setOccupyStock(BigDecimal.ZERO);
                medicinalStockControl.setSurplusStock(BigDecimal.ZERO);
                medicinalStockControl.setReimburseStock(BigDecimal.ZERO);
            }
        }

        super.save(medicinalStockControl);

        //throw new RuntimeException("111");
    }

    /**
     * 新增药品或材料库存时，初始化库存总控制记录数据
     * @param dataEntity
     */
    @Transactional(readOnly = false)
    public void initStockTo(DataEntity<?> dataEntity)
    {
        if (!MedicinalStockControlService.enable)
        {
            return;
        }
        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("drug_stuff_id", "=", dataEntity.getId()));
        List<MedicinalStockControl> list = super.listAll(parameters, "");
        for (MedicinalStockControl list1 : list){
            list1.getCompany().setId(dataEntity.getCompany().getId());
        }
        MedicinalStockControl medicinalStockControl = CollectionUtils.isEmpty(list) ? new MedicinalStockControl() : list.get(0);
        medicinalStockControl.setId(null);

        if (dataEntity instanceof Drug)
        {
            Drug drug = drugService.get(dataEntity.getId());
            if (Objects.isNull(drug) || StringUtils.isBlank(drug.getId()))
            {
                throw new RuntimeException("初始化[药品库存]总控制时,未获取到药品基础维护信息！");
            }

            medicinalStockControl.setDrugStuffName(drug.getGoodsName());
            if (CollectionUtils.isEmpty(list))
            {
                medicinalStockControl.setCompany(drug.getCompany());
                medicinalStockControl.setDrugStuffId(drug.getId());
                medicinalStockControl.setType(1);
                medicinalStockControl.setStorageStock(BigDecimal.ZERO);
                medicinalStockControl.setUsedStock(BigDecimal.ZERO);
                medicinalStockControl.setOccupyStock(BigDecimal.ZERO);
                medicinalStockControl.setSurplusStock(BigDecimal.ZERO);
                medicinalStockControl.setReimburseStock(BigDecimal.ZERO);
            }
        }
        else if (dataEntity instanceof Stuff)
        {
            Stuff stuff = stuffService.get(dataEntity.getId());
            if (Objects.isNull(stuff) || StringUtils.isBlank(stuff.getId()))
            {
                throw new RuntimeException("初始化[材料库存]总控制时,未获取到材料基础维护信息！");
            }

            medicinalStockControl.setDrugStuffName(stuff.getName());
            if (CollectionUtils.isEmpty(list))
            {
                medicinalStockControl.setCompany(stuff.getCompany());
                medicinalStockControl.setDrugStuffId(stuff.getId());
                medicinalStockControl.setType(2);
                medicinalStockControl.setStorageStock(BigDecimal.ZERO);
                medicinalStockControl.setUsedStock(BigDecimal.ZERO);
                medicinalStockControl.setOccupyStock(BigDecimal.ZERO);
                medicinalStockControl.setSurplusStock(BigDecimal.ZERO);
                medicinalStockControl.setReimburseStock(BigDecimal.ZERO);
            }
        }
        medicinalStockControl.setStorageStock(BigDecimal.valueOf(0));
        medicinalStockControl.setUsedStock(BigDecimal.valueOf(0));
        medicinalStockControl.setOccupyStock(BigDecimal.valueOf(0));
        medicinalStockControl.setSurplusStock(BigDecimal.valueOf(0));
        medicinalStockControl.setReimburseStock(BigDecimal.valueOf(0));
        super.save(medicinalStockControl);

        //throw new RuntimeException("111");
    }

    /**
     * 作废删除药品或材料主数据时，校验库存是否使用，如果使用则不允许作废删除主数据；如果未使用，则允许作废删除主数据，同时把库存控制数据打上作废删除标记
     * @param dataEntity
     */
    @Transactional(readOnly = false)
    public void delAllStock(DataEntity<?> dataEntity)
    {
        if (!MedicinalStockControlService.enable)
        {
            return;
        }



        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("drug_stuff_id", "=", dataEntity.getId()));
        List<MedicinalStockControl> list = super.listAll(parameters, "");


        if (CollectionUtils.isEmpty(list))
        {
            //TODO: 在启用动态库存控制功能时，之前的历史产生数据如何处理？？？   不管，还是手工补录数据到数据库
            return;
        }else {
            throw new RuntimeException("该药品存在库存不能删除！");
        }

//        MedicinalStockControl medicinalStockControl = list.get(0);
//        BigDecimal storageStock = medicinalStockControl.getStorageStock();    //入库总量
//        BigDecimal surplusStock = medicinalStockControl.getSurplusStock();    //剩余可用库存数
//        if (storageStock.compareTo(surplusStock) != 0)
//        {
//            throw new RuntimeException("动态库存控制已发生变化，不能作废删除基础配置数据！");
//        }
//
//        //总库存控制数据打上作废删除标记
//        super.delete(medicinalStockControl);
//
//        //TODO:是否需要把动态库存操作明细删除掉
    }

    @Transactional
    public List<MedicinalStockControl> getByDrugOrStuffId(String drugOrStuffId, String companyId) {
        return this.dao.getByDrugOrStuffId(drugOrStuffId,companyId);
    }

    public List<MedicinalStockControl> listAlls(List<Parameter> parameters, String orderby) {
        Optional<Parameter> cartOptional = parameters.stream().filter(item -> item.getColumnName().equals("`company_id`")).findFirst();
        parameters.remove(0);
        String id = (String) cartOptional.get().getValue();
        String institution = companyService.getInstitution(id);
        PageRequest pageRequest = new PageRequest(parameters, orderby, id, institution);

        return dao.listAlls(pageRequest);
    }
}