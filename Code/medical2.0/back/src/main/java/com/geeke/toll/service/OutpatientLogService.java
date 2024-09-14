package com.geeke.toll.service;



import com.geeke.common.data.Page;
import com.geeke.outpatient.entity.Patient;
import com.geeke.toll.dao.OutpatientLogDao;
import com.geeke.toll.entity.*;
import com.geeke.utils.excel.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * 门诊日志Service
 * @author lc
 * @version 2022-06-15
 */
 
@Service("outpatientLogService")
@Transactional(readOnly = false)
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

    public int updateLog(OutpatientLog outpatientLogRc) {
        return outpatientLogDao.updateLog(outpatientLogRc);
    }

    public List<Patient> getPoverty(String patientId) {
        List<Patient> poverty = outpatientLogDao.getPoverty(patientId);
        return poverty;
    }

    public void exportExcel(OutpatientLogRc outpatientLogRc, HttpServletResponse response) throws Exception {
        List<OutpatientLog> list = outpatientLogDao.getOutpatientLog(outpatientLogRc);
        ByteArrayOutputStream outputStream = ExcelUtils.writeExcel(list);
        ExcelUtils.writeResponse(outputStream,response,"门诊日志.xlsx");
    }
}