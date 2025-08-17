package com.rogister.mjcompetition.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "超级管理员初始化请求")
public class SuperAdminInitRequest {

    @Schema(description = "用户名", example = "superadmin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "密码", example = "superpassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "管理员姓名", example = "超级管理员", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "邮箱地址", example = "superadmin@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
