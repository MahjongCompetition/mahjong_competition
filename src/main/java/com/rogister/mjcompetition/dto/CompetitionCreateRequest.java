package com.rogister.mjcompetition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionCreateRequest {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("type")
    private String type; // INDIVIDUAL 或 TEAM
    
    @JsonProperty("maxParticipants")
    private Integer maxParticipants;
    
    @JsonProperty("registrationStartTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime registrationStartTime;
    
    @JsonProperty("registrationEndTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime registrationEndTime;
    
    @JsonProperty("startTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonProperty("endTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    
    @JsonProperty("ruleId")
    private Long ruleId;
}
