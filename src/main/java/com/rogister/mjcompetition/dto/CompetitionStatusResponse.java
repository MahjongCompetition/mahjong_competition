package com.rogister.mjcompetition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionStatusResponse {
    
    private Long competitionId;
    private Integer roundNumber;
    private String competitionName;
    private String competitionType;
    private List<PlayerStatusInfo> playerStatusList;
    private List<TeamStatusInfo> teamStatusList;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerStatusInfo {
        private Long playerId;
        private String playerName;
        private String username;
        private Integer initialScore;        // 初始化得分
        private Integer currentRoundScore;   // 当轮得分
        private Integer totalScore;          // 累计得分（初始化得分 + 当轮得分）
        private Integer appearanceCount;     // 出场次数
        private Double averagePosition;      // 平均顺位
        private Integer firstPlaceCount;     // 第一名次数
        private Integer secondPlaceCount;    // 第二名次数
        private Integer thirdPlaceCount;     // 第三名次数
        private Integer fourthPlaceCount;    // 第四名次数
        private String status;               // 轮次状态
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamStatusInfo {
        private Long teamId;
        private String teamName;
        private String teamCode;
        private Integer initialScore;        // 团队初始化得分
        private Integer currentRoundScore;   // 团队当轮得分
        private Integer totalScore;          // 团队累计得分（初始化得分 + 当轮得分）
        private Integer appearanceCount;     // 出场次数
        private Double averagePosition;      // 平均顺位
        private Integer firstPlaceCount;     // 第一名次数
        private Integer secondPlaceCount;    // 第二名次数
        private Integer thirdPlaceCount;     // 第三名次数
        private Integer fourthPlaceCount;    // 第四名次数
        private String status;               // 轮次状态
        private List<TeamMemberScore> memberScores; // 队员得分详情
        private List<PlayerStatusInfo> playerStatusList; // 队员个人状态详情
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberScore {
        private Long playerId;
        private String playerName;
        private String username;
        private Integer individualScore;     // 个人得分（即对团队的贡献分）
    }
}
