package com.rogister.mjcompetiton.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "competitions")
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
    
    // 构造函数
    public Competition() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Competition(String competitionName, CompetitionType competitionType, CompetitionRule rule) {
        this();
        this.competitionName = competitionName;
        this.competitionType = competitionType;
        this.rule = rule;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCompetitionName() {
        return competitionName;
    }
    
    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }
    
    public CompetitionType getCompetitionType() {
        return competitionType;
    }
    
    public void setCompetitionType(CompetitionType competitionType) {
        this.competitionType = competitionType;
    }
    
    public CompetitionRule getRule() {
        return rule;
    }
    
    public void setRule(CompetitionRule rule) {
        this.rule = rule;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 