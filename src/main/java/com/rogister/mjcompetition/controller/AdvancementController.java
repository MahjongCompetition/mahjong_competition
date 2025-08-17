package com.rogister.mjcompetition.controller;

import com.rogister.mjcompetition.dto.ApiResponse;
import com.rogister.mjcompetition.entity.PlayerRoundStatus;
import com.rogister.mjcompetition.entity.TeamRoundStatus;
import com.rogister.mjcompetition.service.AdvancementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/advancement")
@CrossOrigin(origins = "*")
@Tag(name = "晋级管理", description = "个人赛和团队赛的晋级管理功能")
public class AdvancementController {
    
    @Autowired
    private AdvancementService advancementService;
    
    /**
     * 个人赛晋级到指定轮次
     */
    @Operation(summary = "个人赛晋级", description = "将指定玩家晋级到指定轮次")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "晋级成功", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "晋级失败")
    })
    @PostMapping("/players/advance")
    public ResponseEntity<ApiResponse<List<PlayerRoundStatus>>> advancePlayersToRound(
            @Parameter(description = "晋级请求，包含competitionId、playerIds、targetRound、initialScore", required = true)
            @RequestBody Map<String, Object> request) {
        try {
            Long competitionId = Long.valueOf(request.get("competitionId").toString());
            @SuppressWarnings("unchecked")
            List<Object> playerIdsObj = (List<Object>) request.get("playerIds");
            List<Long> playerIds = playerIdsObj.stream()
                    .map(id -> Long.valueOf(id.toString()))
                    .toList();
            // 支持 targetRound 或 roundNumber 字段名
            Object targetRoundObj = request.get("targetRound");
            if (targetRoundObj == null) {
                targetRoundObj = request.get("roundNumber");
            }
            Integer targetRound = Integer.valueOf(targetRoundObj.toString());
            Integer initialScore = Integer.valueOf(request.get("initialScore").toString());
            
            if (competitionId == null || playerIds == null || targetRound == null || initialScore == null) {
                return ResponseEntity.ok(ApiResponse.error("比赛ID、玩家ID列表、目标轮次和初始得分不能为空"));
            }
            
            if (playerIds.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("玩家ID列表不能为空"));
            }
            
            if (targetRound <= 1) {
                return ResponseEntity.ok(ApiResponse.error("目标轮次必须大于1"));
            }
            
            List<PlayerRoundStatus> result = advancementService.advancePlayersToRound(
                    competitionId, playerIds, targetRound, initialScore);
            
            return ResponseEntity.ok(ApiResponse.success("个人赛晋级成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("个人赛晋级失败"));
        }
    }
    
    /**
     * 团队赛晋级到指定轮次
     */
    @PostMapping("/teams/advance")
    public ResponseEntity<ApiResponse<List<TeamRoundStatus>>> advanceTeamsToRound(@RequestBody Map<String, Object> request) {
        try {
            Long competitionId = Long.valueOf(request.get("competitionId").toString());
            @SuppressWarnings("unchecked")
            List<Object> teamIdsObj = (List<Object>) request.get("teamIds");
            List<Long> teamIds = teamIdsObj.stream()
                    .map(id -> Long.valueOf(id.toString()))
                    .toList();
            // 支持 targetRound 或 roundNumber 字段名
            Object targetRoundObj = request.get("targetRound");
            if (targetRoundObj == null) {
                targetRoundObj = request.get("roundNumber");
            }
            Integer targetRound = Integer.valueOf(targetRoundObj.toString());
            Integer initialScore = Integer.valueOf(request.get("initialScore").toString());
            
            if (competitionId == null || teamIds == null || targetRound == null || initialScore == null) {
                return ResponseEntity.ok(ApiResponse.error("比赛ID、团队ID列表、目标轮次和初始得分不能为空"));
            }
            
            if (teamIds.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("团队ID列表不能为空"));
            }
            
            if (targetRound <= 1) {
                return ResponseEntity.ok(ApiResponse.error("目标轮次必须大于1"));
            }
            
            List<TeamRoundStatus> result = advancementService.advanceTeamsToRound(
                    competitionId, teamIds, targetRound, initialScore);
            
            return ResponseEntity.ok(ApiResponse.success("团队赛晋级成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("团队赛晋级失败"));
        }
    }
    
    /**
     * 获取比赛当前最高轮次
     */
    @GetMapping("/competition/{competitionId}/max-round")
    public ResponseEntity<ApiResponse<Integer>> getCurrentMaxRound(@PathVariable Long competitionId) {
        try {
            Integer maxRound = advancementService.getCurrentMaxRound(competitionId);
            return ResponseEntity.ok(ApiResponse.success("获取最高轮次成功", maxRound));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取最高轮次失败"));
        }
    }
    
    /**
     * 获取比赛指定轮次的所有个人赛参与者
     */
    @GetMapping("/competition/{competitionId}/round/{roundNumber}/players")
    public ResponseEntity<ApiResponse<List<PlayerRoundStatus>>> getPlayersInRound(
            @PathVariable Long competitionId, @PathVariable Integer roundNumber) {
        try {
            List<PlayerRoundStatus> players = advancementService.getPlayersInRound(competitionId, roundNumber);
            return ResponseEntity.ok(ApiResponse.success("获取轮次参与者成功", players));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取轮次参与者失败"));
        }
    }
    
    /**
     * 获取比赛指定轮次的所有团队赛参与者
     */
    @GetMapping("/competition/{competitionId}/round/{roundNumber}/teams")
    public ResponseEntity<ApiResponse<List<TeamRoundStatus>>> getTeamsInRound(
            @PathVariable Long competitionId, @PathVariable Integer roundNumber) {
        try {
            List<TeamRoundStatus> teams = advancementService.getTeamsInRound(competitionId, roundNumber);
            return ResponseEntity.ok(ApiResponse.success("获取轮次参与者成功", teams));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取轮次参与者失败"));
        }
    }
    
    /**
     * 更新玩家轮次得分
     */
    @PutMapping("/players/score")
    public ResponseEntity<ApiResponse<PlayerRoundStatus>> updatePlayerScore(@RequestBody Map<String, Object> request) {
        try {
            Long playerId = Long.valueOf(request.get("playerId").toString());
            Long competitionId = Long.valueOf(request.get("competitionId").toString());
            Integer roundNumber = Integer.valueOf(request.get("roundNumber").toString());
            Integer newScore = Integer.valueOf(request.get("newScore").toString());
            
            if (playerId == null || competitionId == null || roundNumber == null || newScore == null) {
                return ResponseEntity.ok(ApiResponse.error("玩家ID、比赛ID、轮次号和得分不能为空"));
            }
            
            PlayerRoundStatus result = advancementService.updatePlayerScore(
                    playerId, competitionId, roundNumber, newScore);
            
            return ResponseEntity.ok(ApiResponse.success("更新玩家得分成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新玩家得分失败"));
        }
    }
    
    /**
     * 更新团队轮次得分
     */
    @PutMapping("/teams/score")
    public ResponseEntity<ApiResponse<TeamRoundStatus>> updateTeamScore(@RequestBody Map<String, Object> request) {
        try {
            Long teamId = Long.valueOf(request.get("teamId").toString());
            Long competitionId = Long.valueOf(request.get("competitionId").toString());
            Integer roundNumber = Integer.valueOf(request.get("roundNumber").toString());
            Integer newScore = Integer.valueOf(request.get("newScore").toString());
            
            if (teamId == null || competitionId == null || roundNumber == null || newScore == null) {
                return ResponseEntity.ok(ApiResponse.error("团队ID、比赛ID、轮次号和得分不能为空"));
            }
            
            TeamRoundStatus result = advancementService.updateTeamScore(
                    teamId, competitionId, roundNumber, newScore);
            
            return ResponseEntity.ok(ApiResponse.success("更新团队得分成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新团队得分失败"));
        }
    }
    
    /**
     * 淘汰玩家
     */
    @PutMapping("/players/eliminate")
    public ResponseEntity<ApiResponse<PlayerRoundStatus>> eliminatePlayer(@RequestBody Map<String, Object> request) {
        try {
            Long playerId = Long.valueOf(request.get("playerId").toString());
            Long competitionId = Long.valueOf(request.get("competitionId").toString());
            Integer roundNumber = Integer.valueOf(request.get("roundNumber").toString());
            
            if (playerId == null || competitionId == null || roundNumber == null) {
                return ResponseEntity.ok(ApiResponse.error("玩家ID、比赛ID和轮次号不能为空"));
            }
            
            PlayerRoundStatus result = advancementService.eliminatePlayer(
                    playerId, competitionId, roundNumber);
            
            return ResponseEntity.ok(ApiResponse.success("淘汰玩家成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("淘汰玩家失败"));
        }
    }
    
    /**
     * 淘汰团队
     */
    @PutMapping("/teams/eliminate")
    public ResponseEntity<ApiResponse<TeamRoundStatus>> eliminateTeam(@RequestBody Map<String, Object> request) {
        try {
            Long teamId = Long.valueOf(request.get("teamId").toString());
            Long competitionId = Long.valueOf(request.get("competitionId").toString());
            Integer roundNumber = Integer.valueOf(request.get("roundNumber").toString());
            
            if (teamId == null || competitionId == null || roundNumber == null) {
                return ResponseEntity.ok(ApiResponse.error("团队ID、比赛ID和轮次号不能为空"));
            }
            
            TeamRoundStatus result = advancementService.eliminateTeam(
                    teamId, competitionId, roundNumber);
            
            return ResponseEntity.ok(ApiResponse.success("淘汰团队成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("淘汰团队失败"));
        }
    }
    
    /**
     * 完成轮次
     */
    @PutMapping("/competition/{competitionId}/round/{roundNumber}/complete")
    public ResponseEntity<ApiResponse<String>> completeRound(
            @PathVariable Long competitionId, @PathVariable Integer roundNumber) {
        try {
            advancementService.completeRound(competitionId, roundNumber);
            return ResponseEntity.ok(ApiResponse.success("完成轮次成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("完成轮次失败"));
        }
    }
}
