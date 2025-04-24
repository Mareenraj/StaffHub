package com.mareenraj.ems_backend.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
