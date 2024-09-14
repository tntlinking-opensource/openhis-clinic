package com.geeke.outpatient.dao;

import com.geeke.common.persistence.CrudDao;
import com.geeke.outpatient.entity.RecipeTemplateDetail;
import com.geeke.outpatient.entity.Recipetemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecipeTemplateDetailDao extends CrudDao<RecipeTemplateDetail> {
    void allSave(RecipeTemplateDetail recipeTemplateDetail);

    List<RecipeTemplateDetail> getByRecipeTemplateInfoId(String recipeTemplateInfoId);

    void updateDetail(RecipeTemplateDetail recipeTemplateDetail);

    int deleteTemplateDetail(String recipeTemplateInfoId);

}
