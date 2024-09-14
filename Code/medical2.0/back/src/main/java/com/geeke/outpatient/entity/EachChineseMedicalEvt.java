package com.geeke.outpatient.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 处方详情Entity
 * @author txl
 * @version 2022-06-07
 */
@Data
public class EachChineseMedicalEvt implements Serializable {

	private static final long serialVersionUID = 1014474470772900087L;
	//名称1
	private String nameOne;
	//名称2
	private String nameTwo;
	//名称3
	private String nameThree;
	//名称4
	private String nameFour;
}