package com.geeke.sys.entity;

import java.io.IOException;
import java.io.InputStream;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
/**
 * 文件附件Entity
 * @author lys
 * @version 2021-04-28
 */
public class Attachment extends DataEntity<Attachment> {

	private static final long serialVersionUID = 1L;
	private String bizId;		// 业务标识
	private String type;		// 文件类型
	private byte[] fileContent;		// 文件内容

	
	public Attachment() {
		super();
	}

	public Attachment(String id){
		super(id);
	}
	
	public Attachment(String bizId, MultipartFile file) throws IOException {
		super();
		
		this.bizId = bizId;
		this.name = file.getOriginalFilename();
		this.type = file.getContentType();
		InputStream is = file.getInputStream();
		byte[] fContent = new byte[(int)file.getSize()];
		is.read(fContent);
		this.fileContent = fContent;
	}

	@Length(min=1, max=20, message="业务标识长度必须介于 1 和 20 之间")
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	
	
	@Length(min=0, max=20, message="文件类型长度必须介于 0 和 20 之间")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	public byte[] getFileContent() {
		return fileContent;
	}
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	
	
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_attachment";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "414950311024263169";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}