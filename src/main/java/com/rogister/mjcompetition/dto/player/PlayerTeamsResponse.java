package com.rogister.mjcompetition.dto.player;

import com.rogister.mjcompetition.dto.team.TeamCompetitionRegistrationRequest;
import lombok.Data;

import java.util.List;

@Data
public class PlayerTeamsResponse {
    /**
     * 我参与的所有团队列表（包括创建的和加入的）
     * 其中isCreatedByMe字段标识是否为我创建的团队
     */
    private List<TeamCompetitionRegistrationRequest.TeamInfo> teams;

    public PlayerTeamsResponse(List<TeamCompetitionRegistrationRequest.TeamInfo> teams) {
        this.teams = teams;
    }
}
