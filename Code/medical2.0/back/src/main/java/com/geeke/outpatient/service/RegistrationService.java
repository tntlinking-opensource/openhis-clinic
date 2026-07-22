package com.geeke.outpatient.service;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.medicareutils.service.MdRegistrationService;
import com.geeke.outpatient.dao.MedicalRecordDao;
import com.geeke.outpatient.dao.RecipelInfoDao;
import com.geeke.outpatient.dao.RegistrationDao;
import com.geeke.outpatient.entity.*;
import com.geeke.sys.entity.Action;
import com.geeke.utils.IdGen;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 挂号信息Service
 * @author txl
 * @version 2022-06-15
 */
 
@Service("registrationService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegistrationService extends CrudService<RegistrationDao, Registration>{
    private final RegistrationDao registrationDao;

    private final MedicalRecordDao medicalRecordDao;

    private final PatientService patientService;

    private final MedicalRecordService medicalRecordService;

    //医保挂号
    private final MdRegistrationService mdRegistrationService;

    @Transactional(readOnly = false)
    public int updateStatus(String id,String status,String departmentId,String doctorId)
    {
        return dao.updateStatus(id,status,departmentId,doctorId);
    }
    @Transactional(readOnly = false)
    public int updateStatusByCompanyId(String companyId){
        //计算过期时间（24小时前）
        long time = new Date().getTime();
        Date date = new Date(time - 24 * 60 * 60 * 1000);
        return dao.updateStatusByCompanyId(companyId, date, date);
    }

    /**
     * 从分页结果的第一条记录中提取companyId，并更新过期状态
     * 消除 Controller 中重复的 companyId 提取逻辑
     */
    @Transactional(readOnly = false)
    public <T> void updateExpiredStatusFromPage(Page<T> result, java.util.function.Function<T, String> companyIdExtractor) {
        if (result != null && result.getTotal() > 0 && result.getRows() != null && !result.getRows().isEmpty()) {
            String companyId = companyIdExtractor.apply(result.getRows().get(0));
            if (companyId != null) {
                updateStatusByCompanyId(companyId);
            }
        }
    }
    /**
     * 退号
     * @author lin.zeng
     **/
    @Transactional(readOnly = false)
    public int refundRegistrationPay(String id,String status,
                              String refundRegistrationPayType, String refundRegistrationRemarks,Date exitNumberDate
    )
    {
        int i = dao.refundRegistrationPay(id, status, refundRegistrationPayType, refundRegistrationRemarks, exitNumberDate);
        if(i>0){

        }

        return i;
    }

    public Page<Registration> listConditionPage(String param, int offset, int limit) {
        return paginate(
            () -> this.dao.countConditionList(param, SessionUtils.getLoginTenantId()),
            () -> this.dao.conditionList(param, SessionUtils.getLoginTenantId(), limit, offset)
        );
    }
    @Transactional(readOnly = false)
    public void updateRecipeStatus(String id) {
        registrationDao.updateRecipeStatus(id);
    }

    @Transactional(readOnly = true)
    public Page<Registration> listPages(PageRegistration pageRegistration) {
        if (!pageRegistration.isValidColumnName()) {
            throw new IllegalArgumentException("Invalid column name: " + pageRegistration.getColumnName());
        }
        List<String> strings = this.dao.countId(pageRegistration);
        int total=strings.size();
        List<Registration> list = null;
        if (total > 0) {

            List<String> strings1 = this.dao.listPages(pageRegistration);
            if(strings1.size()>0){
               list = this.dao.findById(strings1,pageRegistration);
            }
        }

        return new Page<>((long)total, list);
    }


    public List<String> whetherTheDispensing(PageRegistration pageRegistration) {
        List<String> strings = this.dao.countId(pageRegistration);
        return strings;
    }

    public List<String> whetherChargeOrNot(PageRegistration pageRegistration) {
        List<String> strings = this.dao.countId(pageRegistration);
        return strings;
    }

    @Transactional(readOnly = false)
    public Registration wxSave(Registration entity) {
        if (StringUtils.isBlank(entity.getId())) {
            entity.preInsert();
            this.doInsert(entity);
            Action created = this.createAction("created", entity);
            created.setCreateBy("微信");
            created.setUpdateBy("微信");
            this.saveAction(created);
        } else {
            entity.preUpdate();
            this.doUpdate(entity);
            this.saveAction(this.createAction("updated", entity));
        }
        return entity;
    }

    @Transactional(readOnly = true)
    public Page<Registration> getRegistrationByOpenId(List<Parameter> parameters, int offset, int limit, String orderby) {
        PageRequest pageRequest = new PageRequest(offset, limit, parameters, orderby);
        List<Parameter> params = pageRequest.getParams();
        String openId = (String) params.get(1).getValue();
        String companyId = (String) params.get(0).getValue();

        // 使用数据库分页，避免内存分页和N+1查询
        return paginate(
            () -> this.dao.countByOpenId(openId, companyId),
            () -> this.dao.listByOpenId(openId, companyId, offset, limit)
        );
    }

    @Transactional(readOnly = false)
    public int wxReturnPay(Registration registrations) {
        int i = dao.wxReturnPay(registrations);

        return i;
    }


    public Page<ReceptionEvt> wxListPages(PageRegistration pageRegistration) {
        if (!pageRegistration.isValidColumnName()) {
            throw new IllegalArgumentException("Invalid column name: " + pageRegistration.getColumnName());
        }
        List<String> strings = this.dao.wxCount(pageRegistration);
        int total=strings.size();
        List<Registration> list = null;
        List<ReceptionEvt> lists = new ArrayList<>();
        if (total > 0) {

            List<String> strings1 = this.dao.wxListPages(pageRegistration);

            if(strings1.size()>0){
               // list = this.dao.findById(strings1,pageRegistration);
                for (String s : strings1) {
                    ReceptionEvt receptionEvt = medicalRecordService.allNewQuery(s, null);
                    lists.add(receptionEvt);
                }
            }
        }

        return new Page<>((long)total, lists);
    }

    public Page<ReceptionEvt> wxDispensingListPages(PageRegistration pageRegistration) {
        if (!pageRegistration.isValidColumnName()) {
            throw new IllegalArgumentException("Invalid column name: " + pageRegistration.getColumnName());
        }
        List<String> strings = this.dao.wxDispensingCount(pageRegistration);
        int total=strings.size();
        List<Registration> list = null;
        List<ReceptionEvt> lists = new ArrayList<>();
        if (total > 0) {

            List<String> strings1 = this.dao.wxDispensingListPages(pageRegistration);

            if(strings1.size()>0){
                // list = this.dao.findById(strings1,pageRegistration);
                for (String s : strings1) {
                    ReceptionEvt receptionEvt = medicalRecordService.allNewQuery(s, null);
                    lists.add(receptionEvt);
                }
            }
        }

        return new Page<>((long)total, lists);
    }

    @Transactional(readOnly = false)
    public int registrationupdate(Registration registrations) {
        int i = dao.registrationupdate(registrations);

        return i;
    }

    @Transactional(readOnly = false)
    public int medicalRecordInserts(MedicalRecord medicalRecord) {
        medicalRecord.setDiagnose("");
        medicalRecord.setId(IdGen.uuid());
        medicalRecord.setCompany(SessionUtils.getUser().getCompany());
        medicalRecord.setCreateBy(SessionUtils.getUser().getUpdateBy());
        medicalRecord.setCreateDate(SessionUtils.getUser().getUpdateDate());
        medicalRecord.setUpdateBy(SessionUtils.getUser().getUpdateBy());
        medicalRecord.setUpdateDate(SessionUtils.getUser().getUpdateDate());
        medicalRecord.setDelFlag("0");
        int result = medicalRecordDao.insert(medicalRecord);
        return result;
    }

}