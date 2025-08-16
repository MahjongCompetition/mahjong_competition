package com.rogister.mjcompetiton.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "individual_competition_registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    
    // 带参数的构造函数
    public IndividualCompetitionRegistration(Competition competition, Player player) {
        this.registrationTime = LocalDateTime.now();
        this.status = RegistrationStatus.REGISTERED;
        this.competition = competition;
        this.player = player;
    }
} 