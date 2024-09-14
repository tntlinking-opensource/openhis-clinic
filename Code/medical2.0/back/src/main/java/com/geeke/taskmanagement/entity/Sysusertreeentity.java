package com.geeke.taskmanagement.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Sysusertreeentity {
    private String id;
    private String companyId;
    private String name;
    private String patid;
    private String patname;
    List chirldren = new ArrayList<>();
}
