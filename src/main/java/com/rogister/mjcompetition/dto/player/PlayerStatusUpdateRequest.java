package com.rogister.mjcompetition.dto.player;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "玩家状态更新请求")
public class PlayerStatusUpdateRequest {

    @Schema(description = "是否激活状态", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isActive;
}
