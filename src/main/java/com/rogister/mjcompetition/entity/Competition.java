package com.rogister.mjcompetition.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "competitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Competition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "competition_name", nullable = false, length = 200, unique = true)
    private String competitionName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "competition_type", nullable = false)
    private CompetitionType competitionType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private CompetitionRule rule;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 比赛类型枚举
    public enum CompetitionType {
        TEAM,       // 团体赛
        INDIVIDUAL  // 个人赛
    }
    
    // 带参数的构造函数
    public Competition(String competitionName, CompetitionType competitionType, CompetitionRule rule) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.competitionName = competitionName;
        this.competitionType = competitionType;
        this.rule = rule;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 