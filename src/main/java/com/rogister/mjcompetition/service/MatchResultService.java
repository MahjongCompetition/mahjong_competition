package com.rogister.mjcompetition.service;

import com.rogister.mjcompetition.entity.*;
import com.rogister.mjcompetition.repository.MatchResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MatchResultService {
    
    @Autowired
    private MatchResultRepository matchResultRepository;
    
    /**
     * 创建比赛成绩
     */
    public MatchResult createMatchResult(MatchResult matchResult) {
        // 验证总分是否为100000
        matchResult.calculateTotalScore();
        if (!matchResult.isValidTotalScore()) {
            throw new RuntimeException("四人成绩总和必须为100000分，当前总和: " + matchResult.getTotalScore());
        }
        
        // 检查比赛编号是否已存在
        if (matchResultRepository.findByCompetitionAndRoundAndMatchNumber(
                matchResult.getCompetition(), matchResult.getRound(), matchResult.getMatchNumber()).isPresent()) {
            throw new RuntimeException("该轮次的比赛编号已存在: " + matchResult.getMatchNumber());
        }
        
        // 设置比赛时间
        if (matchResult.getMatchTime() == null) {
            matchResult.setMatchTime(LocalDateTime.now());
        }
        
        // 确保罚分字段不为null
        if (matchResult.getEastPenalty() == null) matchResult.setEastPenalty(0);
        if (matchResult.getSouthPenalty() == null) matchResult.setSouthPenalty(0);
        if (matchResult.getWestPenalty() == null) matchResult.setWestPenalty(0);
        if (matchResult.getNorthPenalty() == null) matchResult.setNorthPenalty(0);
        
        return matchResultRepository.save(matchResult);
    }
    
    /**
     * 根据ID查找比赛成绩
     */
    public Optional<MatchResult> findById(Long id) {
        return matchResultRepository.findById(id);
    }
    
    /**
     * 根据比赛、轮次和比赛编号查找比赛成绩
     */
    public Optional<MatchResult> findByCompetitionAndRoundAndMatchNumber(Competition competition, CompetitionRound round, Integer matchNumber) {
        return matchResultRepository.findByCompetitionAndRoundAndMatchNumber(competition, round, matchNumber);
    }
    
    /**
     * 根据比赛和轮次查找所有比赛成绩
     */
    public List<MatchResult> findByCompetitionAndRound(Competition competition, CompetitionRound round) {
        return matchResultRepository.findByCompetitionAndRoundOrderByMatchNumber(competition, round);
    }
    
    /**
     * 根据比赛查找所有比赛成绩
     */
    public List<MatchResult> findByCompetition(Competition competition) {
        return matchResultRepository.findByCompetitionOrderByRoundRoundNumberAscMatchNumberAsc(competition);
    }
    
    /**
     * 根据玩家查找所有参与的比赛成绩
     */
    public List<MatchResult> findByPlayer(Player player) {
        return matchResultRepository.findByEastPlayerOrSouthPlayerOrWestPlayerOrNorthPlayerOrderByCompetitionAscRoundRoundNumberAscMatchNumberAsc(
                player, player, player, player);
    }
    
    /**
     * 根据比赛、轮次和玩家查找该玩家参与的比赛成绩
     */
    public List<MatchResult> findByCompetitionAndRoundAndPlayer(Competition competition, CompetitionRound round, Player player) {
        return matchResultRepository.findByCompetitionAndRoundAndEastPlayerOrSouthPlayerOrWestPlayerOrNorthPlayer(
                competition, round, player, player, player, player);
    }
    
    /**
     * 根据比赛和轮次统计比赛场数
     */
    public long countByCompetitionAndRound(Competition competition, CompetitionRound round) {
        return matchResultRepository.countByCompetitionAndRound(competition, round);
    }
    
    /**
     * 更新比赛成绩
     */
    public MatchResult updateMatchResult(Long id, MatchResult matchResultDetails) {
        MatchResult matchResult = matchResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("比赛成绩不存在，ID: " + id));
        
        // 更新成绩
        matchResult.setEastScore(matchResultDetails.getEastScore());
        matchResult.setSouthScore(matchResultDetails.getSouthScore());
        matchResult.setWestScore(matchResultDetails.getWestScore());
        matchResult.setNorthScore(matchResultDetails.getNorthScore());
        
        // 更新罚分
        matchResult.setEastPenalty(matchResultDetails.getEastPenalty());
        matchResult.setSouthPenalty(matchResultDetails.getSouthPenalty());
        matchResult.setWestPenalty(matchResultDetails.getWestPenalty());
        matchResult.setNorthPenalty(matchResultDetails.getNorthPenalty());
        
        // 更新备注
        matchResult.setRemarks(matchResultDetails.getRemarks());
        
        matchResult.setMatchName(matchResultDetails.getMatchName());
        matchResult.setMatchTime(matchResultDetails.getMatchTime());
        
        // 重新计算总分并验证
        matchResult.calculateTotalScore();
        if (!matchResult.isValidTotalScore()) {
            throw new RuntimeException("四人成绩总和必须为100000分，当前总和: " + matchResult.getTotalScore());
        }
        
        return matchResultRepository.save(matchResult);
    }
    
    /**
     * 删除比赛成绩
     */
    public void deleteMatchResult(Long id) {
        if (!matchResultRepository.existsById(id)) {
            throw new RuntimeException("比赛成绩不存在，ID: " + id);
        }
        matchResultRepository.deleteById(id);
    }
    
    /**
     * 验证比赛成绩总分
     */
    public boolean validateMatchResultScores(Integer eastScore, Integer southScore, Integer westScore, Integer northScore) {
        if (eastScore == null || southScore == null || westScore == null || northScore == null) {
            return false;
        }
        
        int total = eastScore + southScore + westScore + northScore;
        return total == 100000;
    }
    
    /**
     * 获取下一场比赛编号
     */
    public Integer getNextMatchNumber(Competition competition, CompetitionRound round) {
        long currentCount = matchResultRepository.countByCompetitionAndRound(competition, round);
        return (int) (currentCount + 1);
    }
    
    /**
     * 计算比赛排名并返回
     */
    public List<MatchResult.PlayerRank> calculateMatchRanks(Long matchResultId) {
        MatchResult matchResult = matchResultRepository.findById(matchResultId)
                .orElseThrow(() -> new RuntimeException("比赛成绩不存在，ID: " + matchResultId));
        
        return matchResult.calculatePlayerRanks();
    }
    
    /**
     * 获取比赛成绩的详细排名和得分信息
     */
    public MatchResultDetail getMatchResultDetail(Long matchResultId) {
        MatchResult matchResult = matchResultRepository.findById(matchResultId)
                .orElseThrow(() -> new RuntimeException("比赛成绩不存在，ID: " + matchResultId));
        
        List<MatchResult.PlayerRank> playerRanks = matchResult.calculatePlayerRanks();
        
        return new MatchResultDetail(matchResult, playerRanks);
    }
    
    /**
     * 内部类：比赛成绩详细信息
     */
    public static class MatchResultDetail {
        private MatchResult matchResult;
        private List<MatchResult.PlayerRank> playerRanks;
        
        public MatchResultDetail(MatchResult matchResult, List<MatchResult.PlayerRank> playerRanks) {
            this.matchResult = matchResult;
            this.playerRanks = playerRanks;
        }
        
        public MatchResult getMatchResult() { return matchResult; }
        public List<MatchResult.PlayerRank> getPlayerRanks() { return playerRanks; }
    }
} 