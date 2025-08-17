package com.rogister.mjcompetition.dto.team;

import com.rogister.mjcompetition.entity.team.Team;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "团队比赛报名请求")
public class TeamCompetitionRegistrationRequest {

    @Schema(description = "比赛ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long competitionId;

    @Schema(description = "团队ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long teamId;

    @Data
    public static class TeamInfo {
        private Long id;
        private String name;
        private String code;
        private String description;
        private Long captainId;
        private String captainName;
        private boolean isCreatedByMe;

        public TeamInfo(Team team, boolean isCreatedByMe) {
            this.id = team.getId();
            this.name = team.getTeamName();
            this.code = team.getTeamCode();
            this.description = null; // Team实体没有description字段
            this.captainId = team.getCaptainId();
            this.isCreatedByMe = isCreatedByMe;
            // captainName 需要从 team 中获取，但Team实体可能没有直接的captainName字段
            // 这里先设为null，如果需要可以后续通过服务层填充
            this.captainName = null;
        }

        // 重载构造函数，支持传入队长姓名
        public TeamInfo(Team team, boolean isCreatedByMe, String captainName) {
            this(team, isCreatedByMe);
            this.captainName = captainName;
        }
    }
}
