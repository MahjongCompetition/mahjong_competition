package com.rogister.mjcompetition.repository;

import com.rogister.mjcompetition.entity.Competition;
import com.rogister.mjcompetition.entity.MatchResult;
import com.rogister.mjcompetition.entity.Player;
import com.rogister.mjcompetition.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    
    /**
     * 根据比赛ID查找所有比赛记录
     */
    List<MatchResult> findByCompetitionId(Long competitionId);
    
    /**
     * 根据比赛ID和轮次号查找该轮次的所有比赛记录
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.competition.id = :competitionId AND mr.roundNumber = :roundNumber")
    List<MatchResult> findByCompetitionIdAndRoundNumber(@Param("competitionId") Long competitionId, @Param("roundNumber") Integer roundNumber);
    
    /**
     * 根据比赛ID和玩家ID查找该玩家参与的所有比赛记录
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.competition.id = :competitionId AND (mr.eastPlayer.id = :playerId OR mr.southPlayer.id = :playerId OR mr.westPlayer.id = :playerId OR mr.northPlayer.id = :playerId)")
    List<MatchResult> findByCompetitionIdAndPlayerId(@Param("competitionId") Long competitionId, @Param("playerId") Long playerId);
    
    /**
     * 根据玩家ID查找该玩家参与的所有比赛记录
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.eastPlayer.id = :playerId OR mr.southPlayer.id = :playerId OR mr.westPlayer.id = :playerId OR mr.northPlayer.id = :playerId")
    List<MatchResult> findByPlayerId(@Param("playerId") Long playerId);
    
    /**
     * 根据比赛ID、轮次号和玩家ID查找该玩家在该轮次参与的比赛记录
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.competition.id = :competitionId AND mr.roundNumber = :roundNumber AND (mr.eastPlayer.id = :playerId OR mr.southPlayer.id = :playerId OR mr.westPlayer.id = :playerId OR mr.northPlayer.id = :playerId)")
    List<MatchResult> findByCompetitionIdAndRoundNumberAndPlayerId(@Param("competitionId") Long competitionId, @Param("roundNumber") Integer roundNumber, @Param("playerId") Long playerId);
    
    // 注意：MatchResult 实体是为个人赛设计的，不支持团队查询
    // 团队赛需要通过玩家ID来查询团队成员参与的比赛
    
    // 注意：MatchResult 实体是为个人赛设计的，不支持团队查询
    // 团队赛需要通过玩家ID来查询团队成员参与的比赛
    
    /**
     * 根据比赛、轮次和比赛编号查找比赛记录
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.competition.id = :competitionId AND mr.roundNumber = :roundNumber AND mr.matchNumber = :matchNumber")
    Optional<MatchResult> findByCompetitionAndRoundAndMatchNumber(@Param("competitionId") Long competitionId, @Param("roundNumber") Integer roundNumber, @Param("matchNumber") Integer matchNumber);
    
    /**
     * 根据比赛和轮次查找所有比赛记录，按比赛编号排序
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.competition.id = :competitionId AND mr.roundNumber = :roundNumber ORDER BY mr.matchNumber")
    List<MatchResult> findByCompetitionAndRoundOrderByMatchNumber(@Param("competitionId") Long competitionId, @Param("roundNumber") Integer roundNumber);
    
    /**
     * 根据比赛和轮次查找所有比赛记录，按比赛时间升序排序
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.competition.id = :competitionId AND mr.roundNumber = :roundNumber ORDER BY mr.matchTime ASC")
    List<MatchResult> findByCompetitionAndRoundOrderByMatchTimeAsc(@Param("competitionId") Long competitionId, @Param("roundNumber") Integer roundNumber);
    
    /**
     * 根据比赛和轮次查找所有比赛记录，按比赛时间升序、比赛编号升序排序
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.competition.id = :competitionId AND mr.roundNumber = :roundNumber ORDER BY mr.matchTime ASC, mr.matchNumber ASC")
    List<MatchResult> findByCompetitionAndRoundOrderByMatchTimeAscMatchNumberAsc(@Param("competitionId") Long competitionId, @Param("roundNumber") Integer roundNumber);
    
    /**
     * 根据比赛和轮次查找所有比赛记录
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.competition.id = :competitionId AND mr.roundNumber = :roundNumber")
    List<MatchResult> findByCompetitionAndRound(@Param("competitionId") Long competitionId, @Param("roundNumber") Integer roundNumber);
    
    /**
     * 根据比赛、轮次和玩家查找该玩家参与的比赛记录
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.competition.id = :competitionId AND mr.roundNumber = :roundNumber AND (mr.eastPlayer.id = :playerId OR mr.southPlayer.id = :playerId OR mr.westPlayer.id = :playerId OR mr.northPlayer.id = :playerId)")
    List<MatchResult> findByCompetitionAndRoundAndPlayer(@Param("competitionId") Long competitionId, @Param("roundNumber") Integer roundNumber, @Param("playerId") Long playerId);
    
    /**
     * 根据比赛查找所有比赛记录，按轮次号升序、比赛编号升序排序
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.competition.id = :competitionId ORDER BY mr.roundNumber ASC, mr.matchNumber ASC")
    List<MatchResult> findByCompetitionOrderByRoundRoundNumberAscMatchNumberAsc(@Param("competitionId") Long competitionId);
    
    /**
     * 根据玩家查找该玩家参与的所有比赛记录，按比赛升序、轮次号升序、比赛编号升序排序
     */
    @Query("SELECT mr FROM MatchResult mr WHERE mr.eastPlayer.id = :playerId OR mr.southPlayer.id = :playerId OR mr.westPlayer.id = :playerId OR mr.northPlayer.id = :playerId ORDER BY mr.competition.id ASC, mr.roundNumber ASC, mr.matchNumber ASC")
    List<MatchResult> findByEastPlayerOrSouthPlayerOrWestPlayerOrNorthPlayerOrderByCompetitionAscRoundRoundNumberAscMatchNumberAsc(@Param("playerId") Long playerId);
    
    /**
     * 根据比赛和轮次统计比赛记录数量
     */
    @Query("SELECT COUNT(mr) FROM MatchResult mr WHERE mr.competition.id = :competitionId AND mr.roundNumber = :roundNumber")
    long countByCompetitionAndRound(@Param("competitionId") Long competitionId, @Param("roundNumber") Integer roundNumber);
    
    /**
     * 根据比赛、轮次和玩家查询该玩家的PT分数总和
     */
    @Query("SELECT " +
           "COALESCE(SUM(CASE WHEN mr.eastPlayer.id = :playerId THEN mr.eastPtScore ELSE 0 END), 0) + " +
           "COALESCE(SUM(CASE WHEN mr.southPlayer.id = :playerId THEN mr.southPtScore ELSE 0 END), 0) + " +
           "COALESCE(SUM(CASE WHEN mr.westPlayer.id = :playerId THEN mr.westPtScore ELSE 0 END), 0) + " +
           "COALESCE(SUM(CASE WHEN mr.northPlayer.id = :playerId THEN mr.northPtScore ELSE 0 END), 0) " +
           "FROM MatchResult mr " +
           "WHERE mr.competition.id = :competitionId AND mr.roundNumber = :roundNumber " +
           "AND (mr.eastPlayer.id = :playerId OR mr.southPlayer.id = :playerId OR mr.westPlayer.id = :playerId OR mr.northPlayer.id = :playerId)")
    Double getPlayerPtScoreSum(@Param("competitionId") Long competitionId, @Param("roundNumber") Integer roundNumber, @Param("playerId") Long playerId);
} 