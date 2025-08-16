package com.rogister.mjcompetition.controller;

import com.rogister.mjcompetition.dto.ApiResponse;
import com.rogister.mjcompetition.dto.MatchResultCreateRequest;
import com.rogister.mjcompetition.entity.Competition;

import com.rogister.mjcompetition.entity.MatchResult;
import com.rogister.mjcompetition.entity.Player;
import com.rogister.mjcompetition.service.CompetitionService;
import com.rogister.mjcompetition.service.MatchResultService;
import com.rogister.mjcompetition.service.PlayerService;
import com.rogister.mjcompetition.repository.PlayerRoundStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match-results")
@CrossOrigin(origins = "*")
public class MatchResultController {
    
    @Autowired
    private MatchResultService matchResultService;
    
    @Autowired
    private CompetitionService competitionService;
    
    @Autowired
    private PlayerService playerService;
    
    @Autowired
    private PlayerRoundStatusRepository playerRoundStatusRepository;
    
    /**
     * 创建比赛成绩
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MatchResult>> createMatchResult(@RequestBody MatchResultCreateRequest request) {
        try {
            // 验证请求数据
            if (request.getCompetitionId() == null) {
                throw new RuntimeException("比赛ID不能为空");
            }
            if (request.getRoundNumber() == null) {
                throw new RuntimeException("轮次编号不能为空");
            }
            if (request.getMatchNumber() == null) {
                throw new RuntimeException("比赛编号不能为空");
            }
            
            // 验证并获取比赛对象
            Competition competition = competitionService.findById(request.getCompetitionId())
                    .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + request.getCompetitionId()));
            
            // 验证并获取四个玩家对象
            Player eastPlayer = playerService.findById(request.getEastPlayerId())
                    .orElseThrow(() -> new RuntimeException("东家玩家不存在，ID: " + request.getEastPlayerId()));
            
            Player southPlayer = playerService.findById(request.getSouthPlayerId())
                    .orElseThrow(() -> new RuntimeException("南家玩家不存在，ID: " + request.getSouthPlayerId()));
            
            Player westPlayer = playerService.findById(request.getWestPlayerId())
                    .orElseThrow(() -> new RuntimeException("西家玩家不存在，ID: " + request.getWestPlayerId()));
            
            Player northPlayer = playerService.findById(request.getNorthPlayerId())
                    .orElseThrow(() -> new RuntimeException("北家玩家不存在，ID: " + request.getNorthPlayerId()));
            
            // 验证四个玩家是否都有资格参加该轮次比赛
            validatePlayerRoundEligibility(eastPlayer, competition, request.getRoundNumber(), "东家");
            validatePlayerRoundEligibility(southPlayer, competition, request.getRoundNumber(), "南家");
            validatePlayerRoundEligibility(westPlayer, competition, request.getRoundNumber(), "西家");
            validatePlayerRoundEligibility(northPlayer, competition, request.getRoundNumber(), "北家");
            
            // 创建MatchResult对象
            MatchResult matchResult = new MatchResult();
            matchResult.setCompetition(competition);
            matchResult.setRoundNumber(request.getRoundNumber());  // 直接设置轮次编号
            matchResult.setMatchNumber(request.getMatchNumber());
            matchResult.setMatchName(request.getMatchName());
            
            // 验证关键数据是否正确设置
            if (matchResult.getCompetition() == null) {
                throw new RuntimeException("设置比赛信息失败，原始competition对象ID: " + (competition != null ? competition.getId() : "null"));
            }
            
            matchResult.setEastPlayer(eastPlayer);
            matchResult.setEastScore(request.getEastScore());
            matchResult.setEastPenalty(request.getEastPenalty() != null ? request.getEastPenalty() : 0);
            
            matchResult.setSouthPlayer(southPlayer);
            matchResult.setSouthScore(request.getSouthScore());
            matchResult.setSouthPenalty(request.getSouthPenalty() != null ? request.getSouthPenalty() : 0);
            
            matchResult.setWestPlayer(westPlayer);
            matchResult.setWestScore(request.getWestScore());
            matchResult.setWestPenalty(request.getWestPenalty() != null ? request.getWestPenalty() : 0);
            
            matchResult.setNorthPlayer(northPlayer);
            matchResult.setNorthScore(request.getNorthScore());
            matchResult.setNorthPenalty(request.getNorthPenalty() != null ? request.getNorthPenalty() : 0);
            
            matchResult.setRemarks(request.getRemarks());
            
            // 调用服务创建比赛成绩
            MatchResult created = matchResultService.createMatchResult(matchResult);
            return ResponseEntity.ok(ApiResponse.success("创建比赛成绩成功", created));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建比赛成绩失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取比赛成绩
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MatchResult>> getMatchResult(@PathVariable Long id) {
        try {
            return matchResultService.findById(id)
                    .map(result -> ResponseEntity.ok(ApiResponse.success(result)))
                    .orElse(ResponseEntity.ok(ApiResponse.error("比赛成绩不存在")));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取比赛成绩失败"));
        }
    }
    
    /**
     * 获取比赛成绩的详细排名和得分信息
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<MatchResultService.MatchResultDetail>> getMatchResultDetail(@PathVariable Long id) {
        try {
            MatchResultService.MatchResultDetail detail = matchResultService.getMatchResultDetail(id);
            return ResponseEntity.ok(ApiResponse.success(detail));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取比赛成绩详情失败"));
        }
    }
    
    /**
     * 计算并返回比赛排名
     */
    @GetMapping("/{id}/ranks")
    public ResponseEntity<ApiResponse<List<MatchResult.PlayerRank>>> getMatchRanks(@PathVariable Long id) {
        try {
            List<MatchResult.PlayerRank> ranks = matchResultService.calculateMatchRanks(id);
            return ResponseEntity.ok(ApiResponse.success(ranks));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取比赛排名失败"));
        }
    }
    
    /**
     * 更新比赛成绩
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MatchResult>> updateMatchResult(@PathVariable Long id, @RequestBody MatchResult matchResultDetails) {
        try {
            MatchResult updated = matchResultService.updateMatchResult(id, matchResultDetails);
            return ResponseEntity.ok(ApiResponse.success("更新比赛成绩成功", updated));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新比赛成绩失败"));
        }
    }
    
    /**
     * 删除比赛成绩
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMatchResult(@PathVariable Long id) {
        try {
            matchResultService.deleteMatchResult(id);
            return ResponseEntity.ok(ApiResponse.success("删除比赛成绩成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除比赛成绩失败"));
        }
    }
    
    /**
     * 获取玩家在某轮次的PT分数总和
     */
    @GetMapping("/player-pt-score")
    public ResponseEntity<ApiResponse<Double>> getPlayerPtScore(
            @RequestParam Long competitionId, 
            @RequestParam Integer roundNumber,
            @RequestParam Long playerId) {
        try {
            // 查找比赛
            Competition competition = competitionService.findById(competitionId)
                    .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
            
            // 查找玩家
            Player player = playerService.findById(playerId)
                    .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + playerId));
            
            // 获取玩家在该轮次的PT分数总和
            Double ptScoreSum = matchResultService.getPlayerPtScoreSum(competitionId, roundNumber, playerId);
            
            return ResponseEntity.ok(ApiResponse.success(ptScoreSum != null ? ptScoreSum : 0.0));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取玩家PT分数失败"));
        }
    }
    
    /**
     * 验证比赛成绩总分
     */
    @PostMapping("/validate-scores")
    public ResponseEntity<ApiResponse<Boolean>> validateScores(@RequestBody ScoreValidationRequest request) {
        try {
            boolean isValid = matchResultService.validateMatchResultScores(
                    request.getEastScore(), 
                    request.getSouthScore(), 
                    request.getWestScore(), 
                    request.getNorthScore()
            );
            return ResponseEntity.ok(ApiResponse.success(isValid));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("验证比赛成绩失败"));
        }
    }
    
    /**
     * 获取下一场比赛编号
     */
    @GetMapping("/next-match-number")
    public ResponseEntity<ApiResponse<Integer>> getNextMatchNumber(
            @RequestParam Long competitionId, 
            @RequestParam Long roundId) {
        try {
            // 这里需要根据competitionId和roundNumber查找对应的Competition对象
            // 为了简化，这里返回一个示例值
            return ResponseEntity.ok(ApiResponse.success(1));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取下一场比赛编号失败"));
        }
    }
    
    /**
     * 验证玩家是否有资格参加该轮次比赛
     */
    private void validatePlayerRoundEligibility(Player player, Competition competition, Integer roundNumber, String position) {
        // 查找玩家在该轮次的状态
        boolean eligible = playerRoundStatusRepository
                .findByPlayerAndCompetitionAndRoundNumber(player, competition, roundNumber)
                .map(status -> !status.getIsEliminated())  // 未被淘汰的玩家可以参赛
                .orElse(false);  // 如果没有找到状态记录，表示不能参赛
        
        if (!eligible) {
            throw new RuntimeException(position + "玩家(ID:" + player.getId() + ", 姓名:" + player.getNickname() + ")没有资格参加第" + roundNumber + "轮比赛");
        }
    }
    

    
    /**
     * 根据比赛和轮次查询比赛记录，按照时间从早到晚排序
     */
    @GetMapping("/round-records")
    public ResponseEntity<ApiResponse<List<MatchResult>>> getRoundRecordsByTime(
            @RequestParam Long competitionId, 
            @RequestParam Integer roundNumber,
            @RequestParam(defaultValue = "false") boolean includeMatchNumber) {
        try {
            // 查找比赛
            Competition competition = competitionService.findById(competitionId)
                    .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
            
            List<MatchResult> results;
            if (includeMatchNumber) {
                results = matchResultService.findByCompetitionAndRoundOrderByTimeAndNumber(competition, roundNumber);
            } else {
                results = matchResultService.findByCompetitionAndRoundOrderByTime(competition, roundNumber);
            }
            
            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取轮次记录失败"));
        }
    }
    
    /**
     * 根据比赛和轮次查询比赛记录，按照时间从早到晚排序（包含比赛编号作为第二排序条件）
     */
    @GetMapping("/round-records/detailed")
    public ResponseEntity<ApiResponse<List<MatchResult>>> getRoundRecordsByTimeAndNumber(
            @RequestParam Long competitionId, 
            @RequestParam Integer roundNumber) {
        try {
            // 查找比赛
            Competition competition = competitionService.findById(competitionId)
                    .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
            
            List<MatchResult> results = matchResultService.findByCompetitionAndRoundOrderByTimeAndNumber(competition, roundNumber);
            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取轮次记录失败"));
        }
    }
    
    /**
     * 根据比赛和轮次查询比赛记录，返回格式化的信息，按照时间从早到晚排序
     */
    @GetMapping("/round-records/formatted")
    public ResponseEntity<ApiResponse<List<RoundRecordDTO>>> getFormattedRoundRecords(
            @RequestParam Long competitionId, 
            @RequestParam Integer roundNumber) {
        try {
            // 查找比赛
            Competition competition = competitionService.findById(competitionId)
                    .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
            
            List<MatchResult> results = matchResultService.findByCompetitionAndRoundOrderByTime(competition, roundNumber);
            List<RoundRecordDTO> formattedResults = results.stream()
                    .map(RoundRecordDTO::fromMatchResult)
                    .toList();
            
            return ResponseEntity.ok(ApiResponse.success(formattedResults));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取轮次记录失败"));
        }
    }
    
    /**
     * 查询某一轮次下所有玩家的排名
     */
    @GetMapping("/round-rankings")
    public ResponseEntity<ApiResponse<List<MatchResultService.PlayerRoundRanking>>> getRoundPlayerRankings(
            @RequestParam Long competitionId, 
            @RequestParam Integer roundNumber) {
        try {
            // 查找比赛
            Competition competition = competitionService.findById(competitionId)
                    .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
            
            List<MatchResultService.PlayerRoundRanking> rankings = 
                    matchResultService.calculatePlayerRoundRankings(competition, roundNumber);
            
            // 设置排名
            for (int i = 0; i < rankings.size(); i++) {
                rankings.get(i).setRank(i + 1);
            }
            
            return ResponseEntity.ok(ApiResponse.success(rankings));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取轮次排名失败"));
        }
    }
    
    /**
     * 查询某一轮次下指定玩家的排名统计
     */
    @GetMapping("/round-player-ranking")
    public ResponseEntity<ApiResponse<MatchResultService.PlayerRoundRanking>> getPlayerRoundRanking(
            @RequestParam Long competitionId, 
            @RequestParam Integer roundNumber,
            @RequestParam Long playerId) {
        try {
            // 查找比赛和玩家
            Competition competition = competitionService.findById(competitionId)
                    .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
            
            Player player = playerService.findById(playerId)
                    .orElseThrow(() -> new RuntimeException("玩家不存在，ID: " + playerId));
            
            MatchResultService.PlayerRoundRanking ranking = 
                    matchResultService.calculatePlayerRoundRanking(competition, roundNumber, player);
            
            return ResponseEntity.ok(ApiResponse.success(ranking));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取玩家排名失败"));
        }
    }
    
    /**
     * 查询某一轮次下所有玩家的排名（格式化输出）
     */
    @GetMapping("/round-rankings/formatted")
    public ResponseEntity<ApiResponse<List<PlayerRankingDTO>>> getFormattedRoundPlayerRankings(
            @RequestParam Long competitionId, 
            @RequestParam Integer roundNumber) {
        try {
            // 查找比赛
            Competition competition = competitionService.findById(competitionId)
                    .orElseThrow(() -> new RuntimeException("比赛不存在，ID: " + competitionId));
            
            List<MatchResultService.PlayerRoundRanking> rankings = 
                    matchResultService.calculatePlayerRoundRankings(competition, roundNumber);
            
            // 设置排名
            for (int i = 0; i < rankings.size(); i++) {
                rankings.get(i).setRank(i + 1);
            }
            
            // 转换为DTO格式
            List<PlayerRankingDTO> formattedRankings = rankings.stream()
                    .map(PlayerRankingDTO::fromPlayerRoundRanking)
                    .toList();
            
            return ResponseEntity.ok(ApiResponse.success(formattedRankings));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取轮次排名失败"));
        }
    }
    
    /**
     * 内部类：分数验证请求
     */
    public static class ScoreValidationRequest {
        private Integer eastScore;
        private Integer southScore;
        private Integer westScore;
        private Integer northScore;
        
        // Getter和Setter方法
        public Integer getEastScore() { return eastScore; }
        public void setEastScore(Integer eastScore) { this.eastScore = eastScore; }
        
        public Integer getSouthScore() { return southScore; }
        public void setSouthScore(Integer southScore) { this.southScore = southScore; }
        
        public Integer getWestScore() { return westScore; }
        public void setWestScore(Integer westScore) { this.westScore = westScore; }
        
        public Integer getNorthScore() { return northScore; }
        public void setNorthScore(Integer northScore) { this.northScore = northScore; }
    }
    
    /**
     * 内部类：轮次比赛记录DTO
     */
    public static class RoundRecordDTO {
        private Long id;
        private Integer matchNumber;
        private String matchName;
        private String eastPlayerName;
        private Integer eastScore;
        private Integer eastPenalty;
        private String southPlayerName;
        private Integer southScore;
        private Integer southPenalty;
        private String westPlayerName;
        private Integer westScore;
        private Integer westPenalty;
        private String northPlayerName;
        private Integer northScore;
        private Integer northPenalty;
        private String matchTime;
        private String remarks;
        
        public static RoundRecordDTO fromMatchResult(MatchResult matchResult) {
            RoundRecordDTO dto = new RoundRecordDTO();
            dto.setId(matchResult.getId());
            dto.setMatchNumber(matchResult.getMatchNumber());
            dto.setMatchName(matchResult.getMatchName());
            
            // 设置四个方位的玩家信息
            if (matchResult.getEastPlayer() != null) {
                dto.setEastPlayerName(matchResult.getEastPlayer().getNickname());
                dto.setEastScore(matchResult.getEastScore());
                dto.setEastPenalty(matchResult.getEastPenalty());
            }
            
            if (matchResult.getSouthPlayer() != null) {
                dto.setSouthPlayerName(matchResult.getSouthPlayer().getNickname());
                dto.setSouthScore(matchResult.getSouthScore());
                dto.setSouthPenalty(matchResult.getSouthPenalty());
            }
            
            if (matchResult.getWestPlayer() != null) {
                dto.setWestPlayerName(matchResult.getWestPlayer().getNickname());
                dto.setWestScore(matchResult.getWestScore());
                dto.setWestPenalty(matchResult.getWestPenalty());
            }
            
            if (matchResult.getNorthPlayer() != null) {
                dto.setNorthPlayerName(matchResult.getNorthPlayer().getNickname());
                dto.setNorthScore(matchResult.getNorthScore());
                dto.setNorthPenalty(matchResult.getNorthPenalty());
            }
            
            if (matchResult.getMatchTime() != null) {
                dto.setMatchTime(matchResult.getMatchTime().toString());
            }
            
            dto.setRemarks(matchResult.getRemarks());
            
            return dto;
        }
        
        // Getter和Setter方法
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Integer getMatchNumber() { return matchNumber; }
        public void setMatchNumber(Integer matchNumber) { this.matchNumber = matchNumber; }
        
        public String getMatchName() { return matchName; }
        public void setMatchName(String matchName) { this.matchName = matchName; }
        
        public String getEastPlayerName() { return eastPlayerName; }
        public void setEastPlayerName(String eastPlayerName) { this.eastPlayerName = eastPlayerName; }
        
        public Integer getEastScore() { return eastScore; }
        public void setEastScore(Integer eastScore) { this.eastScore = eastScore; }
        
        public Integer getEastPenalty() { return eastPenalty; }
        public void setEastPenalty(Integer eastPenalty) { this.eastPenalty = eastPenalty; }
        
        public String getSouthPlayerName() { return southPlayerName; }
        public void setSouthPlayerName(String southPlayerName) { this.southPlayerName = southPlayerName; }
        
        public Integer getSouthScore() { return southScore; }
        public void setSouthScore(Integer southScore) { this.southScore = southScore; }
        
        public Integer getSouthPenalty() { return southPenalty; }
        public void setSouthPenalty(Integer southPenalty) { this.southPenalty = southPenalty; }
        
        public String getWestPlayerName() { return westPlayerName; }
        public void setWestPlayerName(String westPlayerName) { this.westPlayerName = westPlayerName; }
        
        public Integer getWestScore() { return westScore; }
        public void setWestScore(Integer westScore) { this.westScore = westScore; }
        
        public Integer getWestPenalty() { return westPenalty; }
        public void setWestPenalty(Integer westPenalty) { this.westPenalty = westPenalty; }
        
        public String getNorthPlayerName() { return northPlayerName; }
        public void setNorthPlayerName(String northPlayerName) { this.northPlayerName = northPlayerName; }
        
        public Integer getNorthScore() { return northScore; }
        public void setNorthScore(Integer northScore) { this.northScore = northScore; }
        
        public Integer getNorthPenalty() { return northPenalty; }
        public void setNorthPenalty(Integer northPenalty) { this.northPenalty = northPenalty; }
        
        public String getMatchTime() { return matchTime; }
        public void setMatchTime(String matchTime) { this.matchTime = matchTime; }
        
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }
    
    /**
     * 内部类：玩家排名DTO
     */
    public static class PlayerRankingDTO {
        private Integer rank;                    // 排名
        private Long playerId;                   // 玩家ID
        private String playerName;               // 玩家姓名
        private String mahjongId;                // 麻将ID
        private Double totalActualPoints;        // 实际得分总和
        private Integer totalOriginalScore;      // 原始得分总和
        private Integer totalPenalty;            // 罚分总和
        private Integer matchCount;              // 比赛场数
        private Integer firstPlaceCount;         // 第一名次数
        private Integer secondPlaceCount;        // 第二名次数
        private Integer thirdPlaceCount;         // 第三名次数
        private Integer fourthPlaceCount;        // 第四名次数
        private Double averagePosition;          // 平均顺位
        
        public static PlayerRankingDTO fromPlayerRoundRanking(MatchResultService.PlayerRoundRanking ranking) {
            PlayerRankingDTO dto = new PlayerRankingDTO();
            dto.setRank(ranking.getRank());
            dto.setPlayerId(ranking.getPlayer().getId());
            dto.setPlayerName(ranking.getPlayerName());
            dto.setMahjongId(ranking.getMahjongId());
            dto.setTotalActualPoints(ranking.getTotalActualPoints());
            dto.setTotalOriginalScore(ranking.getTotalOriginalScore());
            dto.setTotalPenalty(ranking.getTotalPenalty());
            dto.setMatchCount(ranking.getMatchCount());
            dto.setFirstPlaceCount(ranking.getFirstPlaceCount());
            dto.setSecondPlaceCount(ranking.getSecondPlaceCount());
            dto.setThirdPlaceCount(ranking.getThirdPlaceCount());
            dto.setFourthPlaceCount(ranking.getFourthPlaceCount());
            dto.setAveragePosition(ranking.getAveragePosition());
            return dto;
        }
        
        // Getter和Setter方法
        public Integer getRank() { return rank; }
        public void setRank(Integer rank) { this.rank = rank; }
        
        public Long getPlayerId() { return playerId; }
        public void setPlayerId(Long playerId) { this.playerId = playerId; }
        
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        
        public String getMahjongId() { return mahjongId; }
        public void setMahjongId(String mahjongId) { this.mahjongId = mahjongId; }
        
        public Double getTotalActualPoints() { return totalActualPoints; }
        public void setTotalActualPoints(Double totalActualPoints) { this.totalActualPoints = totalActualPoints; }
        
        public Integer getTotalOriginalScore() { return totalOriginalScore; }
        public void setTotalOriginalScore(Integer totalOriginalScore) { this.totalOriginalScore = totalOriginalScore; }
        
        public Integer getTotalPenalty() { return totalPenalty; }
        public void setTotalPenalty(Integer totalPenalty) { this.totalPenalty = totalPenalty; }
        
        public Integer getMatchCount() { return matchCount; }
        public void setMatchCount(Integer matchCount) { this.matchCount = matchCount; }
        
        public Integer getFirstPlaceCount() { return firstPlaceCount; }
        public void setFirstPlaceCount(Integer firstPlaceCount) { this.firstPlaceCount = firstPlaceCount; }
        
        public Integer getSecondPlaceCount() { return secondPlaceCount; }
        public void setSecondPlaceCount(Integer secondPlaceCount) { this.secondPlaceCount = secondPlaceCount; }
        
        public Integer getThirdPlaceCount() { return thirdPlaceCount; }
        public void setThirdPlaceCount(Integer thirdPlaceCount) { this.thirdPlaceCount = thirdPlaceCount; }
        
        public Integer getFourthPlaceCount() { return fourthPlaceCount; }
        public void setFourthPlaceCount(Integer fourthPlaceCount) { this.fourthPlaceCount = fourthPlaceCount; }
        
        public Double getAveragePosition() { return averagePosition; }
        public void setAveragePosition(Double averagePosition) { this.averagePosition = averagePosition; }
    }
}
