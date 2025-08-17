package com.rogister.mjcompetition.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "管理员修改密码请求")
public class AdminChangePasswordRequest {

    @Schema(description = "旧密码", example = "oldpassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;

    @Schema(description = "新密码", example = "newpassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
