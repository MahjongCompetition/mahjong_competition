package com.rogister.mjcompetition.dto.team;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "团队比赛报名请求")
public class TeamCompetitionRegistrationRequest {

    @Schema(description = "比赛ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long competitionId;

    @Schema(description = "团队ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long teamId;
}
