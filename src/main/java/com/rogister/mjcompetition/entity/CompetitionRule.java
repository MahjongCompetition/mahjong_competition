package com.rogister.mjcompetition.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "competition_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "rule_name", nullable = false, length = 200)
    private String ruleName;
    
    @Column(name = "origin_points", nullable = false)
    private Integer originPoints;
    
    @Column(name = "first_place_points", nullable = false)
    private Integer firstPlacePoints;
    
    @Column(name = "second_place_points", nullable = false)
    private Integer secondPlacePoints;
    
    @Column(name = "third_place_points", nullable = false)
    private Integer thirdPlacePoints;
    
    @Column(name = "fourth_place_points", nullable = false)
    private Integer fourthPlacePoints;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 带参数的构造函数
    public CompetitionRule(String ruleName, Integer originPoints, Integer firstPlacePoints, Integer secondPlacePoints, 
                          Integer thirdPlacePoints, Integer fourthPlacePoints) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.ruleName = ruleName;
        this.originPoints = originPoints;
        this.firstPlacePoints = firstPlacePoints;
        this.secondPlacePoints = secondPlacePoints;
        this.thirdPlacePoints = thirdPlacePoints;
        this.fourthPlacePoints = fourthPlacePoints;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 