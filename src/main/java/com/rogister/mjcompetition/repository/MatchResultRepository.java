package com.rogister.mjcompetition.repository;

import com.rogister.mjcompetition.entity.Competition;
import com.rogister.mjcompetition.entity.CompetitionRound;
import com.rogister.mjcompetition.entity.MatchResult;
import com.rogister.mjcompetition.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    /**
     * 根据比赛和轮次查找所有比赛记录，按照比赛时间从早到晚排序
     */
    List<MatchResult> findByCompetitionAndRoundOrderByMatchTimeAsc(Competition competition, CompetitionRound round);
    
    /**
     * 根据比赛和轮次查找所有比赛记录，按照比赛时间从早到晚排序（包含比赛编号作为第二排序条件）
     */
    List<MatchResult> findByCompetitionAndRoundOrderByMatchTimeAscMatchNumberAsc(Competition competition, CompetitionRound round);
    
    /**
     * 根据比赛和轮次查找所有比赛记录，用于计算玩家排名
     */
    List<MatchResult> findByCompetitionAndRound(Competition competition, CompetitionRound round);
    
    /**
     * 根据比赛、轮次和玩家查找该玩家参与的所有比赛记录
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.competition = :competition AND mr.round = :round " +
           "AND (mr.eastPlayer = :player OR mr.southPlayer = :player OR mr.westPlayer = :player OR mr.northPlayer = :player)")
    List<MatchResult> findByCompetitionAndRoundAndPlayer(
            @Param("competition") Competition competition, 
            @Param("round") CompetitionRound round, 
            @Param("player") Player player);
} 