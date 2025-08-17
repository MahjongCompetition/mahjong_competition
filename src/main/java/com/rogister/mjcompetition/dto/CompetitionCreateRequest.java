package com.rogister.mjcompetition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "比赛创建请求")
public class CompetitionCreateRequest {

    @JsonProperty("name")
    @Schema(description = "比赛名称", example = "2024年麻将锦标赛", required = true)
    private String name;

    @JsonProperty("description")
    @Schema(description = "比赛描述", example = "年度重要比赛")
    private String description;

    @JsonProperty("type")
    @Schema(description = "比赛类型", example = "INDIVIDUAL", allowableValues = { "INDIVIDUAL", "TEAM" }, required = true)
    private String type; // INDIVIDUAL 或 TEAM

    @JsonProperty("maxParticipants")
    @Schema(description = "最大参与人数", example = "64")
    private Integer maxParticipants;

    @JsonProperty("registrationStartTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "报名开始时间", example = "2024-01-01T00:00:00")
    private LocalDateTime registrationStartTime;

    @JsonProperty("registrationEndTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "报名结束时间", example = "2024-01-31T23:59:59", required = true)
    private LocalDateTime registrationEndTime;

    @JsonProperty("startTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "比赛开始时间", example = "2024-02-01T09:00:00")
    private LocalDateTime startTime;

    @JsonProperty("endTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "比赛结束时间", example = "2024-02-28T18:00:00")
    private LocalDateTime endTime;

    @JsonProperty("ruleId")
    @Schema(description = "比赛规则ID", example = "1", required = true)
    private Long ruleId;
}
