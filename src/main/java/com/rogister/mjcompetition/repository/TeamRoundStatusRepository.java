package com.rogister.mjcompetition.repository;

import com.rogister.mjcompetition.entity.TeamRoundStatus;
import com.rogister.mjcompetition.entity.Team;
import com.rogister.mjcompetition.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRoundStatusRepository extends JpaRepository<TeamRoundStatus, Long> {
    
    /**
     * 根据比赛ID和轮次号查找团队轮次状态
     */
    List<TeamRoundStatus> findByCompetitionIdAndRoundNumber(Long competitionId, Integer roundNumber);
    
    /**
     * 根据团队和比赛查找指定轮次的状态
     */
    Optional<TeamRoundStatus> findByTeamAndCompetitionAndRoundNumber(Team team, Competition competition, Integer roundNumber);
    
    /**
     * 根据比赛ID查找最大轮次号
     */
    @Query("SELECT MAX(trs.roundNumber) FROM TeamRoundStatus trs WHERE trs.competition.id = :competitionId")
    Integer findMaxRoundNumberByCompetitionId(@Param("competitionId") Long competitionId);
    
    /**
     * 根据比赛ID查找所有团队轮次状态
     */
    List<TeamRoundStatus> findByCompetitionId(Long competitionId);
    
    /**
     * 根据团队ID查找所有轮次状态
     */
    List<TeamRoundStatus> findByTeamId(Long teamId);
    
    /**
     * 根据比赛ID和轮次号查找活跃状态的团队轮次状态
     */
    List<TeamRoundStatus> findByCompetitionIdAndRoundNumberAndStatus(Long competitionId, Integer roundNumber, TeamRoundStatus.RoundStatus status);
    
    /**
     * 根据团队ID、比赛ID和轮次号查找团队状态
     */
    Optional<TeamRoundStatus> findByTeamIdAndCompetitionIdAndRoundNumber(Long teamId, Long competitionId, Integer roundNumber);
    
    /**
     * 根据团队ID和比赛ID查找所有轮次状态
     */
    List<TeamRoundStatus> findByTeamIdAndCompetitionId(Long teamId, Long competitionId);
}
