package com.rogister.mjcompetition.dto.team;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "团队操作请求（加入、退出、解散）")
public class TeamOperationRequest {

    @Schema(description = "团队ID", example = "1")
    private Long teamId;

    @Schema(description = "团队编号（用于加入团队）", example = "TEAM001")
    private String teamCode;
}
