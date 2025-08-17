package com.rogister.mjcompetition.dto.competition;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "个人比赛报名请求")
public class CompetitionRegistrationRequest {

    @Schema(description = "比赛ID", example = "1", required = true)
    private Long competitionId;
}
