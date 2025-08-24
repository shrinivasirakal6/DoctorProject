package com.DAA.Dto;


import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DoctorDTO {
    private long doctorId;

    private String mobileNo;

    private String password;
    private String name;
    private String specialty;
    private String location;
    private  String email;
    private Boolean insuranceAcceptance;
    private String education;
    private String experience;
    private String type; // for distinguishing roles (doctor/patient)
    private String doctorImg;
    private Boolean validDoctor ;
}

