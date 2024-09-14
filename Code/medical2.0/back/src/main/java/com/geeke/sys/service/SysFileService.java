package com.geeke.sys.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geeke.common.constants.ActionConstants;
import com.geeke.common.data.PageRequest;
import com.geeke.common.data.Parameter;
import com.geeke.common.service.CrudService;
import com.geeke.config.exception.CommonJsonException;
import com.geeke.sys.entity.Action;
import com.geeke.sys.entity.ActionRecycle;
import com.geeke.sys.entity.SysFile;
import com.geeke.sys.dao.SysFileDao;
import com.geeke.sys.entity.SysFileContent;
import com.geeke.sys.dao.SysFileContentDao;
import com.geeke.utils.Reflections;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import com.geeke.utils.constants.ErrorEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统附件Service
 * @author szy
 * @version 2021-09-22
 */
 
@Service("sysFileService")
@Transactional(readOnly = true)
public class SysFileService extends CrudService<SysFileDao, SysFile>{

    @Autowired
    private SysFileContentDao sysFileContentDao;
    
    @Override
    public SysFile get(String id) {
        SysFile sysFile = super.get(id);

        List<Parameter> params = null;
        PageRequest pageRequest;
        /*获取子表列表   附件字节表*/
        params = Lists.newArrayList();
        params.add(new Parameter("id", "=", sysFile.getId()));
        pageRequest = new PageRequest(params);
        sysFile.setSysFileContentList(sysFileContentDao.listAll(pageRequest));        
        return sysFile;
    }

    @Override
    @Transactional(readOnly = false)
    public SysFile save(SysFile sysFile) {
	
        SysFile sysFileTemp = super.save(sysFile);
        if (StringUtils.isNoneBlank(sysFileTemp.getId())) {

            List<Parameter> params = null;
            PageRequest pageRequest;
            /* 处理子表     附件字节表 */
            params = Lists.newArrayList();
            params.add(new Parameter("id", "=", sysFile.getId()));
            pageRequest = new PageRequest(params);
            List<SysFileContent> list_SysFileContent = sysFileContentDao.listAll(pageRequest);            
            List<SysFileContent> deleteSysFileContents = Lists.newArrayList(); // 删除列表
            List<SysFileContent> insertSysFileContents = Lists.newArrayList(); // 添加列表
            List<SysFileContent> updateSysFileContents = Lists.newArrayList(); // 更新列表
            for(SysFileContent sysFileContentSaved: list_SysFileContent) {
                boolean found = false;   
                for (SysFileContent sysFileContent : sysFile.getSysFileContentList()){
                   if(sysFileContentSaved.getId().equals(sysFileContent.getId())){
                       found = true;
                       break;
                   }
                }
                if(!found) {
                   deleteSysFileContents.add(sysFileContentSaved);
                }
            }
            if(deleteSysFileContents.size() > 0) {
                sysFileContentDao.bulkDelete(deleteSysFileContents);
            }
            for (SysFileContent sysFileContent : sysFile.getSysFileContentList()){
            

                if (StringUtils.isBlank(sysFileContent.getId())) {
                    sysFileContent.setId(sysFileTemp.getId());  // 子表id与父表id一致
                    preInsert(sysFile,sysFileContent);
                    insertSysFileContents.add(sysFileContent);
                } else {
                    preInsert(sysFile,sysFileContent);
                    updateSysFileContents.add(sysFileContent);
                }

            }
            if(updateSysFileContents.size() > 0) {
            	sysFileContentDao.bulkUpdate(updateSysFileContents);
            }
            if(insertSysFileContents.size() > 0) {
            	sysFileContentDao.bulkInsert(insertSysFileContents);
            }
        }
        return sysFileTemp;
    }

    /**
     * multipartFiles文件转为SysFile集合对象
     * @param multipartFiles
     * @param objectId
     * @return
     */
    @Transactional(readOnly = false)
    public List<SysFile> changeAndSaveSysFileList(MultipartFile[] multipartFiles, String objectId) throws IOException {
        List<SysFile> fileList = new ArrayList<>(); // 文件集合对象
        for(MultipartFile file : multipartFiles) {
            List<SysFileContent> fileContentList = new ArrayList<>(); // 文件内容集合对象
            SysFile sysFile = new SysFile();
            String originalFilename = file.getOriginalFilename();
            int begin = originalFilename.indexOf(".");
            int last = originalFilename.length();
            //获得文件后缀名
            String suffix = originalFilename.substring(begin, last);
            sysFile.setFileType(suffix); // 文件类型

            sysFile.setObjectId(Long.parseLong(objectId)); // 业务表ID
            sysFile.setName(file.getOriginalFilename()); // 文件名
            sysFile.setFileSize(file.getSize()); // 文件大小

            SysFileContent fileContent = new SysFileContent();
            InputStream is = file.getInputStream();
            byte[] fContent = new byte[(int) file.getSize()];
            is.read(fContent);
            fileContent.setFileByte(fContent); // 字节
            fileContent.setFileType(suffix); // 文件类型
            fileContentList.add(fileContent); // 文件存储内容
            sysFile.setSysFileContentList(fileContentList);
            fileList.add(sysFile);
            save(sysFile);
        }
        return fileList;
    }

    /**
     * 根据objectId删除附件和附件存储表信息信息
     * @param objectId
     */
    @Transactional(readOnly = false)
    public void deleteByObjectId(String objectId) {
        List<Parameter> params = null;
        PageRequest pageRequest;
        params = Lists.newArrayList();
        params.add(new Parameter("object_id", "=", objectId));
        pageRequest = new PageRequest(params);
        List<SysFile> sysFiles = dao.listAll(pageRequest);
        for (SysFile file : sysFiles) {
            delete(file); // 删除附件包含存储表信息
        }
        /*if (sysFiles.size() > 0) {
            dao.bulkDelete(sysFiles); // 删除附件表信息
        }*/

    }


    /**
     * @param sysFile
     * @param sysFileContent
     * 为了保存系统附件和系统字节表更新时间一致，用户回显图片做浏览器缓存使用
     * 用原来的创建附件、更改附件用的是preInsert ,preUpdate方法 每张表更新都会new date() 会导致时间不同
     */

    private void preInsert(SysFile sysFile, SysFileContent sysFileContent) {
        sysFileContent.setCreateDate(sysFile.getCreateDate());
        sysFileContent.setUpdateDate(sysFile.getUpdateDate());
        sysFileContent.setCreateBy(sysFile.getCreateBy());
        sysFileContent.setUpdateBy(sysFile.getUpdateBy());
    }

    /**
     * 删除
     * @param entity
     */
    @Override
    @Transactional(readOnly = false)
    public int delete(SysFile sysFile) {
        List<Parameter> params = null;
        PageRequest pageRequest;
        /* 处理子表     附件字节表 */
        params = Lists.newArrayList();
        params.add(new Parameter("id", "=", sysFile.getId()));
        pageRequest = new PageRequest(params);
        sysFile.setSysFileContentList(sysFileContentDao.listAll(pageRequest));        

        if(sysFile.getSysFileContentList() != null && sysFile.getSysFileContentList().size() > 0) {
            sysFileContentDao.bulkDelete(sysFile.getSysFileContentList());
        }

        int rows = super.delete(sysFile);
        return rows;
    }

    @Transactional(readOnly = false)
    public void delete(String[] ids) {
        // 根据数组ID，删除附件
        dao.deleteByIds(ids);
        // 根据数组ID，删除附件存储表
        sysFileContentDao.deleteByIds(ids);
    }

    /**
     * 生成操作日志
     * @param actionTypeId  操作类型Id
     * @param entity        操作的实体对象
     * @return
     */
    @Override
    protected Action createAction(String actionTypeId, SysFile entity) {
        Action action = super.createAction(actionTypeId, entity);
        if(action == null) {
            return null;
        }
        // 删除时记录把保存的数据保存到回收站
        if(ActionConstants.ACTION_DELETED.equals(actionTypeId)) {   
            for(SysFileContent child: entity.getSysFileContentList()) {
                ActionRecycle recycle = new ActionRecycle();
                recycle.setTableName(child.getBusTableName());
                recycle.setObjectId(child.getId());
                recycle.setObjectName((String)Reflections.invokeGetter(child, "name"));
                action.getActionRecycleList().add(recycle);            
            }
        }
        return action;
    }
}