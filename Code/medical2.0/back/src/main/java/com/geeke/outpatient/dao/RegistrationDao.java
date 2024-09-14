package com.geeke.outpatient.dao;

import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.persistence.CrudDao;
import com.geeke.outpatient.entity.MedicalRecord;
import com.geeke.outpatient.entity.PageRegistration;
import com.geeke.outpatient.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * 挂号信息DAO接口
 * @author txl
 * @version 2022-06-15
 */
@Mapper
public interface RegistrationDao extends CrudDao<Registration> {
    @Update("update registration set status = #{status},department_id=#{departmentId},doctor_id=#{doctorId} where id = #{id}")
    int updateStatus(@Param("id") String id,@Param("status") String status,@Param("departmentId")String departmentId,@Param("doctorId")String doctorId);

    @Update("update registration set status = #{status},refund_registration_pay_type = #{refund_registration_pay_type}" +
            ",refund_registration_remarks=#{refund_registration_remarks},exit_number_date=#{exit_number_date} where id = #{id}")
    int refundRegistrationPay(@Param("id") String id, @Param("status") String status,
                              @Param("refund_registration_pay_type") String refundRegistrationPayType,
                              @Param("refund_registration_remarks") String refundRegistrationRemarks,
                              @Param("exit_number_date") Date exitNumberDate
                              );

    List<Registration> conditionList(@Param("value") String value,
                                     @Param("companyId")String companyId,
                                     @Param("limit")int limit,
                                     @Param("offset")int offset);

    int countConditionList(@Param("value") String value,@Param("companyId")String companyId);

    void updateRecipeStatus(String id);

    int updateStatusByCompanyId(@Param("companyId") String companyId,@Param("createDate") Date createDate,@Param("subscribeDate") Date subscribeDate);

    List<String> countId(PageRegistration pageRegistration);

    List<String> listPages(PageRegistration pageRegistration);

    List<Registration> findById(@Param("list") List<String> list,@Param("pageRegistration") PageRegistration pageRegistration);

    List<Registration> getByPatientId(String patientId);

    int wxReturnPay(Registration registrations);

    List<String> wxCount(PageRegistration pageRegistration);

    List<String> wxListPages(PageRegistration pageRegistration);

    List<String> wxDispensingCount(PageRegistration pageRegistration);

    List<String> wxDispensingListPages(PageRegistration pageRegistration);

    int registrationupdate(Registration registrations);
}