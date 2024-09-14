package com.geeke.sys.service;

import com.geeke.common.data.Page;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.cure.entity.InspectionCheckDetail;
import com.geeke.sys.dao.MyTaskDao;
import com.geeke.sys.entity.MyTask;
import com.geeke.sys.entity.SysFile;
import com.geeke.sys.utils.SessionUtils;
import com.geeke.treatment.entity.CostItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service("myTaskService")
public class MyTaskService extends CrudService<MyTaskDao,MyTask> {
    @Autowired
    private MyTaskDao myTaskDaol;

    @Autowired
    private SysFileService sysFileService;

    @Transactional
    public void save(MyTask myTask, MultipartFile[] fileIdUploads, String[] deleteIds) throws IOException {
        //保存上传文件
        List<SysFile> sysFiles = sysFileService.changeAndSaveSysFileList(fileIdUploads, myTask.getId());
        if(sysFiles!=null&&sysFiles.size()>0){
            myTask.setFileId(sysFiles.get(0).getId());
        }
        // 根据附件ID删除附件信息
        if (null != deleteIds && deleteIds.length > 0) {
            sysFileService.delete(deleteIds);
        }
        myTask.setUpdateBy(SessionUtils.getUserJson().getString("name"));
        myTask.setUpdateDate(new Date());
        this.dao.update(myTask);


    }

    @Transactional
    public MyTask update(MyTask myTask) {
        int i =this.dao.update(myTask);
        if(i>0){
            return myTask;
        }
        return null;
    }
}
