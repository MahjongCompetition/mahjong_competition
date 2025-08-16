package com.rogister.mjcompetition.controller;

import com.rogister.mjcompetition.dto.ApiResponse;
import com.rogister.mjcompetition.entity.Team;
import com.rogister.mjcompetition.entity.TeamMember;
import com.rogister.mjcompetition.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {
    
    @Autowired
    private TeamService teamService;
    
    /**
     * 创建团队
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Team>> createTeam(@RequestBody Map<String, Object> request) {
        try {
            String teamName = (String) request.get("teamName");
            Long captainId = Long.valueOf(request.get("captainId").toString());
            Integer maxMembers = Integer.valueOf(request.get("maxMembers").toString());
            
            if (teamName == null || captainId == null || maxMembers == null) {
                return ResponseEntity.ok(ApiResponse.error("团队名称、队长ID和最大成员数不能为空"));
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
    public ResponseEntity<ApiResponse<Team>> joinTeam(@RequestBody Map<String, Object> request) {
        try {
            String teamCode = (String) request.get("teamCode");
            Long playerId = Long.valueOf(request.get("playerId").toString());
            
            if (teamCode == null || playerId == null) {
                return ResponseEntity.ok(ApiResponse.error("团队编号和玩家ID不能为空"));
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
    public ResponseEntity<ApiResponse<String>> leaveTeam(@RequestBody Map<String, Object> request) {
        try {
            Long teamId = Long.valueOf(request.get("teamId").toString());
            Long playerId = Long.valueOf(request.get("playerId").toString());
            
            if (teamId == null || playerId == null) {
                return ResponseEntity.ok(ApiResponse.error("团队ID和玩家ID不能为空"));
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
    public ResponseEntity<ApiResponse<String>> dissolveTeam(@RequestBody Map<String, Object> request) {
        try {
            Long teamId = Long.valueOf(request.get("teamId").toString());
            Long captainId = Long.valueOf(request.get("captainId").toString());
            
            if (teamId == null || captainId == null) {
                return ResponseEntity.ok(ApiResponse.error("团队ID和队长ID不能为空"));
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
