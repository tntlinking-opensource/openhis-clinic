package com.geeke.outpatient.controller;

import java.util.List;

import com.geeke.outpatient.entity.RecipeTemplateDetail;
import com.geeke.outpatient.entity.RecipeTemplateInfo;
import com.geeke.outpatient.service.RecipetemplateDetailService;
import com.geeke.outpatient.service.RecipetemplateInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.geeke.outpatient.entity.Recipetemplate;
import com.geeke.outpatient.service.RecipetemplateService;
import com.geeke.common.controller.CrudController;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.utils.ResultUtil;

/**
 * 模板处方Controller
 * @author rys
 * @version 2022-10-08
 */
@RestController
@RequestMapping(value = "/outpatient/recipetemplate")
public class RecipetemplateController extends CrudController<RecipetemplateService, Recipetemplate> {

	@Autowired
	protected RecipetemplateService recipetemplateService;

	@Autowired
    private RecipetemplateInfoService recipetemplateInfoService;

	@Autowired
    private RecipetemplateDetailService recipetemplateDetailService;

    @Override
    protected RecipetemplateService getService() {
        return recipetemplateService;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        Recipetemplate entity = recipetemplateService.getById(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @Override
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<Recipetemplate> result = recipetemplateService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        List<Recipetemplate> recipetemplates = result.getRows();
        if(!CollectionUtils.isEmpty(recipetemplates)){
            for (Recipetemplate recipetemplate : recipetemplates) {
                //获取处方信息
                RecipeTemplateInfo recipeTemplateInfo=recipetemplateInfoService.getByRecipeTemplateId(recipetemplate.getId());

                //获取处方详情
                List<RecipeTemplateDetail> recipeTemplateDetails=recipetemplateDetailService.getByRecipeTemplateInfoId(recipeTemplateInfo.getId());

                recipetemplate.setRecipeTemplateInfo(recipeTemplateInfo);
                recipetemplate.setRecipeTemplateDetail(recipeTemplateDetails);
            }
        }
        Page<Recipetemplate> page = new Page<>(result.getTotal(), recipetemplates);
        return ResponseEntity.ok(ResultUtil.successJson(page));
    }

    @PostMapping(value = "allSave")
    public ResponseEntity<JSONObject> allSave(@RequestBody Recipetemplate entity) {
        recipetemplateService.allSave(entity);
//        String id = recipetemplateService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(""));
    }

    // ai模板保存
    @PostMapping("aiSave")
    public ResponseEntity<JSONObject> aiSave(@RequestBody Recipetemplate entity) {
        final String s = recipetemplateService.aiSave(entity);
        return ResponseEntity.ok(ResultUtil.successJson(s));
    }

    // ai模板查询
    @PostMapping(value = "listAi")
    public ResponseEntity<JSONObject> listAi(@RequestBody String id) {
        List<Recipetemplate> result = recipetemplateService.listAi(id);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
}
