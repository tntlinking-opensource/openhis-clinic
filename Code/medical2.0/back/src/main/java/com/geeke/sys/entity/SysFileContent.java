package com.geeke.sys.entity;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 系统附件Entity
 * @author szy
 * @version 2021-09-22
 */
public class SysFileContent extends DataEntity<SysFileContent> {

	private static final long serialVersionUID = 592905357446275244L;
	private byte[] fileByte;		// 图片字节
	private String fileType;		// 文件类型
	private String base64Str;
	private String name;
	
	public SysFileContent() {
		super();
	}

	public SysFileContent(String id){
		super(id);
	}
	

	public byte[] getFileByte() {
		return fileByte;
	}
	public void setFileByte(byte[] fileByte) {
		this.fileByte = fileByte;
	}
	
	
	@Length(min=0, max=64, message="文件类型长度必须介于 0 和 64 之间")
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getBase64Str() {
		return base64Str;
	}

	public void setBase64Str(String base64Str) {
		this.base64Str = base64Str;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_file_content";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "592905357446275244";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}