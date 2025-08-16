package com.rogister.mjcompetition.controller;

import com.rogister.mjcompetition.dto.ApiResponse;
import com.rogister.mjcompetition.dto.CompetitionRegistrationRequest;
import com.rogister.mjcompetition.entity.PlayerCompetitionRegistration;
import com.rogister.mjcompetition.service.PlayerCompetitionRegistrationService;
import com.rogister.mjcompetition.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/player-registrations")
@CrossOrigin(origins = "*")
public class PlayerCompetitionRegistrationController {
    
    @Autowired
    private PlayerCompetitionRegistrationService registrationService;
    
    @Autowired
    private PlayerService playerService;
    
    /**
     * 玩家报名比赛
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PlayerCompetitionRegistration>> registerForCompetition(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CompetitionRegistrationRequest request) {
        try {
            // 从Authorization header中提取token
            String token = authorization.replace("Bearer ", "");
            
            // 验证token并获取用户名
            String username = playerService.extractUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.ok(ApiResponse.error("无效的token"));
            }
            
            // 获取玩家ID（这里需要根据用户名查找玩家ID）
            // 为了简化，我们假设token中包含足够的信息
            // 在实际应用中，你可能需要从token中提取更多信息
            
            // 这里需要根据实际情况调整
            Long playerId = 1L; // 临时硬编码，实际应该从token或数据库中获取
            
            PlayerCompetitionRegistration registration = registrationService.registerForCompetition(playerId, request.getCompetitionId());
            return ResponseEntity.ok(ApiResponse.success("报名成功", registration));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("服务器内部错误"));
        }
    }
    
    /**
     * 取消报名
     */
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> cancelRegistration(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CompetitionRegistrationRequest request) {
        try {
            String token = authorization.replace("Bearer ", "");
            String username = playerService.extractUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.ok(ApiResponse.error("无效的token"));
            }
            
            Long playerId = 1L; // 临时硬编码
            
            registrationService.cancelRegistration(playerId, request.getCompetitionId());
            return ResponseEntity.ok(ApiResponse.success("取消报名成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("服务器内部错误"));
        }
    }
    
    /**
     * 获取玩家的所有报名记录
     */
    @GetMapping("/player/{playerId}")
    public ResponseEntity<ApiResponse<List<PlayerCompetitionRegistration>>> getPlayerRegistrations(@PathVariable Long playerId) {
        try {
            List<PlayerCompetitionRegistration> registrations = registrationService.getPlayerRegistrations(playerId);
            return ResponseEntity.ok(ApiResponse.success(registrations));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取报名记录失败"));
        }
    }
    
    /**
     * 获取比赛的所有报名玩家
     */
    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<ApiResponse<List<PlayerCompetitionRegistration>>> getCompetitionRegistrations(@PathVariable Long competitionId) {
        try {
            List<PlayerCompetitionRegistration> registrations = registrationService.getCompetitionRegistrations(competitionId);
            return ResponseEntity.ok(ApiResponse.success(registrations));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取报名记录失败"));
        }
    }
    
    /**
     * 检查玩家是否已报名某场比赛
     */
    @GetMapping("/check/{playerId}/{competitionId}")
    public ResponseEntity<ApiResponse<Boolean>> isPlayerRegistered(@PathVariable Long playerId, @PathVariable Long competitionId) {
        try {
            boolean isRegistered = registrationService.isPlayerRegistered(playerId, competitionId);
            return ResponseEntity.ok(ApiResponse.success(isRegistered));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("检查报名状态失败"));
        }
    }
}
