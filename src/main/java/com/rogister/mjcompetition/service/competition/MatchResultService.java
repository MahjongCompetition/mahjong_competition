package com.rogister.mjcompetition.service.competition;

import com.rogister.mjcompetition.entity.competition.Competition;
import com.rogister.mjcompetition.entity.competition.MatchResult;
import com.rogister.mjcompetition.entity.player.Player;
import com.rogister.mjcompetition.repository.competition.MatchResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class MatchResultService {
    
    @Autowired
    private MatchResultRepository matchResultRepository;
    
    /**
     * 创建比赛成绩
     */
    public MatchResult createMatchResult(MatchResult matchResult) {
        // 验证基础数据
        if (matchResult.getCompetition() == null) {
            throw new RuntimeException("比赛信息不能为空");
        }
        if (matchResult.getRoundNumber() == null) {
            throw new RuntimeException("轮次编号不能为空");
        }
        if (matchResult.getMatchNumber() == null) {
            throw new RuntimeException("比赛编号不能为空");
        }
        
        // 验证总分是否为100000
        matchResult.calculateTotalScore();
        if (!matchResult.isValidTotalScore()) {
            throw new RuntimeException("四人成绩总和必须为100000分，当前总和: " + matchResult.getTotalScore());
        }
        
        // 检查比赛编号是否已存在
        if (matchResultRepository.findByCompetitionAndRoundAndMatchNumber(
                matchResult.getCompetition().getId(), matchResult.getRoundNumber(), matchResult.getMatchNumber()).isPresent()) {
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
        
        // 计算并设置PT分数
        matchResult.calculateAndSetPtScores();
        
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
    public Optional<MatchResult> findByCompetitionAndRoundAndMatchNumber(Competition competition, Integer roundNumber, Integer matchNumber) {
        return matchResultRepository.findByCompetitionAndRoundAndMatchNumber(competition.getId(), roundNumber, matchNumber);
    }
    
    /**
     * 根据比赛和轮次查找所有比赛成绩
     */
    public List<MatchResult> findByCompetitionAndRound(Competition competition, Integer roundNumber) {
        return matchResultRepository.findByCompetitionAndRoundOrderByMatchNumber(competition.getId(), roundNumber);
    }
    
    /**
     * 根据比赛和轮次查找所有比赛记录，按照比赛时间从早到晚排序
     */
    public List<MatchResult> findByCompetitionAndRoundOrderByTime(Competition competition, Integer roundNumber) {
        return matchResultRepository.findByCompetitionAndRoundOrderByMatchTimeAsc(competition.getId(), roundNumber);
    }
    
    /**
     * 根据比赛和轮次查找所有比赛记录，按照比赛时间从早到晚排序（包含比赛编号作为第二排序条件）
     */
    public List<MatchResult> findByCompetitionAndRoundOrderByTimeAndNumber(Competition competition, Integer roundNumber) {
        return matchResultRepository.findByCompetitionAndRoundOrderByMatchTimeAscMatchNumberAsc(competition.getId(), roundNumber);
    }
    
    /**
     * 计算某一轮次下所有玩家的排名
     */
    public List<PlayerRoundRanking> calculatePlayerRoundRankings(Competition competition, Integer roundNumber) {
        // 获取该轮次下的所有比赛记录
        List<MatchResult> matchResults = matchResultRepository.findByCompetitionAndRound(competition.getId(), roundNumber);
        
        // 用于存储每个玩家的统计信息
        Map<Player, PlayerRoundRanking> playerRankings = new HashMap<>();
        
        // 遍历每场比赛，计算每个玩家的得分和排名
        for (MatchResult matchResult : matchResults) {
            // 直接从数据库读取PT分数
            addPlayerPtScore(playerRankings, matchResult.getEastPlayer(), matchResult.getEastPtScore(), 
                            matchResult.getEastScore(), matchResult.getEastPenalty(), competition, roundNumber);
            addPlayerPtScore(playerRankings, matchResult.getSouthPlayer(), matchResult.getSouthPtScore(), 
                            matchResult.getSouthScore(), matchResult.getSouthPenalty(), competition, roundNumber);
            addPlayerPtScore(playerRankings, matchResult.getWestPlayer(), matchResult.getWestPtScore(), 
                            matchResult.getWestScore(), matchResult.getWestPenalty(), competition, roundNumber);
            addPlayerPtScore(playerRankings, matchResult.getNorthPlayer(), matchResult.getNorthPtScore(), 
                            matchResult.getNorthScore(), matchResult.getNorthPenalty(), competition, roundNumber);
            
            // 计算排名用于统计顺位次数
            List<MatchResult.PlayerRank> matchRanks = matchResult.calculatePlayerRanks();
            for (MatchResult.PlayerRank matchRank : matchRanks) {
                Player player = matchRank.getPlayer();
                PlayerRoundRanking ranking = playerRankings.get(player);
                if (ranking != null) {
                    // 统计顺位次数
                    ranking.addPositionCount(matchRank.getRank());
                }
            }
        }
        
        // 计算平均顺位并排序
        List<PlayerRoundRanking> rankings = new ArrayList<>(playerRankings.values());
        rankings.forEach(PlayerRoundRanking::calculateAveragePosition);
        rankings.sort(PlayerRoundRanking::compareTo);
        
        return rankings;
    }
    
    /**
     * 添加玩家PT分数到排名统计中
     */
    private void addPlayerPtScore(Map<Player, PlayerRoundRanking> playerRankings, Player player, 
                                 Double ptScore, Integer originalScore, Integer penalty, 
                                 Competition competition, Integer roundNumber) {
        // 获取或创建玩家的排名统计对象
        PlayerRoundRanking ranking = playerRankings.computeIfAbsent(player, 
            k -> new PlayerRoundRanking(player, competition, roundNumber));
        
        // 累加PT分数
        ranking.addActualPoints(ptScore);
        
        // 累加原始得分
        ranking.addOriginalScore(originalScore);
        
        // 累加罚分
        ranking.addPenalty(penalty);
        
        // 增加比赛场数
        ranking.incrementMatchCount();
    }
    
    /**
     * 计算某一轮次下指定玩家的排名统计
     */
    public PlayerRoundRanking calculatePlayerRoundRanking(Competition competition, Integer roundNumber, Player player) {
        // 获取该玩家在该轮次下的所有比赛记录
        List<MatchResult> playerMatches = matchResultRepository.findByCompetitionAndRoundAndPlayer(
                competition.getId(), roundNumber, player.getId());
        
        PlayerRoundRanking ranking = new PlayerRoundRanking(player, competition, roundNumber);
        
        // 遍历该玩家的所有比赛记录
        for (MatchResult matchResult : playerMatches) {
            // 直接从数据库读取该玩家的PT分数和其他信息
            Double ptScore = null;
            Integer originalScore = null;
            Integer penalty = null;
            
            if (matchResult.getEastPlayer().getId().equals(player.getId())) {
                ptScore = matchResult.getEastPtScore();
                originalScore = matchResult.getEastScore();
                penalty = matchResult.getEastPenalty();
            } else if (matchResult.getSouthPlayer().getId().equals(player.getId())) {
                ptScore = matchResult.getSouthPtScore();
                originalScore = matchResult.getSouthScore();
                penalty = matchResult.getSouthPenalty();
            } else if (matchResult.getWestPlayer().getId().equals(player.getId())) {
                ptScore = matchResult.getWestPtScore();
                originalScore = matchResult.getWestScore();
                penalty = matchResult.getWestPenalty();
            } else if (matchResult.getNorthPlayer().getId().equals(player.getId())) {
                ptScore = matchResult.getNorthPtScore();
                originalScore = matchResult.getNorthScore();
                penalty = matchResult.getNorthPenalty();
            }
            
            if (ptScore != null) {
                // 累加PT分数
                ranking.addActualPoints(ptScore);
                
                // 累加原始得分
                ranking.addOriginalScore(originalScore);
                
                // 累加罚分
                ranking.addPenalty(penalty);
                
                // 计算排名用于统计顺位次数
                List<MatchResult.PlayerRank> matchRanks = matchResult.calculatePlayerRanks();
                MatchResult.PlayerRank playerRank = matchRanks.stream()
                        .filter(rank -> rank.getPlayer().getId().equals(player.getId()))
                        .findFirst()
                        .orElse(null);
                
                if (playerRank != null) {
                    // 统计顺位次数
                    ranking.addPositionCount(playerRank.getRank());
                }
                
                // 增加比赛场数
                ranking.incrementMatchCount();
            }
        }
        
        // 计算平均顺位
        ranking.calculateAveragePosition();
        
        return ranking;
    }
    
    /**
     * 根据比赛查找所有比赛成绩
     */
    public List<MatchResult> findByCompetition(Competition competition) {
        return matchResultRepository.findByCompetitionOrderByRoundRoundNumberAscMatchNumberAsc(competition.getId());
    }
    
    /**
     * 根据玩家查找所有参与的比赛成绩
     */
    public List<MatchResult> findByPlayer(Player player) {
        return matchResultRepository.findByEastPlayerOrSouthPlayerOrWestPlayerOrNorthPlayerOrderByCompetitionAscRoundRoundNumberAscMatchNumberAsc(
                player.getId());
    }
    
    /**
     * 根据比赛、轮次和玩家查找该玩家参与的比赛成绩
     */
    public List<MatchResult> findByCompetitionAndRoundAndPlayer(Competition competition, Integer roundNumber, Player player) {
        return matchResultRepository.findByCompetitionAndRoundAndPlayer(
                competition.getId(), roundNumber, player.getId());
    }
    
    /**
     * 根据比赛和轮次统计比赛场数
     */
    public long countByCompetitionAndRound(Competition competition, Integer roundNumber) {
        return matchResultRepository.countByCompetitionAndRound(competition.getId(), roundNumber);
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
        
        // 重新计算并设置PT分数
        matchResult.calculateAndSetPtScores();
        
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
    public Integer getNextMatchNumber(Competition competition, Integer roundNumber) {
        long currentCount = matchResultRepository.countByCompetitionAndRound(competition.getId(), roundNumber);
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
     * 获取玩家在某轮次的PT分数总和
     */
    public Double getPlayerPtScoreSum(Long competitionId, Integer roundNumber, Long playerId) {
        Double sum = matchResultRepository.getPlayerPtScoreSum(competitionId, roundNumber, playerId);
        return sum != null ? sum : 0.0;
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
    
    /**
     * 内部类：玩家轮次排名统计
     */
    public static class PlayerRoundRanking implements Comparable<PlayerRoundRanking> {
        private Player player;
        private Competition competition;
        private Integer roundNumber;
        private Double totalActualPoints = 0.0;        // 实际得分总和
        private Integer totalOriginalScore = 0;        // 原始得分总和
        private Integer totalPenalty = 0;              // 罚分总和
        private Integer matchCount = 0;                // 比赛场数
        private Integer firstPlaceCount = 0;           // 第一名次数
        private Integer secondPlaceCount = 0;          // 第二名次数
        private Integer thirdPlaceCount = 0;           // 第三名次数
        private Integer fourthPlaceCount = 0;          // 第四名次数
        private Double averagePosition = 0.0;          // 平均顺位
        private Integer rank = 0;                      // 当前排名
        
        public PlayerRoundRanking(Player player, Competition competition, Integer roundNumber) {
            this.player = player;
            this.competition = competition;
            this.roundNumber = roundNumber;
        }
        
        /**
         * 添加实际得分
         */
        public void addActualPoints(Double points) {
            if (points != null) {
                this.totalActualPoints += points;
            }
        }
        
        /**
         * 添加原始得分
         */
        public void addOriginalScore(Integer score) {
            if (score != null) {
                this.totalOriginalScore += score;
            }
        }
        
        /**
         * 添加罚分
         */
        public void addPenalty(Integer penalty) {
            if (penalty != null) {
                this.totalPenalty += penalty;
            }
        }
        
        /**
         * 统计顺位次数
         */
        public void addPositionCount(Integer position) {
            if (position != null) {
                switch (position) {
                    case 1: firstPlaceCount++; break;
                    case 2: secondPlaceCount++; break;
                    case 3: thirdPlaceCount++; break;
                    case 4: fourthPlaceCount++; break;
                }
            }
        }
        
        /**
         * 增加比赛场数
         */
        public void incrementMatchCount() {
            this.matchCount++;
        }
        
        /**
         * 计算平均顺位
         */
        public void calculateAveragePosition() {
            if (matchCount > 0) {
                double totalPosition = firstPlaceCount + (secondPlaceCount * 2.0) + 
                                     (thirdPlaceCount * 3.0) + (fourthPlaceCount * 4.0);
                this.averagePosition = totalPosition / matchCount;
            }
        }
        
        /**
         * 设置排名
         */
        public void setRank(Integer rank) {
            this.rank = rank;
        }
        
        /**
         * 比较方法：按实际得分从高到低排序，得分相同时按平均顺位从低到高排序
         */
        @Override
        public int compareTo(PlayerRoundRanking other) {
            // 首先按实际得分从高到低排序
            int pointsComparison = Double.compare(other.totalActualPoints, this.totalActualPoints);
            if (pointsComparison != 0) {
                return pointsComparison;
            }
            
            // 得分相同时，按平均顺位从低到高排序
            return Double.compare(this.averagePosition, other.averagePosition);
        }
        
        // Getter方法
        public Player getPlayer() { return player; }
        public Competition getCompetition() { return competition; }
        public Integer getRoundNumber() { return roundNumber; }
        public Double getTotalActualPoints() { return totalActualPoints; }
        public Integer getTotalOriginalScore() { return totalOriginalScore; }
        public Integer getTotalPenalty() { return totalPenalty; }
        public Integer getMatchCount() { return matchCount; }
        public Integer getFirstPlaceCount() { return firstPlaceCount; }
        public Integer getSecondPlaceCount() { return secondPlaceCount; }
        public Integer getThirdPlaceCount() { return thirdPlaceCount; }
        public Integer getFourthPlaceCount() { return fourthPlaceCount; }
        public Double getAveragePosition() { return averagePosition; }
        public Integer getRank() { return rank; }
        
        /**
         * 获取玩家姓名
         */
        public String getPlayerName() {
            return player != null ? player.getNickname() : "未知玩家";
        }
        
        /**
         * 获取玩家麻将ID
         */
        public String getMahjongId() {
            return player != null ? player.getMahjongId() : "";
        }
    }
} 