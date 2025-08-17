package com.rogister.mjcompetition.repository.admin;

import com.rogister.mjcompetition.entity.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    /**
     * 根据用户名查找管理员
     */
    Optional<Admin> findByUsername(String username);
    
    /**
     * 根据邮箱查找管理员
     */
    Optional<Admin> findByEmail(String email);
    
    /**
     * 根据用户名或邮箱查找管理员
     */
    @Query("SELECT a FROM Admin a WHERE a.username = :identifier OR a.email = :identifier")
    Optional<Admin> findByUsernameOrEmail(@Param("identifier") String identifier);
    
    /**
     * 查找所有激活的管理员
     */
    List<Admin> findByIsActiveTrue();
    
    /**
     * 根据角色查找管理员
     */
    List<Admin> findByRole(String role);
    
    /**
     * 根据角色和激活状态查找管理员
     */
    List<Admin> findByRoleAndIsActive(String role, Boolean isActive);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
}
