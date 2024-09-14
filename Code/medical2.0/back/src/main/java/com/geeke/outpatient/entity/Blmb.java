package com.geeke.outpatient.entity;

import com.geeke.common.persistence.DataEntity;
import lombok.Data;

import java.util.Date;

@Data
public class Blmb  {
    /** 模板编码 */
    private String mbbm ;
    /** 租户号 */
    private String companyId ;
    /** 删除标记;（0：正常；1：删除） */
    private String delFlag ;
    /** 创建人 */
    private String createdBy ;
    /** 创建时间 */
    private Date createdTime ;
    /** 更新人 */
    private String updatedBy ;
    /** 更新时间 */
    private Date updatedTime ;
    /** 模板名称 */
    private String mbmc ;
    /** 模板类型;(0：通用，1：个人) */
    private String mblx ;
    /** 病历类型;(0：西医病历，1：中医病历) */
    private String bllx ;
    /** 主诉 */
    private String zs ;
    /** 个人史 */
    private String grs ;
    /** 过敏史 */
    private String gms ;
    /** 疾病史 */
    private String jbs ;
    /** 传染病史 */
    private String crbs ;
    /** 手术史 */
    private String sss ;
    /** 输血史 */
    private String sxs ;
    /** 体格检查 */
    private String tgjc ;
    /** 急诊诊断 */
    private String jzzd ;
    /** 急诊效果 */
    private String jzxg ;
    /** 家族史 */
    private String jzs ;
    /** 月经史 */
    private String yjz ;
    /** 婚育史 */
    private String hys ;
    /** 辅助检查 */
    private String fzjc ;
    /** 现病史 */
    private String xbs ;
    /** 既往史 */
    private String jws ;
    /** 其他检查 */
    private String qtjc ;
    /** 处理情况 */
    private String clqk ;
    /** 流行病学史 */
    private String lxbxs ;
    /** 个体化健康教育 */
    private String gthjkjy ;
    /**
     * 创建人ID
     */
    private  String createdID;
    private  String cjr;
    /**
     * 体温
     */
    private String temperature;
    /**
     * 脉搏
     */
    private String pulse;
    /**
     * 呼吸
     */
    private String breathe;
    /**
     * 血压
     */
    private String bloodPressure;
    /**
     * 西医诊断
     */
    private String westernDiagnose;
    /**
     * 中医诊断
     */
    private String chinaDiagnose;

    public String getCjr() {
        return cjr;
    }

    public void setCjr(String cjr) {
        this.cjr = cjr;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getBreathe() {
        return breathe;
    }

    public void setBreathe(String breathe) {
        this.breathe = breathe;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getWesternDiagnose() {
        return westernDiagnose;
    }

    public void setWesternDiagnose(String westernDiagnose) {
        this.westernDiagnose = westernDiagnose;
    }

    public String getChinaDiagnose() {
        return chinaDiagnose;
    }

    public void setChinaDiagnose(String chinaDiagnose) {
        this.chinaDiagnose = chinaDiagnose;
    }

    /** 模板编码 */
    public String getMbbm(){
        return this.mbbm;
    }
    /** 模板编码 */
    public void setMbbm(String mbbm){
        this.mbbm=mbbm;
    }
    /** 租户号 */
    public String getCompanyId(){
        return this.companyId;
    }
    /** 租户号 */
    public void setCompanyId(String companyId){
        this.companyId=companyId;
    }
    /** 删除标记;（0：正常；1：删除） */
    public String getDelFlag(){
        return this.delFlag;
    }
    /** 删除标记;（0：正常；1：删除） */
    public void setDelFlag(String delFlag){
        this.delFlag=delFlag;
    }
    /** 创建人 */
    public String getCreatedBy(){
        return this.createdBy;
    }
    /** 创建人 */
    public void setCreatedBy(String createdBy){
        this.createdBy=createdBy;
    }
    /** 创建时间 */
    public Date getCreatedTime(){
        return this.createdTime;
    }
    /** 创建时间 */
    public void setCreatedTime(Date createdTime){
        this.createdTime=createdTime;
    }
    /** 更新人 */
    public String getUpdatedBy(){
        return this.updatedBy;
    }
    /** 更新人 */
    public void setUpdatedBy(String updatedBy){
        this.updatedBy=updatedBy;
    }
    /** 更新时间 */
    public Date getUpdatedTime(){
        return this.updatedTime;
    }
  /** 更新时间 */
    public void setUpdatedTime(Date updatedTime){
        this.updatedTime=updatedTime;
    }
    /** 模板名称 */
    public String getMbmc(){
        return this.mbmc;
    }
    /** 模板名称 */
    public void setMbmc(String mbmc){
        this.mbmc=mbmc;
    }
    /** 模板类型;(0：通用，1：个人) */
    public String getMblx(){
        return this.mblx;
    }
    /** 模板类型;(0：通用，1：个人) */
    public void setMblx(String mblx){
        this.mblx=mblx;
    }
    /** 病历类型;(0：西医病历，1：中医病历) */
    public String getBllx(){
        return this.bllx;
    }
    /** 病历类型;(0：西医病历，1：中医病历) */
    public void setBllx(String bllx){
        this.bllx=bllx;
    }
    /** 主诉 */
    public String getZs(){
        return this.zs;
    }
    /** 主诉 */
    public void setZs(String zs){
        this.zs=zs;
    }
    /** 个人史 */
    public String getGrs(){
        return this.grs;
    }
    /** 个人史 */
    public void setGrs(String grs){
        this.grs=grs;
    }
    /** 过敏史 */
    public String getGms(){
        return this.gms;
    }
    /** 过敏史 */
    public void setGms(String gms){
        this.gms=gms;
    }
    /** 疾病史 */
    public String getJbs(){
        return this.jbs;
    }
    /** 疾病史 */
    public void setJbs(String jbs){
        this.jbs=jbs;
    }
    /** 传染病史 */
    public String getCrbs(){
        return this.crbs;
    }
    /** 传染病史 */
    public void setCrbs(String crbs){
        this.crbs=crbs;
    }
    /** 手术史 */
    public String getSss(){
        return this.sss;
    }
    /** 手术史 */
    public void setSss(String sss){
        this.sss=sss;
    }
    /** 输血史 */
    public String getSxs(){
        return this.sxs;
    }
    /** 输血史 */
    public void setSxs(String sxs){
        this.sxs=sxs;
    }
    /** 体格检查 */
    public String getTgjc(){
        return this.tgjc;
    }
    /** 体格检查 */
    public void setTgjc(String tgjc){
        this.tgjc=tgjc;
    }
    /** 急诊诊断 */
    public String getJzzd(){
        return this.jzzd;
    }
    /** 急诊诊断 */
    public void setJzzd(String jzzd){
        this.jzzd=jzzd;
    }
    /** 急诊效果 */
    public String getJzxg(){
        return this.jzxg;
    }
    /** 急诊效果 */
    public void setJzxg(String jzxg){
        this.jzxg=jzxg;
    }
    /** 家族史 */
    public String getJzs(){
        return this.jzs;
    }
    /** 家族史 */
    public void setJzs(String jzs){
        this.jzs=jzs;
    }
    /** 月经史 */
    public String getYjz(){
        return this.yjz;
    }
    /** 月经史 */
    public void setYjz(String yjz){
        this.yjz=yjz;
    }
    /** 婚育史 */
    public String getHys(){
        return this.hys;
    }
    /** 婚育史 */
    public void setHys(String hys){
        this.hys=hys;
    }
    /** 辅助检查 */
    public String getFzjc(){
        return this.fzjc;
    }
    /** 辅助检查 */
    public void setFzjc(String fzjc){
        this.fzjc=fzjc;
    }
    /** 现病史 */
    public String getXbs(){
        return this.xbs;
    }
    /** 现病史 */
    public void setXbs(String xbs){
        this.xbs=xbs;
    }
    /** 既往史 */
    public String getJws(){
        return this.jws;
    }
    /** 既往史 */
    public void setJws(String jws){
        this.jws=jws;
    }
    /** 其他检查 */
    public String getQtjc(){
        return this.qtjc;
    }
    /** 其他检查 */
    public void setQtjc(String qtjc){
        this.qtjc=qtjc;
    }
    /** 其他检查 */
    public String getClqk(){
        return this.clqk;
    }
    /** 其他检查 */
    public void setClqk(String clqk){
        this.clqk=clqk;
    }
    /** 其他检查 */
    public String getGthjkjy(){
        return this.gthjkjy;
    }
    /** 其他检查 */
    public void setGthjkjy(String gthjkjy){
        this.gthjkjy=gthjkjy;
    }
    /** 流行病学史 */
    public String getLxbxs(){
        return this.lxbxs;
    }
    /** 流行病学史 */
    public void setLxbxs(String lxbxs){
        this.lxbxs=lxbxs;
    }
    /** 创建人ID */
    public String getCreatedID(){
        return this.createdID;
    }
    /** 创建人ID */
    public void setCreatedID(String createdID){
        this.createdID=createdID;
    }
}
