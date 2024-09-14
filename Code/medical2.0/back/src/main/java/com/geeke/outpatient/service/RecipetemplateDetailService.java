package com.geeke.outpatient.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.service.CrudService;
import com.geeke.org.entity.Company;
import com.geeke.outpatient.dao.RecipeTemplateDetailDao;
import com.geeke.outpatient.dao.RecipetemplateDao;
import com.geeke.outpatient.entity.*;
import com.geeke.stock.entity.Drug;
import com.geeke.stock.entity.Stuff;
import com.geeke.stock.service.DrugService;
import com.geeke.stock.service.StuffService;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.service.CostItemService;
import com.geeke.utils.IdGen;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 模板处方Service
 * @author rys
 * @version 2022-10-08
 */
 
@Service
@Transactional(readOnly = false)
public class RecipetemplateDetailService extends CrudService<RecipeTemplateDetailDao, RecipeTemplateDetail> {
    protected String createBy;
    protected Date createDate;
    protected String updateBy;
    protected Date updateDate;
    protected String delFlag;
    private String code="MB000000001";

    @Autowired
    private RecipeTemplateDetailDao recipeTemplateDetailDao;

    @Autowired
    private DrugService drugService;

    @Autowired
    private StuffService stuffService;

    @Autowired
    private CostItemService costItemService;


    public void allSave(List<RecipeTemplateDetail> recipeTemplateDetails){
        for (RecipeTemplateDetail recipeTemplateDetail:
        recipeTemplateDetails) {
            recipeTemplateDetail.preInsert();
            recipeTemplateDetail.setId(IdGen.uuid());
            recipeTemplateDetailDao.allSave(recipeTemplateDetail);
        }

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

    public List<RecipeTemplateDetail> getByRecipeTemplateInfoId(String recipeTemplateInfoId) {
        List<RecipeTemplateDetail> recipeTemplateDetails=this.dao.getByRecipeTemplateInfoId(recipeTemplateInfoId);

        //获取到处方详情后根据处方详情获取到药品材料
        if(!CollectionUtils.isEmpty(recipeTemplateDetails)){
            for (RecipeTemplateDetail recipeTemplateDetail:
            recipeTemplateDetails) {
                DrugStuffEvt drugStuffEvt = this.getDrugStuffEvt(recipeTemplateDetail);
                recipeTemplateDetail.setDrugStuffId(drugStuffEvt);
            }
        }
        return recipeTemplateDetails;
    }

    public DrugStuffEvt getDrugStuffEvt(RecipeTemplateDetail recipeTemplateDetail) {
        DrugStuffEvt drugStuffEvt = new DrugStuffEvt();
        String stuffType = recipeTemplateDetail.getStuffType();        // 物料类型:0-西药 1-中药  2-输液 3-诊疗项目  4-材料
        if ("0".equals(stuffType) || "1".equals(stuffType) || "2".equals(stuffType)) {
            //中药、西药、输液处方查询药品信息
            Drug drug = this.drugService.get(recipeTemplateDetail.getDrugStuffId().getDrugStuffId());
            drugStuffEvt.setDrugStuffId(drug.getId());
            drugStuffEvt.setName(drug.getGoodsName());
            drugStuffEvt.setPrice(drug.getPrice());
            drugStuffEvt.setRetailPrice(drug.getRetailPrice());
            drugStuffEvt.setIsUnpackSell(drug.getIsUnpackSell());
            drugStuffEvt.setDosisUnit(drug.getDosisUnit());
            drugStuffEvt.setPreparationUnit(drug.getPreparationUnit());
            drugStuffEvt.setPack(drug.getPack());
            drugStuffEvt.setDrug(drug);
        } else if ("3".equals(stuffType)) {
            //查询诊疗项目信息
            CostItem costItem = this.costItemService.get(recipeTemplateDetail.getDrugStuffId().getDrugStuffId());
            drugStuffEvt.setDrugStuffId(costItem.getId());
            drugStuffEvt.setName(costItem.getItemName());
            drugStuffEvt.setPrice(costItem.getSalePrice());

            drugStuffEvt.setRetailPrice(null);
            drugStuffEvt.setIsUnpackSell("0");
            drugStuffEvt.setDosisUnit(costItem.getUnit());
            drugStuffEvt.setPack(costItem.getUnit());
            drugStuffEvt.setCostItem(costItem);
        } else if ("4".equals(stuffType)) {
            Stuff stuff = this.stuffService.get(recipeTemplateDetail.getDrugStuffId().getDrugStuffId());
            drugStuffEvt.setDrugStuffId(stuff.getId());
            drugStuffEvt.setName(stuff.getName());
            drugStuffEvt.setPrice(stuff.getPriceOutSell());
            drugStuffEvt.setRetailPrice(stuff.getRetailPrice());
            drugStuffEvt.setIsUnpackSell(stuff.getIsUnpackSell());
            drugStuffEvt.setDosisUnit(stuff.getMinUnit());
            drugStuffEvt.setPreparationUnit(stuff.getMinUnit());
            drugStuffEvt.setPack(stuff.getPackUnit());
            drugStuffEvt.setStuff(stuff);
        }

        return drugStuffEvt;
    }

    @Transactional
    public void updateDetail(List<RecipeTemplateDetail> recipeTemplateDetails, Company company) {
        RecipeTemplateInfo recipeTemplateInfoId=recipeTemplateDetails.get(0).getRecipeTemplateInfo();

        //详情的修改操作是先进行删除操作在进行添加操作
        int i=this.dao.deleteTemplateDetail(recipeTemplateInfoId.getId());

        if(!CollectionUtils.isEmpty(recipeTemplateDetails)&&i>0){
            for (RecipeTemplateDetail recipeTemplateDetail : recipeTemplateDetails) {

                    recipeTemplateDetail.preInsert();
                    recipeTemplateDetail.setId(IdGen.uuid());
                    recipeTemplateDetail.setRecipeTemplateInfo(recipeTemplateInfoId);
                    recipeTemplateDetail.setCompany(company);
                    this.dao.allSave(recipeTemplateDetail);

            }
        }
    }
}