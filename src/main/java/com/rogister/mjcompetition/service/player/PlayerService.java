package com.rogister.mjcompetition.service.player;

import com.rogister.mjcompetition.entity.player.Player;
import com.rogister.mjcompetition.repository.player.PlayerRepository;
import com.rogister.mjcompetition.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 玩家登录
     */
    public String login(String username, String password) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        if (!passwordEncoder.matches(password, player.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (!player.getIsActive()) {
            throw new RuntimeException("账户已被禁用");
        }
        
        // 更新最后登录时间
        player.setLastLoginTime(LocalDateTime.now());
        playerRepository.save(player);
        
        // 生成包含角色信息的JWT token
        return jwtUtil.generateTokenWithRole(username, "PLAYER", "PLAYER");
    }
    
    /**
     * 创建新玩家
     */
    public Player createPlayer(Player player) {
        // 验证用户名是否已存在
        if (playerRepository.existsByUsername(player.getUsername())) {
            throw new RuntimeException("用户名已存在: " + player.getUsername());
        }
        
        // 验证QQ是否已存在
        if (playerRepository.existsByQq(player.getQq())) {
            throw new RuntimeException("QQ号已存在: " + player.getQq());
        }
        
        // 验证麻将ID是否已存在
        if (playerRepository.existsByMahjongId(player.getMahjongId())) {
            throw new RuntimeException("麻将ID已存在: " + player.getMahjongId());
        }
        
        // 加密密码
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        
        // 设置默认值
        if (player.getCreatedAt() == null) {
            player.setCreatedAt(LocalDateTime.now());
        }
        if (player.getUpdatedAt() == null) {
            player.setUpdatedAt(LocalDateTime.now());
        }
        if (player.getIsActive() == null) {
            player.setIsActive(true);
        }
        
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
            throw new RuntimeException("QQ号已存在: " + playerDetails.getQq());
        }
        
        // 检查麻将ID唯一性（如果麻将ID被修改）
        if (!player.getMahjongId().equals(playerDetails.getMahjongId()) && 
            playerRepository.existsByMahjongId(playerDetails.getMahjongId())) {
            throw new RuntimeException("麻将ID已存在: " + playerDetails.getMahjongId());
        }
        
        player.setUsername(playerDetails.getUsername());
        player.setQq(playerDetails.getQq());
        player.setNickname(playerDetails.getNickname());
        player.setMahjongId(playerDetails.getMahjongId());
        player.setMahjongNickname(playerDetails.getMahjongNickname());
        
        // 如果密码被修改，需要重新加密
        if (playerDetails.getPassword() != null && !playerDetails.getPassword().isEmpty()) {
            player.setPassword(passwordEncoder.encode(playerDetails.getPassword()));
        }
        
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
     * 验证token
     */
    public boolean validateToken(String token, String username) {
        return jwtUtil.validateToken(token, username);
    }
    
    /**
     * 从token中提取用户名
     */
    public String extractUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }
} 