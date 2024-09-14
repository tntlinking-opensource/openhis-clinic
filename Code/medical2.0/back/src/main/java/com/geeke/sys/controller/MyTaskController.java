package com.geeke.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;

import com.geeke.cure.entity.InspectionCheckInfo;
import com.geeke.sys.entity.MyTask;
import com.geeke.sys.service.MyTaskService;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/myTask/myTask")
public class MyTaskController {

    @Autowired
    private MyTaskService myTaskService;

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        MyTask entity = myTaskService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @PostMapping("update")
    public ResponseEntity<JSONObject> update(@RequestBody MyTask myTask){
        MyTask myTask1 =myTaskService.update(myTask);
        return ResponseEntity.ok(ResultUtil.successJson(myTask1));
    }

    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<MyTask> result = myTaskService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<MyTask> result = myTaskService.listAll(searchParams.getParams(), searchParams.getOrderby());

        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestParam("entity") String strTask,
                                           @RequestParam("fileIdUploads") MultipartFile[] fileIdUploads,  // 文件: 用户图片
                                           @RequestParam(value = "deleteIds", required = false) String strDeleteIds) throws IOException {
        MyTask myTask = JSONObject.parseObject(strTask, MyTask.class);
        // MultipartFile[] multipartFiles = JSONObject.parseObject(fileIdUploads, MultipartFile[].class);
        String[] deleteIds = JSONObject.parseObject(strDeleteIds, String[].class);
        System.out.println(myTask);
        myTaskService.save(myTask, fileIdUploads, deleteIds);
        return ResponseEntity.ok(ResultUtil.successJson(myTask.getId()));
    }
//        @PostMapping(value = "delete")
//        public ResponseEntity<JSONObject> delete(@RequestBody ClinicOffice entity) {
//            int rows = myTaskService.delete(entity);
//            return ResponseEntity.ok(ResultUtil.successJson(rows));
//        }
//
//        @PostMapping(value = "bulkInsert")
//        public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<ClinicOffice> entitys) {
//            List<String> ids = myTaskService.bulkInsert(entitys);
//            return ResponseEntity.ok(ResultUtil.successJson(ids));
//        }
//
//        @PostMapping(value = "bulkUpdate")
//        public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<ClinicOffice> entitys) {
//            List<String> ids = myTaskService.bulkUpdate(entitys);
//            return ResponseEntity.ok(ResultUtil.successJson(ids));
//        }
//
//        @PostMapping(value = "bulkDelete")
//        public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<ClinicOffice> entitys) {
//            int rows = myTaskService.bulkDelete(entitys);
//            return ResponseEntity.ok(ResultUtil.successJson(rows));
//        }
//

}
