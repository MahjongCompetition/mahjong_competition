package com.rogister.mjcompetiton.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_round_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    
    // 带参数的构造函数
    public PlayerRoundStatus(Competition competition, CompetitionRound round, Player player) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.competition = competition;
        this.round = round;
        this.player = player;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 