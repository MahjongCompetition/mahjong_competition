package com.rogister.mjcompetition.controller;

import com.rogister.mjcompetition.dto.ApiResponse;
import com.rogister.mjcompetition.dto.CompetitionStatusResponse;
import com.rogister.mjcompetition.service.CompetitionStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competition-status")
@CrossOrigin(origins = "*")
public class CompetitionStatusController {
    
    @Autowired
    private CompetitionStatusService competitionStatusService;
    
    /**
     * 查询比赛状态
     * 无需鉴权，公开接口
     */
    @GetMapping("/{competitionId}/round/{roundNumber}")
    public ResponseEntity<ApiResponse<CompetitionStatusResponse>> getCompetitionStatus(
            @PathVariable Long competitionId, 
            @PathVariable Integer roundNumber) {
        try {
            CompetitionStatusResponse response = competitionStatusService
                    .getCompetitionStatus(competitionId, roundNumber);
            
            return ResponseEntity.ok(ApiResponse.success("查询比赛状态成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询比赛状态失败"));
        }
    }
    
    /**
     * 查询比赛当前轮次状态
     */
    @GetMapping("/{competitionId}/current")
    public ResponseEntity<ApiResponse<CompetitionStatusResponse>> getCurrentRoundStatus(
            @PathVariable Long competitionId) {
        try {
            // 获取当前最高轮次
            Integer currentRound = competitionStatusService.getCurrentMaxRound(competitionId);
            
            if (currentRound == 0) {
                return ResponseEntity.ok(ApiResponse.error("比赛尚未开始"));
            }
            
            CompetitionStatusResponse response = competitionStatusService
                    .getCompetitionStatus(competitionId, currentRound);
            
            return ResponseEntity.ok(ApiResponse.success("查询当前轮次状态成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询当前轮次状态失败"));
        }
    }
}
