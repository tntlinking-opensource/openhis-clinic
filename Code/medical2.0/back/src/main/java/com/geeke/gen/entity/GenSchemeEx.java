package com.geeke.gen.entity;

/**
 * 代码方案管理Entity
 * @author lys
 * @version 2019-12-06
 */
public class GenSchemeEx extends GenScheme {
	private static final long serialVersionUID = 3923849151258880385L;
	
	private boolean replace;		//是否替换代码文件
	
	public boolean isReplace() {
		return replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}	
}