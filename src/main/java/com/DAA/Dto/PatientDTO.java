package com.DAA.Dto;


import lombok.Data;

@Data
public class PatientDTO {
    private Long id;
    private String name;
    private String email;
    private String mobileNo;

    private String password; // usually you won’t return this in responses

}

