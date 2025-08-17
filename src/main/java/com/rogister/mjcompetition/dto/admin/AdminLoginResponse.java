package com.rogister.mjcompetition.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {
    private String token;
    private String username;
    private String name;
    private String role;
    private String email;
}
