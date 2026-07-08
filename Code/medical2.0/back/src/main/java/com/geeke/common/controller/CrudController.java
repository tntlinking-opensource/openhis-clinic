package com.geeke.common.controller;

import java.util.List;

import com.geeke.utils.constants.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.data.Page;
import com.geeke.common.persistence.DataEntity;
import com.geeke.common.service.CrudService;
import com.geeke.sys.controller.BaseController;
import com.geeke.utils.ResultUtil;

/**
 * Controller基类 - 提供标准CRUD接口
 * @param <S> Service类型
 * @param <T> 实体类型
 */
public abstract class CrudController<S extends CrudService<?, T>, T extends DataEntity<T>> extends BaseController {

    /**
     * 子类必须声明此字段并添加 @Autowired 注解，以明确指定要注入的 Service。
     * 例如：@Autowired protected SupplierService service;
     * 不要在基类使用 @Autowired，否则多个 CrudService 子类会导致歧义。
     */
    protected S service;

    /**
     * 获取Service实例 - 子类可以通过覆盖此方法来提供自己的Service
     */
    protected S getService() {
        return service;
    }

    /**
     * 根据ID获取实体
     */
    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        T entity = getService().get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    /**
     * 分页查询
     */
    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<T> result = getService().listPage(searchParams.getParams(), searchParams.getOffset(),
                searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 查询全部数据
     */
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<T> result = getService().listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 保存前钩子 — 子类可覆盖以添加预处理逻辑（如文件处理、参数补充）
     * 返回true继续保存，返回false中止保存
     */
    protected boolean beforeSave(T entity) {
        return true;
    }

    /**
     * 保存后钩子 — 子类可覆盖以添加后置逻辑（如发送通知、同步数据）
     */
    protected void afterSave(T entity) {
    }

    /**
     * 删除前钩子 — 子类可覆盖以添加删除前校验
     * 返回true继续删除，返回false中止删除
     */
    protected boolean beforeDelete(T entity) {
        return true;
    }

    /**
     * 删除后钩子 — 子类可覆盖以添加后置逻辑
     */
    protected void afterDelete(T entity) {
    }

    /**
     * 保存实体（新增或更新）
     */
    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@Valid @RequestBody T entity) {
        if (!beforeSave(entity)) {
            return ResponseEntity.ok(ResultUtil.warningJson(ErrorEnum.E_50002, "保存前校验未通过"));
        }
        String id = getService().save(entity).getId();
        afterSave(entity);
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    /**
     * 删除实体
     */
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody T entity) {
        if (entity == null) {
            return ResponseEntity.ok(ResultUtil.errorJson(ErrorEnum.E_400));
        }
        if (!beforeDelete(entity)) {
            return ResponseEntity.ok(ResultUtil.warningJson(ErrorEnum.E_50002, "删除前校验未通过"));
        }
        int rows = getService().delete(entity);
        afterDelete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    /**
     * 批量新增
     */
    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<T> entitys) {
        List<String> ids = getService().bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    /**
     * 批量更新
     */
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<T> entitys) {
        List<String> ids = getService().bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }

    /**
     * 批量删除
     */
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<T> entitys) {
        int rows = getService().bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }
}
