package com.rogister.mjcompetition.controller.competition;

import com.rogister.mjcompetition.dto.common.ApiResponse;
import com.rogister.mjcompetition.dto.competition.CompetitionCreateRequest;
import com.rogister.mjcompetition.entity.competition.Competition;
import com.rogister.mjcompetition.entity.competition.CompetitionRule;
import com.rogister.mjcompetition.service.competition.CompetitionService;
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

@RestController
@RequestMapping("/api/competitions")
@CrossOrigin(origins = "*")
@Tag(name = "比赛管理", description = "比赛的创建、查询、更新、删除等功能")
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    /**
     * 创建新比赛
     */
    @Operation(summary = "创建比赛", description = "创建新的比赛")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "创建成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "创建失败")
    })
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Competition>> createCompetition(
            @Parameter(description = "比赛创建请求信息", requiredMode = Schema.RequiredMode.REQUIRED) @RequestBody CompetitionCreateRequest request) {
        try {
            // 验证请求参数
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("比赛名称不能为空"));
            }
            if (request.getType() == null || request.getType().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("比赛类型不能为空"));
            }
            if (request.getRuleId() == null) {
                return ResponseEntity.ok(ApiResponse.error("比赛规则ID不能为空"));
            }
            if (request.getRegistrationEndTime() == null) {
                return ResponseEntity.ok(ApiResponse.error("报名结束时间不能为空"));
            }

            // 将DTO转换为实体
            Competition competition = new Competition();
            competition.setCompetitionName(request.getName().trim());
            competition.setDescription(request.getDescription());
            competition.setCompetitionType(Competition.CompetitionType.valueOf(request.getType().toUpperCase()));
            competition.setMaxParticipants(request.getMaxParticipants());
            competition.setRegistrationStartTime(request.getRegistrationStartTime());
            competition.setRegistrationDeadline(request.getRegistrationEndTime());
            competition.setStartTime(request.getStartTime());
            competition.setEndTime(request.getEndTime());

            // 设置规则
            CompetitionRule rule = new CompetitionRule();
            rule.setId(request.getRuleId());
            competition.setRule(rule);

            Competition createdCompetition = competitionService.createCompetition(competition);
            return ResponseEntity.ok(ApiResponse.success("创建比赛成功", createdCompetition));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.error("比赛类型无效，请使用 INDIVIDUAL 或 TEAM"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("服务器内部错误"));
        }
    }

    /**
     * 获取所有比赛
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Competition>>> getAllCompetitions() {
        try {
            List<Competition> competitions = competitionService.findAllCompetitions();
            return ResponseEntity.ok(ApiResponse.success(competitions));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取比赛列表失败"));
        }
    }

    /**
     * 根据ID获取比赛
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Competition>> getCompetitionById(@PathVariable Long id) {
        try {
            return competitionService.findById(id)
                    .map(competition -> ResponseEntity.ok(ApiResponse.success(competition)))
                    .orElse(ResponseEntity.ok(ApiResponse.error("比赛不存在")));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取比赛信息失败"));
        }
    }

    /**
     * 根据比赛名称获取比赛
     */
    @GetMapping("/name/{competitionName}")
    public ResponseEntity<ApiResponse<Competition>> getCompetitionByName(@PathVariable String competitionName) {
        try {
            return competitionService.findByCompetitionName(competitionName)
                    .map(competition -> ResponseEntity.ok(ApiResponse.success(competition)))
                    .orElse(ResponseEntity.ok(ApiResponse.error("比赛不存在")));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取比赛信息失败"));
        }
    }

    /**
     * 根据比赛类型获取比赛
     */
    @GetMapping("/type/{competitionType}")
    public ResponseEntity<ApiResponse<List<Competition>>> getCompetitionsByType(@PathVariable String competitionType) {
        try {
            Competition.CompetitionType type = Competition.CompetitionType.valueOf(competitionType.toUpperCase());
            List<Competition> competitions = competitionService.findByCompetitionType(type);
            return ResponseEntity.ok(ApiResponse.success(competitions));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.error("无效的比赛类型"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取比赛列表失败"));
        }
    }

    /**
     * 根据比赛规则获取比赛
     */
    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<ApiResponse<List<Competition>>> getCompetitionsByRule(@PathVariable Long ruleId) {
        try {
            List<Competition> competitions = competitionService.findByRule(ruleId);
            return ResponseEntity.ok(ApiResponse.success(competitions));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取比赛列表失败"));
        }
    }

    /**
     * 根据比赛名称搜索比赛
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Competition>>> searchCompetitions(@RequestParam String competitionName) {
        try {
            List<Competition> competitions = competitionService.searchByCompetitionName(competitionName);
            return ResponseEntity.ok(ApiResponse.success(competitions));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("搜索比赛失败"));
        }
    }

    /**
     * 更新比赛信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Competition>> updateCompetition(@PathVariable Long id,
            @RequestBody Competition competitionDetails) {
        try {
            Competition updatedCompetition = competitionService.updateCompetition(id, competitionDetails);
            return ResponseEntity.ok(ApiResponse.success("更新比赛成功", updatedCompetition));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新比赛失败"));
        }
    }

    /**
     * 删除比赛
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCompetition(@PathVariable Long id) {
        try {
            competitionService.deleteCompetition(id);
            return ResponseEntity.ok(ApiResponse.success("删除比赛成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除比赛失败"));
        }
    }
}