package com.geeke.workbench.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class Visitprogresspara {

    private int limit;
    private int offset;
    private String companyId;
    private String status;
    private String values;
    private String kssj;
    private  String jssj;
}
