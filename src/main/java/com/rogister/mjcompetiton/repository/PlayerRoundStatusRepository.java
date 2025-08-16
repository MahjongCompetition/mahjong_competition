package com.rogister.mjcompetiton.repository;

import com.rogister.mjcompetiton.entity.Competition;
import com.rogister.mjcompetiton.entity.CompetitionRound;
import com.rogister.mjcompetiton.entity.Player;
import com.rogister.mjcompetiton.entity.PlayerRoundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRoundStatusRepository extends JpaRepository<PlayerRoundStatus, Long> {
    
    /**
     * 根据比赛、轮次和玩家查找状态
     */
    Optional<PlayerRoundStatus> findByCompetitionAndRoundAndPlayer(Competition competition, CompetitionRound round, Player player);
    
    /**
     * 根据比赛和轮次查找所有选手状态
     */
    List<PlayerRoundStatus> findByCompetitionAndRoundOrderByCurrentScoreDesc(Competition competition, CompetitionRound round);
    
    /**
     * 根据比赛和轮次查找晋级的选手
     */
    List<PlayerRoundStatus> findByCompetitionAndRoundAndIsAdvancedTrueOrderByCurrentScoreDesc(Competition competition, CompetitionRound round);
    
    /**
     * 根据比赛和玩家查找所有轮次状态
     */
    List<PlayerRoundStatus> findByCompetitionAndPlayerOrderByRoundRoundNumber(Competition competition, Player player);
    
    /**
     * 根据比赛查找当前轮次的所有选手状态
     */
    List<PlayerRoundStatus> findByCompetitionAndRoundIsActiveTrueOrderByCurrentScoreDesc(Competition competition);
    
    /**
     * 检查玩家在指定轮次是否已存在状态记录
     */
    boolean existsByCompetitionAndRoundAndPlayer(Competition competition, CompetitionRound round, Player player);
} 