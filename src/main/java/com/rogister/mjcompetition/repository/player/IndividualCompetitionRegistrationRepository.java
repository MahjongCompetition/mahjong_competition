package com.rogister.mjcompetition.repository.player;

import com.rogister.mjcompetition.entity.competition.Competition;
import com.rogister.mjcompetition.entity.player.IndividualCompetitionRegistration;
import com.rogister.mjcompetition.entity.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndividualCompetitionRegistrationRepository extends JpaRepository<IndividualCompetitionRegistration, Long> {
    
    /**
     * 根据比赛和玩家查找报名记录
     */
    Optional<IndividualCompetitionRegistration> findByCompetitionAndPlayer(Competition competition, Player player);
    
    /**
     * 根据比赛查找所有报名记录
     */
    List<IndividualCompetitionRegistration> findByCompetition(Competition competition);
    
    /**
     * 根据比赛和状态查找报名记录
     */
    List<IndividualCompetitionRegistration> findByCompetitionAndStatus(Competition competition, IndividualCompetitionRegistration.RegistrationStatus status);
    
    /**
     * 根据玩家查找所有报名记录
     */
    List<IndividualCompetitionRegistration> findByPlayer(Player player);
    
    /**
     * 检查玩家是否已报名某场比赛
     */
    boolean existsByCompetitionAndPlayer(Competition competition, Player player);
    
    /**
     * 根据比赛和状态统计报名人数
     */
    long countByCompetitionAndStatus(Competition competition, IndividualCompetitionRegistration.RegistrationStatus status);
} 