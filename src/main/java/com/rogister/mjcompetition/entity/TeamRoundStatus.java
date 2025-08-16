package com.rogister.mjcompetition.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_round_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamRoundStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Team team;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "competition_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Competition competition;
    
    @Column(name = "round_number", nullable = false)
    private Integer roundNumber; // 轮次号
    
    @Column(name = "initial_score", nullable = false)
    private Integer initialScore; // 该轮次的初始得分
    
    @Column(name = "current_score")
    private Integer currentScore; // 当前得分（初始得分 + 轮次内得分）
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoundStatus status; // 轮次状态
    
    @Column(name = "is_eliminated", nullable = false)
    private Boolean isEliminated; // 是否被淘汰
    
    @Column(name = "elimination_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eliminationTime; // 淘汰时间
    
    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
    private LocalDateTime updatedAt;
    
    // 轮次状态枚举
    public enum RoundStatus {
        ACTIVE,      // 活跃（正在参与）
        ADVANCED,    // 已晋级
        ELIMINATED,  // 已淘汰
        COMPLETED    // 已完成
    }
    
    // 带参数的构造函数
    public TeamRoundStatus(Team team, Competition competition, Integer roundNumber, Integer initialScore) {
        this.team = team;
        this.competition = competition;
        this.roundNumber = roundNumber;
        this.initialScore = initialScore;
        this.currentScore = initialScore; // 初始得分等于当前得分
        this.status = RoundStatus.ACTIVE;
        this.isEliminated = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.isEliminated == null) {
            this.isEliminated = false;
        }
        if (this.currentScore == null) {
            this.currentScore = this.initialScore;
        }
        if (this.status == null) {
            this.status = RoundStatus.ACTIVE;
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
