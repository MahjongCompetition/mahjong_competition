package com.rogister.mjcompetition.repository.competition;

import com.rogister.mjcompetition.entity.competition.Competition;
import com.rogister.mjcompetition.entity.competition.CompetitionRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionRoundRepository extends JpaRepository<CompetitionRound, Long> {
    
    /**
     * 根据比赛查找所有轮次
     */
    List<CompetitionRound> findByCompetitionOrderByRoundNumber(Competition competition);
    
    /**
     * 根据比赛和轮次编号查找轮次
     */
    Optional<CompetitionRound> findByCompetitionAndRoundNumber(Competition competition, Integer roundNumber);
    
    /**
     * 根据比赛查找当前活跃轮次
     */
    Optional<CompetitionRound> findByCompetitionAndIsActiveTrue(Competition competition);
    
    /**
     * 根据比赛查找下一个轮次
     */
    Optional<CompetitionRound> findByCompetitionAndRoundNumberGreaterThanOrderByRoundNumber(Competition competition, Integer currentRoundNumber);
    
    /**
     * 根据比赛查找最大轮次编号
     */
    Optional<Integer> findTopByCompetitionOrderByRoundNumberDesc(Competition competition);
} 