package com.geeke.outpatient.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class RecipelStastics
{
    @Tolerate
    public RecipelStastics(){

    }

    //本次明细的id
    private String id;
    //本次明细属于什么类型
    private String stuffType;
    //本次明细占用数目
    private int occupy;
}
