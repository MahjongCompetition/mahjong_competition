package com.rogister.mjcompetition.controller;

import com.rogister.mjcompetition.dto.ApiResponse;
import com.rogister.mjcompetition.dto.LoginRequest;
import com.rogister.mjcompetition.dto.LoginResponse;
import com.rogister.mjcompetition.entity.Player;
import com.rogister.mjcompetition.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private PlayerService playerService;
    
    /**
     * 玩家登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = playerService.login(loginRequest.getUsername(), loginRequest.getPassword());
            Player player = playerService.findByUsername(loginRequest.getUsername()).orElse(null);
            
            if (player != null) {
                LoginResponse loginResponse = new LoginResponse(token, player.getUsername(), player.getNickname());
                return ResponseEntity.ok(ApiResponse.success("登录成功", loginResponse));
            } else {
                return ResponseEntity.ok(ApiResponse.error("登录失败"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("服务器内部错误"));
        }
    }
    
    /**
     * 玩家注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Player>> register(@RequestBody Player player) {
        try {
            Player createdPlayer = playerService.createPlayer(player);
            return ResponseEntity.ok(ApiResponse.success("注册成功", createdPlayer));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("服务器内部错误"));
        }
    }
}
