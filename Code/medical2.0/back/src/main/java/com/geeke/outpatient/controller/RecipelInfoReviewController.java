package com.geeke.outpatient.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.outpatient.entity.RecipelInfoReview;
import com.geeke.outpatient.service.RecipelInfoReviewService;
import com.geeke.outpatient.vo.PrescriptionStatisticsVO;
import com.geeke.outpatient.vo.ReviewVO;
import com.geeke.outpatient.vo.StatementVO;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * 处方信息审查Controller
 * @author lc
 */
@RestController
@RequestMapping(value = "/outpatient/review")
public class RecipelInfoReviewController extends BaseController {

    @Resource
    private RecipelInfoReviewService recipelInfoReviewService;

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        RecipelInfoReview entity = recipelInfoReviewService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
    @GetMapping("/recipelInfo/{recipelInfoId}")
    public ResponseEntity<JSONObject> getByRecipelInfoId(@PathVariable("recipelInfoId") String recipelInfoId) {
        RecipelInfoReview entity = recipelInfoReviewService.getByRecipelInfoId(recipelInfoId);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<RecipelInfoReview> result = recipelInfoReviewService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<RecipelInfoReview> result = recipelInfoReviewService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody RecipelInfoReview entity) {
    	String id = recipelInfoReviewService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "save/list")
    public ResponseEntity<JSONObject> saveList(@RequestBody List<RecipelInfoReview> entitys) {
        if (CollectionUtil.isNotEmpty(entitys)) {
            entitys.forEach(recipelInfoReviewService::save);
        }
        return ResponseEntity.ok(ResultUtil.successJson("操作成功"));
    }


    @DeleteMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody RecipelInfoReview entity) {
        int rows = recipelInfoReviewService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @GetMapping("/form/{recipelInfoId}")
    public ResponseEntity<JSONObject> reviewForm(@PathVariable("recipelInfoId") String recipelInfoId) {
        ReviewVO vo = recipelInfoReviewService.reviewForm(recipelInfoId);
        return ResponseEntity.ok(ResultUtil.successJson(vo));
    }

    @PostMapping(value = {"list/statement", ""})
    public ResponseEntity<JSONObject> listPageStatement(@RequestBody SearchParams searchParams) {
        Page<StatementVO> result = recipelInfoReviewService.listPageStatement(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @GetMapping("/prescription/statistics/{id}")
    public ResponseEntity<JSONObject> prescriptionStatistics(@PathVariable("id") String id,
                                                             @RequestParam String startTime,
                                                             @RequestParam String endTime) {
        PrescriptionStatisticsVO entity = recipelInfoReviewService.prescriptionStatistics(id,startTime,endTime);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }
}