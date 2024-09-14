package com.geeke.treatment.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.entity.CostItemPackage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CostItemPackageDao extends CrudDao<CostItemPackage> {
    public List<CostItemPackage> getAll(String costItemPckId);
    public void deleteById(String costItemPkgId);
    public CostItemPackage findById(String id);
}
