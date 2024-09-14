package com.geeke.sys.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.util.List;
import com.google.common.collect.Lists;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 系统附件Entity
 * @author szy
 * @version 2021-09-22
 */
public class SysFile extends DataEntity<SysFile> {

	private static final long serialVersionUID = 592905357446275112L;
    @JsonSerialize(using= ToStringSerializer.class)
	private Long objectId;		// 业务表
	private String fileType;		// 文件类型
    @JsonSerialize(using= ToStringSerializer.class)
	private Long fileSize;		// 文件大小
	private String fileUrl;		// 文件路径
	private byte[] fileByte;		// 图片字节

	private List<SysFileContent> sysFileContentList = Lists.newArrayList();		// 子表列表
	
	public SysFile() {
		super();
	}

	public SysFile(String id){
		super(id);
	}
	

	@NotNull(message="业务表不能为空")
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	
	
	@Length(min=0, max=64, message="文件类型长度必须介于 0 和 64 之间")
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	
	
	@Length(min=0, max=255, message="文件路径长度必须介于 0 和 255 之间")
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public byte[] getFileByte() {
		return fileByte;
	}
	public void setFileByte(byte[] fileByte) {
		this.fileByte = fileByte;
	}
	
	public List<SysFileContent> getSysFileContentList() {
		return sysFileContentList;
	}

	public void setSysFileContentList(List<SysFileContent> sysFileContentList) {
		this.sysFileContentList = sysFileContentList;
	}
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_file";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "592905357446275112";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}