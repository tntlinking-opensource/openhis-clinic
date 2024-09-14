package com.geeke.taskmanagement.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.geeke.admin.entity.User;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.outpatient.entity.scheduling;


import java.util.Date;
/**
 * 文章管理Entity
 * @author txl
 * @version 2023-02-09
 */


public class article extends DataEntity<article>{
    private static final long serialVersionUID = 1014474470772900019L;
    /** 诊所标识 */
    private String companyid;
    /** 用户标识 */
    private String userid ;
    /** 发布时间 */
    private Date aricledate ;
    /** 标题 */
    private String title ;
    /** 摘要 */
    private String abstracts;
    /** 正文内容 */
    private String content ;
    /** 来源作者 */
    private String sources ;
    /** 原文链接 */
    private String originallink ;

    /** 审核人 */
    private String auditor ;

    /** 审核时间 */
    private Date auditordate ;
    /** 发布人名称 */
    private String username ;
    /** 审核状态（0：待审核;1：通过 2：不通过） */
    private String auditstatus ;
    /** 是否启用（0：是；1：否） */
    private String isLocked ;

    private Company company;

    private User user;

    public article() {
        super();
    }

    public article(String id){
        super(id);
    }

    /** 诊所标识 */
    public String getCompanyid() {
        return companyid;
    }
    /** 诊所标识 */
    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }
    /** 发布人标识 */
    public String getUserid(){
        return this.userid;
    }
    /** 发布人标识 */
    public void setUserid(String userid){
        this.userid=userid;
    }
    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getAricledate(){
        return this.aricledate;
    }
    /** 发布时间 */
    public void setAricledate(Date aricledate){
        this.aricledate=aricledate;
    }
    /** 标题 */
    public String getTitle(){
        return this.title;
    }
    /** 标题 */
    public void setTitle(String title){
        this.title=title;
    }
    /** 摘要 */
    public String getAbstracts(){
        return this.abstracts;
    }
    /** 摘要 */
    public void setAbstracts(String abstracts){
        this.abstracts=abstracts;
    }
    /** 正文内容 */
    public String getContent(){
        return this.content;
    }
    /** 正文内容 */
    public void setContent(String content){
        this.content=content;
    }
    /** 来源作者 */
    public String getSources(){
        return this.sources;
    }
    /** 来源作者 */
    public void setSources(String sources){
        this.sources=sources;
    }
    /** 原文链接 */
    public String getOriginallink(){
        return this.originallink;
    }
    /** 原文链接 */
    public void setOriginallink(String originallink){
        this.originallink=originallink;
    }
    /** 审核人 */
    public String getAuditor(){
        return this.auditor;
    }
    /** 审核人 */
    public void setAuditor(String auditor){
        this.auditor=auditor;
    }
    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getAuditordate(){
        return this.auditordate;
    }
    /** 审核时间 */
    public void setAuditordate(Date auditordate){
        this.auditordate=auditordate;
    }
    /** 发布人名称 */
    public String getUsername(){
        return this.username;
    }
    /** 发布人名称 */
    public void setUsername(String username){
        this.username=username;
    }
    /** 审核状态（0：待审核;1：通过 2：不通过） */
    public String getAuditstatus(){
        return this.auditstatus;
    }
    /** 审核状态（0：待审核;1：通过 2：不通过） */
    public void setAuditstatus(String auditstatus){
        this.auditstatus=auditstatus;
    }
    /** 是否启用（0：是；1：否） */
    public String getIsLocked(){
        return this.isLocked;
    }
    /** 是否启用（0：是；1：否） */
    public void setIsLocked(String isLocked){
        this.isLocked=isLocked;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
