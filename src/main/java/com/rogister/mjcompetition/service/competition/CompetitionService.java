package com.rogister.mjcompetition.service.competition;

import com.rogister.mjcompetition.entity.competition.Competition;
import com.rogister.mjcompetition.entity.competition.CompetitionRule;
import com.rogister.mjcompetition.repository.competition.CompetitionRepository;
import com.rogister.mjcompetition.repository.competition.CompetitionRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CompetitionService {
    
    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private CompetitionRuleRepository competitionRuleRepository;
    
    /**
     * 创建新比赛
     */
    public Competition createCompetition(Competition competition) {
        // 验证比赛名称
        if (competition.getCompetitionName() == null || competition.getCompetitionName().trim().isEmpty()) {
            throw new RuntimeException("比赛名称不能为空");
        }
        
        // 验证比赛类型
        if (competition.getCompetitionType() == null) {
            throw new RuntimeException("比赛类型不能为空");
        }
        
        // 验证比赛规则
        if (competition.getRule() == null || competition.getRule().getId() == null) {
            throw new RuntimeException("比赛规则不能为空");
        }
        
        // 验证报名结束时间
        if (competition.getRegistrationDeadline() == null) {
            throw new RuntimeException("报名结束时间不能为空");
        }
        
        if (competition.getRegistrationDeadline().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("报名结束时间不能早于当前时间");
        }
        
        // 检查比赛名称是否已存在
        if (competitionRepository.existsByCompetitionName(competition.getCompetitionName())) {
            throw new RuntimeException("比赛名称已存在: " + competition.getCompetitionName());
        }
        
        // 验证规则是否存在
        CompetitionRule rule = competitionRuleRepository.findById(competition.getRule().getId())
                .orElseThrow(() -> new RuntimeException("比赛规则不存在，ID: " + competition.getRule().getId()));
        
        // 设置完整的规则对象
        competition.setRule(rule);
        
        // 保存比赛
        Competition savedCompetition = competitionRepository.save(competition);
        
        // 重新查询以确保返回完整数据
        return competitionRepository.findById(savedCompetition.getId()).orElse(savedCompetition);
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