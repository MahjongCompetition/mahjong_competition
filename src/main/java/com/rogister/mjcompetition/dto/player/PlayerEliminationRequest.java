package com.rogister.mjcompetition.dto.player;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "玩家淘汰请求")
public class PlayerEliminationRequest {

    @Schema(description = "比赛ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long competitionId;

    @Schema(description = "玩家ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long playerId;

    @Schema(description = "轮次编号", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer roundNumber;

    @Schema(description = "淘汰原因", example = "成绩不达标")
    private String reason;
}
