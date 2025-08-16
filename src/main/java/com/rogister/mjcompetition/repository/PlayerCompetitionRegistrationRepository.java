package com.rogister.mjcompetition.repository;

import com.rogister.mjcompetition.entity.Competition;
import com.rogister.mjcompetition.entity.Player;
import com.rogister.mjcompetition.entity.PlayerCompetitionRegistration;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerCompetitionRegistrationRepository extends JpaRepository<PlayerCompetitionRegistration, Long> {
    
    /**
     * 根据玩家查找报名记录
     */
    @EntityGraph(attributePaths = {"competition", "competition.rule"})
    List<PlayerCompetitionRegistration> findByPlayer(Player player);
    
    /**
     * 根据比赛查找报名记录
     */
    @EntityGraph(attributePaths = {"player"})
    List<PlayerCompetitionRegistration> findByCompetition(Competition competition);
    
    /**
     * 根据玩家和比赛查找报名记录
     */
    Optional<PlayerCompetitionRegistration> findByPlayerAndCompetition(Player player, Competition competition);
    
    /**
     * 检查玩家是否已报名某场比赛
     */
    boolean existsByPlayerAndCompetition(Player player, Competition competition);
    
    /**
     * 根据比赛ID统计报名人数
     */
    long countByCompetition(Competition competition);
}
