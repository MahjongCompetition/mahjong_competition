package com.rogister.mjcompetition.service;

import com.rogister.mjcompetition.entity.Competition;
import com.rogister.mjcompetition.entity.Player;
import com.rogister.mjcompetition.entity.PlayerCompetitionRegistration;
import com.rogister.mjcompetition.entity.PlayerRoundStatus;
import com.rogister.mjcompetition.repository.CompetitionRepository;
import com.rogister.mjcompetition.repository.PlayerCompetitionRegistrationRepository;
import com.rogister.mjcompetition.repository.PlayerRepository;
import com.rogister.mjcompetition.repository.PlayerRoundStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerCompetitionRegistrationService {
    
    @Autowired
    private PlayerCompetitionRegistrationRepository registrationRepository;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private PlayerRoundStatusRepository playerRoundStatusRepository;
    
    /**
     * 玩家报名比赛
     */
    @Transactional
    public PlayerCompetitionRegistration registerForCompetition(Long playerId, Long competitionId) {
        // 验证玩家是否存在
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + playerId));
        
        // 验证比赛是否存在
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
        
        // 检查报名是否已结束
        if (competition.isRegistrationClosed()) {
            throw new RuntimeException("报名已结束，无法报名");
        }
        
        // 检查是否已经报名
        if (registrationRepository.existsByPlayerIdAndCompetitionId(player.getId(), competition.getId())) {
            throw new RuntimeException("您已经报名了这场比赛");
        }
        
        // 创建报名记录
        PlayerCompetitionRegistration registration = new PlayerCompetitionRegistration(player, competition);
        PlayerCompetitionRegistration savedRegistration = registrationRepository.save(registration);
        
        // 自动创建第一轮次状态，初始得分为0
        PlayerRoundStatus firstRoundStatus = new PlayerRoundStatus(player, competition, 1, 0);
        playerRoundStatusRepository.save(firstRoundStatus);
        
        return savedRegistration;
    }
    
    /**
     * 取消报名
     */
    @Transactional
    public void cancelRegistration(Long playerId, Long competitionId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + playerId));
        
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
        
        PlayerCompetitionRegistration registration = registrationRepository.findByPlayerIdAndCompetitionId(player.getId(), competition.getId())
                .orElseThrow(() -> new RuntimeException("未找到报名记录"));
        
        // 检查报名是否已结束
        if (competition.isRegistrationClosed()) {
            throw new RuntimeException("报名已结束，无法取消报名");
        }
        
        // 删除该玩家在此比赛中的所有轮次状态
        List<PlayerRoundStatus> playerRoundStatuses = playerRoundStatusRepository.findByPlayerIdAndCompetitionId(playerId, competitionId);
        if (!playerRoundStatuses.isEmpty()) {
            playerRoundStatusRepository.deleteAll(playerRoundStatuses);
        }
        
        // 设置报名状态为已取消
        registration.setStatus(PlayerCompetitionRegistration.RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
    }
    
    /**
     * 获取玩家的所有报名记录
     */
    public List<PlayerCompetitionRegistration> getPlayerRegistrations(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + playerId));
        
        return registrationRepository.findByPlayerId(player.getId());
    }
    
    /**
     * 获取比赛的所有报名玩家
     */
    public List<PlayerCompetitionRegistration> getCompetitionRegistrations(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
        
        return registrationRepository.findByCompetitionId(competition.getId());
    }
    
    /**
     * 检查玩家是否已报名某场比赛
     */
    public boolean isPlayerRegistered(Long playerId, Long competitionId) {
        Player player = playerRepository.findById(playerId).orElse(null);
        Competition competition = competitionRepository.findById(competitionId).orElse(null);
        
        if (player == null || competition == null) {
            return false;
        }
        
        return registrationRepository.existsByPlayerIdAndCompetitionId(player.getId(), competition.getId());
    }
}
