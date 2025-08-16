package com.rogister.mjcompetition.controller;

import com.rogister.mjcompetition.dto.ApiResponse;
import com.rogister.mjcompetition.entity.CompetitionRule;
import com.rogister.mjcompetition.service.CompetitionRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competition-rules")
@CrossOrigin(origins = "*")
public class CompetitionRuleController {
    
    @Autowired
    private CompetitionRuleService competitionRuleService;
    
    /**
     * 创建新比赛规则
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CompetitionRule>> createCompetitionRule(@RequestBody CompetitionRule competitionRule) {
        try {
            CompetitionRule createdRule = competitionRuleService.createCompetitionRule(competitionRule);
            return ResponseEntity.ok(ApiResponse.success("创建比赛规则成功", createdRule));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("服务器内部错误"));
        }
    }
    
    /**
     * 获取所有比赛规则
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CompetitionRule>>> getAllCompetitionRules() {
        try {
            List<CompetitionRule> rules = competitionRuleService.findAllRules();
            return ResponseEntity.ok(ApiResponse.success(rules));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取比赛规则列表失败"));
        }
    }
    
    /**
     * 根据ID获取比赛规则
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompetitionRule>> getCompetitionRuleById(@PathVariable Long id) {
        try {
            return competitionRuleService.findById(id)
                    .map(rule -> ResponseEntity.ok(ApiResponse.success(rule)))
                    .orElse(ResponseEntity.ok(ApiResponse.error("比赛规则不存在")));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取比赛规则失败"));
        }
    }
    
    /**
     * 根据规则名称获取比赛规则
     */
    @GetMapping("/name/{ruleName}")
    public ResponseEntity<ApiResponse<CompetitionRule>> getCompetitionRuleByName(@PathVariable String ruleName) {
        try {
            return competitionRuleService.findByRuleName(ruleName)
                    .map(rule -> ResponseEntity.ok(ApiResponse.success(rule)))
                    .orElse(ResponseEntity.ok(ApiResponse.error("比赛规则不存在")));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取比赛规则失败"));
        }
    }
    
    /**
     * 根据规则名称搜索比赛规则
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CompetitionRule>>> searchCompetitionRules(@RequestParam String ruleName) {
        try {
            List<CompetitionRule> rules = competitionRuleService.searchByRuleName(ruleName);
            return ResponseEntity.ok(ApiResponse.success(rules));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("搜索比赛规则失败"));
        }
    }
    
    /**
     * 更新比赛规则
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompetitionRule>> updateCompetitionRule(@PathVariable Long id, @RequestBody CompetitionRule competitionRule) {
        try {
            CompetitionRule updatedRule = competitionRuleService.updateCompetitionRule(id, competitionRule);
            return ResponseEntity.ok(ApiResponse.success("更新比赛规则成功", updatedRule));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新比赛规则失败"));
        }
    }
    
    /**
     * 删除比赛规则
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCompetitionRule(@PathVariable Long id) {
        try {
            competitionRuleService.deleteCompetitionRule(id);
            return ResponseEntity.ok(ApiResponse.success("删除比赛规则成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除比赛规则失败"));
        }
    }
} 