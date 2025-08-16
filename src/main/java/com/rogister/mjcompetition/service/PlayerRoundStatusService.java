package com.rogister.mjcompetition.service;

import com.rogister.mjcompetition.entity.*;
import com.rogister.mjcompetition.repository.PlayerRoundStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerRoundStatusService {
    
    @Autowired
    private PlayerRoundStatusRepository playerRoundStatusRepository;
    
    /**
     * 创建选手轮次状态记录
     */
    public PlayerRoundStatus createPlayerRoundStatus(Competition competition, CompetitionRound round, Player player) {
        // 检查是否已存在状态记录
        if (playerRoundStatusRepository.existsByCompetitionAndRoundAndPlayer(competition, round, player)) {
            throw new RuntimeException("选手在该轮次的状态记录已存在");
        }
        
        PlayerRoundStatus status = new PlayerRoundStatus(competition, round, player);
        return playerRoundStatusRepository.save(status);
    }
    
    /**
     * 根据ID查找选手轮次状态
     */
    public Optional<PlayerRoundStatus> findById(Long id) {
        return playerRoundStatusRepository.findById(id);
    }
    
    /**
     * 根据比赛、轮次和玩家查找状态
     */
    public Optional<PlayerRoundStatus> findByCompetitionAndRoundAndPlayer(Competition competition, CompetitionRound round, Player player) {
        return playerRoundStatusRepository.findByCompetitionAndRoundAndPlayer(competition, round, player);
    }
    
    /**
     * 根据比赛和轮次查找所有选手状态
     */
    public List<PlayerRoundStatus> findByCompetitionAndRound(Competition competition, CompetitionRound round) {
        return playerRoundStatusRepository.findByCompetitionAndRoundOrderByCurrentScoreDesc(competition, round);
    }
    
    /**
     * 根据比赛和轮次查找晋级的选手
     */
    public List<PlayerRoundStatus> findAdvancedPlayersByCompetitionAndRound(Competition competition, CompetitionRound round) {
        return playerRoundStatusRepository.findByCompetitionAndRoundAndIsAdvancedTrueOrderByCurrentScoreDesc(competition, round);
    }
    
    /**
     * 根据比赛和玩家查找所有轮次状态
     */
    public List<PlayerRoundStatus> findByCompetitionAndPlayer(Competition competition, Player player) {
        return playerRoundStatusRepository.findByCompetitionAndPlayerOrderByRoundRoundNumber(competition, player);
    }
    
    /**
     * 根据比赛查找当前轮次的所有选手状态
     */
    public List<PlayerRoundStatus> findCurrentRoundPlayers(Competition competition) {
        return playerRoundStatusRepository.findByCompetitionAndRoundIsActiveTrueOrderByCurrentScoreDesc(competition);
    }
    
    /**
     * 设置选手晋级状态
     */
    public PlayerRoundStatus setPlayerAdvancement(Long statusId, Boolean isAdvanced, Integer startingScore) {
        PlayerRoundStatus status = playerRoundStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("选手轮次状态不存在，ID: " + statusId));
        
        status.setIsAdvanced(isAdvanced);
        if (startingScore != null) {
            status.setStartingScore(startingScore);
            status.setCurrentScore(startingScore);
        }
        
        return playerRoundStatusRepository.save(status);
    }
    
    /**
     * 批量设置选手晋级状态
     */
    public void batchSetPlayerAdvancement(List<Long> statusIds, Boolean isAdvanced, Integer startingScore) {
        for (Long statusId : statusIds) {
            setPlayerAdvancement(statusId, isAdvanced, startingScore);
        }
    }
    
    /**
     * 更新选手当前得分
     */
    public PlayerRoundStatus updatePlayerCurrentScore(Long statusId, Integer currentScore) {
        PlayerRoundStatus status = playerRoundStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("选手轮次状态不存在，ID: " + statusId));
        
        status.setCurrentScore(currentScore);
        return playerRoundStatusRepository.save(status);
    }
    
    /**
     * 设置选手排名
     */
    public PlayerRoundStatus setPlayerRank(Long statusId, Integer rank) {
        PlayerRoundStatus status = playerRoundStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("选手轮次状态不存在，ID: " + statusId));
        
        status.setRankInRound(rank);
        return playerRoundStatusRepository.save(status);
    }
    
    /**
     * 根据比赛和轮次自动计算排名
     */
    public void calculateRanksForRound(Competition competition, CompetitionRound round) {
        List<PlayerRoundStatus> playerStatuses = playerRoundStatusRepository.findByCompetitionAndRoundOrderByCurrentScoreDesc(competition, round);
        
        for (int i = 0; i < playerStatuses.size(); i++) {
            PlayerRoundStatus status = playerStatuses.get(i);
            status.setRankInRound(i + 1);
            playerRoundStatusRepository.save(status);
        }
    }
    
    /**
     * 删除选手轮次状态
     */
    public void deletePlayerRoundStatus(Long id) {
        if (!playerRoundStatusRepository.existsById(id)) {
            throw new RuntimeException("选手轮次状态不存在，ID: " + id);
        }
        playerRoundStatusRepository.deleteById(id);
    }
} 