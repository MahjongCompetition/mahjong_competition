package com.rogister.mjcompetition.controller;

import com.rogister.mjcompetition.dto.ApiResponse;
import com.rogister.mjcompetition.dto.LoginRequest;
import com.rogister.mjcompetition.dto.AdminLoginResponse;
import com.rogister.mjcompetition.entity.Admin;
import com.rogister.mjcompetition.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@Tag(name = "管理员功能", description = "管理员登录、创建、查询、更新、删除等功能")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AdminLoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = adminService.login(loginRequest.getUsername(), loginRequest.getPassword());
            Optional<Admin> adminOpt = adminService.findByUsername(loginRequest.getUsername());

            if (adminOpt.isEmpty()) {
                // 可能是用邮箱登录的，再试试邮箱
                adminOpt = adminService.findByEmail(loginRequest.getUsername());
            }

            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                AdminLoginResponse loginResponse = new AdminLoginResponse(
                        token,
                        admin.getUsername(),
                        admin.getName(),
                        admin.getRole(),
                        admin.getEmail());
                return ResponseEntity.ok(ApiResponse.success("管理员登录成功", loginResponse));
            } else {
                return ResponseEntity.ok(ApiResponse.error("登录失败"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("服务器内部错误"));
        }
    }

    /**
     * 创建管理员
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Admin>> createAdmin(@RequestBody Admin admin) {
        try {
            Admin createdAdmin = adminService.createAdmin(admin);
            // 清除密码信息
            createdAdmin.setPassword(null);
            return ResponseEntity.ok(ApiResponse.success("管理员创建成功", createdAdmin));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("服务器内部错误"));
        }
    }

    /**
     * 获取所有管理员
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Admin>>> getAllAdmins() {
        try {
            List<Admin> admins = adminService.getAllAdmins();
            // 清除所有管理员的密码信息
            admins.forEach(admin -> admin.setPassword(null));
            return ResponseEntity.ok(ApiResponse.success("获取管理员列表成功", admins));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取管理员列表失败"));
        }
    }

    /**
     * 获取激活的管理员
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Admin>>> getActiveAdmins() {
        try {
            List<Admin> admins = adminService.getActiveAdmins();
            // 清除所有管理员的密码信息
            admins.forEach(admin -> admin.setPassword(null));
            return ResponseEntity.ok(ApiResponse.success("获取激活管理员列表成功", admins));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取激活管理员列表失败"));
        }
    }

    /**
     * 根据ID获取管理员
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Admin>> getAdminById(@PathVariable Long id) {
        try {
            Optional<Admin> adminOpt = adminService.getAdminById(id);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                // 清除密码信息
                admin.setPassword(null);
                return ResponseEntity.ok(ApiResponse.success("获取管理员信息成功", admin));
            } else {
                return ResponseEntity.ok(ApiResponse.error("管理员不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取管理员信息失败"));
        }
    }

    /**
     * 更新管理员信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Admin>> updateAdmin(@PathVariable Long id, @RequestBody Admin admin) {
        try {
            Admin updatedAdmin = adminService.updateAdmin(id, admin);
            // 清除密码信息
            updatedAdmin.setPassword(null);
            return ResponseEntity.ok(ApiResponse.success("管理员信息更新成功", updatedAdmin));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新管理员信息失败"));
        }
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAdmin(@PathVariable Long id) {
        try {
            adminService.deleteAdmin(id);
            return ResponseEntity.ok(ApiResponse.success("管理员删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除管理员失败"));
        }
    }

    /**
     * 激活/禁用管理员
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Admin>> toggleAdminStatus(@PathVariable Long id) {
        try {
            Admin admin = adminService.toggleAdminStatus(id);
            // 清除密码信息
            admin.setPassword(null);
            return ResponseEntity.ok(ApiResponse.success("管理员状态切换成功", admin));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("切换管理员状态失败"));
        }
    }

    /**
     * 修改管理员密码
     */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@PathVariable Long id,
            @RequestBody Map<String, String> passwordRequest) {
        try {
            String oldPassword = passwordRequest.get("oldPassword");
            String newPassword = passwordRequest.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.ok(ApiResponse.error("旧密码和新密码不能为空"));
            }

            adminService.changePassword(id, oldPassword, newPassword);
            return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("修改密码失败"));
        }
    }

    /**
     * 初始化超级管理员账号（仅当系统中不存在超级管理员时可用）
     */
    @PostMapping("/initialize-super-admin")
    public ResponseEntity<ApiResponse<Admin>> initializeSuperAdmin(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String email = request.get("email");
            String name = request.get("name");

            if (username == null || password == null || email == null) {
                return ResponseEntity.ok(ApiResponse.error("用户名、密码和邮箱不能为空"));
            }

            Admin superAdmin = adminService.initializeSuperAdmin(username, password, email, name);
            // 清除密码信息
            superAdmin.setPassword(null);
            return ResponseEntity.ok(ApiResponse.success("超级管理员初始化成功", superAdmin));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("初始化超级管理员失败"));
        }
    }

    /**
     * 检查是否存在超级管理员
     */
    @GetMapping("/has-super-admin")
    public ResponseEntity<ApiResponse<Boolean>> hasSuperAdmin() {
        try {
            boolean hasSuperAdmin = adminService.hasSuperAdmin();
            return ResponseEntity.ok(ApiResponse.success("查询成功", hasSuperAdmin));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询失败"));
        }
    }
}
