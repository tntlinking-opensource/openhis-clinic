package com.geeke.stock.entity;

import lombok.Getter;

@Getter
public enum InstantPatient
{
    RETAIL("零售收费", 2),
    INSTANT("快速接诊",1),
    NORMAL("普通接诊",0);

    InstantPatient(String name, int code) {
        this.name = name;
        this.code = code;
    }

    private String name;
    private int code;
}
