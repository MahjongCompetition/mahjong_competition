package com.rogister.mjcompetition.dto.player;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "个人赛晋级请求")
public class PlayerAdvancementRequest {

    @Schema(description = "比赛ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long competitionId;

    @Schema(description = "玩家ID列表", example = "[1, 2, 3, 4]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> playerIds;

    @Schema(description = "目标轮次", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer targetRound;

    @Schema(description = "初始得分", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer initialScore;
}
