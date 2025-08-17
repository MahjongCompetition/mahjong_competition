package com.rogister.mjcompetition.repository.player;

import com.rogister.mjcompetition.entity.competition.Competition;
import com.rogister.mjcompetition.entity.player.Player;
import com.rogister.mjcompetition.entity.player.PlayerCompetitionRegistration;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerCompetitionRegistrationRepository extends JpaRepository<PlayerCompetitionRegistration, Long> {
    
    /**
     * 根据玩家ID查找报名记录
     */
    @EntityGraph(attributePaths = {"competition", "competition.rule"})
    List<PlayerCompetitionRegistration> findByPlayerId(Long playerId);
    
    /**
     * 根据比赛ID查找报名记录
     */
    @EntityGraph(attributePaths = {"player"})
    List<PlayerCompetitionRegistration> findByCompetitionId(Long competitionId);
    
    /**
     * 根据玩家和比赛查找报名记录
     */
    Optional<PlayerCompetitionRegistration> findByPlayerAndCompetition(Player player, Competition competition);
    
    /**
     * 根据玩家ID和比赛ID查找报名记录
     */
    Optional<PlayerCompetitionRegistration> findByPlayerIdAndCompetitionId(Long playerId, Long competitionId);
    
    /**
     * 检查玩家是否已报名某场比赛
     */
    boolean existsByPlayerAndCompetition(Player player, Competition competition);
    
    /**
     * 根据玩家ID和比赛ID检查玩家是否已报名
     */
    boolean existsByPlayerIdAndCompetitionId(Long playerId, Long competitionId);
    
    /**
     * 根据比赛ID统计报名人数
     */
    long countByCompetitionId(Long competitionId);
    
    /**
     * 根据比赛ID和状态查找报名记录
     */
    List<PlayerCompetitionRegistration> findByCompetitionIdAndStatus(Long competitionId, PlayerCompetitionRegistration.RegistrationStatus status);
    
    /**
     * 根据玩家ID和状态查找报名记录
     */
    List<PlayerCompetitionRegistration> findByPlayerIdAndStatus(Long playerId, PlayerCompetitionRegistration.RegistrationStatus status);
}
