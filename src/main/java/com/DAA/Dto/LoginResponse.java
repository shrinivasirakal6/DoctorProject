package com.DAA.Dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String jwtToken;
    private String tokenType;
}
