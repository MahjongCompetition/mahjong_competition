package com.rogister.mjcompetition.repository.competition;

import com.rogister.mjcompetition.entity.competition.CompetitionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionRuleRepository extends JpaRepository<CompetitionRule, Long> {
    
    /**
     * 根据规则名称查找比赛规则
     */
    Optional<CompetitionRule> findByRuleName(String ruleName);
    
    /**
     * 根据规则名称模糊搜索
     */
    List<CompetitionRule> findByRuleNameContainingIgnoreCase(String ruleName);
    
    /**
     * 检查规则名称是否已存在
     */
    boolean existsByRuleName(String ruleName);
} 