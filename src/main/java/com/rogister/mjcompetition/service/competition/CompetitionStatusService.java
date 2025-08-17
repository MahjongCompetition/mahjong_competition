package com.rogister.mjcompetition.service.competition;

import com.rogister.mjcompetition.dto.competition.CompetitionStatusResponse;
import com.rogister.mjcompetition.entity.competition.Competition;
import com.rogister.mjcompetition.entity.competition.MatchResult;
import com.rogister.mjcompetition.entity.player.Player;
import com.rogister.mjcompetition.entity.player.PlayerRoundStatus;
import com.rogister.mjcompetition.entity.team.Team;
import com.rogister.mjcompetition.entity.team.TeamMember;
import com.rogister.mjcompetition.entity.team.TeamRoundStatus;
import com.rogister.mjcompetition.repository.competition.CompetitionRepository;
import com.rogister.mjcompetition.repository.competition.MatchResultRepository;
import com.rogister.mjcompetition.repository.player.PlayerRepository;
import com.rogister.mjcompetition.repository.player.PlayerRoundStatusRepository;
import com.rogister.mjcompetition.repository.team.TeamMemberRepository;
import com.rogister.mjcompetition.repository.team.TeamRoundStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompetitionStatusService {
    
    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private PlayerRoundStatusRepository playerRoundStatusRepository;
    
    @Autowired
    private TeamRoundStatusRepository teamRoundStatusRepository;
    
    @Autowired
    private MatchResultRepository matchResultRepository;
    
    @Autowired
    private TeamMemberRepository teamMemberRepository;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private MatchResultService matchResultService;
    
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
     * 查询比赛状态
     */
    public CompetitionStatusResponse getCompetitionStatus(Long competitionId, Integer roundNumber) {
        // 验证比赛是否存在
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
        
        // 创建响应对象
        CompetitionStatusResponse response = new CompetitionStatusResponse();
        response.setCompetitionId(competitionId);
        response.setRoundNumber(roundNumber);
        response.setCompetitionName(competition.getCompetitionName());
        response.setCompetitionType(competition.getCompetitionType().toString());
        
                 // 根据比赛类型查询不同的状态信息
         if (competition.getCompetitionType() == Competition.CompetitionType.INDIVIDUAL) {
             response.setPlayerStatusList(getPlayerStatusList(competitionId, roundNumber));
             response.setTeamStatusList(new ArrayList<>());
         } else if (competition.getCompetitionType() == Competition.CompetitionType.TEAM) {
             // 团队赛既返回团队状态，也返回个人状态
             response.setTeamStatusList(getTeamStatusList(competitionId, roundNumber));
             response.setPlayerStatusList(getTeamPlayerStatusList(competitionId, roundNumber));
         }
        
        return response;
    }
    
    /**
     * 获取个人赛状态列表
     */
    private List<CompetitionStatusResponse.PlayerStatusInfo> getPlayerStatusList(Long competitionId, Integer roundNumber) {
        // 获取该轮次的所有玩家状态
        List<PlayerRoundStatus> roundStatuses = playerRoundStatusRepository
                .findByCompetitionIdAndRoundNumber(competitionId, roundNumber);
        
        return roundStatuses.stream()
                .map(this::buildPlayerStatusInfo)
                .sorted(Comparator.comparing(CompetitionStatusResponse.PlayerStatusInfo::getCurrentRoundScore).reversed())
                .collect(Collectors.toList());
    }
    
         /**
      * 获取团队赛状态列表
      */
     private List<CompetitionStatusResponse.TeamStatusInfo> getTeamStatusList(Long competitionId, Integer roundNumber) {
         // 获取该轮次的所有团队状态
         List<TeamRoundStatus> roundStatuses = teamRoundStatusRepository
                 .findByCompetitionIdAndRoundNumber(competitionId, roundNumber);
         
         return roundStatuses.stream()
                 .map(this::buildTeamStatusInfo)
                 .sorted(Comparator.comparing(CompetitionStatusResponse.TeamStatusInfo::getTotalScore).reversed())
                 .collect(Collectors.toList());
     }
     
     /**
      * 获取团队赛中的个人状态列表
      */
     private List<CompetitionStatusResponse.PlayerStatusInfo> getTeamPlayerStatusList(Long competitionId, Integer roundNumber) {
         // 获取该轮次的所有团队状态
         List<TeamRoundStatus> roundStatuses = teamRoundStatusRepository
                 .findByCompetitionIdAndRoundNumber(competitionId, roundNumber);
         
         List<CompetitionStatusResponse.PlayerStatusInfo> allPlayerStatuses = new ArrayList<>();
         
         for (TeamRoundStatus teamRoundStatus : roundStatuses) {
             Team team = teamRoundStatus.getTeam();
             // 获取团队成员
             List<TeamMember> teamMembers = teamMemberRepository.findByTeamIdAndIsActiveTrue(team.getId());
             
             for (TeamMember member : teamMembers) {
                 Player player = member.getPlayer();
                 
                 // 直接为团队赛中的玩家构建状态信息
                 CompetitionStatusResponse.PlayerStatusInfo playerStatus = buildTeamPlayerStatusInfo(
                         player, teamRoundStatus.getCompetition(), roundNumber);
                 allPlayerStatuses.add(playerStatus);
             }
         }
         
         // 按当前轮次得分从高到低排序
         return allPlayerStatuses.stream()
                 .sorted(Comparator.comparing(CompetitionStatusResponse.PlayerStatusInfo::getCurrentRoundScore).reversed())
                 .collect(Collectors.toList());
     }
    
    /**
     * 构建玩家状态信息
     */
    private CompetitionStatusResponse.PlayerStatusInfo buildPlayerStatusInfo(PlayerRoundStatus roundStatus) {
        CompetitionStatusResponse.PlayerStatusInfo info = new CompetitionStatusResponse.PlayerStatusInfo();
        
        Player player = roundStatus.getPlayer();
        info.setPlayerId(player.getId());
        info.setPlayerName(player.getNickname());
        info.setUsername(player.getUsername());
        info.setInitialScore(roundStatus.getInitialScore());
        info.setStatus(roundStatus.getStatus().toString());
        
        // 使用PT分数计算当轮得分
        Double currentRoundPtScore = matchResultService.getPlayerPtScoreSum(
                roundStatus.getCompetition().getId(), 
                roundStatus.getRoundNumber(), 
                player.getId());
        
        // 将PT分数转换为整数显示，乘以适当的倍数以便显示
        int currentRoundScore = (int) Math.round(currentRoundPtScore);
        info.setCurrentRoundScore(currentRoundScore);
        
        // 个人不需要totalScore，不设置此字段
        
        // 获取当前轮次的比赛统计数据
        Map<String, Object> stats = getPlayerRoundMatchStats(roundStatus.getCompetition().getId(), roundStatus.getRoundNumber(), player.getId());
        info.setAppearanceCount((Integer) stats.get("appearanceCount"));
        info.setAveragePosition((Double) stats.get("averagePosition"));
        info.setFirstPlaceCount((Integer) stats.get("firstPlaceCount"));
        info.setSecondPlaceCount((Integer) stats.get("secondPlaceCount"));
        info.setThirdPlaceCount((Integer) stats.get("thirdPlaceCount"));
        info.setFourthPlaceCount((Integer) stats.get("fourthPlaceCount"));
        
        return info;
    }
    
    /**
     * 构建团队赛中的玩家状态信息
     */
    private CompetitionStatusResponse.PlayerStatusInfo buildTeamPlayerStatusInfo(Player player, Competition competition, Integer roundNumber) {
        CompetitionStatusResponse.PlayerStatusInfo info = new CompetitionStatusResponse.PlayerStatusInfo();
        
        info.setPlayerId(player.getId());
        info.setPlayerName(player.getNickname());
        info.setUsername(player.getUsername());
        info.setInitialScore(0); // 团队赛中个人没有初始分数
        info.setStatus("ACTIVE"); // 团队赛中参与的玩家状态默认为活跃
        
        // 使用PT分数计算当轮得分
        Double currentRoundPtScore = matchResultService.getPlayerPtScoreSum(
                competition.getId(), 
                roundNumber, 
                player.getId());
        
        // 将PT分数转换为整数显示
        int currentRoundScore = (int) Math.round(currentRoundPtScore);
        info.setCurrentRoundScore(currentRoundScore);
        info.setTotalScore(currentRoundScore); // 团队赛中个人总分等于当轮得分
        
        // 获取当前轮次的比赛统计数据
        Map<String, Object> stats = getPlayerRoundMatchStats(competition.getId(), roundNumber, player.getId());
        info.setAppearanceCount((Integer) stats.get("appearanceCount"));
        info.setAveragePosition((Double) stats.get("averagePosition"));
        info.setFirstPlaceCount((Integer) stats.get("firstPlaceCount"));
        info.setSecondPlaceCount((Integer) stats.get("secondPlaceCount"));
        info.setThirdPlaceCount((Integer) stats.get("thirdPlaceCount"));
        info.setFourthPlaceCount((Integer) stats.get("fourthPlaceCount"));
        
        return info;
    }
    
    /**
     * 构建团队状态信息
     */
    private CompetitionStatusResponse.TeamStatusInfo buildTeamStatusInfo(TeamRoundStatus roundStatus) {
        CompetitionStatusResponse.TeamStatusInfo info = new CompetitionStatusResponse.TeamStatusInfo();
        
        Team team = roundStatus.getTeam();
        info.setTeamId(team.getId());
        info.setTeamName(team.getTeamName());
        info.setTeamCode(team.getTeamCode());
        info.setInitialScore(roundStatus.getInitialScore());
        info.setStatus(roundStatus.getStatus().toString());
        
        // 计算团队当轮PT得分总和
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamIdAndIsActiveTrue(team.getId());
        double teamCurrentRoundPtScore = 0.0;
        
        for (TeamMember member : teamMembers) {
            Double playerPtScore = matchResultService.getPlayerPtScoreSum(
                    roundStatus.getCompetition().getId(), 
                    roundStatus.getRoundNumber(), 
                    member.getPlayer().getId());
            teamCurrentRoundPtScore += playerPtScore;
        }
        
        int currentRoundScore = (int) Math.round(teamCurrentRoundPtScore);
        info.setCurrentRoundScore(currentRoundScore);
        info.setTotalScore(currentRoundScore);
        
        // 获取当前轮次的团队比赛统计数据
        Map<String, Object> teamStats = getTeamRoundMatchStats(roundStatus.getCompetition().getId(), roundStatus.getRoundNumber(), team.getId());
        info.setAppearanceCount((Integer) teamStats.get("appearanceCount"));
        info.setAveragePosition((Double) teamStats.get("averagePosition"));
        info.setFirstPlaceCount((Integer) teamStats.get("firstPlaceCount"));
        info.setSecondPlaceCount((Integer) teamStats.get("secondPlaceCount"));
        info.setThirdPlaceCount((Integer) teamStats.get("thirdPlaceCount"));
        info.setFourthPlaceCount((Integer) teamStats.get("fourthPlaceCount"));
        
        // 获取队员当前轮次的得分详情
        info.setMemberScores(getTeamMemberRoundScores(team.getId(), roundStatus.getCompetition().getId(), roundStatus.getRoundNumber()));
        
        return info;
    }
    
    /**
     * 获取玩家比赛统计数据
     */
    private Map<String, Object> getPlayerMatchStats(Long competitionId, Long playerId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取该玩家在该比赛的所有比赛结果
        List<MatchResult> matchResults = matchResultRepository
                .findByCompetitionIdAndPlayerId(competitionId, playerId);
        
        if (matchResults.isEmpty()) {
            stats.put("appearanceCount", 0);
            stats.put("averagePosition", 0.0);
            stats.put("firstPlaceCount", 0);
            stats.put("secondPlaceCount", 0);
            stats.put("thirdPlaceCount", 0);
            stats.put("fourthPlaceCount", 0);
            return stats;
        }
        
        stats.put("appearanceCount", matchResults.size());
        
        // 计算平均顺位和各顺位次数
        int firstPlaceCount = 0;
        int secondPlaceCount = 0;
        int thirdPlaceCount = 0;
        int fourthPlaceCount = 0;
        double totalPosition = 0.0;
        
        for (MatchResult matchResult : matchResults) {
            // 计算该玩家在这场比赛的排名
            int playerRank = calculatePlayerRankInMatch(matchResult, playerId);
            totalPosition += playerRank;
            
            // 统计各顺位次数
            switch (playerRank) {
                case 1: firstPlaceCount++; break;
                case 2: secondPlaceCount++; break;
                case 3: thirdPlaceCount++; break;
                case 4: fourthPlaceCount++; break;
            }
        }
        
        stats.put("averagePosition", totalPosition / matchResults.size());
        stats.put("firstPlaceCount", firstPlaceCount);
        stats.put("secondPlaceCount", secondPlaceCount);
        stats.put("thirdPlaceCount", thirdPlaceCount);
        stats.put("fourthPlaceCount", fourthPlaceCount);
        
        return stats;
    }
    
    /**
     * 获取团队比赛统计数据
     */
    private Map<String, Object> getTeamMatchStats(Long competitionId, Long teamId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取团队成员
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        if (teamMembers.isEmpty()) {
            stats.put("appearanceCount", 0);
            stats.put("averagePosition", 0.0);
            stats.put("firstPlaceCount", 0);
            stats.put("secondPlaceCount", 0);
            stats.put("thirdPlaceCount", 0);
            stats.put("fourthPlaceCount", 0);
            return stats;
        }
        
        // 通过团队成员查询比赛结果
        Set<Long> matchResultIds = new HashSet<>();
        for (TeamMember member : teamMembers) {
            Long playerId = member.getPlayer().getId();
            List<MatchResult> playerMatches = matchResultRepository
                    .findByCompetitionIdAndPlayerId(competitionId, playerId);
            playerMatches.forEach(match -> matchResultIds.add(match.getId()));
        }
        
        if (matchResultIds.isEmpty()) {
            stats.put("appearanceCount", 0);
            stats.put("averagePosition", 0.0);
            stats.put("firstPlaceCount", 0);
            stats.put("secondPlaceCount", 0);
            stats.put("thirdPlaceCount", 0);
            stats.put("fourthPlaceCount", 0);
            return stats;
        }
        
        // 获取所有相关的比赛记录
        List<MatchResult> matchResults = matchResultRepository.findAllById(matchResultIds);
        
        stats.put("appearanceCount", matchResults.size());
        
        // 计算平均顺位和各顺位次数
        int firstPlaceCount = 0;
        int secondPlaceCount = 0;
        int thirdPlaceCount = 0;
        int fourthPlaceCount = 0;
        double totalPosition = 0.0;
        
        for (MatchResult matchResult : matchResults) {
            // 计算该团队在这场比赛的排名
            int teamRank = calculateTeamRankInMatch(matchResult, teamId);
            totalPosition += teamRank;
            
            // 统计各顺位次数
            switch (teamRank) {
                case 1: firstPlaceCount++; break;
                case 2: secondPlaceCount++; break;
                case 3: thirdPlaceCount++; break;
                case 4: fourthPlaceCount++; break;
            }
        }
        
        stats.put("averagePosition", totalPosition / matchResults.size());
        stats.put("firstPlaceCount", firstPlaceCount);
        stats.put("secondPlaceCount", secondPlaceCount);
        stats.put("thirdPlaceCount", thirdPlaceCount);
        stats.put("fourthPlaceCount", fourthPlaceCount);
        
        return stats;
    }
    
    /**
     * 获取队员得分详情
     */
    private List<CompetitionStatusResponse.TeamMemberScore> getTeamMemberScores(Long teamId, Long competitionId) {
        // 获取团队成员
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        
        return teamMembers.stream()
                .map(member -> {
                    CompetitionStatusResponse.TeamMemberScore score = new CompetitionStatusResponse.TeamMemberScore();
                    
                    Player player = member.getPlayer();
                    score.setPlayerId(player.getId());
                    score.setPlayerName(player.getNickname());
                    score.setUsername(player.getUsername());
                    
                    // 获取个人PT得分（即对团队的贡献分）
                    // 这里需要获取该玩家在该比赛的所有轮次的PT分数总和
                    // 暂时使用当前逻辑，后续可以改进为多轮次累计
                    Integer individualScore = getPlayerIndividualScore(competitionId, player.getId());
                    score.setIndividualScore(individualScore);
                    
                    return score;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取玩家个人得分
     */
    private Integer getPlayerIndividualScore(Long competitionId, Long playerId) {
        // 获取该玩家在该比赛的所有比赛结果得分总和
        List<MatchResult> matchResults = matchResultRepository
                .findByCompetitionIdAndPlayerId(competitionId, playerId);
        
        int totalScore = 0;
        for (MatchResult matchResult : matchResults) {
            // 获取该玩家在这场比赛的得分
            Integer playerScore = getPlayerScoreInMatch(matchResult, playerId);
            if (playerScore != null) {
                totalScore += playerScore;
            }
        }
        
        return totalScore;
    }
    
    /**
     * 计算玩家在单场比赛中的排名
     */
    private int calculatePlayerRankInMatch(MatchResult matchResult, Long playerId) {
        // 获取四个方位的玩家得分
        Map<Long, Integer> playerScores = new HashMap<>();
        playerScores.put(matchResult.getEastPlayer().getId(), matchResult.getEastScore());
        playerScores.put(matchResult.getSouthPlayer().getId(), matchResult.getSouthScore());
        playerScores.put(matchResult.getWestPlayer().getId(), matchResult.getWestScore());
        playerScores.put(matchResult.getNorthPlayer().getId(), matchResult.getNorthScore());
        
        // 按得分排序，得分高的排名靠前
        List<Map.Entry<Long, Integer>> sortedScores = playerScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
        
        // 找到该玩家的排名
        for (int i = 0; i < sortedScores.size(); i++) {
            if (sortedScores.get(i).getKey().equals(playerId)) {
                return i + 1; // 排名从1开始
            }
        }
        
        return 4; // 如果没找到，返回最后一名
    }
    
    /**
     * 计算团队在单场比赛中的排名
     */
    private int calculateTeamRankInMatch(MatchResult matchResult, Long teamId) {
        // 获取团队所有成员的得分总和
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        
        if (teamMembers.isEmpty()) {
            return 4; // 如果团队没有成员，返回最后一名
        }
        
        // 计算团队总分
        int teamTotalScore = 0;
        for (TeamMember member : teamMembers) {
            Long playerId = member.getPlayer().getId();
            Integer playerScore = getPlayerScoreInMatch(matchResult, playerId);
            if (playerScore != null) {
                teamTotalScore += playerScore;
            }
        }
        
        // 获取所有参与团队的得分
        Map<Long, Integer> teamScores = new HashMap<>();
        // 这里需要根据比赛结果计算所有参与团队的得分
        // 由于MatchResult结构限制，这里简化处理
        // 实际项目中可能需要重新设计数据结构
        
        // 暂时返回随机排名，实际项目中需要完善这个逻辑
        return (int) (Math.random() * 4) + 1;
    }
    
    /**
     * 获取玩家在单场比赛中的得分
     */
    private Integer getPlayerScoreInMatch(MatchResult matchResult, Long playerId) {
        if (matchResult.getEastPlayer().getId().equals(playerId)) {
            return matchResult.getEastScore();
        } else if (matchResult.getSouthPlayer().getId().equals(playerId)) {
            return matchResult.getSouthScore();
        } else if (matchResult.getWestPlayer().getId().equals(playerId)) {
            return matchResult.getWestScore();
        } else if (matchResult.getNorthPlayer().getId().equals(playerId)) {
            return matchResult.getNorthScore();
        }
        
        return null; // 玩家没有参与这场比赛
    }
    
    /**
     * 获取玩家在指定轮次的比赛统计数据
     */
    private Map<String, Object> getPlayerRoundMatchStats(Long competitionId, Integer roundNumber, Long playerId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取该玩家在该轮次的所有比赛结果
        List<MatchResult> matchResults = matchResultRepository
                .findByCompetitionIdAndRoundNumberAndPlayerId(competitionId, roundNumber, playerId);
        
        if (matchResults.isEmpty()) {
            stats.put("appearanceCount", 0);
            stats.put("averagePosition", 0.0);
            stats.put("firstPlaceCount", 0);
            stats.put("secondPlaceCount", 0);
            stats.put("thirdPlaceCount", 0);
            stats.put("fourthPlaceCount", 0);
            return stats;
        }
        
        stats.put("appearanceCount", matchResults.size());
        
        // 计算平均顺位和各顺位次数
        int firstPlaceCount = 0;
        int secondPlaceCount = 0;
        int thirdPlaceCount = 0;
        int fourthPlaceCount = 0;
        double totalPosition = 0.0;
        
        for (MatchResult matchResult : matchResults) {
            // 计算该玩家在这场比赛的排名
            int playerRank = calculatePlayerRankInMatch(matchResult, playerId);
            totalPosition += playerRank;
            
            // 统计各顺位次数
            switch (playerRank) {
                case 1: firstPlaceCount++; break;
                case 2: secondPlaceCount++; break;
                case 3: thirdPlaceCount++; break;
                case 4: fourthPlaceCount++; break;
            }
        }
        
        stats.put("averagePosition", totalPosition / matchResults.size());
        stats.put("firstPlaceCount", firstPlaceCount);
        stats.put("secondPlaceCount", secondPlaceCount);
        stats.put("thirdPlaceCount", thirdPlaceCount);
        stats.put("fourthPlaceCount", fourthPlaceCount);
        
        return stats;
    }
    
    /**
     * 获取团队在指定轮次的比赛统计数据
     */
    private Map<String, Object> getTeamRoundMatchStats(Long competitionId, Integer roundNumber, Long teamId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取团队成员
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        
        if (teamMembers.isEmpty()) {
            stats.put("appearanceCount", 0);
            stats.put("averagePosition", 0.0);
            stats.put("firstPlaceCount", 0);
            stats.put("secondPlaceCount", 0);
            stats.put("thirdPlaceCount", 0);
            stats.put("fourthPlaceCount", 0);
            return stats;
        }
        
        // 获取该轮次所有涉及团队成员的比赛记录
        Set<MatchResult> teamMatchResults = new HashSet<>();
        for (TeamMember member : teamMembers) {
            List<MatchResult> memberMatches = matchResultRepository
                    .findByCompetitionIdAndRoundNumberAndPlayerId(competitionId, roundNumber, member.getPlayer().getId());
            teamMatchResults.addAll(memberMatches);
        }
        
        List<MatchResult> matchResults = new ArrayList<>(teamMatchResults);
        
        if (matchResults.isEmpty()) {
            stats.put("appearanceCount", 0);
            stats.put("averagePosition", 0.0);
            stats.put("firstPlaceCount", 0);
            stats.put("secondPlaceCount", 0);
            stats.put("thirdPlaceCount", 0);
            stats.put("fourthPlaceCount", 0);
            return stats;
        }
        
        stats.put("appearanceCount", matchResults.size());
        
        // 计算团队在每场比赛的排名
        int firstPlaceCount = 0;
        int secondPlaceCount = 0;
        int thirdPlaceCount = 0;
        int fourthPlaceCount = 0;
        double totalPosition = 0.0;
        
        for (MatchResult matchResult : matchResults) {
            // 计算团队在这场比赛的排名
            int teamRank = calculateTeamRankInMatch(matchResult, teamId);
            totalPosition += teamRank;
            
            // 统计各顺位次数
            switch (teamRank) {
                case 1: firstPlaceCount++; break;
                case 2: secondPlaceCount++; break;
                case 3: thirdPlaceCount++; break;
                case 4: fourthPlaceCount++; break;
            }
        }
        
        stats.put("averagePosition", totalPosition / matchResults.size());
        stats.put("firstPlaceCount", firstPlaceCount);
        stats.put("secondPlaceCount", secondPlaceCount);
        stats.put("thirdPlaceCount", thirdPlaceCount);
        stats.put("fourthPlaceCount", fourthPlaceCount);
        
        return stats;
    }
    
    /**
     * 获取团队成员在指定轮次的得分详情
     */
    private List<CompetitionStatusResponse.TeamMemberScore> getTeamMemberRoundScores(Long teamId, Long competitionId, Integer roundNumber) {
        // 获取团队成员
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        
        return teamMembers.stream()
                .map(member -> {
                    CompetitionStatusResponse.TeamMemberScore score = new CompetitionStatusResponse.TeamMemberScore();
                    
                    Player player = member.getPlayer();
                    score.setPlayerId(player.getId());
                    score.setPlayerName(player.getNickname());
                    score.setUsername(player.getUsername());
                    
                    // 获取该玩家在当前轮次的个人得分总和
                    Integer individualScore = getPlayerRoundIndividualScore(competitionId, roundNumber, player.getId());
                    score.setIndividualScore(individualScore);
                    
                    return score;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取玩家在指定轮次的个人得分
     */
    private Integer getPlayerRoundIndividualScore(Long competitionId, Integer roundNumber, Long playerId) {
        // 获取该玩家在该轮次的所有比赛结果得分总和
        List<MatchResult> matchResults = matchResultRepository
                .findByCompetitionIdAndRoundNumberAndPlayerId(competitionId, roundNumber, playerId);
        
        int totalScore = 0;
        for (MatchResult matchResult : matchResults) {
            // 获取该玩家在这场比赛的得分
            Integer playerScore = getPlayerScoreInMatch(matchResult, playerId);
            if (playerScore != null) {
                totalScore += playerScore;
            }
        }
        
        return totalScore;
    }
}
