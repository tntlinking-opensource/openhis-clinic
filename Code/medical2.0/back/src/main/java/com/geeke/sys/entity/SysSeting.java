package com.geeke.sys.entity;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.geeke.common.persistence.DataEntity;
/**
 * 系统设置Entity
 * @author szy
 * @version 2021-08-26
 */
public class SysSeting extends DataEntity<SysSeting> {

	private static final long serialVersionUID = 1L;
	private String sysName;		// 系统名称
	private String sysAbbrname;		// 系统简介
	private String loginBgcolor;		// 登录背景色
	private String loginLayout;		// 登陆布局
	private SysFile projectLogo;      // 项目标志 
	private SysFile loginLogo;      // 登录标志 
	private SysFile sysLogo;      // 系统标志 
	private SysFile favicon;      // 系统图标 
	private SysFile loginBg;      // 登录背景图 
	private SysFile loginGraph;      // 登录配图 
    private Integer projectLogoIndex;      // 项目标志
    private Integer loginLogoIndex;      // 登录标志
    private Integer sysLogoIndex;      // 系统标志
    private Integer faviconIndex;      // 系统图标
    private Integer loginBgIndex;      // 登录背景图
    private Integer loginGraphIndex;      // 登录配图

    public Integer getProjectLogoIndex() {
        return projectLogoIndex;
    }

    public void setProjectLogoIndex(Integer projectLogoIndex) {
        this.projectLogoIndex = projectLogoIndex;
    }

    public Integer getLoginLogoIndex() {
        return loginLogoIndex;
    }

    public void setLoginLogoIndex(Integer loginLogoIndex) {
        this.loginLogoIndex = loginLogoIndex;
    }

    public Integer getSysLogoIndex() {
        return sysLogoIndex;
    }

    public void setSysLogoIndex(Integer sysLogoIndex) {
        this.sysLogoIndex = sysLogoIndex;
    }

    public Integer getFaviconIndex() {
        return faviconIndex;
    }

    public void setFaviconIndex(Integer faviconIndex) {
        this.faviconIndex = faviconIndex;
    }

    public Integer getLoginBgIndex() {
        return loginBgIndex;
    }

    public void setLoginBgIndex(Integer loginBgIndex) {
        this.loginBgIndex = loginBgIndex;
    }

    public Integer getLoginGraphIndex() {
        return loginGraphIndex;
    }

    public void setLoginGraphIndex(Integer loginGraphIndex) {
        this.loginGraphIndex = loginGraphIndex;
    }

    public SysSeting() {
		super();
	}

	public SysSeting(String id){
		super(id);
	}
	

	@Length(min=0, max=255, message="系统名称长度必须介于 0 和 255 之间")
	public String getSysName() {
		return sysName;
	}
	public void setSysName(String sysName) {
		this.sysName = sysName;
	}
	
	
	@Length(min=0, max=128, message="系统简介长度必须介于 0 和 128 之间")
	public String getSysAbbrname() {
		return sysAbbrname;
	}
	public void setSysAbbrname(String sysAbbrname) {
		this.sysAbbrname = sysAbbrname;
	}
	
	
	@Length(min=0, max=20, message="登录背景色长度必须介于 0 和 20 之间")
	public String getLoginBgcolor() {
		return loginBgcolor;
	}
	public void setLoginBgcolor(String loginBgcolor) {
		this.loginBgcolor = loginBgcolor;
	}
	
	
	@Length(min=0, max=20, message="登陆布局长度必须介于 0 和 20 之间")
	public String getLoginLayout() {
		return loginLayout;
	}
	public void setLoginLayout(String loginLayout) {
		this.loginLayout = loginLayout;
	}
	
	
    public SysFile getProjectLogo() {
        return projectLogo;
    }

    public void setProjectLogo(SysFile projectLogo) {
        this.projectLogo = projectLogo;
    }
    
    public SysFile getLoginLogo() {
        return loginLogo;
    }

    public void setLoginLogo(SysFile loginLogo) {
        this.loginLogo = loginLogo;
    }
    
    public SysFile getSysLogo() {
        return sysLogo;
    }

    public void setSysLogo(SysFile sysLogo) {
        this.sysLogo = sysLogo;
    }
    
    public SysFile getFavicon() {
        return favicon;
    }

    public void setFavicon(SysFile favicon) {
        this.favicon = favicon;
    }
    
    public SysFile getLoginBg() {
        return loginBg;
    }

    public void setLoginBg(SysFile loginBg) {
        this.loginBg = loginBg;
    }
    
    public SysFile getLoginGraph() {
        return loginGraph;
    }

    public void setLoginGraph(SysFile loginGraph) {
        this.loginGraph = loginGraph;
    }
    
	
	
	
	/**
     * 获取实体对应表名
    */
    @Override
    @JsonIgnore
    public String getBusTableName() {
        return "sys_seting";
    }
    
    /**
     * 获取实体对应Id
    */
    @Override
    @JsonIgnore
    public String getBusTableId() {
        return "592905357446275093";
    }
    
    
    /**
     * 获取实体表中是否存在del_flag字段
     */
    @JsonIgnore
    public boolean getBusTableHasDelFlag() {
            return true;
    }
}