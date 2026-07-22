package com.geeke.treatment.service.impl;

import com.geeke.common.sequence.service.SequenceService;
import com.geeke.common.service.CrudService;
import com.geeke.treatment.dao.CostItemDao;
import com.geeke.treatment.dao.CostItemPackageDao;
import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.entity.CostItemPackage;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
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
        if(costItemPackages!=null){
            for (CostItemPackage costItemPackage : costItemPackages) {
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
