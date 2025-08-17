package com.rogister.mjcompetition.dto.team;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "团队赛晋级请求")
public class TeamAdvancementRequest {

    @Schema(description = "比赛ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long competitionId;

    @Schema(description = "团队ID列表", example = "[1, 2, 3, 4]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> teamIds;

    @Schema(description = "目标轮次", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer targetRound;

    @Schema(description = "初始得分", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer initialScore;
}
