package com.rogister.mjcompetiton.service;

import com.rogister.mjcompetiton.entity.CompetitionRule;
import com.rogister.mjcompetiton.repository.CompetitionRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompetitionRuleService {
    
    @Autowired
    private CompetitionRuleRepository competitionRuleRepository;
    
    /**
     * 创建新比赛规则
     */
    public CompetitionRule createCompetitionRule(CompetitionRule rule) {
        if (competitionRuleRepository.existsByRuleName(rule.getRuleName())) {
            throw new RuntimeException("比赛规则名称已存在: " + rule.getRuleName());
        }
        
        // 验证原点字段
        if (rule.getOriginPoints() == null || rule.getOriginPoints() <= 0 || rule.getOriginPoints() >= 100000) {
            throw new RuntimeException("原点必须是一个大于0小于100000的数字");
        }
        
        return competitionRuleRepository.save(rule);
    }
    
    /**
     * 根据ID查找比赛规则
     */
    public Optional<CompetitionRule> findById(Long id) {
        return competitionRuleRepository.findById(id);
    }
    
    /**
     * 根据规则名称查找比赛规则
     */
    public Optional<CompetitionRule> findByRuleName(String ruleName) {
        return competitionRuleRepository.findByRuleName(ruleName);
    }
    
    /**
     * 根据规则名称搜索比赛规则
     */
    public List<CompetitionRule> searchByRuleName(String ruleName) {
        return competitionRuleRepository.findByRuleNameContainingIgnoreCase(ruleName);
    }
    
    /**
     * 获取所有比赛规则
     */
    public List<CompetitionRule> findAllRules() {
        return competitionRuleRepository.findAll();
    }
    
    /**
     * 更新比赛规则
     */
    public CompetitionRule updateCompetitionRule(Long id, CompetitionRule ruleDetails) {
        CompetitionRule rule = competitionRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("比赛规则不存在，ID: " + id));
        
        // 检查规则名称唯一性（如果名称被修改）
        if (!rule.getRuleName().equals(ruleDetails.getRuleName()) && 
            competitionRuleRepository.existsByRuleName(ruleDetails.getRuleName())) {
            throw new RuntimeException("比赛规则名称已存在: " + ruleDetails.getRuleName());
        }
        
        // 验证原点字段
        if (ruleDetails.getOriginPoints() == null || ruleDetails.getOriginPoints() <= 0 || ruleDetails.getOriginPoints() >= 100000) {
            throw new RuntimeException("原点必须是一个大于0小于100000的数字");
        }
        
        rule.setRuleName(ruleDetails.getRuleName());
        rule.setOriginPoints(ruleDetails.getOriginPoints());
        rule.setFirstPlacePoints(ruleDetails.getFirstPlacePoints());
        rule.setSecondPlacePoints(ruleDetails.getSecondPlacePoints());
        rule.setThirdPlacePoints(ruleDetails.getThirdPlacePoints());
        rule.setFourthPlacePoints(ruleDetails.getFourthPlacePoints());
        
        return competitionRuleRepository.save(rule);
    }
    
    /**
     * 删除比赛规则
     */
    public void deleteCompetitionRule(Long id) {
        if (!competitionRuleRepository.existsById(id)) {
            throw new RuntimeException("比赛规则不存在，ID: " + id);
        }
        competitionRuleRepository.deleteById(id);
    }
} 