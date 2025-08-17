package com.rogister.mjcompetition.controller.player;

import com.rogister.mjcompetition.dto.common.ApiResponse;
import com.rogister.mjcompetition.dto.competition.CompetitionRegistrationRequest;
import com.rogister.mjcompetition.dto.team.TeamCompetitionRegistrationRequest;
import com.rogister.mjcompetition.entity.player.Player;
import com.rogister.mjcompetition.entity.player.PlayerCompetitionRegistration;
import com.rogister.mjcompetition.entity.team.TeamCompetitionRegistration;
import com.rogister.mjcompetition.service.player.PlayerCompetitionRegistrationService;
import com.rogister.mjcompetition.service.competition.CompetitionRegistrationService;
import com.rogister.mjcompetition.service.player.PlayerService;
import com.rogister.mjcompetition.service.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/api/player-competition-registrations")
@CrossOrigin(origins = "*")
@Tag(name = "比赛报名", description = "玩家和团队的比赛报名管理功能")
public class PlayerCompetitionRegistrationController {

    @Autowired
    private PlayerCompetitionRegistrationService registrationService;

    @Autowired
    private CompetitionRegistrationService competitionRegistrationService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    /**
     * 玩家报名比赛
     */
    @Operation(summary = "个人报名比赛", description = "玩家个人报名参加比赛")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "报名成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "报名失败")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PlayerCompetitionRegistration>> registerForCompetition(
            @Parameter(description = "JWT认证令牌", required = true) @RequestHeader("Authorization") String authorization,
            @Parameter(description = "比赛报名请求信息", required = true) @RequestBody CompetitionRegistrationRequest request) {
        try {
            // 从Authorization header中提取token
            String token = authorization.replace("Bearer ", "");

            // 验证token并获取用户名
            String username = playerService.extractUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.ok(ApiResponse.error("无效的token"));
            }

            // 根据用户名获取玩家信息
            Player player = playerService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("玩家不存在，用户名: " + username));

            Long playerId = player.getId();

            PlayerCompetitionRegistration registration = registrationService.registerForCompetition(playerId,
                    request.getCompetitionId());
            return ResponseEntity.ok(ApiResponse.success("报名成功", registration));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("服务器内部错误"));
        }
    }

    /**
     * 团队报名比赛
     */
    @PostMapping("/register-team")
    public ResponseEntity<ApiResponse<TeamCompetitionRegistration>> registerTeamForCompetition(
            @RequestHeader("Authorization") String authorization,
            @RequestBody TeamCompetitionRegistrationRequest request) {
        try {
            // 从Authorization header中提取token
            String token = authorization.replace("Bearer ", "");

            // 验证token并获取用户名
            String username = playerService.extractUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.ok(ApiResponse.error("无效的token"));
            }

            // 根据用户名获取玩家信息
            Player player = playerService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("玩家不存在，用户名: " + username));

            Long playerId = player.getId();

            // 验证teamId参数
            if (request.getTeamId() == null) {
                return ResponseEntity.ok(ApiResponse.error("团队ID不能为空"));
            }

            // 前置验证：检查用户是否有权限使用这个团队
            if (!teamService.isTeamMember(request.getTeamId(), playerId)) {
                return ResponseEntity.ok(ApiResponse.error("您不是该团队的成员，无法使用此团队报名"));
            }

            if (!teamService.isCaptain(request.getTeamId(), playerId)) {
                return ResponseEntity.ok(ApiResponse.error("只有队长可以代表团队报名比赛"));
            }

            // 调用统一报名接口进行团队报名
            Object registration = competitionRegistrationService.registerForCompetition(playerId,
                    request.getCompetitionId(), request.getTeamId());

            if (registration instanceof TeamCompetitionRegistration) {
                return ResponseEntity.ok(ApiResponse.success("团队报名成功", (TeamCompetitionRegistration) registration));
            } else {
                return ResponseEntity.ok(ApiResponse.error("报名失败：比赛类型不匹配，请确认这是团队赛"));
            }
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

            // 根据用户名获取玩家信息
            Player player = playerService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("玩家不存在，用户名: " + username));

            Long playerId = player.getId();

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
    public ResponseEntity<ApiResponse<List<PlayerCompetitionRegistration>>> getPlayerRegistrations(
            @PathVariable Long playerId) {
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
    public ResponseEntity<ApiResponse<List<PlayerCompetitionRegistration>>> getCompetitionRegistrations(
            @PathVariable Long competitionId) {
        try {
            List<PlayerCompetitionRegistration> registrations = registrationService
                    .getCompetitionRegistrations(competitionId);
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
    public ResponseEntity<ApiResponse<Boolean>> isPlayerRegistered(@PathVariable Long playerId,
            @PathVariable Long competitionId) {
        try {
            boolean isRegistered = registrationService.isPlayerRegistered(playerId, competitionId);
            return ResponseEntity.ok(ApiResponse.success(isRegistered));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("检查报名状态失败"));
        }
    }
}
