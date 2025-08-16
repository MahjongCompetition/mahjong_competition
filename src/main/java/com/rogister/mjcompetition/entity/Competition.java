package com.rogister.mjcompetition.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "rule_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CompetitionRule rule;
    
    @Column(name = "registration_deadline", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime registrationDeadline;
    
    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
    private LocalDateTime updatedAt;
    
    // 比赛类型枚举
    public enum CompetitionType {
        TEAM,       // 团体赛
        INDIVIDUAL  // 个人赛
    }
    
    // 带参数的构造函数
    public Competition(String competitionName, CompetitionType competitionType, CompetitionRule rule, LocalDateTime registrationDeadline) {
        this.competitionName = competitionName;
        this.competitionType = competitionType;
        this.rule = rule;
        this.registrationDeadline = registrationDeadline;
    }
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查报名是否已结束
     */
    public boolean isRegistrationClosed() {
        return LocalDateTime.now().isAfter(this.registrationDeadline);
    }
    
    /**
     * 检查是否可以录入成绩（报名结束后）
     */
    public boolean canEnterResults() {
        return isRegistrationClosed();
    }
} 