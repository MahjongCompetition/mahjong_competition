package com.rogister.mjcompetition.dto.team;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "团队得分更新请求")
public class TeamScoreUpdateRequest {

    @Schema(description = "比赛ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long competitionId;

    @Schema(description = "团队ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long teamId;

    @Schema(description = "轮次编号", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer roundNumber;

    @Schema(description = "新的得分", example = "1500", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer newScore;
}
