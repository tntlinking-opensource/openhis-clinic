package com.geeke.outpatient.entity;

import lombok.Data;

import java.awt.*;
import java.util.List;

@Data
public class RecipelInfoDTO extends RecipelInfo{
    private List<RecipelDetail> recipelDetail;
    private Patient patient;
    private MedicalRecord medicalRecord;
}
