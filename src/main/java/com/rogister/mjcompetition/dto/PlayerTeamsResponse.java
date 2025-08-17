package com.rogister.mjcompetition.dto;

import com.rogister.mjcompetition.entity.Team;
import lombok.Data;

import java.util.List;

@Data
public class PlayerTeamsResponse {
    /**
     * 我创建的团队（我是队长的团队）
     */
    private List<Team> createdTeams;
    
    /**
     * 我加入的团队（包括我创建的团队）
     */
    private List<Team> joinedTeams;
    
    public PlayerTeamsResponse(List<Team> createdTeams, List<Team> joinedTeams) {
        this.createdTeams = createdTeams;
        this.joinedTeams = joinedTeams;
    }
}
