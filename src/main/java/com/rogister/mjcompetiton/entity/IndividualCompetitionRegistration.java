package com.rogister.mjcompetiton.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "individual_competition_registrations")
public class IndividualCompetitionRegistration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    
    @Column(name = "registration_time", nullable = false)
    private LocalDateTime registrationTime;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RegistrationStatus status;
    
    // 报名状态枚举
    public enum RegistrationStatus {
        REGISTERED("已报名"),
        APPROVED("已通过"),
        REJECTED("已拒绝"),
        WITHDRAWN("已退赛");
        
        private final String displayName;
        
        RegistrationStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // 构造函数
    public IndividualCompetitionRegistration() {
        this.registrationTime = LocalDateTime.now();
        this.status = RegistrationStatus.REGISTERED;
    }
    
    public IndividualCompetitionRegistration(Competition competition, Player player) {
        this();
        this.competition = competition;
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
    
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }
    
    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }
    
    public RegistrationStatus getStatus() {
        return status;
    }
    
    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }
} 