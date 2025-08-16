package com.rogister.mjcompetiton.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "competition_rules")
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
    
    // 构造函数
    public CompetitionRule() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public CompetitionRule(String ruleName, Integer originPoints, Integer firstPlacePoints, Integer secondPlacePoints, 
                          Integer thirdPlacePoints, Integer fourthPlacePoints) {
        this();
        this.ruleName = ruleName;
        this.originPoints = originPoints;
        this.firstPlacePoints = firstPlacePoints;
        this.secondPlacePoints = secondPlacePoints;
        this.thirdPlacePoints = thirdPlacePoints;
        this.fourthPlacePoints = fourthPlacePoints;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRuleName() {
        return ruleName;
    }
    
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    
    public Integer getOriginPoints() {
        return originPoints;
    }
    
    public void setOriginPoints(Integer originPoints) {
        this.originPoints = originPoints;
    }
    
    public Integer getFirstPlacePoints() {
        return firstPlacePoints;
    }
    
    public void setFirstPlacePoints(Integer firstPlacePoints) {
        this.firstPlacePoints = firstPlacePoints;
    }
    
    public Integer getSecondPlacePoints() {
        return secondPlacePoints;
    }
    
    public void setSecondPlacePoints(Integer secondPlacePoints) {
        this.secondPlacePoints = secondPlacePoints;
    }
    
    public Integer getThirdPlacePoints() {
        return thirdPlacePoints;
    }
    
    public void setThirdPlacePoints(Integer thirdPlacePoints) {
        this.thirdPlacePoints = thirdPlacePoints;
    }
    
    public Integer getFourthPlacePoints() {
        return fourthPlacePoints;
    }
    
    public void setFourthPlacePoints(Integer fourthPlacePoints) {
        this.fourthPlacePoints = fourthPlacePoints;
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