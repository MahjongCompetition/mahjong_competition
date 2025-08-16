package com.rogister.mjcompetiton.controller;

import com.rogister.mjcompetiton.entity.MatchResult;
import com.rogister.mjcompetiton.service.MatchResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match-results")
@CrossOrigin(origins = "*")
public class MatchResultController {
    
    @Autowired
    private MatchResultService matchResultService;
    
    /**
     * 创建比赛成绩
     */
    @PostMapping
    public ResponseEntity<MatchResult> createMatchResult(@RequestBody MatchResult matchResult) {
        try {
            MatchResult created = matchResultService.createMatchResult(matchResult);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据ID获取比赛成绩
     */
    @GetMapping("/{id}")
    public ResponseEntity<MatchResult> getMatchResult(@PathVariable Long id) {
        return matchResultService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取比赛成绩的详细排名和得分信息
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<MatchResultService.MatchResultDetail> getMatchResultDetail(@PathVariable Long id) {
        try {
            MatchResultService.MatchResultDetail detail = matchResultService.getMatchResultDetail(id);
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 计算并返回比赛排名
     */
    @GetMapping("/{id}/ranks")
    public ResponseEntity<List<MatchResult.PlayerRank>> getMatchRanks(@PathVariable Long id) {
        try {
            List<MatchResult.PlayerRank> ranks = matchResultService.calculateMatchRanks(id);
            return ResponseEntity.ok(ranks);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 更新比赛成绩
     */
    @PutMapping("/{id}")
    public ResponseEntity<MatchResult> updateMatchResult(@PathVariable Long id, @RequestBody MatchResult matchResultDetails) {
        try {
            MatchResult updated = matchResultService.updateMatchResult(id, matchResultDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除比赛成绩
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatchResult(@PathVariable Long id) {
        try {
            matchResultService.deleteMatchResult(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 验证比赛成绩总分
     */
    @PostMapping("/validate-scores")
    public ResponseEntity<Boolean> validateScores(@RequestBody ScoreValidationRequest request) {
        boolean isValid = matchResultService.validateMatchResultScores(
                request.getEastScore(), 
                request.getSouthScore(), 
                request.getWestScore(), 
                request.getNorthScore()
        );
        return ResponseEntity.ok(isValid);
    }
    
    /**
     * 获取下一场比赛编号
     */
    @GetMapping("/next-match-number")
    public ResponseEntity<Integer> getNextMatchNumber(
            @RequestParam Long competitionId, 
            @RequestParam Long roundId) {
        // 这里需要根据competitionId和roundId查找对应的Competition和CompetitionRound对象
        // 为了简化，这里返回一个示例值
        return ResponseEntity.ok(1);
    }
    
    /**
     * 内部类：分数验证请求
     */
    public static class ScoreValidationRequest {
        private Integer eastScore;
        private Integer southScore;
        private Integer westScore;
        private Integer northScore;
        
        // Getter和Setter方法
        public Integer getEastScore() { return eastScore; }
        public void setEastScore(Integer eastScore) { this.eastScore = eastScore; }
        
        public Integer getSouthScore() { return southScore; }
        public void setSouthScore(Integer southScore) { this.southScore = southScore; }
        
        public Integer getWestScore() { return westScore; }
        public void setWestScore(Integer westScore) { this.westScore = westScore; }
        
        public Integer getNorthScore() { return northScore; }
        public void setNorthScore(Integer northScore) { this.northScore = northScore; }
    }
}
