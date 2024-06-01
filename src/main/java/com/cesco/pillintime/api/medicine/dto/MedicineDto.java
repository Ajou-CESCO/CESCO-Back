package com.cesco.pillintime.api.medicine.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MedicineDto {

    private String companyName;
    private String medicineName;
    private String medicineCode;
    private String medicineImage;
    private String medicineEffect;
    private String useMethod;
    private String useWarning;
    private String useSideEffect;
    private String depositMethod;
    private Map<String, String> typeNamelist;
}
