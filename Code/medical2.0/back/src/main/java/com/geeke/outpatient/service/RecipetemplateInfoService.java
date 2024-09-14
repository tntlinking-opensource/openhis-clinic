package com.geeke.outpatient.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.service.CrudService;
import com.geeke.outpatient.dao.RecipeTemplateInfoDao;
import com.geeke.outpatient.dao.RecipetemplateDao;
import com.geeke.outpatient.entity.RecipeTemplateDetail;
import com.geeke.outpatient.entity.RecipeTemplateInfo;
import com.geeke.outpatient.entity.Recipetemplate;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.utils.IdGen;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 模板处方Service
 * @author rys
 * @version 2022-10-08
 */
 
@Service
@Transactional(readOnly = false)
public class RecipetemplateInfoService extends CrudService<RecipeTemplateInfoDao, RecipeTemplateInfo>{
    protected String createBy;
    protected Date createDate;
    protected String updateBy;
    protected Date updateDate;
    protected String delFlag;

    @Autowired
    private RecipeTemplateInfoDao recipeTemplateInfoDao;


    public void allSave(RecipeTemplateInfo recipeTemplateInfo){
        recipeTemplateInfo.preInsert();
        recipeTemplateInfo.setId(IdGen.uuid());
        recipeTemplateInfoDao.allSave(recipeTemplateInfo);
    }

    public void preInsert() {
        JSONObject userObj = SessionUtils.getUserJson();
        if (userObj != null && StringUtils.isNotBlank(userObj.getString("id"))) {
            this.updateBy = userObj.getString("name") ;
            this.createBy = userObj.getString("name") ;
        }
        this.updateDate = new Date();
        this.createDate = this.updateDate;
    }

    public RecipeTemplateInfo getByRecipeTemplateId(String recipeTemplateId) {
        RecipeTemplateInfo recipeTemplateInfo=this.dao.getByRecipeTemplateId(recipeTemplateId);
        return recipeTemplateInfo;
    }

    public void updateInfo(RecipeTemplateInfo recipeTemplateInfo) {
        this.dao.updateInfo(recipeTemplateInfo);
    }
}