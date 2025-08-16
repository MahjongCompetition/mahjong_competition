package com.rogister.mjcompetiton.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "qq", nullable = false, unique = true, length = 20)
    private String qq;
    
    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;
    
    @Column(name = "mahjong_id", nullable = false, unique = true, length = 50)
    private String mahjongId;
    
    @Column(name = "mahjong_nickname", nullable = false, length = 100)
    private String mahjongNickname;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 带参数的构造函数
    public Player(String username, String password, String qq, String nickname, String mahjongId, String mahjongNickname) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.username = username;
        this.password = password;
        this.qq = qq;
        this.nickname = nickname;
        this.mahjongId = mahjongId;
        this.mahjongNickname = mahjongNickname;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 