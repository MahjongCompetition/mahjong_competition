package com.rogister.mjcompetition.dto;

import lombok.Data;

@Data
public class TeamCompetitionRegistrationRequest {
    private Long competitionId;
    private Long teamId;
}
