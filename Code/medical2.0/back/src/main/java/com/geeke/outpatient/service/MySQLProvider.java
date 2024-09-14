package com.geeke.outpatient.service;

import com.bstek.ureport.exception.ReportException;
import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import com.geeke.common.data.Parameter;
import com.geeke.outpatient.entity.Patient;
import com.geeke.outpatient.entity.UReportFile;
import com.geeke.outpatient.service.ReportFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySQLProvider implements ReportProvider {

    /**
     * 存储器名称
     */
    private static final String NAME = "Newtouch-provider";

    /**
     * 报表文件名前缀
     */
    private String prefix = "Newtouch:";

    /**
     * 是否禁用
     */
    private boolean disabled = false;

    @Autowired
    private ReportFileService reportFileService;

    @Autowired
    private PatientService patientService;

    /**
     * 根据报表名加载报表文件
     *
     * @param file 报表名称
     * @return 返回的InputStream
     */
    @Override
    public InputStream loadReport(String file) {
        UReportFile reportFile = reportFileService.getReportFileByName(getCorrectName(file));
        ByteArrayInputStream stream = null;
        if (null != reportFile) {
            try {
                stream = new ByteArrayInputStream(reportFile.getContent().getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new ReportException(e);
            }
        }
        return stream;
    }

    /**
     * 根据报表名，删除指定的报表文件
     *
     * @param file 报表名称
     */
    @Override
    public void deleteReport(String file) {
        UReportFile reportFile = reportFileService.getReportFileByName(getCorrectName(file));
        if (null != reportFile) {
            reportFileService.delete(reportFile);
        }
    }

    /**
     * 获取所有的报表文件
     *
     * @return 返回报表文件列表
     */
    @Override
    public List<ReportFile> getReportFiles() {
        List<UReportFile> list = reportFileService.getListAll();
        List<ReportFile> reportList = new ArrayList<>();
        for (UReportFile file : list) {
            reportList.add(new ReportFile(file.getName(), file.getUpdateDate()));
        }
        return reportList;
    }

    /**
     * 保存报表文件
     *
     * @param file 报表名称
     * @param content 报表的XML内容
     */
    @Override
    public void saveReport(String file, String content) {
        file = getCorrectName(file);
        UReportFile reportFile = reportFileService.getReportFileByName(file);
        if(null == reportFile){
            reportFile = new UReportFile();
            reportFile.setName(file);
            reportFile.setContent(content);
            reportFile.setCreateBy("测试");
            reportFile.setUpdateBy("测试");
            reportFileService.save(reportFile);
        }else{
            reportFile.setContent(content);
            reportFileService.save(reportFile);
        }
    }

    /**
     * @return 返回存储器名称
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * @return 返回是否禁用
     */
    @Override
    public boolean disabled() {
        return disabled;
    }

    /**
     * @return 返回报表文件名前缀
     */
    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * 获取没有前缀的文件名
     *
     * @param name 报表名称
     */
    private String getCorrectName(String name){
        if(name.startsWith(prefix)){
            name = name.substring(prefix.length());
        }
        return name;
    }
}
