package com.rogister.mjcompetition.service.team;

import com.rogister.mjcompetition.entity.team.Team;
import com.rogister.mjcompetition.entity.team.TeamMember;
import com.rogister.mjcompetition.entity.player.Player;
import com.rogister.mjcompetition.repository.team.TeamRepository;
import com.rogister.mjcompetition.repository.team.TeamMemberRepository;
import com.rogister.mjcompetition.repository.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService {
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private TeamMemberRepository teamMemberRepository;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    /**
     * 创建团队
     */
    public Team createTeam(String teamName, Long captainId, Integer maxMembers) {
        // 检查队长是否存在
        Optional<Player> captainOpt = playerRepository.findById(captainId);
        if (captainOpt.isEmpty()) {
            throw new RuntimeException("队长不存在");
        }
        
        // 检查队长是否已经是其他团队的成员
        List<TeamMember> existingMemberships = teamMemberRepository.findByPlayerIdAndIsActiveTrue(captainId);
        if (!existingMemberships.isEmpty()) {
            throw new RuntimeException("队长已经是其他团队的成员，无法创建新团队");
        }
        
        // 生成唯一的团队编号
        String teamCode = generateUniqueTeamCode();
        
        // 创建团队
        Team team = new Team(teamName, teamCode, captainId, maxMembers);
        Team savedTeam = teamRepository.save(team);
        
        // 创建队长成员关系
        Player captain = playerRepository.findById(captainId)
                .orElseThrow(() -> new RuntimeException("队长不存在，ID: " + captainId));
        TeamMember captainMember = new TeamMember(savedTeam.getId(), captain);
        teamMemberRepository.save(captainMember);
        
        return savedTeam;
    }
    
    /**
     * 生成唯一的团队编号
     */
    private String generateUniqueTeamCode() {
        String teamCode;
        do {
            // 生成6位随机字符串
            teamCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (teamRepository.existsByTeamCode(teamCode));
        return teamCode;
    }
    
    /**
     * 加入团队
     */
    public TeamMember joinTeam(String teamCode, Long playerId) {
        // 检查团队是否存在
        Optional<Team> teamOpt = teamRepository.findByTeamCode(teamCode);
        if (teamOpt.isEmpty()) {
            throw new RuntimeException("团队不存在");
        }
        
        Team team = teamOpt.get();
        
        // 检查团队是否已满
        if (team.getCurrentMembers() >= team.getMaxMembers()) {
            throw new RuntimeException("团队已满，无法加入");
        }
        
        // 检查玩家是否已经是该团队成员
        if (teamMemberRepository.existsByTeamIdAndPlayerIdAndIsActiveTrue(team.getId(), playerId)) {
            throw new RuntimeException("玩家已经是该团队成员");
        }
        
        // 检查玩家是否已经是其他团队的成员
        List<TeamMember> existingMemberships = teamMemberRepository.findByPlayerIdAndIsActiveTrue(playerId);
        if (!existingMemberships.isEmpty()) {
            throw new RuntimeException("玩家已经是其他团队的成员，无法加入新团队");
        }
        
        // 创建成员关系
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + playerId));
        TeamMember member = new TeamMember(team.getId(), player);
        TeamMember savedMember = teamMemberRepository.save(member);
        
        // 更新团队当前成员数
        team.setCurrentMembers(team.getCurrentMembers() + 1);
        teamRepository.save(team);
        
        return savedMember;
    }
    
    /**
     * 离开团队
     */
    public void leaveTeam(Long teamId, Long playerId) {
        // 检查成员关系是否存在
        Optional<TeamMember> memberOpt = teamMemberRepository.findByTeamIdAndPlayerIdAndIsActiveTrue(teamId, playerId);
        if (memberOpt.isEmpty()) {
            throw new RuntimeException("玩家不是该团队成员");
        }
        
        TeamMember member = memberOpt.get();
        
        // 检查是否是队长
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isPresent() && teamOpt.get().getCaptainId().equals(playerId)) {
            throw new RuntimeException("队长无法离开团队，请先解散团队或转让队长");
        }
        
        // 标记成员关系为非激活
        member.setIsActive(false);
        teamMemberRepository.save(member);
        
        // 更新团队当前成员数
        Team team = teamOpt.get();
        team.setCurrentMembers(team.getCurrentMembers() - 1);
        teamRepository.save(team);
    }
    
    /**
     * 解散团队（只有队长可以解散）
     */
    public void dissolveTeam(Long teamId, Long captainId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            throw new RuntimeException("团队不存在");
        }
        
        Team team = teamOpt.get();
        
        // 检查是否是队长
        if (!team.getCaptainId().equals(captainId)) {
            throw new RuntimeException("只有队长可以解散团队");
        }
        
        // 标记团队为非激活
        team.setIsActive(false);
        teamRepository.save(team);
        
        // 标记所有成员关系为非激活
        List<TeamMember> members = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        for (TeamMember member : members) {
            member.setIsActive(false);
            teamMemberRepository.save(member);
        }
    }
    
    /**
     * 根据团队编号查找团队
     */
    public Optional<Team> findByTeamCode(String teamCode) {
        return teamRepository.findByTeamCode(teamCode);
    }
    
    /**
     * 根据队长ID查找团队
     */
    public List<Team> findByCaptainId(Long captainId) {
        return teamRepository.findByCaptainIdAndIsActiveTrue(captainId);
    }
    
    /**
     * 根据玩家ID查找所属团队
     */
    public Optional<Team> findTeamByPlayerId(Long playerId) {
        List<TeamMember> memberships = teamMemberRepository.findByPlayerIdAndIsActiveTrue(playerId);
        if (memberships.isEmpty()) {
            return Optional.empty();
        }
        
        // 玩家只能属于一个团队
        Long teamId = memberships.get(0).getTeamId();
        return teamRepository.findById(teamId);
    }
    
    /**
     * 获取团队所有成员
     */
    public List<TeamMember> getTeamMembers(Long teamId) {
        return teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
    }
    
    /**
     * 检查玩家是否是队长
     */
    public boolean isCaptain(Long teamId, Long playerId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        return teamOpt.isPresent() && teamOpt.get().getCaptainId().equals(playerId);
    }
    
    /**
     * 检查玩家是否是团队成员
     */
    public boolean isTeamMember(Long teamId, Long playerId) {
        return teamMemberRepository.existsByTeamIdAndPlayerIdAndIsActiveTrue(teamId, playerId);
    }
    
    /**
     * 获取玩家加入的所有团队（包括历史团队）
     */
    public List<Team> findAllTeamsByPlayerId(Long playerId) {
        List<TeamMember> memberships = teamMemberRepository.findByPlayerIdAndIsActiveTrue(playerId);
        return memberships.stream()
                .map(membership -> teamRepository.findById(membership.getTeamId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(Team::getIsActive) // 只返回活跃的团队
                .toList();
    }
}
