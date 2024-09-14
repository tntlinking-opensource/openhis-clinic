package com.geeke.treatment.service.impl;

import com.geeke.common.sequence.service.SequenceService;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.treatment.dao.CostItemDao;
import com.geeke.treatment.dao.CostItemPackageDao;
import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.entity.CostItemPackage;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CostItemPackageService extends CrudService<CostItemPackageDao, CostItemPackage> {
    @Autowired
    SequenceService sequenceService;

    @Autowired
    private CostItemPackageDao costItemPackageDao;

    @Transactional(readOnly = false)
    public void save(List<CostItemPackage> costItemPackages, String costId) {
        // 新增时, 处理自动编号字段
//        if (StringUtils.isBlank(costItem.getId())){
//            String sn = sequenceService.generate(SessionUtils.getLoginTenantId(), "treatment_item_code", costItem);
//            costItem.setItemCode(sn);
//        }
//        String id = this.dao.GetIDByName(costItem.getItemName(),SessionUtils.getLoginTenantId());
//        if(null!=id && !id.equals(costItem.getId()))
//        {
//            throw new CommonJsonException(ResultUtil.warningJson(ErrorEnum.E_50001, "已经存在一个相同的项目名，请重新输入！"));
//        }
        for (CostItemPackage costItemPackage : costItemPackages) {
            String id = costItemPackage.getId();
            costItemPackage.setCostItemId(id);
            costItemPackage.setCostItemPkgId(costId);
            costItemPackage.setId(null);
            CostItemPackage CostItemPackageTemp = super.save(costItemPackage);
        }

    }

    @Transactional(readOnly = false)
    public List<CostItemPackage> getAll(String costItemPckId){
        List<CostItemPackage> all = costItemPackageDao.getAll(costItemPckId);
        return all;
    }

    @Transactional(readOnly = false)
    public void deleteById(List<CostItemPackage> costItemPackages){
        System.out.println(costItemPackages);
        if(costItemPackages!=null){
            for (CostItemPackage costItemPackage : costItemPackages) {
                System.out.println(costItemPackage);
                costItemPackageDao.deleteById(costItemPackage.getCostItemPkgId());
            }
        }
    }

    @Transactional(readOnly = false)
    public List<CostItemPackage> findById(List<CostItemPackage> costItemPackages){
        List<CostItemPackage> costItemPackages1 = new ArrayList<>();
        for (CostItemPackage costItemPackage : costItemPackages) {
            CostItemPackage costItemPackageDaoById = costItemPackageDao.findById(costItemPackage.getId());
            if(costItemPackageDaoById!=null){
                costItemPackages1.add(costItemPackageDaoById);
            }
        }
        return costItemPackages1;
    }

}
