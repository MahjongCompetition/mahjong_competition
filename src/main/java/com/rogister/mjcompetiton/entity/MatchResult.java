package com.rogister.mjcompetiton.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "match_results")
public class MatchResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private CompetitionRound round;
    
    @Column(name = "match_number", nullable = false)
    private Integer matchNumber;
    
    @Column(name = "match_name", length = 200)
    private String matchName;
    
    // 东南西北四个方位的玩家
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "east_player_id", nullable = false)
    private Player eastPlayer;
    
    @Column(name = "east_score", nullable = false)
    private Integer eastScore;
    
    @Column(name = "east_penalty", nullable = false)
    private Integer eastPenalty = 0; // 东家罚分，默认为0
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "south_player_id", nullable = false)
    private Player southPlayer;
    
    @Column(name = "south_score", nullable = false)
    private Integer southScore;
    
    @Column(name = "south_penalty", nullable = false)
    private Integer southPenalty = 0; // 南家罚分，默认为0
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "west_player_id", nullable = false)
    private Player westPlayer;
    
    @Column(name = "west_score", nullable = false)
    private Integer westScore;
    
    @Column(name = "west_penalty", nullable = false)
    private Integer westPenalty = 0; // 西家罚分，默认为0
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "north_player_id", nullable = false)
    private Player northPlayer;
    
    @Column(name = "north_score", nullable = false)
    private Integer northScore;
    
    @Column(name = "north_penalty", nullable = false)
    private Integer northPenalty = 0; // 北家罚分，默认为0
    
    @Column(name = "total_score", nullable = false)
    private Integer totalScore;
    
    @Column(name = "match_time")
    private LocalDateTime matchTime;
    
    @Column(name = "remarks", length = 1000)
    private String remarks; // 比赛备注，描述比赛情况
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public MatchResult() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.totalScore = 100000; // 默认总分100000
        this.eastPenalty = 0;
        this.southPenalty = 0;
        this.westPenalty = 0;
        this.northPenalty = 0;
    }
    
    public MatchResult(Competition competition, CompetitionRound round, Integer matchNumber,
                      Player eastPlayer, Player southPlayer, Player westPlayer, Player northPlayer) {
        this();
        this.competition = competition;
        this.round = round;
        this.matchNumber = matchNumber;
        this.eastPlayer = eastPlayer;
        this.southPlayer = southPlayer;
        this.westPlayer = westPlayer;
        this.northPlayer = northPlayer;
    }
    
    // 计算总分的方法
    public void calculateTotalScore() {
        this.totalScore = (eastScore != null ? eastScore : 0) +
                         (southScore != null ? southScore : 0) +
                         (westScore != null ? westScore : 0) +
                         (northScore != null ? northScore : 0);
    }
    
    // 验证总分是否为100000
    public boolean isValidTotalScore() {
        return this.totalScore == 100000;
    }
    
    // 计算玩家实际得分（包含排名和罚分）
    public List<PlayerRank> calculatePlayerRanks() {
        List<PlayerRank> playerRanks = new ArrayList<>();
        
        // 添加四个方位的玩家、得分和罚分
        playerRanks.add(new PlayerRank(eastPlayer, eastScore, eastPenalty, "东"));
        playerRanks.add(new PlayerRank(southPlayer, southScore, southPenalty, "南"));
        playerRanks.add(new PlayerRank(westPlayer, westScore, westPenalty, "西"));
        playerRanks.add(new PlayerRank(northPlayer, northScore, northPenalty, "北"));
        
        // 按照得分从高到低排序，得分相同时按照逆时针顺序（东南西北）排序
        playerRanks.sort((a, b) -> {
            if (!Objects.equals(a.score, b.score)) {
                return Integer.compare(b.score, a.score); // 得分高的在前
            }
            // 得分相同时，按照逆时针顺序排序
            return getPositionOrder(a.position).compareTo(getPositionOrder(b.position));
        });
        
        // 设置排名并计算实际得分
        for (int i = 0; i < playerRanks.size(); i++) {
            PlayerRank playerRank = playerRanks.get(i);
            playerRank.rank = i + 1;
            
            // 计算实际得分：(玩家得分-比赛规则原点)/1000+比赛规则的顺位点+罚分
            playerRank.calculateActualPoints(competition.getRule());
        }
        
        return playerRanks;
    }
    
    // 获取方位在逆时针顺序中的权重
    private Integer getPositionOrder(String position) {
        switch (position) {
            case "东": return 0;
            case "南": return 1;
            case "西": return 2;
            case "北": return 3;
            default: return 999;
        }
    }
    
    // 内部类：玩家排名
    public static class PlayerRank {
        public Player player;
        public Integer score;
        public Integer penalty;
        public String position;
        public Integer rank;
        public Double actualPoints; // 实际得分
        
        public PlayerRank(Player player, Integer score, Integer penalty, String position) {
            this.player = player;
            this.score = score;
            this.penalty = penalty;
            this.position = position;
        }
        
        // 计算实际得分
        public void calculateActualPoints(CompetitionRule rule) {
            if (rule == null || rule.getOriginPoints() == null || score == null) {
                this.actualPoints = 0.0;
                return;
            }
            
            // 计算基础得分：(玩家得分-比赛规则原点)/1000
            double basePoints = (score - rule.getOriginPoints()) / 1000.0;
            
            // 获取顺位点
            int rankPoints = 0;
            switch (rank) {
                case 1: rankPoints = rule.getFirstPlacePoints(); break;
                case 2: rankPoints = rule.getSecondPlacePoints(); break;
                case 3: rankPoints = rule.getThirdPlacePoints(); break;
                case 4: rankPoints = rule.getFourthPlacePoints(); break;
            }
            
            // 实际得分 = 基础得分 + 顺位点 + 罚分
            this.actualPoints = basePoints + rankPoints + (penalty != null ? penalty : 0);
        }
        
        public Player getPlayer() { return player; }
        public Integer getScore() { return score; }
        public Integer getPenalty() { return penalty; }
        public String getPosition() { return position; }
        public Integer getRank() { return rank; }
        public Double getActualPoints() { return actualPoints; }
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Competition getCompetition() {
        return competition;
    }
    
    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
    
    public CompetitionRound getRound() {
        return round;
    }
    
    public void setRound(CompetitionRound round) {
        this.round = round;
    }
    
    public Integer getMatchNumber() {
        return matchNumber;
    }
    
    public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }
    
    public String getMatchName() {
        return matchName;
    }
    
    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }
    
    public Player getEastPlayer() {
        return eastPlayer;
    }
    
    public void setEastPlayer(Player eastPlayer) {
        this.eastPlayer = eastPlayer;
    }
    
    public Integer getEastScore() {
        return eastScore;
    }
    
    public void setEastScore(Integer eastScore) {
        this.eastScore = eastScore;
    }
    
    public Integer getEastPenalty() {
        return eastPenalty;
    }
    
    public void setEastPenalty(Integer eastPenalty) {
        this.eastPenalty = eastPenalty != null ? eastPenalty : 0;
    }
    
    public Player getSouthPlayer() {
        return southPlayer;
    }
    
    public void setSouthPlayer(Player southPlayer) {
        this.southPlayer = southPlayer;
    }
    
    public Integer getSouthScore() {
        return southScore;
    }
    
    public void setSouthScore(Integer southScore) {
        this.southScore = southScore;
    }
    
    public Integer getSouthPenalty() {
        return southPenalty;
    }
    
    public void setSouthPenalty(Integer southPenalty) {
        this.southPenalty = southPenalty != null ? southPenalty : 0;
    }
    
    public Player getWestPlayer() {
        return westPlayer;
    }
    
    public void setWestPlayer(Player westPlayer) {
        this.westPlayer = westPlayer;
    }
    
    public Integer getWestScore() {
        return westScore;
    }
    
    public void setWestScore(Integer westScore) {
        this.westScore = westScore;
    }
    
    public Integer getWestPenalty() {
        return westPenalty;
    }
    
    public void setWestPenalty(Integer westPenalty) {
        this.westPenalty = westPenalty != null ? westPenalty : 0;
    }
    
    public Player getNorthPlayer() {
        return northPlayer;
    }
    
    public void setNorthPlayer(Player northPlayer) {
        this.northPlayer = northPlayer;
    }
    
    public Integer getNorthScore() {
        return northScore;
    }
    
    public void setNorthScore(Integer northScore) {
        this.northScore = northScore;
    }
    
    public Integer getNorthPenalty() {
        return northPenalty;
    }
    
    public void setNorthPenalty(Integer northPenalty) {
        this.northPenalty = northPenalty != null ? northPenalty : 0;
    }
    
    public Integer getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }
    
    public LocalDateTime getMatchTime() {
        return matchTime;
    }
    
    public void setMatchTime(LocalDateTime matchTime) {
        this.matchTime = matchTime;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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