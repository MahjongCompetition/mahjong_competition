package com.rogister.mjcompetition.controller.competition;

import com.rogister.mjcompetition.dto.common.ApiResponse;
import com.rogister.mjcompetition.dto.competition.CompetitionStatusResponse;
import com.rogister.mjcompetition.service.competition.CompetitionStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/competition-status")
@CrossOrigin(origins = "*")
@Tag(name = "比赛状态", description = "比赛状态查询功能，无需认证的公开接口")
public class CompetitionStatusController {

    @Autowired
    private CompetitionStatusService competitionStatusService;

    /**
     * 查询比赛状态
     * 无需鉴权，公开接口
     */
    @Operation(summary = "查询指定轮次比赛状态", description = "查询指定比赛指定轮次的状态信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "比赛或轮次不存在")
    })
    @GetMapping("/{competitionId}/round/{roundNumber}")
    public ResponseEntity<ApiResponse<CompetitionStatusResponse>> getCompetitionStatus(
            @Parameter(description = "比赛ID", required = true, example = "1") @PathVariable Long competitionId,
            @Parameter(description = "轮次编号", required = true, example = "1") @PathVariable Integer roundNumber) {
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
