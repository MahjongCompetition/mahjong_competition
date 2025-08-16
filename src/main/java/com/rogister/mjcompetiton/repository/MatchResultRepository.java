package com.rogister.mjcompetiton.repository;

import com.rogister.mjcompetiton.entity.Competition;
import com.rogister.mjcompetiton.entity.CompetitionRound;
import com.rogister.mjcompetiton.entity.MatchResult;
import com.rogister.mjcompetiton.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    
    /**
     * 根据比赛和轮次查找所有比赛成绩
     */
    List<MatchResult> findByCompetitionAndRoundOrderByMatchNumber(Competition competition, CompetitionRound round);
    
    /**
     * 根据比赛、轮次和比赛编号查找比赛成绩
     */
    Optional<MatchResult> findByCompetitionAndRoundAndMatchNumber(Competition competition, CompetitionRound round, Integer matchNumber);
    
    /**
     * 根据比赛查找所有比赛成绩
     */
    List<MatchResult> findByCompetitionOrderByRoundRoundNumberAscMatchNumberAsc(Competition competition);
    
    /**
     * 根据玩家查找所有参与的比赛成绩
     */
    List<MatchResult> findByEastPlayerOrSouthPlayerOrWestPlayerOrNorthPlayerOrderByCompetitionAscRoundRoundNumberAscMatchNumberAsc(
            Player eastPlayer, Player southPlayer, Player westPlayer, Player northPlayer);
    
    /**
     * 根据比赛和轮次统计比赛场数
     */
    long countByCompetitionAndRound(Competition competition, CompetitionRound round);
    
    /**
     * 根据比赛、轮次和玩家查找该玩家参与的比赛成绩
     */
    List<MatchResult> findByCompetitionAndRoundAndEastPlayerOrSouthPlayerOrWestPlayerOrNorthPlayer(
            Competition competition, CompetitionRound round, Player eastPlayer, Player southPlayer, Player westPlayer, Player northPlayer);
} 