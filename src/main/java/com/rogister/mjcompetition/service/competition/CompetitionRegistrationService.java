package com.rogister.mjcompetition.service.competition;

import com.rogister.mjcompetition.entity.competition.Competition;
import com.rogister.mjcompetition.entity.player.Player;
import com.rogister.mjcompetition.entity.player.PlayerCompetitionRegistration;
import com.rogister.mjcompetition.entity.player.PlayerRoundStatus;
import com.rogister.mjcompetition.entity.team.Team;
import com.rogister.mjcompetition.entity.team.TeamCompetitionRegistration;
import com.rogister.mjcompetition.entity.team.TeamRoundStatus;
import com.rogister.mjcompetition.repository.competition.CompetitionRepository;
import com.rogister.mjcompetition.repository.player.PlayerCompetitionRegistrationRepository;
import com.rogister.mjcompetition.repository.player.PlayerRepository;
import com.rogister.mjcompetition.repository.player.PlayerRoundStatusRepository;
import com.rogister.mjcompetition.repository.team.TeamCompetitionRegistrationRepository;
import com.rogister.mjcompetition.repository.team.TeamRepository;
import com.rogister.mjcompetition.repository.team.TeamRoundStatusRepository;
import com.rogister.mjcompetition.service.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CompetitionRegistrationService {
    
    @Autowired
    private PlayerCompetitionRegistrationRepository playerRegistrationRepository;
    
    @Autowired
    private TeamCompetitionRegistrationRepository teamRegistrationRepository;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private TeamService teamService;
    
    @Autowired
    private PlayerRoundStatusRepository playerRoundStatusRepository;
    
    @Autowired
    private TeamRoundStatusRepository teamRoundStatusRepository;
    
    /**
     * 统一报名接口 - 根据比赛类型自动选择报名方式
     */
    public Object registerForCompetition(Long playerId, Long competitionId, Long teamId) {
        // 验证玩家是否存在
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + playerId));
        
        // 验证比赛是否存在
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
        
        // 检查报名是否已结束
        if (competition.isRegistrationClosed()) {
            throw new RuntimeException("报名已结束，无法报名");
        }
        
        // 根据比赛类型进行报名
        if (competition.getCompetitionType() == Competition.CompetitionType.INDIVIDUAL) {
            return registerForIndividualCompetition(player, competition);
        } else if (competition.getCompetitionType() == Competition.CompetitionType.TEAM) {
            return registerForTeamCompetition(player, competition, teamId);
        } else {
            throw new RuntimeException("未知的比赛类型");
        }
    }
    
    /**
     * 个人赛报名
     */
    @Transactional
    private PlayerCompetitionRegistration registerForIndividualCompetition(Player player, Competition competition) {
        // 检查是否已经报名
        if (playerRegistrationRepository.existsByPlayerIdAndCompetitionId(player.getId(), competition.getId())) {
            throw new RuntimeException("您已经报名了这场比赛");
        }
        
        // 创建报名记录
        PlayerCompetitionRegistration registration = new PlayerCompetitionRegistration(player, competition);
        PlayerCompetitionRegistration savedRegistration = playerRegistrationRepository.save(registration);
        
        // 自动创建第一轮状态，初始得分为0
        PlayerRoundStatus firstRoundStatus = new PlayerRoundStatus(player, competition, 1, 0);
        playerRoundStatusRepository.save(firstRoundStatus);
        
        return savedRegistration;
    }
    
    /**
     * 团队赛报名
     */
    @Transactional
    private TeamCompetitionRegistration registerForTeamCompetition(Player player, Competition competition, Long teamId) {
        if (teamId == null) {
            throw new RuntimeException("团队赛需要提供团队ID");
        }
        
        // 验证团队是否存在
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            throw new RuntimeException("团队不存在，ID: " + teamId);
        }
        
        Team team = teamOpt.get();
        
        // 首先检查玩家是否是该团队的成员
        if (!teamService.isTeamMember(teamId, player.getId())) {
            throw new RuntimeException("您不是该团队的成员，无法使用此团队报名");
        }
        
        // 然后检查玩家是否是队长（只有队长才能代表团队报名）
        if (!teamService.isCaptain(teamId, player.getId())) {
            throw new RuntimeException("只有队长可以代表团队报名比赛");
        }
        
        // 检查团队是否已经报名
        if (teamRegistrationRepository.existsByTeamIdAndCompetitionId(teamId, competition.getId())) {
            throw new RuntimeException("团队已经报名了这场比赛");
        }
        
        // 检查团队人数是否满足要求（假设团队赛至少需要2人）
        if (team.getCurrentMembers() < 2) {
            throw new RuntimeException("团队人数不足，无法报名团队赛");
        }
        
        // 创建团队报名记录
        TeamCompetitionRegistration registration = new TeamCompetitionRegistration(team, competition);
        TeamCompetitionRegistration savedRegistration = teamRegistrationRepository.save(registration);
        
        // 自动创建第一轮状态，初始得分为0
        TeamRoundStatus firstRoundStatus = new TeamRoundStatus(team, competition, 1, 0);
        teamRoundStatusRepository.save(firstRoundStatus);
        
        return savedRegistration;
    }
    
    /**
     * 取消个人赛报名
     */
    public void cancelIndividualRegistration(Long playerId, Long competitionId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + playerId));
        
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
        
        PlayerCompetitionRegistration registration = playerRegistrationRepository.findByPlayerIdAndCompetitionId(player.getId(), competition.getId())
                .orElseThrow(() -> new RuntimeException("未找到报名记录"));
        
        // 检查报名是否已结束
        if (competition.isRegistrationClosed()) {
            throw new RuntimeException("报名已结束，无法取消报名");
        }
        
        // 检查报名状态是否允许取消
        if (registration.getStatus() != PlayerCompetitionRegistration.RegistrationStatus.REGISTERED && 
            registration.getStatus() != PlayerCompetitionRegistration.RegistrationStatus.CONFIRMED) {
            throw new RuntimeException("当前报名状态不允许取消");
        }
        
        registration.setStatus(PlayerCompetitionRegistration.RegistrationStatus.CANCELLED);
        playerRegistrationRepository.save(registration);
    }
    
    /**
     * 取消团队赛报名（只有队长可以取消）
     */
    public void cancelTeamRegistration(Long teamId, Long competitionId, Long captainId) {
        // 验证团队是否存在
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            throw new RuntimeException("团队不存在，ID: " + teamId);
        }
        
        Team team = teamOpt.get();
        
        // 检查是否是队长
        if (!teamService.isCaptain(teamId, captainId)) {
            throw new RuntimeException("只有队长可以取消团队报名");
        }
        
        // 查找报名记录
        Optional<TeamCompetitionRegistration> registrationOpt = teamRegistrationRepository.findByTeamIdAndCompetitionId(teamId, competitionId);
        if (registrationOpt.isEmpty()) {
            throw new RuntimeException("未找到团队报名记录");
        }
        
        TeamCompetitionRegistration registration = registrationOpt.get();
        
        // 检查报名是否已结束
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));
        
        if (competition.isRegistrationClosed()) {
            throw new RuntimeException("报名已结束，无法取消报名");
        }
        
        // 检查报名状态是否允许取消
        if (registration.getStatus() != TeamCompetitionRegistration.RegistrationStatus.REGISTERED && 
            registration.getStatus() != TeamCompetitionRegistration.RegistrationStatus.CONFIRMED) {
            throw new RuntimeException("当前报名状态不允许取消");
        }
        
        registration.setStatus(TeamCompetitionRegistration.RegistrationStatus.CANCELLED);
        teamRegistrationRepository.save(registration);
    }
    
    /**
     * 获取玩家的所有个人赛报名记录
     */
    public List<PlayerCompetitionRegistration> getPlayerIndividualRegistrations(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + playerId));
        
        return playerRegistrationRepository.findByPlayerId(playerId);
    }
    
    /**
     * 获取玩家的团队赛报名记录
     */
    public List<TeamCompetitionRegistration> getPlayerTeamRegistrations(Long playerId) {
        // 查找玩家所属的团队
        Optional<Team> teamOpt = teamService.findTeamByPlayerId(playerId);
        if (teamOpt.isEmpty()) {
            return List.of(); // 玩家没有团队
        }
        
        Team team = teamOpt.get();
        return teamRegistrationRepository.findByTeamId(team.getId());
    }
    
    /**
     * 获取比赛的所有个人赛报名记录
     */
    public List<PlayerCompetitionRegistration> getCompetitionIndividualRegistrations(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
        
        return playerRegistrationRepository.findByCompetitionId(competitionId);
    }
    
    /**
     * 获取比赛的所有团队赛报名记录
     */
    public List<TeamCompetitionRegistration> getCompetitionTeamRegistrations(Long competitionId) {
        return teamRegistrationRepository.findByCompetitionId(competitionId);
    }
    
    /**
     * 检查玩家是否已报名某场比赛
     */
    public boolean isPlayerRegistered(Long playerId, Long competitionId) {
        Player player = playerRepository.findById(playerId).orElse(null);
        Competition competition = competitionRepository.findById(competitionId).orElse(null);
        
        if (player == null || competition == null) {
            return false;
        }
        
        // 个人赛报名检查
        if (competition.getCompetitionType() == Competition.CompetitionType.INDIVIDUAL) {
            return playerRegistrationRepository.existsByPlayerIdAndCompetitionId(playerId, competitionId);
        }
        
        // 团队赛报名检查
        if (competition.getCompetitionType() == Competition.CompetitionType.TEAM) {
            Optional<Team> teamOpt = teamService.findTeamByPlayerId(playerId);
            if (teamOpt.isPresent()) {
                return teamRegistrationRepository.existsByTeamIdAndCompetitionId(teamOpt.get().getId(), competitionId);
            }
        }
        
        return false;
    }
}
