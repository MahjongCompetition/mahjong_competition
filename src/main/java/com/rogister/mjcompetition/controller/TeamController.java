package com.rogister.mjcompetition.controller;

import com.rogister.mjcompetition.dto.ApiResponse;
import com.rogister.mjcompetition.entity.Team;
import com.rogister.mjcompetition.entity.TeamMember;
import com.rogister.mjcompetition.entity.Player;
import com.rogister.mjcompetition.service.TeamService;
import com.rogister.mjcompetition.service.PlayerService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
@Tag(name = "团队管理", description = "团队创建、加入、查询、退出等功能")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 从请求中获取当前登录的玩家
     */
    private Player getCurrentPlayer(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("未提供有效的认证token");
        }

        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            throw new RuntimeException("Token格式无效");
        }

        if (!jwtUtil.validateToken(token, username)) {
            throw new RuntimeException("Token无效或已过期");
        }

        return playerService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("玩家不存在，用户名: " + username));
    }

    /**
     * 创建团队
     */
    @Operation(summary = "创建团队", description = "创建新的团队，当前玩家自动成为队长")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "创建成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "创建失败")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Team>> createTeam(
            @Parameter(description = "团队创建信息，包含teamName和maxMembers", required = true) @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        try {
            // 从JWT token中获取当前玩家作为队长
            Player captain = getCurrentPlayer(httpRequest);
            Long captainId = captain.getId();

            // 获取请求参数
            String teamName = (String) request.get("teamName");
            Object maxMembersObj = request.get("maxMembers");

            if (teamName == null || maxMembersObj == null) {
                return ResponseEntity.ok(ApiResponse.error("团队名称和最大成员数不能为空"));
            }

            Integer maxMembers;
            try {
                maxMembers = Integer.valueOf(maxMembersObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.ok(ApiResponse.error("最大成员数必须是有效的数字"));
            }

            if (maxMembers < 2 || maxMembers > 10) {
                return ResponseEntity.ok(ApiResponse.error("团队人数必须在2-10人之间"));
            }

            Team team = teamService.createTeam(teamName, captainId, maxMembers);
            return ResponseEntity.ok(ApiResponse.success("团队创建成功", team));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建团队失败"));
        }
    }

    /**
     * 加入团队
     */
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Team>> joinTeam(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        try {
            // 从JWT token中获取当前玩家
            Player player = getCurrentPlayer(httpRequest);
            Long playerId = player.getId();

            String teamCode = (String) request.get("teamCode");

            if (teamCode == null) {
                return ResponseEntity.ok(ApiResponse.error("团队编号不能为空"));
            }

            TeamMember member = teamService.joinTeam(teamCode, playerId);
            Optional<Team> teamOpt = teamService.findByTeamCode(teamCode);

            if (teamOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("成功加入团队", teamOpt.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.error("加入团队失败"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("加入团队失败"));
        }
    }

    /**
     * 离开团队
     */
    @PostMapping("/leave")
    public ResponseEntity<ApiResponse<String>> leaveTeam(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        try {
            // 从JWT token中获取当前玩家
            Player player = getCurrentPlayer(httpRequest);
            Long playerId = player.getId();

            Object teamIdObj = request.get("teamId");

            if (teamIdObj == null) {
                return ResponseEntity.ok(ApiResponse.error("团队ID不能为空"));
            }

            Long teamId;
            try {
                teamId = Long.valueOf(teamIdObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.ok(ApiResponse.error("团队ID必须是有效的数字"));
            }

            teamService.leaveTeam(teamId, playerId);
            return ResponseEntity.ok(ApiResponse.success("成功离开团队", null));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("离开团队失败"));
        }
    }

    /**
     * 解散团队
     */
    @PostMapping("/dissolve")
    public ResponseEntity<ApiResponse<String>> dissolveTeam(@RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        try {
            // 从JWT token中获取当前玩家作为队长
            Player captain = getCurrentPlayer(httpRequest);
            Long captainId = captain.getId();

            Object teamIdObj = request.get("teamId");

            if (teamIdObj == null) {
                return ResponseEntity.ok(ApiResponse.error("团队ID不能为空"));
            }

            Long teamId;
            try {
                teamId = Long.valueOf(teamIdObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.ok(ApiResponse.error("团队ID必须是有效的数字"));
            }

            teamService.dissolveTeam(teamId, captainId);
            return ResponseEntity.ok(ApiResponse.success("团队解散成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("解散团队失败"));
        }
    }

    /**
     * 根据团队编号查找团队
     */
    @GetMapping("/code/{teamCode}")
    public ResponseEntity<ApiResponse<Team>> getTeamByCode(@PathVariable String teamCode) {
        try {
            Optional<Team> teamOpt = teamService.findByTeamCode(teamCode);
            if (teamOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("获取团队信息成功", teamOpt.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.error("团队不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取团队信息失败"));
        }
    }

    /**
     * 根据队长ID查找团队
     */
    @GetMapping("/captain/{captainId}")
    public ResponseEntity<ApiResponse<List<Team>>> getTeamsByCaptain(@PathVariable Long captainId) {
        try {
            List<Team> teams = teamService.findByCaptainId(captainId);
            return ResponseEntity.ok(ApiResponse.success("获取队长团队列表成功", teams));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取队长团队列表失败"));
        }
    }

    /**
     * 根据玩家ID查找所属团队
     */
    @GetMapping("/player/{playerId}")
    public ResponseEntity<ApiResponse<Team>> getTeamByPlayer(@PathVariable Long playerId) {
        try {
            Optional<Team> teamOpt = teamService.findTeamByPlayerId(playerId);
            if (teamOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("获取玩家团队信息成功", teamOpt.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.success("玩家未加入任何团队", null));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取玩家团队信息失败"));
        }
    }

    /**
     * 获取团队所有成员
     */
    @GetMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<List<TeamMember>>> getTeamMembers(@PathVariable Long teamId) {
        try {
            List<TeamMember> members = teamService.getTeamMembers(teamId);
            return ResponseEntity.ok(ApiResponse.success("获取团队成员列表成功", members));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取团队成员列表失败"));
        }
    }

    /**
     * 检查玩家是否是队长
     */
    @GetMapping("/{teamId}/is-captain/{playerId}")
    public ResponseEntity<ApiResponse<Boolean>> isCaptain(@PathVariable Long teamId, @PathVariable Long playerId) {
        try {
            boolean isCaptain = teamService.isCaptain(teamId, playerId);
            return ResponseEntity.ok(ApiResponse.success("检查队长身份成功", isCaptain));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("检查队长身份失败"));
        }
    }

    /**
     * 检查玩家是否是团队成员
     */
    @GetMapping("/{teamId}/is-member/{playerId}")
    public ResponseEntity<ApiResponse<Boolean>> isTeamMember(@PathVariable Long teamId, @PathVariable Long playerId) {
        try {
            boolean isMember = teamService.isTeamMember(teamId, playerId);
            return ResponseEntity.ok(ApiResponse.success("检查团队成员身份成功", isMember));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("检查团队成员身份失败"));
        }
    }
}
