package com.rogister.mjcompetition.repository;

import com.rogister.mjcompetition.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    /**
     * 根据团队编号查找团队
     */
    Optional<Team> findByTeamCode(String teamCode);
    
    /**
     * 根据队长ID查找团队
     */
    List<Team> findByCaptainId(Long captainId);
    
    /**
     * 根据队长ID查找激活的团队
     */
    List<Team> findByCaptainIdAndIsActiveTrue(Long captainId);
    
    /**
     * 查找所有激活的团队
     */
    List<Team> findByIsActiveTrue();
    
    /**
     * 检查团队编号是否存在
     */
    boolean existsByTeamCode(String teamCode);
    
    /**
     * 根据团队名称模糊搜索
     */
    @Query("SELECT t FROM Team t WHERE t.teamName LIKE %:teamName% AND t.isActive = true")
    List<Team> findByTeamNameContaining(@Param("teamName") String teamName);
}
