package com.rogister.mjcompetition.dto.match;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "比赛成绩创建请求")
public class MatchResultCreateRequest {

    @Schema(description = "比赛ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long competitionId;

    @Schema(description = "轮次编号，如：1表示第一轮，2表示第二轮", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer roundNumber;

    @Schema(description = "比赛编号", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer matchNumber;

    @Schema(description = "比赛名称", example = "第一轮第一场")
    private String matchName;

    // 东南西北四个方位的玩家ID和成绩
    @Schema(description = "东家玩家ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long eastPlayerId;

    @Schema(description = "东家得分", example = "1000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer eastScore;

    @Schema(description = "东家罚分", example = "0")
    private Integer eastPenalty = 0;

    @Schema(description = "南家玩家ID", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long southPlayerId;

    @Schema(description = "南家得分", example = "800", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer southScore;

    @Schema(description = "南家罚分", example = "0")
    private Integer southPenalty = 0;

    @Schema(description = "西家玩家ID", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long westPlayerId;

    @Schema(description = "西家得分", example = "1200", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer westScore;

    @Schema(description = "西家罚分", example = "0")
    private Integer westPenalty = 0;

    @Schema(description = "北家玩家ID", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long northPlayerId;

    @Schema(description = "北家得分", example = "1000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer northScore;

    @Schema(description = "北家罚分", example = "0")
    private Integer northPenalty = 0;

    @Schema(description = "备注信息", example = "比赛顺利进行")
    private String remarks;
}
