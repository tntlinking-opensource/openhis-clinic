package com.geeke.outpatient.controller;

import java.util.List;

import com.geeke.outpatient.entity.RecipeTemplateDTO;
import com.geeke.outpatient.entity.RecipeTemplateDetail;
import com.geeke.outpatient.entity.RecipeTemplateInfo;
import com.geeke.outpatient.service.RecipetemplateDetailService;
import com.geeke.outpatient.service.RecipetemplateInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.geeke.outpatient.entity.Recipetemplate;
import com.geeke.outpatient.service.RecipetemplateService;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;

/**
 * 模板处方Controller
 * @author rys
 * @version 2022-10-08
 */
@RestController
@RequestMapping(value = "/outpatient/recipetemplate")
public class RecipetemplateController extends BaseController {

	@Autowired
	private RecipetemplateService recipetemplateService;

	@Autowired
    private RecipetemplateInfoService recipetemplateInfoService;

	@Autowired
    private RecipetemplateDetailService recipetemplateDetailService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        Recipetemplate entity = recipetemplateService.getById(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    
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
        Page page = new Page(result.getTotal(), recipetemplates);
        return ResponseEntity.ok(ResultUtil.successJson(page));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<Recipetemplate> result = recipetemplateService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "allSave")
    public ResponseEntity<JSONObject> allSave(@RequestBody Recipetemplate entity) {
        recipetemplateService.allSave(entity);
//        String id = recipetemplateService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(""));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody Recipetemplate entity) {
        int rows = recipetemplateService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<Recipetemplate> entitys) {
        List<String> ids = recipetemplateService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<Recipetemplate> entitys) {
        List<String> ids = recipetemplateService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<Recipetemplate> entitys) {
        int rows = recipetemplateService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
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