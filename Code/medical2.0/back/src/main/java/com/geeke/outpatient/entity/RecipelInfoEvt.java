package com.geeke.outpatient.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeke.common.persistence.DataEntity;
import com.geeke.org.entity.Company;
import com.geeke.sys.entity.DictItem;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;

/**
 * 处方信息Evt
 * @author txl
 * @version 2022-06-07
 */
public class RecipelInfoEvt {

	private static final long serialVersionUID = 1014474470772900008L;

	private RecipelInfo recipelInfo;
	private List<RecipelDetail> recipelDetailEvtList;


    public RecipelInfo getRecipelInfo() {
        return recipelInfo;
    }

    public void setRecipelInfo(RecipelInfo recipelInfo) {
        this.recipelInfo = recipelInfo;
    }

    public List<RecipelDetail> getRecipelDetailEvtList() {
        return recipelDetailEvtList;
    }

    public void setRecipelDetailEvtList(List<RecipelDetail> recipelDetailEvtList) {
        this.recipelDetailEvtList = recipelDetailEvtList;
    }
}