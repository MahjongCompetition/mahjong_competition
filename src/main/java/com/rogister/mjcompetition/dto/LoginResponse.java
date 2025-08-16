package com.rogister.mjcompetition.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String username;
    private String nickname;
    private String message;
    
    public LoginResponse(String token, String username, String nickname) {
        this.token = token;
        this.username = username;
        this.nickname = nickname;
        this.message = "登录成功";
    }
    
    public LoginResponse(String message) {
        this.message = message;
    }
}
