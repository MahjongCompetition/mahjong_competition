package com.rogister.mjcompetition.repository.competition;

import com.rogister.mjcompetition.entity.competition.Competition;
import com.rogister.mjcompetition.entity.competition.CompetitionRule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    
    /**
     * 根据比赛名称查找比赛（包含规则信息）
     */
    @EntityGraph(attributePaths = {"rule"})
    Optional<Competition> findByCompetitionName(String competitionName);
    
    /**
     * 根据比赛类型查找比赛（包含规则信息）
     */
    @EntityGraph(attributePaths = {"rule"})
    List<Competition> findByCompetitionType(Competition.CompetitionType competitionType);
    
    /**
     * 根据比赛规则查找比赛（包含规则信息）
     */
    @EntityGraph(attributePaths = {"rule"})
    List<Competition> findByRule(CompetitionRule rule);
    
    /**
     * 根据比赛名称搜索比赛（模糊匹配，包含规则信息）
     */
    @EntityGraph(attributePaths = {"rule"})
    List<Competition> findByCompetitionNameContainingIgnoreCase(String competitionName);
    
    /**
     * 根据ID查找比赛（包含规则信息）
     */
    @EntityGraph(attributePaths = {"rule"})
    Optional<Competition> findById(Long id);
    
    /**
     * 查找所有比赛（包含规则信息）
     */
    @EntityGraph(attributePaths = {"rule"})
    List<Competition> findAll();
    
    /**
     * 检查比赛名称是否存在
     */
    boolean existsByCompetitionName(String competitionName);
} 