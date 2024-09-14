package com.geeke.sys.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.geeke.sys.dao.SysFileContentDao;
import com.geeke.sys.entity.SysFile;
import com.geeke.sys.entity.SysFileContent;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.geeke.common.service.CrudService;
import com.geeke.sys.dao.SysSetingDao;
import com.geeke.sys.entity.SysSeting;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统设置Service
 *
 * @author szy
 * @version 2021-08-26
 */

@Service("sysSetingService")
public class SysSetingService extends CrudService<SysSetingDao, SysSeting> {


    @Autowired
    private SysFileContentService sysFileContentService; //系统文件存储字节服务

    @Autowired
    private SysFileService sysFileService;  //系统文件服务


    /**
     * 保存系统设置
     *
     * @param entity
     * @return
     */

    @Transactional(readOnly = false)
    public SysSeting save(SysSeting entity, MultipartFile[] multipartFiles) {

        //存储系统设置基本信息
        String id = super.save(entity).getId();
        try {
            if (StringUtils.isNotBlank(id)) {
                //项目标志
                Integer projectLogoIndex = entity.getProjectLogoIndex();
                if(projectLogoIndex >= 0 ) {
                    SysFile sysFile = entity.getProjectLogo();
                    sysFile = setFileAndFileByte(multipartFiles[projectLogoIndex], id, sysFile);
                    entity.setProjectLogo(sysFile);
                }else {
                    entity.setProjectLogo(entity.getProjectLogo());
                }
                //登录标志
                Integer loginLogoIndex = entity.getLoginLogoIndex();
                if(loginLogoIndex >= 0 ) {
                    SysFile sysFile = entity.getLoginLogo();
                    sysFile = setFileAndFileByte(multipartFiles[loginLogoIndex], id, sysFile);
                    entity.setLoginLogo(sysFile);
                }else {
                    entity.setLoginLogo(entity.getLoginLogo());
                }
                // 系统标志
                Integer sysLogoIndex = entity.getSysLogoIndex();
                if(sysLogoIndex >= 0 ) {
                    SysFile sysFile = entity.getSysLogo();
                    sysFile = setFileAndFileByte(multipartFiles[sysLogoIndex], id, sysFile);
                    entity.setSysLogo(sysFile);
                }else {
                    entity.setSysLogo(entity.getSysLogo());
                }
                // 系统图标
                Integer faviconIndex = entity.getFaviconIndex();
                if(faviconIndex >= 0 ) {
                    SysFile sysFile = entity.getFavicon();
                    sysFile = setFileAndFileByte(multipartFiles[faviconIndex], id, sysFile);
                    entity.setFavicon(sysFile);
                }else {
                    entity.setFavicon(entity.getFavicon());
                }

                //  登录背景图
                Integer loginBgIndex = entity.getLoginBgIndex();
                if(loginBgIndex >= 0 ) {
                    SysFile sysFile = entity.getLoginBg();
                    sysFile = setFileAndFileByte(multipartFiles[loginBgIndex], id, sysFile);
                    entity.setLoginBg(sysFile);
                }else {
                    entity.setLoginBg(entity.getLoginBg());
                }

                //  登录背景图
                Integer loginGraphIndex = entity.getLoginGraphIndex();
                if(loginGraphIndex >= 0 ) {
                    SysFile sysFile = entity.getLoginGraph();
                    sysFile = setFileAndFileByte(multipartFiles[loginGraphIndex], id, sysFile);
                    entity.setLoginGraph(sysFile);
                }else {
                    entity.setLoginGraph(entity.getLoginGraph());
                }
                super.save(entity);
            } else {
                logger.info("系统设置保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     *
     * @param multipartFile 文件对象
     * @param id  系统设置id
     * @param sysFile 文件
     * @return
     * @throws IOException
     */
    private SysFile setFileAndFileByte(MultipartFile multipartFile, String id, SysFile sysFile) throws IOException {
        List<SysFileContent>  sysFileContentList= Lists.newArrayList();
        if(sysFile == null || StringUtils.isBlank(sysFile.getId())) {
            sysFile = new SysFile();
            sysFileContentList.add(new SysFileContent());
        }else {
            sysFileContentList.add(sysFileContentService.get(sysFile.getId()));
        }
        MultipartFile file = multipartFile;

        sysFile.setObjectId(Long.parseLong(id));
        sysFile.setName(file.getOriginalFilename());
        sysFile.setFileType(file.getContentType()) ;
        sysFile.setFileSize(file.getSize());
        InputStream is = file.getInputStream();
        byte[] fContent = new byte[(int) file.getSize()];
        is.read(fContent);

        sysFileContentList.get(0).setFileByte(fContent);
        sysFileContentList.get(0).setFileType(file.getContentType());
        sysFile.setSysFileContentList(sysFileContentList);

        //返回文件id
        SysFile sysFileTemp =  sysFileService.save(sysFile);
        return sysFileTemp;
    }
}