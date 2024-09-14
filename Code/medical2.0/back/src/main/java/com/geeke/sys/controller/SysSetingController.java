package com.geeke.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.geeke.common.controller.SearchParams;
import com.geeke.sys.entity.SysFile;
import com.geeke.sys.entity.SysFileContent;
import com.geeke.sys.entity.SysSeting;
import com.geeke.sys.service.SysFileContentService;
import com.geeke.sys.service.SysFileService;
import com.geeke.sys.service.SysSetingService;
import com.geeke.utils.ResultUtil;
import com.geeke.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 系统设置Controller
 *
 * @author szy
 * @version 2021-08-26
 */
@RestController
@RequestMapping(value = "/sys/sysSeting")
public class SysSetingController extends BaseController {

    @Autowired
    private SysSetingService sysSetingService;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private SysFileContentService sysFileContentService;

    @PostMapping(value = "listAll")
    public ResponseEntity<JSONObject> listAll(@RequestBody SearchParams searchParams) {
        List<SysSeting> result = sysSetingService.listAll(searchParams.getParams(), searchParams.getOrderby());
        return ResponseEntity.ok(ResultUtil.successJson(result));
    }

    @PostMapping(value = "save")
    public ResponseEntity<JSONObject> save(@RequestParam("sysSeting") String sysSeting, @RequestParam("attachments") MultipartFile[] files) {
        SysSeting entity = JSONObject.parseObject(sysSeting, SysSeting.class);
        String id = sysSetingService.save(entity, files).getId();
        return ResponseEntity.ok(ResultUtil.successJson(id));
    }

    @PostMapping(value = "delete")
    public ResponseEntity<JSONObject> delete(@RequestBody SysSeting entity) {
        int rows = sysSetingService.delete(entity);
        return ResponseEntity.ok(ResultUtil.successJson(rows));
    }

    /**
     * 系统设置回显图片
     *
     * @param id
     * @param response
     */
    @GetMapping("/getPhoto/{id}")
    public ResponseEntity getPhotoById(@PathVariable("id") String id, HttpServletResponse response, HttpServletRequest request) throws Exception {
        SysFileContent sysFileContent = sysFileContentService.get(id);

        String base64Str = Base64.getEncoder().encodeToString(sysFileContent.getFileByte());
        return ResponseEntity.ok(base64Str);

    }

    /**
     * 系统设置回显图片
     *
     * @param id
     * @param response
     */
    @GetMapping("/getFiled/{id}")
    public ResponseEntity getFiledId(@PathVariable("id") String id, HttpServletResponse response, HttpServletRequest request) throws Exception {
        SysFileContent sysFileContent = sysFileContentService.get(id);
        SysFile sysFile = sysFileService.get(id);
        String base64Str = Base64.getEncoder().encodeToString(sysFileContent.getFileByte());
        sysFileContent.setBase64Str(base64Str);
        sysFileContent.setName(sysFile.getName());
        return ResponseEntity.ok(sysFileContent);

    }

    /**
     * 系统设置回显图片
     *
     * @param id
     * @param response
     */
    @GetMapping("/getFile/{id}")
    public void getById(@PathVariable("id") String id, HttpServletResponse response, HttpServletRequest request) throws Exception {
        if (id.indexOf(".") >= 0) {
            id = id.substring(0, id.lastIndexOf("."));
        }
        String modifiedSice = request.getHeader("if-modified-since");
        //日期格式化
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        if (StringUtils.isNotBlank(modifiedSice)) {
            //Thu, 04 Feb 2021 07:02:25 GMT   转化日期
            Date modifiedSiceDate = simpleDateFormat.parse(modifiedSice);
            SysFile sysFile = sysFileService.get(id);
            if (sysFile != null) {
                Date updateDate = sysFile.getUpdateDate();
                if (updateDate.getTime() == modifiedSiceDate.getTime()) {
                    response.setStatus(304);
                    return;
                }
            }
        }
        SysFileContent sysFileContent = sysFileContentService.get(id);
        if (sysFileContent != null) {
            response.setHeader("Content-Type", sysFileContent.getFileType());
            Date updateDate = sysFileContent.getUpdateDate();
            String format = simpleDateFormat.format(updateDate);
            response.addHeader("Last-Modified", format);
            try {
                OutputStream os = response.getOutputStream();

                os.write(sysFileContent.getFileByte());

                os.flush();

                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/filedownload")
    public void filedownload(@RequestParam("fileId") String fileId, HttpServletResponse response, HttpServletRequest request) throws Exception {
        if (fileId.indexOf(".") >= 0) {
            fileId = fileId.substring(0, fileId.lastIndexOf("."));
        }
        logger.info("fileId = {}", fileId);
        String modifiedSice = request.getHeader("if-modified-since");
        //日期格式化
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        if (StringUtils.isNotBlank(modifiedSice)) {
            //Thu, 04 Feb 2021 07:02:25 GMT   转化日期
            Date modifiedSiceDate = simpleDateFormat.parse(modifiedSice);
            SysFile sysFile = sysFileService.get(fileId);
            if (sysFile != null) {
                Date updateDate = sysFile.getUpdateDate();
                if (updateDate.getTime() == modifiedSiceDate.getTime()) {
                    response.setStatus(304);
                    return;
                }
            }
        }
        SysFileContent sysFileContent = sysFileContentService.get(fileId);
        response.setHeader("Content-Type", sysFileContent.getFileType());
        Date updateDate = sysFileContent.getUpdateDate();
        String format = simpleDateFormat.format(updateDate);
        response.addHeader("Last-Modified", format);
        try {
            OutputStream os = response.getOutputStream();

            os.write(sysFileContent.getFileByte());

            os.flush();

            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}