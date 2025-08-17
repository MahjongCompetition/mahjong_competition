package com.rogister.mjcompetition.entity.team;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "team_name", nullable = false, length = 100)
    private String teamName;
    
    @Column(name = "team_code", nullable = false, unique = true, length = 20)
    private String teamCode; // 团队编号，用于队员加入
    
    @Column(name = "captain_id", nullable = false)
    private Long captainId; // 队长ID
    
    @Column(name = "max_members", nullable = false)
    private Integer maxMembers; // 最大成员数
    
    @Column(name = "current_members", nullable = false)
    private Integer currentMembers; // 当前成员数
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 带参数的构造函数
    public Team(String teamName, String teamCode, Long captainId, Integer maxMembers) {
        this.teamName = teamName;
        this.teamCode = teamCode;
        this.captainId = captainId;
        this.maxMembers = maxMembers;
        this.currentMembers = 1; // 创建时只有队长一人
        this.isActive = true;
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
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.currentMembers == null) {
            this.currentMembers = 1;
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
