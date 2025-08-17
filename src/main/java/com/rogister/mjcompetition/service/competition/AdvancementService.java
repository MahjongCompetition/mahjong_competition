package com.rogister.mjcompetition.service.competition;

import com.rogister.mjcompetition.entity.competition.Competition;
import com.rogister.mjcompetition.entity.player.Player;
import com.rogister.mjcompetition.entity.player.PlayerRoundStatus;
import com.rogister.mjcompetition.entity.team.Team;
import com.rogister.mjcompetition.entity.team.TeamRoundStatus;
import com.rogister.mjcompetition.repository.competition.CompetitionRepository;
import com.rogister.mjcompetition.repository.player.PlayerCompetitionRegistrationRepository;
import com.rogister.mjcompetition.repository.player.PlayerRepository;
import com.rogister.mjcompetition.repository.player.PlayerRoundStatusRepository;
import com.rogister.mjcompetition.repository.team.TeamCompetitionRegistrationRepository;
import com.rogister.mjcompetition.repository.team.TeamRepository;
import com.rogister.mjcompetition.repository.team.TeamRoundStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdvancementService {
    
    @Autowired
    private PlayerRoundStatusRepository playerRoundStatusRepository;
    
    @Autowired
    private TeamRoundStatusRepository teamRoundStatusRepository;
    
    @Autowired
    private PlayerCompetitionRegistrationRepository playerRegistrationRepository;
    
    @Autowired
    private TeamCompetitionRegistrationRepository teamRegistrationRepository;
    
    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    /**
     * 个人赛晋级到指定轮次
     */
    @Transactional
    public List<PlayerRoundStatus> advancePlayersToRound(Long competitionId, List<Long> playerIds,
                                                         Integer targetRound, Integer initialScore) {
        // 验证比赛是否存在
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
        
        // 验证目标轮次是否有效
        if (targetRound <= 1) {
            throw new RuntimeException("目标轮次必须大于1");
        }
        
        // 获取当前最高轮次
        Integer currentMaxRound = getCurrentMaxRound(competitionId);
        if (targetRound <= currentMaxRound) {
            throw new RuntimeException("目标轮次必须超过当前最高轮次: " + currentMaxRound);
        }
        
        // 验证所有玩家是否都已报名该比赛
        for (Long playerId : playerIds) {
            if (!isPlayerRegistered(competitionId, playerId)) {
                throw new RuntimeException("玩家ID " + playerId + " 未报名该比赛");
            }
        }
        
        // 为每个玩家创建新轮次状态
        List<PlayerRoundStatus> roundStatuses = playerIds.stream()
                .map(playerId -> {
                    Player player = playerRepository.findById(playerId)
                            .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + playerId));
                    
                    // 检查是否已有该轮次状态
                    Optional<PlayerRoundStatus> existingStatus = playerRoundStatusRepository
                            .findByPlayerAndCompetitionAndRoundNumber(player, competition, targetRound);
                    
                    if (existingStatus.isPresent()) {
                        throw new RuntimeException("玩家 " + player.getUsername() + " 已存在于第" + targetRound + "轮");
                    }
                    
                    // 创建新轮次状态
                    PlayerRoundStatus roundStatus = new PlayerRoundStatus(player, competition, targetRound, initialScore);
                    roundStatus.setStatus(PlayerRoundStatus.RoundStatus.ACTIVE);
                    
                    return playerRoundStatusRepository.save(roundStatus);
                })
                .toList();
        
        return roundStatuses;
    }
    
    /**
     * 团队赛晋级到指定轮次
     */
    @Transactional
    public List<TeamRoundStatus> advanceTeamsToRound(Long competitionId, List<Long> teamIds,
                                                     Integer targetRound, Integer initialScore) {
        // 验证比赛是否存在
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
        
        // 验证目标轮次是否有效
        if (targetRound <= 1) {
            throw new RuntimeException("目标轮次必须大于1");
        }
        
        // 获取当前最高轮次
        Integer currentMaxRound = getCurrentMaxRound(competitionId);
        if (targetRound <= currentMaxRound) {
            throw new RuntimeException("目标轮次必须超过当前最高轮次: " + currentMaxRound);
        }
        
        // 验证所有团队是否都已报名该比赛
        for (Long teamId : teamIds) {
            if (!isTeamRegistered(competitionId, teamId)) {
                throw new RuntimeException("团队ID " + teamId + " 未报名该比赛");
            }
        }
        
        // 为每个团队创建新轮次状态
        List<TeamRoundStatus> roundStatuses = teamIds.stream()
                .map(teamId -> {
                    Team team = teamRepository.findById(teamId)
                            .orElseThrow(() -> new RuntimeException("团队不存在，ID: " + teamId));
                    
                    // 检查是否已有该轮次状态
                    Optional<TeamRoundStatus> existingStatus = teamRoundStatusRepository
                            .findByTeamAndCompetitionAndRoundNumber(team, competition, targetRound);
                    
                    if (existingStatus.isPresent()) {
                        throw new RuntimeException("团队 " + team.getTeamName() + " 已存在于第" + targetRound + "轮");
                    }
                    
                    // 创建新轮次状态
                    TeamRoundStatus roundStatus = new TeamRoundStatus(team, competition, targetRound, initialScore);
                    roundStatus.setStatus(TeamRoundStatus.RoundStatus.ACTIVE);
                    
                    return teamRoundStatusRepository.save(roundStatus);
                })
                .toList();
        
        return roundStatuses;
    }
    
    /**
     * 获取比赛当前最高轮次
     */
    public Integer getCurrentMaxRound(Long competitionId) {
        // 检查个人赛轮次
        Integer maxPlayerRound = playerRoundStatusRepository.findMaxRoundNumberByCompetitionId(competitionId);
        
        // 检查团队赛轮次
        Integer maxTeamRound = teamRoundStatusRepository.findMaxRoundNumberByCompetitionId(competitionId);
        
        // 返回最大值，如果没有则返回0
        if (maxPlayerRound == null && maxTeamRound == null) {
            return 0;
        } else if (maxPlayerRound == null) {
            return maxTeamRound;
        } else if (maxTeamRound == null) {
            return maxPlayerRound;
        } else {
            return Math.max(maxPlayerRound, maxTeamRound);
        }
    }
    
    /**
     * 检查玩家是否已报名比赛
     */
    private boolean isPlayerRegistered(Long competitionId, Long playerId) {
        return playerRegistrationRepository.existsByPlayerIdAndCompetitionId(playerId, competitionId);
    }
    
    /**
     * 检查团队是否已报名比赛
     */
    private boolean isTeamRegistered(Long competitionId, Long teamId) {
        return teamRegistrationRepository.existsByTeamIdAndCompetitionId(teamId, competitionId);
    }
    
    /**
     * 获取比赛指定轮次的所有个人赛参与者
     */
    public List<PlayerRoundStatus> getPlayersInRound(Long competitionId, Integer roundNumber) {
        return playerRoundStatusRepository.findByCompetitionIdAndRoundNumber(competitionId, roundNumber);
    }
    
    /**
     * 获取比赛指定轮次的所有团队赛参与者
     */
    public List<TeamRoundStatus> getTeamsInRound(Long competitionId, Integer roundNumber) {
        return teamRoundStatusRepository.findByCompetitionIdAndRoundNumber(competitionId, roundNumber);
    }
    
    /**
     * 更新玩家轮次得分
     */
    @Transactional
    public PlayerRoundStatus updatePlayerScore(Long playerId, Long competitionId, Integer roundNumber, Integer newScore) {
        PlayerRoundStatus roundStatus = playerRoundStatusRepository
                .findByPlayerIdAndCompetitionIdAndRoundNumber(playerId, competitionId, roundNumber)
                .orElseThrow(() -> new RuntimeException("未找到玩家轮次状态"));
        
        roundStatus.setCurrentScore(newScore);
        roundStatus.setUpdatedAt(LocalDateTime.now());
        
        return playerRoundStatusRepository.save(roundStatus);
    }
    
    /**
     * 更新团队轮次得分
     */
    @Transactional
    public TeamRoundStatus updateTeamScore(Long teamId, Long competitionId, Integer roundNumber, Integer newScore) {
        TeamRoundStatus roundStatus = teamRoundStatusRepository
                .findByTeamIdAndCompetitionIdAndRoundNumber(teamId, competitionId, roundNumber)
                .orElseThrow(() -> new RuntimeException("未找到团队轮次状态"));
        
        roundStatus.setCurrentScore(newScore);
        roundStatus.setUpdatedAt(LocalDateTime.now());
        
        return teamRoundStatusRepository.save(roundStatus);
    }
    
    /**
     * 淘汰玩家（设置状态为已淘汰）
     */
    @Transactional
    public PlayerRoundStatus eliminatePlayer(Long playerId, Long competitionId, Integer roundNumber) {
        PlayerRoundStatus roundStatus = playerRoundStatusRepository
                .findByPlayerIdAndCompetitionIdAndRoundNumber(playerId, competitionId, roundNumber)
                .orElseThrow(() -> new RuntimeException("未找到玩家轮次状态"));
        
        roundStatus.setStatus(PlayerRoundStatus.RoundStatus.ELIMINATED);
        roundStatus.setIsEliminated(true);
        roundStatus.setEliminationTime(LocalDateTime.now());
        roundStatus.setUpdatedAt(LocalDateTime.now());
        
        return playerRoundStatusRepository.save(roundStatus);
    }
    
    /**
     * 淘汰团队（设置状态为已淘汰）
     */
    @Transactional
    public TeamRoundStatus eliminateTeam(Long teamId, Long competitionId, Integer roundNumber) {
        TeamRoundStatus roundStatus = teamRoundStatusRepository
                .findByTeamIdAndCompetitionIdAndRoundNumber(teamId, competitionId, roundNumber)
                .orElseThrow(() -> new RuntimeException("未找到团队轮次状态"));
        
        roundStatus.setStatus(TeamRoundStatus.RoundStatus.ELIMINATED);
        roundStatus.setIsEliminated(true);
        roundStatus.setEliminationTime(LocalDateTime.now());
        roundStatus.setUpdatedAt(LocalDateTime.now());
        
        return teamRoundStatusRepository.save(roundStatus);
    }
    
    /**
     * 完成轮次（设置状态为已完成）
     */
    @Transactional
    public void completeRound(Long competitionId, Integer roundNumber) {
        // 完成个人赛轮次
        List<PlayerRoundStatus> playerStatuses = playerRoundStatusRepository
                .findByCompetitionIdAndRoundNumber(competitionId, roundNumber);
        
        for (PlayerRoundStatus status : playerStatuses) {
            if (status.getStatus() == PlayerRoundStatus.RoundStatus.ACTIVE) {
                status.setStatus(PlayerRoundStatus.RoundStatus.COMPLETED);
                status.setUpdatedAt(LocalDateTime.now());
                playerRoundStatusRepository.save(status);
            }
        }
        
        // 完成团队赛轮次
        List<TeamRoundStatus> teamStatuses = teamRoundStatusRepository
                .findByCompetitionIdAndRoundNumber(competitionId, roundNumber);
        
        for (TeamRoundStatus status : teamStatuses) {
            if (status.getStatus() == TeamRoundStatus.RoundStatus.ACTIVE) {
                status.setStatus(TeamRoundStatus.RoundStatus.COMPLETED);
                status.setUpdatedAt(LocalDateTime.now());
                teamRoundStatusRepository.save(status);
            }
        }
    }
}
