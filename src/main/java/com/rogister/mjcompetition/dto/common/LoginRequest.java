package com.rogister.mjcompetition.dto.common;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
