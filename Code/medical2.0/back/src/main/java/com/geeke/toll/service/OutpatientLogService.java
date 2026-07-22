package com.geeke.toll.service;



import com.geeke.common.data.Page;
import com.geeke.outpatient.entity.Patient;
import com.geeke.toll.dao.OutpatientLogDao;
import com.geeke.toll.entity.*;
import com.geeke.utils.excel.ExcelExportBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 门诊日志Service
 * @author lc
 * @version 2022-06-15
 */
 
@Service("outpatientLogService")
@Transactional(readOnly = true)
public class OutpatientLogService {
    @Autowired
    private OutpatientLogDao outpatientLogDao;

    public OutpatientLog getLogs(String id) {
        return outpatientLogDao.getLogs(id);
    }

    public Page<OutpatientLog> listLog(OutpatientLogRc outpatientLogRc){
        List<OutpatientLog> list=outpatientLogDao.getOutpatientLog(outpatientLogRc);
        int total=list.size();
        List<OutpatientLog> list2=null;
        if(total>0){
            List<OutpatientLog> outpatientlist = outpatientLogDao.getOutpatientlist(outpatientLogRc);
            Patient patient = new Patient();
            List<Patient> povert = outpatientLogDao.getPovert(patient);
            for (OutpatientLog student : outpatientlist) {
                for (Patient po : povert) {
                    if (student.getCertificate().equals(po.getCard())) {
                        student.setPoverty(po.getPoverty());
                    }
                }
            }
            list2=outpatientlist;

            // list2=outpatientLogDao.getOutpatientlist(outpatientLogRc);
        }
        return new Page<>((long) total,list2);
    }

    @Transactional(readOnly = false)
    public int updateLog(OutpatientLog outpatientLogRc) {
        return outpatientLogDao.updateLog(outpatientLogRc);
    }

    public List<Patient> getPoverty(String patientId) {
        List<Patient> poverty = outpatientLogDao.getPoverty(patientId);
        return poverty;
    }

    public void exportExcel(OutpatientLogRc outpatientLogRc, HttpServletResponse response) throws Exception {
        List<OutpatientLog> list = outpatientLogDao.getOutpatientLog(outpatientLogRc);
        new ExcelExportBuilder("门诊日志")
            .addColumns(
                new ExcelExportBuilder.Column("患者姓名", "patientName"),
                new ExcelExportBuilder.Column("家长姓名", "patriarchName"),
                new ExcelExportBuilder.Column("性别", "sex"),
                new ExcelExportBuilder.Column("患者年龄", "age"),
                new ExcelExportBuilder.Column("民族", "nation"),
                new ExcelExportBuilder.Column("职业", "occupation"),
                new ExcelExportBuilder.Column("详细地址", "address"),
                new ExcelExportBuilder.Column("就诊日期", "visitDate"),
                new ExcelExportBuilder.Column("初/复诊", "initialVisit"),
                new ExcelExportBuilder.Column("血压", "bloodPressure"),
                new ExcelExportBuilder.Column("临床症状", "symptom"),
                new ExcelExportBuilder.Column("体温发热", "fever"),
                new ExcelExportBuilder.Column("流行病学史", "epidemicDisease"),
                new ExcelExportBuilder.Column("西医诊断", "westernDiagnose"),
                new ExcelExportBuilder.Column("中医诊断", "chinaDiagnose"),
                new ExcelExportBuilder.Column("传染病", "infect"),
                new ExcelExportBuilder.Column("处理情况", "handle"),
                new ExcelExportBuilder.Column("有效证件号", "certificate"),
                new ExcelExportBuilder.Column("工作单位", "unit"),
                new ExcelExportBuilder.Column("医生签名", "signature"),
                new ExcelExportBuilder.Column("贫困标志", "poverty"),
                new ExcelExportBuilder.Column("联系电话", "telephone"),
                new ExcelExportBuilder.Column("发病日期", "morbidityDate"),
                new ExcelExportBuilder.Column("实验室阳性结果", "positiveResult"),
                new ExcelExportBuilder.Column("个体化健康教育", "healthEducation")
            )
            .data(list)
            .write(response, "门诊日志.xlsx");
    }
}