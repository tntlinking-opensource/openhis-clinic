package com.geeke.toll.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.common.data.Page;
import com.geeke.outpatient.entity.*;
import com.geeke.outpatient.service.MedicalRecordService;
import com.geeke.outpatient.service.RecipelInfoService;
import com.geeke.outpatient.service.RegistrationService;
import com.geeke.sys.controller.BaseController;
import com.geeke.toll.entity.*;
import com.geeke.toll.service.TollInfoService;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收费管理Controller
 * @author lc
 * @version 2022-06-22
 */
@RestController
@RequestMapping(value = "/toll/tollInfo")
public class TollInfoController extends BaseController {

	@Autowired
	private TollInfoService tollInfoService;
	@Autowired
    private RecipelInfoService recipelInfoService;
	@Autowired
    private RegistrationService registrationService;
	@Autowired
    private MedicalRecordService medicalRecordService;
	
    @GetMapping("/getTollInfoByRegistrationId/{registrationId}")
    public ResponseEntity<JSONObject> getTollInfoByRegistrationId(@PathVariable("registrationId") String registrationId) {
        TollInfo entity = tollInfoService.getTollInfoByRegistrationId(registrationId);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getById(@PathVariable("id") String id) {
        TollInfo entity = tollInfoService.get(id);
        return ResponseEntity.ok(ResultUtil.successJson(entity));
    }

    @PostMapping(value = {"list", ""})
    public ResponseEntity<JSONObject> listPage(@RequestBody SearchParams searchParams) {
        Page<TollInfo> result = tollInfoService.listPage(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }
    
    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<TollInfo> result = tollInfoService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestBody TollInfo entity) {
        if("amountStatus_2".equals(entity.getAmountStatus().getValue())){
            TollInfo tollInfoByRegistrationId = tollInfoService.getTollInfoByRegistrationId(entity.getRegistrationFeeId());
            tollInfoByRegistrationId.setReturnType(1);
            tollInfoService.save(tollInfoByRegistrationId);
            entity.setReturnType(1);
        }
        String id = tollInfoService.save(entity).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }
  
    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody TollInfo entity) {
        int rows = tollInfoService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "bulkInsert")
    public ResponseEntity<JSONObject> bulkInsert(@RequestBody List<TollInfo> entitys) {
        List<String> ids = tollInfoService.bulkInsert(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkUpdate")
    public ResponseEntity<JSONObject> bulkUpdate(@RequestBody List<TollInfo> entitys) {
        List<String> ids = tollInfoService.bulkUpdate(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(ids));
    }
    
    @PostMapping(value = "bulkDelete")
    public ResponseEntity<JSONObject> bulkDelete(@RequestBody List<TollInfo> entitys) {
        int rows = tollInfoService.bulkDelete(entitys);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    @PostMapping(value = "tollTotalForm")
    public ResponseEntity<JSONObject> tollTotalForm(@RequestBody SearchParams searchParams) {
        return tollInfoService.tollTotalForm(searchParams);
    }

    @PostMapping(value = "tollDetailForm")
    public ResponseEntity<JSONObject> tollDetailForm(@RequestBody SearchParams searchParams) {
        return tollInfoService.tollDetailForm(searchParams);
    }

    //根据缴费信息查询挂号信息
    @GetMapping("registration/{chargeStatus}")
    public ResponseEntity<JSONObject> listRegistrationPage(@PathVariable("chargeStatus") String chargeStatus) {
        List<MedicalRecord> medicalRecords = medicalRecordService.listMedicalRecordByChargeStatus(chargeStatus);
        List<Registration> registrations = medicalRecords.stream().map(m -> m.getRegistration()).collect(Collectors.toList());
        Page<Registration> result = new Page<>(registrations.size(),registrations);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    //根据缴费状态获取挂号信息下的处方
    @PostMapping(value = "medicalRecipelEvt/{chargeStatus}")
    public ResponseEntity<JSONObject> medicalRecipelEvt(@RequestBody Registration registration,
                                               @PathVariable("chargeStatus") String chargeStatus)
    {
        if(StringUtils.isNullOrEmpty(chargeStatus))
        {
            throw new RuntimeException("参数有误");
        }
        MedicalRecipelEvt medicalRecipelEvt = medicalRecordService.allQuery(registration,chargeStatus);
        return ResponseEntity.ok(ResultUtil.successJson(medicalRecipelEvt));
    }

    //收费接口 -- 增加type
    @PostMapping(value = "save/toll/{type}")
    public ResponseEntity<JSONObject> saveToll(@RequestBody @NotNull TollEvt tollEvt,
                                               @PathVariable("type") String type) {
        tollInfoService.saveToll(tollEvt,type);
        return ResponseEntity.ok(ResultUtil.successJson(""));
    }

    @PostMapping(value = "getCreateBy")
    public ResponseEntity<JSONObject> getCreateBy(@RequestBody SearchParams searchParams) {
        List<TollInfo> result = tollInfoService.getCreateBy(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "getWorkload")
    public ResponseEntity<JSONObject> getWorkload(@RequestBody SearchParams searchParams) {
        Page<WorkLoad> workload = tollInfoService.getWorkload(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(workload));
    }

    @PostMapping(value = "getDrugSales")
    public ResponseEntity<JSONObject> getDrugSales(@RequestBody SearchParams searchParams) {
        final Page<DrugSales> drugSales = tollInfoService.getDrugSales(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(drugSales));
    }

    @PostMapping(value = "getWorkLoadStat")
    public ResponseEntity<JSONObject> getWorkLoadStat(@RequestBody SearchParams searchParams){
        WorkLoadStat workLoadStat = tollInfoService.getWorkLoadStat(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(workLoadStat));
    }

    @PostMapping(value = "getDrugSalesStat")
    public ResponseEntity<JSONObject> getDrugSalesStat(@RequestBody SearchParams searchParams) {
        DrugSales drugSalesStat = tollInfoService.getDrugSalesStat(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(drugSalesStat));
    }

    /**
     * 药品进销存信息
     * @param ypjxcRc
     * @return
     */
    @PostMapping(value = "getypjxcmanagement")
    public ResponseEntity<JSONObject> getypjxcmanagement(@RequestBody YpjxcRc ypjxcRc){
        Page<Ypjxc> result=tollInfoService.getypjxcmanagement(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 获取药品进销存信息汇总数量和金额
     * @param ypjxcRc
     * @return
     */
    @PostMapping(value = "getypjxcmanagementsums")
    public ResponseEntity<JSONObject> getypjxcmanagementsums(@RequestBody YpjxcRc ypjxcRc){
        Page<Ypjxc> result=tollInfoService.getypjxcmanagementsums(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 获取耗材销售信息
     * @param stuffsalessummaryRc
     * @return
     */
    @PostMapping(value = "getStuffsalessummarylists")
    public ResponseEntity<JSONObject> getStuffsalessummarylists(@RequestBody StuffsalessummaryRc stuffsalessummaryRc){
        Page<Stuffsalessummary> result=tollInfoService.getStuffsalessummarylists(stuffsalessummaryRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 获取耗材销售信息汇总价格和数量
     * @param stuffsalessummaryRc
     * @return
     */
    @PostMapping(value = "getStuffsalessummarysumss")
    public ResponseEntity<JSONObject> getStuffsalessummarysumss(@RequestBody StuffsalessummaryRc stuffsalessummaryRc){

        Page<Stuffsalessummary> result=tollInfoService.getStuffsalessummarysumss(stuffsalessummaryRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }


    /**
     * 材料进销存信息
     * @param ypjxcRc
     * @return
     */
    @PostMapping(value = "getcljxcmanagement")
    public ResponseEntity<JSONObject> getcljxcmanagement(@RequestBody YpjxcRc ypjxcRc){
        Page<Ypjxc> result=tollInfoService.getcljxcmanagement(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 获取材料进销存信息汇总数量和金额
     * @param ypjxcRc
     * @return
     */
    @PostMapping(value = "getcljxcmanagementsums")
    public ResponseEntity<JSONObject> getcljxcmanagementsums(@RequestBody YpjxcRc ypjxcRc){
        Page<Ypjxc> result=tollInfoService.getcljxcmanagementsums(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 药品材料入库信息统计
     * @param ypjxcRc
     * @return
     */
    @PostMapping(value = "getypclrkcxlist")
    public ResponseEntity<JSONObject> getypclrkcxlist(@RequestBody YpjxcRc ypjxcRc){
        Page<Ypclrkcx> result=tollInfoService.getypclrkcxlist(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 药品材料入库信息汇总
     * @param ypjxcRc
     * @return
     */
    @PostMapping(value = "getypclrkcxsums")
    public ResponseEntity<JSONObject> getypclrkcxsums(@RequestBody YpjxcRc ypjxcRc){
        Page<Ypclrkcx> result=tollInfoService.getypclrkcxsums(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 报表导出
     * @param searchParams
     * @param response
     * @param request
     * @throws IOException
     */
    @PostMapping("exportExcel")
    public void exportExcel(@RequestBody SearchParams searchParams, HttpServletResponse response, HttpServletRequest request) throws IOException {
        tollInfoService.exportExcel(searchParams,response);
    }


    /**
     *
     * @param ypjxcRc
     * @param response
     * @param request
     */
    @PostMapping("exportDrugOrStuffStock")
    public void exportDrugOrStuffStock(@RequestBody YpjxcRc ypjxcRc,HttpServletResponse response,HttpServletRequest request) throws IOException {
        tollInfoService.exportDrugOrStuffStock(ypjxcRc,response);
    }


    /**
     * 机构管理-药品进销存信息
     * @param ypjxcRc
     * @return
     */
    @PostMapping(value = "getpharmaceuticalInventoryManagement")
    public ResponseEntity<JSONObject> getpharmaceuticalInventoryManagement(@RequestBody YpjxcRc ypjxcRc){
        Page<Ypjxc> result=tollInfoService.getpharmaceuticalInventoryManagement(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 机构管理-获取药品进销存信息汇总数量和金额
     * @param ypjxcRc
     * @return
     */
    @PostMapping(value = "getpharmaceuticalInventoryManagementsums")
    public ResponseEntity<JSONObject> getpharmaceuticalInventoryManagementsums(@RequestBody YpjxcRc ypjxcRc){
        Page<Ypjxc> result=tollInfoService.getpharmaceuticalInventoryManagementsums(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /*
    获取租户底下的诊所
     */
    @PostMapping(value = "getjglist")
    public ResponseEntity<JSONObject> getjglist(@RequestBody YpjxcRc ypjxcRc){
        List<Ypjxc> result=tollInfoService.getjglist(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 机构管理-材料进销存信息
     * @param ypjxcRc
     * @return
     */
    @PostMapping(value = "getmaterialmanagement")
    public ResponseEntity<JSONObject> getmaterialmanagement(@RequestBody YpjxcRc ypjxcRc){
        Page<Ypjxc> result=tollInfoService.getmaterialmanagement(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 机构管理-获取材料进销存信息汇总数量和金额
     * @param ypjxcRc
     * @return
     */
    @PostMapping(value = "getmaterialmanagementsums")
    public ResponseEntity<JSONObject> getmaterialmanagementsums(@RequestBody YpjxcRc ypjxcRc){
        Page<Ypjxc> result=tollInfoService.getmaterialmanagementsums(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 药品材料入库信息统计
     * @param ypjxcRc
     * @return
     */
    @PostMapping(value = "drugmaterialsstockmanagement")
    public ResponseEntity<JSONObject> drugmaterialsstockmanagement(@RequestBody YpjxcRc ypjxcRc){
        Page<Ypclrkcx> result=tollInfoService.drugmaterialsstockmanagement(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 药品材料入库信息汇总
     * @param ypjxcRc
     * @return
     */
    @PostMapping(value = "drugmaterialsstockmanagementsums")
    public ResponseEntity<JSONObject> drugmaterialsstockmanagementsums(@RequestBody YpjxcRc ypjxcRc){
        Page<Ypclrkcx> result=tollInfoService.drugmaterialsstockmanagementsums(ypjxcRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 机构管理-获取耗材销售信息
     * @param stuffsalessummaryRc
     * @return
     */
    @PostMapping(value = "getconsumablemarketstatistics")
    public ResponseEntity<JSONObject> getconsumablemarketstatistics(@RequestBody StuffsalessummaryRc stuffsalessummaryRc){
        Page<Stuffsalessummary> result=tollInfoService.getconsumablemarketstatistics(stuffsalessummaryRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    /**
     * 机构管理-获取耗材销售信息汇总价格和数量
     * @param stuffsalessummaryRc
     * @return
     */
    @PostMapping(value = "getconsumablemarketstatisticssum")
    public ResponseEntity<JSONObject> getconsumablemarketstatisticssum(@RequestBody StuffsalessummaryRc stuffsalessummaryRc){
        Page<Stuffsalessummary> result=tollInfoService.getconsumablemarketstatisticssum(stuffsalessummaryRc);
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "getdrugmarketstatistics")
    public ResponseEntity<JSONObject> getdrugmarketstatistics(@RequestBody SearchParams searchParams) {
        final Page<DrugSales> drugSales = tollInfoService.getdrugmarketstatistics(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(drugSales));
    }
    @PostMapping(value = "getdrugmarketstatisticsStat")
    public ResponseEntity<JSONObject> getdrugmarketstatisticsStat(@RequestBody SearchParams searchParams) {
        DrugSales drugSalesStat = tollInfoService.getdrugmarketstatisticsStat(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(drugSalesStat));
    }


    /*
    机构管理-医生收入
     */
    @PostMapping(value = "getdoctorDetailstatistics")
    public ResponseEntity<JSONObject> getdoctorDetailstatistics(@RequestBody SearchParams searchParams) {
        Page<WorkLoad> workload = tollInfoService.getdoctorDetailstatistics(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(workload));
    }

    /*
    机构管理-医生收入
     */
    @PostMapping(value = "getdoctorDetailstatisticsStat")
    public ResponseEntity<JSONObject> getdoctorDetailstatisticsStat(@RequestBody SearchParams searchParams){
        WorkLoadStat workLoadStat = tollInfoService.getdoctorDetailstatisticsStat(searchParams.getParams(), searchParams.getOffset(), searchParams.getLimit(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(workLoadStat));
    }

    @PostMapping(value = "orgtolldetail")
    public ResponseEntity<JSONObject> orgtolldetail(@RequestBody SearchParams searchParams) {
        return tollInfoService.orgtolldetail(searchParams);
    }


    @PostMapping(value = "orgtollDetailForm")
    public ResponseEntity<JSONObject> orgtollDetailForm(@RequestBody SearchParams searchParams) {
        return tollInfoService.orgtollDetailForm(searchParams);
    }

}