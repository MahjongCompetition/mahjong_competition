package com.rogister.mjcompetition.repository;

import com.rogister.mjcompetition.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    
    /**
     * 根据团队ID查找所有成员
     */
    List<TeamMember> findByTeamId(Long teamId);
    
    /**
     * 根据团队ID查找激活的成员
     */
    List<TeamMember> findByTeamIdAndIsActiveTrue(Long teamId);
    
    /**
     * 根据玩家ID查找所有团队关系
     */
    @Query("SELECT tm FROM TeamMember tm WHERE tm.player.id = :playerId")
    List<TeamMember> findByPlayerId(@Param("playerId") Long playerId);
    
    /**
     * 根据玩家ID查找激活的团队关系
     */
    @Query("SELECT tm FROM TeamMember tm WHERE tm.player.id = :playerId AND tm.isActive = true")
    List<TeamMember> findByPlayerIdAndIsActiveTrue(@Param("playerId") Long playerId);
    
    /**
     * 根据团队ID和玩家ID查找成员关系
     */
    @Query("SELECT tm FROM TeamMember tm WHERE tm.teamId = :teamId AND tm.player.id = :playerId")
    Optional<TeamMember> findByTeamIdAndPlayerId(@Param("teamId") Long teamId, @Param("playerId") Long playerId);
    
    /**
     * 根据团队ID和玩家ID查找激活的成员关系
     */
    @Query("SELECT tm FROM TeamMember tm WHERE tm.teamId = :teamId AND tm.player.id = :playerId AND tm.isActive = true")
    Optional<TeamMember> findByTeamIdAndPlayerIdAndIsActiveTrue(@Param("teamId") Long teamId, @Param("playerId") Long playerId);
    
    /**
     * 检查玩家是否已经是团队成员
     */
    @Query("SELECT COUNT(tm) > 0 FROM TeamMember tm WHERE tm.teamId = :teamId AND tm.player.id = :playerId AND tm.isActive = true")
    boolean existsByTeamIdAndPlayerIdAndIsActiveTrue(@Param("teamId") Long teamId, @Param("playerId") Long playerId);
    
    /**
     * 统计团队的当前成员数
     */
    @Query("SELECT COUNT(tm) FROM TeamMember tm WHERE tm.teamId = :teamId AND tm.isActive = true")
    long countActiveMembersByTeamId(@Param("teamId") Long teamId);
}
