package com.rogister.mjcompetiton.service;

import com.rogister.mjcompetiton.entity.Player;
import com.rogister.mjcompetiton.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {
    
    @Autowired
    private PlayerRepository playerRepository;
    
    /**
     * 玩家注册账户
     */
    public Player registerPlayer(Player player) {
        // 检查用户名是否已存在
        if (playerRepository.existsByUsername(player.getUsername())) {
            throw new RuntimeException("用户名已存在: " + player.getUsername());
        }
        
        // 检查QQ是否已存在
        if (playerRepository.existsByQq(player.getQq())) {
            throw new RuntimeException("QQ已存在: " + player.getQq());
        }
        
        // 检查雀魂ID是否已存在
        if (playerRepository.existsByMahjongId(player.getMahjongId())) {
            throw new RuntimeException("雀魂ID已存在: " + player.getMahjongId());
        }
        
        // 设置默认值
        player.setIsActive(true);
        player.setCreatedAt(LocalDateTime.now());
        player.setUpdatedAt(LocalDateTime.now());
        
        return playerRepository.save(player);
    }
    
    /**
     * 玩家登录验证
     */
    public Player loginPlayer(String username, String password) {
        Player player = playerRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        if (!player.getIsActive()) {
            throw new RuntimeException("账户已被禁用");
        }
        
        // 更新最后登录时间
        player.setLastLoginTime(LocalDateTime.now());
        return playerRepository.save(player);
    }
    
    /**
     * 修改密码
     */
    public Player changePassword(Long playerId, String oldPassword, String newPassword) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + playerId));
        
        if (!player.getPassword().equals(oldPassword)) {
            throw new RuntimeException("原密码错误");
        }
        
        player.setPassword(newPassword);
        player.setUpdatedAt(LocalDateTime.now());
        return playerRepository.save(player);
    }
    
    /**
     * 根据ID查找玩家
     */
    public Optional<Player> findById(Long id) {
        return playerRepository.findById(id);
    }
    
    /**
     * 根据用户名查找玩家
     */
    public Optional<Player> findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }
    
    /**
     * 根据QQ查找玩家
     */
    public Optional<Player> findByQq(String qq) {
        return playerRepository.findByQq(qq);
    }
    
    /**
     * 根据雀魂ID查找玩家
     */
    public Optional<Player> findByMahjongId(String mahjongId) {
        return playerRepository.findByMahjongId(mahjongId);
    }
    
    /**
     * 根据昵称查找玩家
     */
    public List<Player> findByNickname(String nickname) {
        return playerRepository.findByNicknameContainingIgnoreCase(nickname);
    }
    
    /**
     * 根据雀魂昵称查找玩家
     */
    public List<Player> findByMahjongNickname(String mahjongNickname) {
        return playerRepository.findByMahjongNicknameContainingIgnoreCase(mahjongNickname);
    }
    
    /**
     * 获取所有玩家
     */
    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }
    
    /**
     * 更新玩家信息
     */
    public Player updatePlayer(Long id, Player playerDetails) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + id));
        
        // 检查用户名唯一性（如果用户名被修改）
        if (!player.getUsername().equals(playerDetails.getUsername()) && 
            playerRepository.existsByUsername(playerDetails.getUsername())) {
            throw new RuntimeException("用户名已存在: " + playerDetails.getUsername());
        }
        
        // 检查QQ唯一性（如果QQ被修改）
        if (!player.getQq().equals(playerDetails.getQq()) && 
            playerRepository.existsByQq(playerDetails.getQq())) {
            throw new RuntimeException("QQ已存在: " + playerDetails.getQq());
        }
        
        // 检查雀魂ID唯一性（如果雀魂ID被修改）
        if (!player.getMahjongId().equals(playerDetails.getMahjongId()) && 
            playerRepository.existsByMahjongId(playerDetails.getMahjongId())) {
            throw new RuntimeException("雀魂ID已存在: " + playerDetails.getMahjongId());
        }
        
        // 更新基本信息（不更新密码）
        player.setUsername(playerDetails.getUsername());
        player.setQq(playerDetails.getQq());
        player.setNickname(playerDetails.getNickname());
        player.setMahjongId(playerDetails.getMahjongId());
        player.setMahjongNickname(playerDetails.getMahjongNickname());
        player.setIsActive(playerDetails.getIsActive());
        player.setUpdatedAt(LocalDateTime.now());
        
        return playerRepository.save(player);
    }
    
    /**
     * 启用/禁用玩家账户
     */
    public Player togglePlayerStatus(Long id, Boolean isActive) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + id));
        
        player.setIsActive(isActive);
        player.setUpdatedAt(LocalDateTime.now());
        return playerRepository.save(player);
    }
    
    /**
     * 删除玩家
     */
    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new RuntimeException("玩家不存在，ID: " + id);
        }
        playerRepository.deleteById(id);
    }
    
    /**
     * 检查用户名是否可用
     */
    public boolean isUsernameAvailable(String username) {
        return !playerRepository.existsByUsername(username);
    }
    
    /**
     * 检查QQ是否可用
     */
    public boolean isQqAvailable(String qq) {
        return !playerRepository.existsByQq(qq);
    }
    
    /**
     * 检查雀魂ID是否可用
     */
    public boolean isMahjongIdAvailable(String mahjongId) {
        return !playerRepository.existsByMahjongId(mahjongId);
    }
} 