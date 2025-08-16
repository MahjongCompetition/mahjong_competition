package com.rogister.mjcompetiton.controller;

import com.rogister.mjcompetiton.entity.Player;
import com.rogister.mjcompetiton.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class PlayerController {
    
    @Autowired
    private PlayerService playerService;
    
    /**
     * 玩家注册账户
     */
    @PostMapping("/register")
    public ResponseEntity<Player> registerPlayer(@RequestBody Player player) {
        try {
            Player registeredPlayer = playerService.registerPlayer(player);
            return ResponseEntity.ok(registeredPlayer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 玩家登录
     */
    @PostMapping("/login")
    public ResponseEntity<Player> loginPlayer(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            if (username == null || password == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Player loggedInPlayer = playerService.loginPlayer(username, password);
            return ResponseEntity.ok(loggedInPlayer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Player> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordRequest) {
        try {
            String oldPassword = passwordRequest.get("oldPassword");
            String newPassword = passwordRequest.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Player updatedPlayer = playerService.changePassword(id, oldPassword, newPassword);
            return ResponseEntity.ok(updatedPlayer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(@RequestParam String username) {
        boolean isAvailable = playerService.isUsernameAvailable(username);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }
    
    /**
     * 检查QQ是否可用
     */
    @GetMapping("/check-qq")
    public ResponseEntity<Map<String, Boolean>> checkQqAvailability(@RequestParam String qq) {
        boolean isAvailable = playerService.isQqAvailable(qq);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }
    
    /**
     * 检查雀魂ID是否可用
     */
    @GetMapping("/check-mahjong-id")
    public ResponseEntity<Map<String, Boolean>> checkMahjongIdAvailability(@RequestParam String mahjongId) {
        boolean isAvailable = playerService.isMahjongIdAvailable(mahjongId);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }
    
    /**
     * 获取所有玩家
     */
    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        List<Player> players = playerService.findAllPlayers();
        return ResponseEntity.ok(players);
    }
    
    /**
     * 根据ID获取玩家
     */
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return playerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据用户名获取玩家
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Player> getPlayerByUsername(@PathVariable String username) {
        return playerService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据QQ获取玩家
     */
    @GetMapping("/qq/{qq}")
    public ResponseEntity<Player> getPlayerByQq(@PathVariable String qq) {
        return playerService.findByQq(qq)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据雀魂ID获取玩家
     */
    @GetMapping("/mahjong/{mahjongId}")
    public ResponseEntity<Player> getPlayerByMahjongId(@PathVariable String mahjongId) {
        return playerService.findByMahjongId(mahjongId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据昵称搜索玩家
     */
    @GetMapping("/search/nickname")
    public ResponseEntity<List<Player>> searchPlayersByNickname(@RequestParam String nickname) {
        List<Player> players = playerService.findByNickname(nickname);
        return ResponseEntity.ok(players);
    }
    
    /**
     * 根据雀魂昵称搜索玩家
     */
    @GetMapping("/search/mahjong-nickname")
    public ResponseEntity<List<Player>> searchPlayersByMahjongNickname(@RequestParam String mahjongNickname) {
        List<Player> players = playerService.findByMahjongNickname(mahjongNickname);
        return ResponseEntity.ok(players);
    }
    
    /**
     * 更新玩家信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody Player playerDetails) {
        try {
            Player updatedPlayer = playerService.updatePlayer(id, playerDetails);
            return ResponseEntity.ok(updatedPlayer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 启用/禁用玩家账户
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Player> togglePlayerStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> statusRequest) {
        try {
            Boolean isActive = statusRequest.get("isActive");
            if (isActive == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Player updatedPlayer = playerService.togglePlayerStatus(id, isActive);
            return ResponseEntity.ok(updatedPlayer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除玩家
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        try {
            playerService.deletePlayer(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 