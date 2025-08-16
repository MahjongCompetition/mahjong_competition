package com.rogister.mjcompetiton.controller;

import com.rogister.mjcompetiton.entity.Competition;
import com.rogister.mjcompetiton.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
@CrossOrigin(origins = "*")
public class CompetitionController {
    
    @Autowired
    private CompetitionService competitionService;
    
    /**
     * 创建新比赛
     */
    @PostMapping
    public ResponseEntity<Competition> createCompetition(@RequestBody Competition competition) {
        try {
            Competition createdCompetition = competitionService.createCompetition(competition);
            return ResponseEntity.ok(createdCompetition);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取所有比赛
     */
    @GetMapping
    public ResponseEntity<List<Competition>> getAllCompetitions() {
        List<Competition> competitions = competitionService.findAllCompetitions();
        return ResponseEntity.ok(competitions);
    }
    
    /**
     * 根据ID获取比赛
     */
    @GetMapping("/{id}")
    public ResponseEntity<Competition> getCompetitionById(@PathVariable Long id) {
        return competitionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据比赛名称获取比赛
     */
    @GetMapping("/name/{competitionName}")
    public ResponseEntity<Competition> getCompetitionByName(@PathVariable String competitionName) {
        return competitionService.findByCompetitionName(competitionName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据比赛类型获取比赛
     */
    @GetMapping("/type/{competitionType}")
    public ResponseEntity<List<Competition>> getCompetitionsByType(@PathVariable String competitionType) {
        try {
            Competition.CompetitionType type = Competition.CompetitionType.valueOf(competitionType.toUpperCase());
            List<Competition> competitions = competitionService.findByCompetitionType(type);
            return ResponseEntity.ok(competitions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据比赛规则获取比赛
     */
    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<Competition>> getCompetitionsByRule(@PathVariable Long ruleId) {
        try {
            List<Competition> competitions = competitionService.findByRule(ruleId);
            return ResponseEntity.ok(competitions);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据比赛名称搜索比赛
     */
    @GetMapping("/search")
    public ResponseEntity<List<Competition>> searchCompetitions(@RequestParam String competitionName) {
        List<Competition> competitions = competitionService.searchByCompetitionName(competitionName);
        return ResponseEntity.ok(competitions);
    }
    
    /**
     * 更新比赛信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Competition> updateCompetition(@PathVariable Long id, @RequestBody Competition competitionDetails) {
        try {
            Competition updatedCompetition = competitionService.updateCompetition(id, competitionDetails);
            return ResponseEntity.ok(updatedCompetition);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除比赛
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompetition(@PathVariable Long id) {
        try {
            competitionService.deleteCompetition(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 