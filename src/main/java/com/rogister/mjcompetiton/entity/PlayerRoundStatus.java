package com.rogister.mjcompetiton.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_round_status")
public class PlayerRoundStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private CompetitionRound round;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    
    @Column(name = "is_advanced", nullable = false)
    private Boolean isAdvanced = false;
    
    @Column(name = "starting_score", nullable = false)
    private Integer startingScore = 0;
    
    @Column(name = "current_score")
    private Integer currentScore;
    
    @Column(name = "rank_in_round")
    private Integer rankInRound;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public PlayerRoundStatus() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public PlayerRoundStatus(Competition competition, CompetitionRound round, Player player) {
        this();
        this.competition = competition;
        this.round = round;
        this.player = player;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Competition getCompetition() {
        return competition;
    }
    
    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
    
    public CompetitionRound getRound() {
        return round;
    }
    
    public void setRound(CompetitionRound round) {
        this.round = round;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public Boolean getIsAdvanced() {
        return isAdvanced;
    }
    
    public void setIsAdvanced(Boolean isAdvanced) {
        this.isAdvanced = isAdvanced;
    }
    
    public Integer getStartingScore() {
        return startingScore;
    }
    
    public void setStartingScore(Integer startingScore) {
        this.startingScore = startingScore != null ? startingScore : 0;
    }
    
    public Integer getCurrentScore() {
        return currentScore;
    }
    
    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
    }
    
    public Integer getRankInRound() {
        return rankInRound;
    }
    
    public void setRankInRound(Integer rankInRound) {
        this.rankInRound = rankInRound;
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