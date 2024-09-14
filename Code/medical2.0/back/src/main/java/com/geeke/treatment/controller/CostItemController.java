package com.geeke.treatment.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.stock.entity.MedicalProject;
import com.geeke.sys.controller.BaseController;
import com.geeke.treatment.entity.CostItem;
import com.geeke.treatment.entity.CostItemDTO;
import com.geeke.treatment.entity.CostItemPackage;
import com.geeke.treatment.service.CostItemService;
import com.geeke.treatment.service.impl.CostItemPackageService;
import com.geeke.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 诊疗项目Controller
 * @author txl
 * @version 2022-05-30
 */
@RestController
@RequestMapping(value = "/treatment/costItem")
public class CostItemController extends BaseController {

	@Autowired
	private CostItemService costItemService;
	@Autowired
    private CostItemPackageService costItemPackageService;
	
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        //根据诊疗项目的id获取到对应的诊疗项目信息
        CostItem entity = costItemService.get(id);
        //通过诊疗项目的id获取到对应的子项目的信息
        List<CostItemPackage> costItemPackages = costItemPackageService.getAll(id);
        entity.setCostItemPackages(costItemPackages);
        //根据子项目的项目id获取其对应的项目信息
        List<CostItem> costItems = new ArrayList<>();
        for (CostItemPackage costItemPackage : costItemPackages) {
            CostItem costItem = costItemService.get(costItemPackage.getCostItemId());
            if(costItem!=null){
                List<CostItemPackage> all = costItemPackageService.getAll(costItem.getId());
                costItem.setCostItemPackages(all);
            }
            costItems.add(costItem);
        }


        CostItemDTO costItemDTO = new CostItemDTO();
        costItemDTO.setCostItem(entity);
        costItemDTO.setCostItemPackage(costItemPackages);
        costItemDTO.setCostItems(costItems);
        return ResponseEntity.ok(ResultUtil.successJson(costItemDTO));
    }
    
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<CostItem> result = costItemService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = {"listForMedical", ""})
    public ResponseEntity<JSONObject> listForMedical(@RequestBody SearchParams searchParams) {
        List<MedicalProject> result = costItemService.listPageForMedical(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<CostItem> result = costItemService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody CostItemDTO costItemDTO) {
        System.out.println(costItemDTO);
        if(costItemDTO.getCostItemPackage().size()!=0&&costItemDTO.getCostItemPackage().get(0).getReferenceUnit()==null){
            costItemDTO.getCostItem().setIsPackage("1");
        }else {
            costItemDTO.getCostItem().setIsPackage("0");
        }
       String id = costItemService.save(costItemDTO.getCostItem()).getId();

        //先将子项目去数据库查看是否存在
        List<CostItemPackage> costItemPackages = costItemPackageService.findById(costItemDTO.getCostItemPackage());
        if(costItemPackages.size()!=0&&costItemDTO.getCostItemPackage().get(0).getReferenceUnit()==null){
            for (int i = 0; i < costItemDTO.getCostItemPackage().size(); i++) {
                for (int j = 0; j < costItemPackages.size(); j++) {
                    if(costItemDTO.getCostItemPackage().get(i).getCostItemId()!=null ){
                        if(costItemDTO.getCostItemPackage().get(i).getCostItemId().equals(costItemPackages.get(j).getCostItemId())){
                            costItemDTO.getCostItemPackage().get(i).setId(costItemPackages.get(j).getCostItemId());
                            System.out.println(costItemDTO.getCostItemPackage().get(i).getId());
                        }
                    }
                }
            }
            costItemPackageService.deleteById(costItemPackages);

        }
        costItemPackageService.save(costItemDTO.getCostItemPackage(),id);


        return ResponseEntity.ok(ResultUtil.successJson(id));

    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody CostItem entity) {
        int rows = costItemService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<CostItem> entitys) {
        List<String> ids = costItemService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<CostItem> entitys) {
        List<String> ids = costItemService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<CostItem> entitys) {
        int rows = costItemService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }


    /**
     * 查询租户的诊疗项目
     *
     * @param searchParams
     * @return
     */
    @PostMapping(value = "listByInstitution")
    public ResponseEntity<JSONObject> listByInstitution(@RequestBody SearchParams searchParams) {
        Page<CostItem> result = costItemService.listByInstitution(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 租户治疗同步至诊所
     *
     * @param entitys
     * @return
     */
    @PostMapping(value = "syncToClinic")
    public ResponseEntity<JSONObject> syncToClinic(@RequestBody List<CostItem> entitys) {
        List<String> ids = costItemService.syncToClinic(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
}