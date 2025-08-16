package com.rogister.mjcompetiton.repository;

import com.rogister.mjcompetiton.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    
    /**
     * 根据用户名查找玩家
     */
    Optional<Player> findByUsername(String username);
    
    /**
     * 根据QQ查找玩家
     */
    Optional<Player> findByQq(String qq);
    
    /**
     * 根据雀魂ID查找玩家
     */
    Optional<Player> findByMahjongId(String mahjongId);
    
    /**
     * 根据昵称查找玩家
     */
    List<Player> findByNicknameContainingIgnoreCase(String nickname);
    
    /**
     * 根据雀魂昵称查找玩家
     */
    List<Player> findByMahjongNicknameContainingIgnoreCase(String mahjongNickname);
    
    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查QQ是否已存在
     */
    boolean existsByQq(String qq);
    
    /**
     * 检查雀魂ID是否已存在
     */
    boolean existsByMahjongId(String mahjongId);
    
    /**
     * 根据用户名和密码查找玩家（用于登录验证）
     */
    Optional<Player> findByUsernameAndPassword(String username, String password);
} 