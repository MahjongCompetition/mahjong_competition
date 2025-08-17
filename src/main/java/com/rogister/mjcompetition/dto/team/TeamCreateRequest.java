package com.rogister.mjcompetition.dto.team;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "团队创建请求")
public class TeamCreateRequest {

    @Schema(description = "团队名称", example = "测试团队", requiredMode = Schema.RequiredMode.REQUIRED)
    private String teamName;

    @Schema(description = "最大成员数量", example = "4", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "2", maximum = "10")
    private Integer maxMembers;
}
