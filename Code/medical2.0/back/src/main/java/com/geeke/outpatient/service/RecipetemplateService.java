package com.geeke.outpatient.service;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.outpatient.entity.RecipeTemplateDetail;
import com.geeke.outpatient.entity.RecipeTemplateInfo;
import com.geeke.outpatient.entity.Registration;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.service.DrugService;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.utils.IdGen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import com.geeke.outpatient.dao.RecipetemplateDao;
import com.geeke.outpatient.entity.Recipetemplate;
import com.geeke.utils.StringUtils;

/**
 * 模板处方Service
 * @author rys
 * @version 2022-10-08
 */
 
@Service("recipetemplateService")
@Transactional(readOnly = false)
public class RecipetemplateService extends CrudService<RecipetemplateDao, Recipetemplate>{
    protected String createBy;
    protected Date createDate;
    protected String updateBy;
    protected Date updateDate;
    protected String delFlag;
    private String code="MB000000001";

    @Autowired
    private RecipetemplateInfoService recipetemplateInfoService;
    @Autowired
    private RecipetemplateDetailService recipetemplateDetailService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private DrugService drugService;

    public void allSave(Recipetemplate recipetemplate){
        if(recipetemplate.getId()=="" || recipetemplate.getId()==null){
            this.saveTemplate(recipetemplate);
        }else {
            this.updateTemplate(recipetemplate);
        }
    }
    @Transactional(readOnly = false)
    private void updateTemplate(Recipetemplate recipetemplate) {
        JSONObject userObj = SessionUtils.getUserJson();
        if (userObj != null && StringUtils.isNotBlank(userObj.getString("id"))) {
            recipetemplate.setUpdateBy(userObj.getString("name"));
        }
        recipetemplate.setUpdateDate(new Date());
        int i = this.dao.updateById(recipetemplate);
        if(i>0){
            //模板处方信息修改
            recipetemplateInfoService.updateInfo(recipetemplate.getRecipeTemplateInfo());
            //模板处方详情修改
            recipetemplateDetailService.updateDetail(recipetemplate.getRecipeTemplateDetail(),recipetemplate.getCompany());
        }
    }

    @Transactional(readOnly = false)
    public void saveTemplate(Recipetemplate recipetemplate){
        //保存模板主数据
        recipetemplate.preInsert();
        recipetemplate.setId(IdGen.uuid());
        //自动生成模板编码
        String code = this.dao.listOne(recipetemplate.getCompany().getId());
        if(code!=null){
            String substring = code.substring(2);
            int i = Integer.parseInt(substring);
            String newCode=(i+1)+"";
            int capacity=newCode.length();
            for (int j = 0; j < 9-capacity; j++) {
                newCode="0"+newCode;
            }
            recipetemplate.setCode("MB"+newCode);
        }else {
            recipetemplate.setCode(this.code);
        }
        this.dao.allSave(recipetemplate);

        //保存模板的处方信息
        RecipeTemplateInfo recipeTemplateInfo=null;
        if(recipetemplate.getRecipeTemplateInfo()!=null){
             recipeTemplateInfo = recipetemplate.getRecipeTemplateInfo();
            recipeTemplateInfo.setRecipetemplate(recipetemplate);
            recipeTemplateInfo.setCompany(recipetemplate.getCompany());
            recipetemplateInfoService.allSave(recipeTemplateInfo);
        }
        //保存模板的处方详情
        List<RecipeTemplateDetail> recipeTemplateDetails = recipetemplate.getRecipeTemplateDetail();
        for (RecipeTemplateDetail recipeTemplateDetail:
        recipeTemplateDetails) {
            recipeTemplateDetail.setRecipeTemplateInfo(recipeTemplateInfo);
            recipeTemplateDetail.setCompany(recipetemplate.getCompany());
        }
        recipetemplateDetailService.allSave(recipeTemplateDetails);
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

    //根据id获取模板处方
    public Recipetemplate getById(String id){
        //获取主表信息
        Recipetemplate recipetemplate=this.dao.getById(id);

        //获取处方信息
        RecipeTemplateInfo recipeTemplateInfo=recipetemplateInfoService.getByRecipeTemplateId(recipetemplate.getId());

        //获取处方详情
        List<RecipeTemplateDetail> recipeTemplateDetails=recipetemplateDetailService.getByRecipeTemplateInfoId(recipeTemplateInfo.getId());

        recipetemplate.setRecipeTemplateInfo(recipeTemplateInfo);
        recipetemplate.setRecipeTemplateDetail(recipeTemplateDetails);
        return recipetemplate;
    }
    public String aiSave(Recipetemplate recipetemplate){
        Registration medicalRecord = registrationService.get(recipetemplate.getRegisterId());
        recipetemplate.setPatientId(medicalRecord.getPatientId().getId());
        recipetemplate.setCompany(medicalRecord.getCompany());
        String a = "";
        for (RecipeTemplateDetail r :recipetemplate.getRecipeTemplateDetail()){
            String id = drugService.getId(r.getDrugStuffId().getName(),r.getCompany().getId());
            if (id != null){
                r.getDrugStuffId().setDrugStuffId(id);
            }else {
                a += r.getDrugStuffId().getName()+ "药品无库存信息\n";
            }
        }
        if (a != "") {
            return a;
        }
        if(recipetemplate.getId()=="" || recipetemplate.getId()==null){
            this.saveTemplate(recipetemplate);
        }else {
            this.updateTemplate(recipetemplate);
        }
        a = "处方信息添加成功";
        return a;
    }

    public List<Recipetemplate> listAi(String id) {
        return dao.listAi(id);
    }
}