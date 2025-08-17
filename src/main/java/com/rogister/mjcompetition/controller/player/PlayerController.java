package com.rogister.mjcompetition.controller.player;

import com.rogister.mjcompetition.dto.common.ApiResponse;
import com.rogister.mjcompetition.dto.player.PlayerTeamsResponse;
import com.rogister.mjcompetition.dto.player.PlayerLoginRequest;
import com.rogister.mjcompetition.dto.common.ChangePasswordRequest;
import com.rogister.mjcompetition.dto.player.PlayerStatusUpdateRequest;
import com.rogister.mjcompetition.entity.player.Player;
import com.rogister.mjcompetition.entity.team.Team;
import com.rogister.mjcompetition.service.player.PlayerService;
import com.rogister.mjcompetition.service.team.TeamService;
import com.rogister.mjcompetition.util.JwtUtil;
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

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
@CrossOrigin(origins = "*")
@Tag(name = "玩家管理", description = "玩家注册、登录、信息管理等功能")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 玩家注册账户
     */
    @Operation(summary = "玩家注册", description = "注册新的玩家账户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "注册成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "注册失败")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerPlayer(
            @Parameter(description = "玩家注册信息", required = true) @RequestBody Player player) {
        try {
            playerService.createPlayer(player);
            return ResponseEntity.ok(ApiResponse.success("注册成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("注册失败"));
        }
    }

    /**
     * 玩家登录
     */
    @Operation(summary = "玩家登录", description = "玩家使用用户名和密码登录系统")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "登录失败")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> loginPlayer(
            @Parameter(description = "玩家登录请求信息", required = true) @RequestBody PlayerLoginRequest loginRequest) {
        try {
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            if (username == null || password == null) {
                return ResponseEntity.ok(ApiResponse.error("用户名和密码不能为空"));
            }

            String token = playerService.login(username, password);
            return ResponseEntity.ok(ApiResponse.success("登录成功", token));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("登录失败"));
        }
    }

    /**
     * 获取当前登录玩家的信息
     */
    @Operation(summary = "获取玩家信息", description = "获取当前登录玩家的详细信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Player>> getPlayerProfile(HttpServletRequest request) {
        try {
            // 从请求头中获取JWT token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.ok(ApiResponse.error("未提供有效的认证token"));
            }

            String token = authHeader.substring(7);

            // 先从token中提取用户名
            String username;
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                return ResponseEntity.ok(ApiResponse.error("Token格式无效"));
            }

            // 验证token和用户名
            if (!jwtUtil.validateToken(token, username)) {
                return ResponseEntity.ok(ApiResponse.error("Token无效或已过期"));
            }

            // 根据用户名查找玩家信息
            return playerService.findByUsername(username)
                    .map(player -> ResponseEntity.ok(ApiResponse.success(player)))
                    .orElse(ResponseEntity.ok(ApiResponse.error("玩家不存在")));

        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取玩家信息失败: " + e.getMessage()));
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Player>> changePassword(@PathVariable Long id,
            @RequestBody ChangePasswordRequest passwordRequest) {
        try {
            String oldPassword = passwordRequest.getOldPassword();
            String newPassword = passwordRequest.getNewPassword();

            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.ok(ApiResponse.error("旧密码和新密码不能为空"));
            }

            // 这里需要调用PlayerService的changePassword方法，但当前没有这个方法
            // 暂时返回错误信息
            return ResponseEntity.ok(ApiResponse.error("修改密码功能暂未实现"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("修改密码失败"));
        }
    }

    /**
     * 获取当前玩家的团队信息
     * 
     * @param type 可选参数：
     *             - 不传或为空：返回我加入的和我创建的所有团队
     *             - "created"：只返回我创建的团队
     */
    @GetMapping("/my-teams")
    public ResponseEntity<ApiResponse<PlayerTeamsResponse>> getMyTeams(
            HttpServletRequest request,
            @RequestParam(value = "type", required = false) String type) {
        try {
            // 从请求头中获取JWT token并提取玩家信息
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.ok(ApiResponse.error("未提供有效的认证token"));
            }

            String token = authHeader.substring(7);
            String username;
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                return ResponseEntity.ok(ApiResponse.error("Token格式无效"));
            }

            if (!jwtUtil.validateToken(token, username)) {
                return ResponseEntity.ok(ApiResponse.error("Token无效或已过期"));
            }

            // 根据用户名查找玩家
            Player player = playerService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("玩家不存在，用户名: " + username));

            Long playerId = player.getId();

            // 根据参数决定返回的团队类型
            List<Team> createdTeams = teamService.findByCaptainId(playerId);
            List<Team> joinedTeams;

            if ("created".equals(type)) {
                // 只返回我创建的团队
                joinedTeams = createdTeams; // 创建的团队也是加入的团队
                PlayerTeamsResponse response = new PlayerTeamsResponse(createdTeams, joinedTeams);
                return ResponseEntity.ok(ApiResponse.success("获取我创建的团队信息成功", response));
            } else {
                // 默认情况：返回我加入的和我创建的所有团队
                joinedTeams = teamService.findAllTeamsByPlayerId(playerId);
                PlayerTeamsResponse response = new PlayerTeamsResponse(createdTeams, joinedTeams);
                return ResponseEntity.ok(ApiResponse.success("获取团队信息成功", response));
            }

        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取团队信息失败: " + e.getMessage()));
        }
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkUsernameAvailability(@RequestParam String username) {
        try {
            boolean isAvailable = !playerService.findByUsername(username).isPresent();
            return ResponseEntity.ok(ApiResponse.success(Map.of("available", isAvailable)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("检查用户名可用性失败"));
        }
    }

    /**
     * 检查QQ是否可用
     */
    @GetMapping("/check-qq")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkQqAvailability(@RequestParam String qq) {
        try {
            // 这里需要调用PlayerService的checkQq方法，但当前没有这个方法
            // 暂时返回错误信息
            return ResponseEntity.ok(ApiResponse.error("检查QQ可用性功能暂未实现"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("检查QQ可用性失败"));
        }
    }

    /**
     * 检查雀魂ID是否可用
     */
    @GetMapping("/check-mahjong-id")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkMahjongIdAvailability(
            @RequestParam String mahjongId) {
        try {
            // 这里需要调用PlayerService的checkMahjongId方法，但当前没有这个方法
            // 暂时返回错误信息
            return ResponseEntity.ok(ApiResponse.error("检查雀魂ID可用性功能暂未实现"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("检查雀魂ID可用性失败"));
        }
    }

    /**
     * 获取所有玩家
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Player>>> getAllPlayers() {
        try {
            List<Player> players = playerService.findAllPlayers();
            return ResponseEntity.ok(ApiResponse.success(players));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取玩家列表失败"));
        }
    }

    /**
     * 根据ID获取玩家
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Player>> getPlayerById(@PathVariable Long id) {
        try {
            return playerService.findById(id)
                    .map(player -> ResponseEntity.ok(ApiResponse.success(player)))
                    .orElse(ResponseEntity.ok(ApiResponse.error("玩家不存在")));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取玩家信息失败"));
        }
    }

    /**
     * 根据用户名获取玩家
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<Player>> getPlayerByUsername(@PathVariable String username) {
        try {
            return playerService.findByUsername(username)
                    .map(player -> ResponseEntity.ok(ApiResponse.success(player)))
                    .orElse(ResponseEntity.ok(ApiResponse.error("玩家不存在")));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取玩家信息失败"));
        }
    }

    /**
     * 根据QQ获取玩家
     */
    @GetMapping("/qq/{qq}")
    public ResponseEntity<ApiResponse<Player>> getPlayerByQq(@PathVariable String qq) {
        try {
            // 这里需要调用PlayerService的findByQq方法，但当前没有这个方法
            // 暂时返回错误信息
            return ResponseEntity.ok(ApiResponse.error("根据QQ查找玩家功能暂未实现"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取玩家信息失败"));
        }
    }

    /**
     * 根据雀魂ID获取玩家
     */
    @GetMapping("/mahjong/{mahjongId}")
    public ResponseEntity<ApiResponse<Player>> getPlayerByMahjongId(@PathVariable String mahjongId) {
        try {
            // 这里需要调用PlayerService的findByMahjongId方法，但当前没有这个方法
            // 暂时返回错误信息
            return ResponseEntity.ok(ApiResponse.error("根据雀魂ID查找玩家功能暂未实现"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取玩家信息失败"));
        }
    }

    /**
     * 根据昵称搜索玩家
     */
    @GetMapping("/search/nickname")
    public ResponseEntity<ApiResponse<List<Player>>> searchPlayersByNickname(@RequestParam String nickname) {
        try {
            // 这里需要调用PlayerService的findByNickname方法，但当前没有这个方法
            // 暂时返回错误信息
            return ResponseEntity.ok(ApiResponse.error("根据昵称搜索玩家功能暂未实现"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("搜索玩家失败"));
        }
    }

    /**
     * 根据雀魂昵称搜索玩家
     */
    @GetMapping("/search/mahjong-nickname")
    public ResponseEntity<ApiResponse<List<Player>>> searchPlayersByMahjongNickname(
            @RequestParam String mahjongNickname) {
        try {
            // 这里需要调用PlayerService的findByMahjongNickname方法，但当前没有这个方法
            // 暂时返回错误信息
            return ResponseEntity.ok(ApiResponse.error("根据雀魂昵称搜索玩家功能暂未实现"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("搜索玩家失败"));
        }
    }

    /**
     * 更新玩家信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Player>> updatePlayer(@PathVariable Long id, @RequestBody Player playerDetails) {
        try {
            Player updatedPlayer = playerService.updatePlayer(id, playerDetails);
            return ResponseEntity.ok(ApiResponse.success("更新玩家信息成功", updatedPlayer));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新玩家信息失败"));
        }
    }

    /**
     * 启用/禁用玩家账户
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Player>> togglePlayerStatus(@PathVariable Long id,
            @RequestBody PlayerStatusUpdateRequest statusRequest) {
        try {
            Boolean isActive = statusRequest.getIsActive();
            if (isActive == null) {
                return ResponseEntity.ok(ApiResponse.error("状态参数不能为空"));
            }

            // 这里需要调用PlayerService的togglePlayerStatus方法，但当前没有这个方法
            // 暂时返回错误信息
            return ResponseEntity.ok(ApiResponse.error("切换玩家状态功能暂未实现"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("切换玩家状态失败"));
        }
    }

    /**
     * 删除玩家
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePlayer(@PathVariable Long id) {
        try {
            playerService.deletePlayer(id);
            return ResponseEntity.ok(ApiResponse.success("删除玩家成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除玩家失败"));
        }
    }
}