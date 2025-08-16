package com.rogister.mjcompetition.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultCreateRequest {
    
    private Long competitionId;
    private Integer roundNumber;  // 轮次编号，如：1表示第一轮，2表示第二轮
    private Integer matchNumber;
    private String matchName;
    
    // 东南西北四个方位的玩家ID和成绩
    private Long eastPlayerId;
    private Integer eastScore;
    private Integer eastPenalty = 0;
    
    private Long southPlayerId;
    private Integer southScore;
    private Integer southPenalty = 0;
    
    private Long westPlayerId;
    private Integer westScore;
    private Integer westPenalty = 0;
    
    private Long northPlayerId;
    private Integer northScore;
    private Integer northPenalty = 0;
    
    private String remarks;
}
