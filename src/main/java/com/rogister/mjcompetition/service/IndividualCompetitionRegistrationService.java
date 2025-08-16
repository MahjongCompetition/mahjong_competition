package com.rogister.mjcompetition.service;

import com.rogister.mjcompetition.entity.Competition;
import com.rogister.mjcompetition.entity.IndividualCompetitionRegistration;
import com.rogister.mjcompetition.entity.Player;
import com.rogister.mjcompetition.repository.IndividualCompetitionRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IndividualCompetitionRegistrationService {
    
    @Autowired
    private IndividualCompetitionRegistrationRepository registrationRepository;
    
    /**
     * 玩家报名个人赛
     */
    public IndividualCompetitionRegistration registerForCompetition(Competition competition, Player player) {
        // 检查比赛类型是否为个人赛
        if (competition.getCompetitionType() != Competition.CompetitionType.INDIVIDUAL) {
            throw new RuntimeException("该比赛不是个人赛");
        }
        
        // 检查是否已经报名
        if (registrationRepository.existsByCompetitionAndPlayer(competition, player)) {
            throw new RuntimeException("您已经报名了该比赛");
        }
        
        IndividualCompetitionRegistration registration = new IndividualCompetitionRegistration(competition, player);
        return registrationRepository.save(registration);
    }
    
    /**
     * 根据ID查找报名记录
     */
    public Optional<IndividualCompetitionRegistration> findById(Long id) {
        return registrationRepository.findById(id);
    }
    
    /**
     * 根据比赛和玩家查找报名记录
     */
    public Optional<IndividualCompetitionRegistration> findByCompetitionAndPlayer(Competition competition, Player player) {
        return registrationRepository.findByCompetitionAndPlayer(competition, player);
    }
    
    /**
     * 根据比赛查找所有报名记录
     */
    public List<IndividualCompetitionRegistration> findByCompetition(Competition competition) {
        return registrationRepository.findByCompetition(competition);
    }
    
    /**
     * 根据比赛和状态查找报名记录
     */
    public List<IndividualCompetitionRegistration> findByCompetitionAndStatus(Competition competition, IndividualCompetitionRegistration.RegistrationStatus status) {
        return registrationRepository.findByCompetitionAndStatus(competition, status);
    }
    
    /**
     * 根据玩家查找所有报名记录
     */
    public List<IndividualCompetitionRegistration> findByPlayer(Player player) {
        return registrationRepository.findByPlayer(player);
    }
    
    /**
     * 更新报名状态
     */
    public IndividualCompetitionRegistration updateRegistrationStatus(Long registrationId, IndividualCompetitionRegistration.RegistrationStatus status) {
        IndividualCompetitionRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("报名记录不存在，ID: " + registrationId));
        
        registration.setStatus(status);
        return registrationRepository.save(registration);
    }
    
    /**
     * 玩家退赛
     */
    public IndividualCompetitionRegistration withdrawFromCompetition(Long registrationId) {
        IndividualCompetitionRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("报名记录不存在，ID: " + registrationId));
        
        registration.setStatus(IndividualCompetitionRegistration.RegistrationStatus.WITHDRAWN);
        return registrationRepository.save(registration);
    }
    
    /**
     * 删除报名记录
     */
    public void deleteRegistration(Long id) {
        if (!registrationRepository.existsById(id)) {
            throw new RuntimeException("报名记录不存在，ID: " + id);
        }
        registrationRepository.deleteById(id);
    }
    
    /**
     * 根据比赛和状态统计报名人数
     */
    public long countByCompetitionAndStatus(Competition competition, IndividualCompetitionRegistration.RegistrationStatus status) {
        return registrationRepository.countByCompetitionAndStatus(competition, status);
    }
    
    /**
     * 检查玩家是否已报名某场比赛
     */
    public boolean isPlayerRegistered(Competition competition, Player player) {
        return registrationRepository.existsByCompetitionAndPlayer(competition, player);
    }
} 