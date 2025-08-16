package com.rogister.mjcompetition.service;

import com.rogister.mjcompetition.entity.Competition;
import com.rogister.mjcompetition.entity.CompetitionRound;
import com.rogister.mjcompetition.repository.CompetitionRoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CompetitionRoundService {
    
    @Autowired
    private CompetitionRoundRepository competitionRoundRepository;
    
    /**
     * 创建新轮次
     */
    public CompetitionRound createRound(Competition competition, Integer roundNumber, String roundName) {
        // 检查轮次编号是否已存在
        if (competitionRoundRepository.findByCompetitionAndRoundNumber(competition, roundNumber).isPresent()) {
            throw new RuntimeException("轮次编号已存在: " + roundNumber);
        }
        
        CompetitionRound round = new CompetitionRound(competition, roundNumber, roundName);
        return competitionRoundRepository.save(round);
    }
    
    /**
     * 根据ID查找轮次
     */
    public Optional<CompetitionRound> findById(Long id) {
        return competitionRoundRepository.findById(id);
    }
    
    /**
     * 根据比赛和轮次编号查找轮次
     */
    public Optional<CompetitionRound> findByCompetitionAndRoundNumber(Competition competition, Integer roundNumber) {
        return competitionRoundRepository.findByCompetitionAndRoundNumber(competition, roundNumber);
    }
    
    /**
     * 根据比赛查找所有轮次
     */
    public List<CompetitionRound> findByCompetition(Competition competition) {
        return competitionRoundRepository.findByCompetitionOrderByRoundNumber(competition);
    }
    
    /**
     * 根据比赛查找当前活跃轮次
     */
    public Optional<CompetitionRound> findCurrentActiveRound(Competition competition) {
        return competitionRoundRepository.findByCompetitionAndIsActiveTrue(competition);
    }
    
    /**
     * 根据比赛查找下一个轮次
     */
    public Optional<CompetitionRound> findNextRound(Competition competition, Integer currentRoundNumber) {
        return competitionRoundRepository.findByCompetitionAndRoundNumberGreaterThanOrderByRoundNumber(competition, currentRoundNumber);
    }
    
    /**
     * 根据比赛查找最大轮次编号
     */
    public Optional<Integer> findMaxRoundNumber(Competition competition) {
        return competitionRoundRepository.findTopByCompetitionOrderByRoundNumberDesc(competition);
    }
    
    /**
     * 激活轮次
     */
    public CompetitionRound activateRound(Long roundId) {
        CompetitionRound round = competitionRoundRepository.findById(roundId)
                .orElseThrow(() -> new RuntimeException("轮次不存在，ID: " + roundId));
        
        // 将其他轮次设为非活跃
        List<CompetitionRound> allRounds = competitionRoundRepository.findByCompetitionOrderByRoundNumber(round.getCompetition());
        for (CompetitionRound otherRound : allRounds) {
            if (!otherRound.getId().equals(roundId)) {
                otherRound.setIsActive(false);
                competitionRoundRepository.save(otherRound);
            }
        }
        
        round.setIsActive(true);
        round.setStartTime(LocalDateTime.now());
        return competitionRoundRepository.save(round);
    }
    
    /**
     * 结束轮次
     */
    public CompetitionRound endRound(Long roundId) {
        CompetitionRound round = competitionRoundRepository.findById(roundId)
                .orElseThrow(() -> new RuntimeException("轮次不存在，ID: " + roundId));
        
        round.setIsActive(false);
        round.setEndTime(LocalDateTime.now());
        return competitionRoundRepository.save(round);
    }
    
    /**
     * 更新轮次信息
     */
    public CompetitionRound updateRound(Long id, CompetitionRound roundDetails) {
        CompetitionRound round = competitionRoundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("轮次不存在，ID: " + id));
        
        round.setRoundName(roundDetails.getRoundName());
        round.setStartTime(roundDetails.getStartTime());
        round.setEndTime(roundDetails.getEndTime());
        
        return competitionRoundRepository.save(round);
    }
    
    /**
     * 删除轮次
     */
    public void deleteRound(Long id) {
        if (!competitionRoundRepository.existsById(id)) {
            throw new RuntimeException("轮次不存在，ID: " + id);
        }
        competitionRoundRepository.deleteById(id);
    }
} 