package com.rogister.mjcompetiton.repository;

import com.rogister.mjcompetiton.entity.Competition;
import com.rogister.mjcompetiton.entity.CompetitionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    
    /**
     * 根据比赛名称查找比赛
     */
    Optional<Competition> findByCompetitionName(String competitionName);
    
    /**
     * 根据比赛类型查找比赛
     */
    List<Competition> findByCompetitionType(Competition.CompetitionType competitionType);
    
    /**
     * 根据比赛规则查找比赛
     */
    List<Competition> findByRule(CompetitionRule rule);
    
    /**
     * 根据比赛名称搜索比赛（模糊匹配）
     */
    List<Competition> findByCompetitionNameContainingIgnoreCase(String competitionName);
    
    /**
     * 检查比赛名称是否存在
     */
    boolean existsByCompetitionName(String competitionName);
} 