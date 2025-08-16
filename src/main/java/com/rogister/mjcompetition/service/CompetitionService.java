package com.rogister.mjcompetition.service;

import com.rogister.mjcompetition.entity.Competition;
import com.rogister.mjcompetition.entity.CompetitionRule;
import com.rogister.mjcompetition.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompetitionService {
    
    @Autowired
    private CompetitionRepository competitionRepository;
    
    /**
     * 创建新比赛
     */
    public Competition createCompetition(Competition competition) {
        if (competitionRepository.existsByCompetitionName(competition.getCompetitionName())) {
            throw new RuntimeException("比赛名称已存在: " + competition.getCompetitionName());
        }
        
        return competitionRepository.save(competition);
    }
    
    /**
     * 根据ID查找比赛
     */
    public Optional<Competition> findById(Long id) {
        return competitionRepository.findById(id);
    }
    
    /**
     * 根据比赛名称查找比赛
     */
    public Optional<Competition> findByCompetitionName(String competitionName) {
        return competitionRepository.findByCompetitionName(competitionName);
    }
    
    /**
     * 根据比赛类型查找比赛
     */
    public List<Competition> findByCompetitionType(Competition.CompetitionType competitionType) {
        return competitionRepository.findByCompetitionType(competitionType);
    }
    
    /**
     * 根据比赛规则查找比赛
     */
    public List<Competition> findByRule(Long ruleId) {
        CompetitionRule rule = new CompetitionRule();
        rule.setId(ruleId);
        return competitionRepository.findByRule(rule);
    }
    
    /**
     * 根据比赛名称搜索比赛
     */
    public List<Competition> searchByCompetitionName(String competitionName) {
        return competitionRepository.findByCompetitionNameContainingIgnoreCase(competitionName);
    }
    
    /**
     * 获取所有比赛
     */
    public List<Competition> findAllCompetitions() {
        return competitionRepository.findAll();
    }
    
    /**
     * 更新比赛信息
     */
    public Competition updateCompetition(Long id, Competition competitionDetails) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + id));
        
        // 检查比赛名称唯一性（如果名称被修改）
        if (!competition.getCompetitionName().equals(competitionDetails.getCompetitionName()) && 
            competitionRepository.existsByCompetitionName(competitionDetails.getCompetitionName())) {
            throw new RuntimeException("比赛名称已存在: " + competitionDetails.getCompetitionName());
        }
        
        competition.setCompetitionName(competitionDetails.getCompetitionName());
        competition.setCompetitionType(competitionDetails.getCompetitionType());
        competition.setRule(competitionDetails.getRule());
        
        return competitionRepository.save(competition);
    }
    
    /**
     * 删除比赛
     */
    public void deleteCompetition(Long id) {
        if (!competitionRepository.existsById(id)) {
            throw new RuntimeException("比赛不存在，ID: " + id);
        }
        competitionRepository.deleteById(id);
    }
} 