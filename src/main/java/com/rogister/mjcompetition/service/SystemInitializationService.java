package com.rogister.mjcompetition.service;

import com.rogister.mjcompetition.entity.Admin;
import com.rogister.mjcompetition.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SystemInitializationService implements CommandLineRunner {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    /**
     * 应用启动时执行初始化
     */
    @Override
    public void run(String... args) throws Exception {
        initializeSuperAdmin();
    }
    
    /**
     * 初始化超级管理员账号
     */
    public void initializeSuperAdmin() {
        // 检查是否已存在超级管理员
        List<Admin> superAdmins = adminRepository.findByRole("SUPER_ADMIN");
        
        if (superAdmins.isEmpty()) {
            System.out.println("正在初始化默认超级管理员账号...");
            
            Admin superAdmin = new Admin();
            superAdmin.setUsername("admin");
            superAdmin.setPassword(passwordEncoder.encode("admin"));
            superAdmin.setEmail("rogisterzs@gmail.com");
            superAdmin.setName("系统超级管理员");
            superAdmin.setRole("SUPER_ADMIN");
            superAdmin.setIsActive(true);
            superAdmin.setCreatedAt(LocalDateTime.now());
            superAdmin.setUpdatedAt(LocalDateTime.now());
            
            try {
                adminRepository.save(superAdmin);
                System.out.println("默认超级管理员账号初始化成功！");
                System.out.println("用户名: admin");
                System.out.println("密码: admin");
                System.out.println("邮箱: rogisterzs@gmail.com");
                System.out.println("请尽快登录系统并修改默认密码！");
            } catch (Exception e) {
                System.err.println("初始化超级管理员账号失败: " + e.getMessage());
            }
        } else {
            System.out.println("超级管理员账号已存在，跳过初始化。");
        }
    }
    
    /**
     * 手动创建超级管理员账号（用于API调用）
     */
    public Admin createSuperAdmin(String username, String password, String email, String name) {
        // 检查用户名是否已存在
        if (adminRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (adminRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }
        
        Admin superAdmin = new Admin();
        superAdmin.setUsername(username);
        superAdmin.setPassword(passwordEncoder.encode(password));
        superAdmin.setEmail(email);
        superAdmin.setName(name != null ? name : "超级管理员");
        superAdmin.setRole("SUPER_ADMIN");
        superAdmin.setIsActive(true);
        superAdmin.setCreatedAt(LocalDateTime.now());
        superAdmin.setUpdatedAt(LocalDateTime.now());
        
        return adminRepository.save(superAdmin);
    }
    
    /**
     * 检查是否存在超级管理员
     */
    public boolean hasSuperAdmin() {
        List<Admin> superAdmins = adminRepository.findByRole("SUPER_ADMIN");
        return !superAdmins.isEmpty();
    }
}
