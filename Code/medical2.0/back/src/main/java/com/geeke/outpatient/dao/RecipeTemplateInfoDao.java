package com.geeke.outpatient.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.outpatient.entity.RecipeTemplateDetail;
import com.geeke.outpatient.entity.RecipeTemplateInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecipeTemplateInfoDao extends CrudDao<RecipeTemplateInfo> {
    void allSave(RecipeTemplateInfo recipeTemplateInfo);

    RecipeTemplateInfo getByRecipeTemplateId(String recipeTemplateId);

    void updateInfo(RecipeTemplateInfo recipeTemplateInfo);

}
