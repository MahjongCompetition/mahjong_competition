package com.rogister.mjcompetition.repository.team;

import com.rogister.mjcompetition.entity.team.TeamCompetitionRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamCompetitionRegistrationRepository extends JpaRepository<TeamCompetitionRegistration, Long> {
    
    /**
     * 根据团队ID查找报名记录
     */
    List<TeamCompetitionRegistration> findByTeamId(Long teamId);
    
    /**
     * 根据比赛ID查找报名记录
     */
    List<TeamCompetitionRegistration> findByCompetitionId(Long competitionId);
    
    /**
     * 根据团队ID和比赛ID查找报名记录
     */
    Optional<TeamCompetitionRegistration> findByTeamIdAndCompetitionId(Long teamId, Long competitionId);
    
    /**
     * 检查团队是否已经报名某场比赛
     */
    boolean existsByTeamIdAndCompetitionId(Long teamId, Long competitionId);
    
    /**
     * 根据比赛ID和状态查找报名记录
     */
    List<TeamCompetitionRegistration> findByCompetitionIdAndStatus(Long competitionId, TeamCompetitionRegistration.RegistrationStatus status);
    
    /**
     * 根据团队ID和状态查找报名记录
     */
    List<TeamCompetitionRegistration> findByTeamIdAndStatus(Long teamId, TeamCompetitionRegistration.RegistrationStatus status);
}
