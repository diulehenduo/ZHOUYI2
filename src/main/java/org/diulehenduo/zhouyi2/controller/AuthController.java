package org.diulehenduo.zhouyi2.controller;

import jakarta.validation.Valid;
import org.diulehenduo.zhouyi2.entity.User;
import org.diulehenduo.zhouyi2.model.request.LoginRequest;
import org.diulehenduo.zhouyi2.model.request.RegisterRequest;
import org.diulehenduo.zhouyi2.model.response.ApiResponse;
import org.diulehenduo.zhouyi2.model.response.AuthResponse;
import org.diulehenduo.zhouyi2.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ApiResponse.success("注册成功", response);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ApiResponse.success("登录成功", response);
        } catch (RuntimeException e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ApiResponse.error(400, "Refresh Token 不能为空");
            }
            AuthResponse response = authService.refreshToken(refreshToken);
            return ApiResponse.success("刷新成功", response);
        } catch (RuntimeException e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> getCurrentUser(Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication.getName());
            Map<String, Object> userInfo = Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "nickname", user.getNickname(),
                    "createdAt", user.getCreatedAt()
            );
            return ApiResponse.success(userInfo);
        } catch (RuntimeException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }
}
