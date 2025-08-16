package com.rogister.mjcompetition.service;

import com.rogister.mjcompetition.entity.Admin;
import com.rogister.mjcompetition.repository.AdminRepository;
import com.rogister.mjcompetition.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 管理员登录
     */
    public String login(String identifier, String password) {
        // 支持用户名或邮箱登录
        Optional<Admin> adminOpt = adminRepository.findByUsernameOrEmail(identifier);
        
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (!admin.getIsActive()) {
                throw new RuntimeException("管理员账户已被禁用");
            }
            
            if (passwordEncoder.matches(password, admin.getPassword())) {
                // 更新最后登录时间
                admin.setLastLoginTime(LocalDateTime.now());
                adminRepository.save(admin);
                
                // 生成包含角色信息的token
                return jwtUtil.generateTokenWithRole(admin.getUsername(), "ADMIN", admin.getRole());
            } else {
                throw new RuntimeException("密码错误");
            }
        } else {
            throw new RuntimeException("管理员不存在");
        }
    }
    
    /**
     * 创建管理员
     */
    public Admin createAdmin(Admin admin) {
        // 检查用户名是否已存在
        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 加密密码
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        
        // 设置默认值
        if (admin.getRole() == null || admin.getRole().isEmpty()) {
            admin.setRole("ADMIN");
        }
        if (admin.getIsActive() == null) {
            admin.setIsActive(true);
        }
        
        return adminRepository.save(admin);
    }
    
    /**
     * 根据用户名查找管理员
     */
    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }
    
    /**
     * 根据邮箱查找管理员
     */
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
    
    /**
     * 获取所有管理员
     */
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
    
    /**
     * 获取所有激活的管理员
     */
    public List<Admin> getActiveAdmins() {
        return adminRepository.findByIsActiveTrue();
    }
    
    /**
     * 根据ID获取管理员
     */
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }
    
    /**
     * 更新管理员信息
     */
    public Admin updateAdmin(Long id, Admin adminDetails) {
        Optional<Admin> adminOpt = adminRepository.findById(id);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            
            // 检查用户名唯一性（排除自己）
            if (!admin.getUsername().equals(adminDetails.getUsername()) && 
                adminRepository.existsByUsername(adminDetails.getUsername())) {
                throw new RuntimeException("用户名已存在");
            }
            
            // 检查邮箱唯一性（排除自己）
            if (!admin.getEmail().equals(adminDetails.getEmail()) && 
                adminRepository.existsByEmail(adminDetails.getEmail())) {
                throw new RuntimeException("邮箱已存在");
            }
            
            admin.setUsername(adminDetails.getUsername());
            admin.setEmail(adminDetails.getEmail());
            admin.setName(adminDetails.getName());
            admin.setRole(adminDetails.getRole());
            admin.setIsActive(adminDetails.getIsActive());
            
            // 如果提供了新密码，则更新密码
            if (adminDetails.getPassword() != null && !adminDetails.getPassword().isEmpty()) {
                admin.setPassword(passwordEncoder.encode(adminDetails.getPassword()));
            }
            
            return adminRepository.save(admin);
        } else {
            throw new RuntimeException("管理员不存在");
        }
    }
    
    /**
     * 删除管理员
     */
    public void deleteAdmin(Long id) {
        if (adminRepository.existsById(id)) {
            adminRepository.deleteById(id);
        } else {
            throw new RuntimeException("管理员不存在");
        }
    }
    
    /**
     * 激活/禁用管理员
     */
    public Admin toggleAdminStatus(Long id) {
        Optional<Admin> adminOpt = adminRepository.findById(id);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            admin.setIsActive(!admin.getIsActive());
            return adminRepository.save(admin);
        } else {
            throw new RuntimeException("管理员不存在");
        }
    }
    
    /**
     * 修改管理员密码
     */
    public void changePassword(Long id, String oldPassword, String newPassword) {
        Optional<Admin> adminOpt = adminRepository.findById(id);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            
            if (passwordEncoder.matches(oldPassword, admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(newPassword));
                adminRepository.save(admin);
            } else {
                throw new RuntimeException("原密码错误");
            }
        } else {
            throw new RuntimeException("管理员不存在");
        }
    }
}
