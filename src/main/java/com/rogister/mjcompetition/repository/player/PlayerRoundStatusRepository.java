package com.rogister.mjcompetition.repository.player;

import com.rogister.mjcompetition.entity.competition.Competition;
import com.rogister.mjcompetition.entity.player.Player;
import com.rogister.mjcompetition.entity.player.PlayerRoundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRoundStatusRepository extends JpaRepository<PlayerRoundStatus, Long> {
    
    /**
     * 根据比赛ID和轮次号查找所有选手状态
     */
    List<PlayerRoundStatus> findByCompetitionIdAndRoundNumber(Long competitionId, Integer roundNumber);
    
    /**
     * 根据玩家和比赛查找指定轮次的状态
     */
    Optional<PlayerRoundStatus> findByPlayerAndCompetitionAndRoundNumber(Player player, Competition competition, Integer roundNumber);
    
    /**
     * 根据比赛ID查找最大轮次号
     */
    @Query("SELECT MAX(prs.roundNumber) FROM PlayerRoundStatus prs WHERE prs.competition.id = :competitionId")
    Integer findMaxRoundNumberByCompetitionId(@Param("competitionId") Long competitionId);
    
    /**
     * 根据比赛ID查找所有选手轮次状态
     */
    List<PlayerRoundStatus> findByCompetitionId(Long competitionId);
    
    /**
     * 根据玩家ID查找所有轮次状态
     */
    List<PlayerRoundStatus> findByPlayerId(Long playerId);
    
    /**
     * 根据比赛ID和轮次号查找活跃状态的选手轮次状态
     */
    List<PlayerRoundStatus> findByCompetitionIdAndRoundNumberAndStatus(Long competitionId, Integer roundNumber, PlayerRoundStatus.RoundStatus status);
    
    /**
     * 根据玩家ID、比赛ID和轮次号查找选手状态
     */
    Optional<PlayerRoundStatus> findByPlayerIdAndCompetitionIdAndRoundNumber(Long playerId, Long competitionId, Integer roundNumber);
    
    /**
     * 根据玩家ID和比赛ID查找所有轮次状态
     */
    List<PlayerRoundStatus> findByPlayerIdAndCompetitionId(Long playerId, Long competitionId);
} 