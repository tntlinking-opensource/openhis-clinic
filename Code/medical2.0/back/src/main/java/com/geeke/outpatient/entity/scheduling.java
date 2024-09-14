package com.geeke.outpatient.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.geeke.common.persistence.DataEntity;


import java.util.Date;
/**
 * 排班主表信息Entity
 * @author txl
 * @version 2022-07-20
 */

public class scheduling extends DataEntity<scheduling> {

    private static final long serialVersionUID = 1014474470772900019L;
    /** 诊所标识 */
    private String companyid;
    /** 用户标识 */
    private String userid ;
    /** 排班日期 */
    private Date schedulingtime ;
    /** 排班时间配置表主键 */
    private String  timeid ;

    /** 是否启用（0：是；1：否） */
    private String isLocked ;



    public scheduling() {
        super();
    }

    public scheduling(String id){
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
    /** 用户标识 */
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
    /** 排班日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getSchedulingtime(){
        return this.schedulingtime;
    }
    public void setSchedulingtime(Date schedulingtime){
        this.schedulingtime=schedulingtime;
    }
    /** 排班时间配置表主键 */
    public String  getTimeid(){
        return this.timeid;
    }
    /** 排班时间配置表主键 */
    public void setTimeid(String timeid){
        this.timeid=timeid;
    }
    /** 是否启用（0：是；1：否） */
    public String getIsLocked(){
        return this.isLocked;
    }
    /** 是否启用（0：是；1：否） */
    public void setIsLocked(String isLocked){
        this.isLocked=isLocked;
    }
}
