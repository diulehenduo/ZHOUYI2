package org.diulehenduo.zhouyi2.service;

import org.diulehenduo.zhouyi2.entity.User;
import org.diulehenduo.zhouyi2.model.request.LoginRequest;
import org.diulehenduo.zhouyi2.model.request.RegisterRequest;
import org.diulehenduo.zhouyi2.model.response.AuthResponse;
import org.diulehenduo.zhouyi2.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * 用户注册
     */
    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEnabled(true);

        userRepository.save(user);

        // 生成 Token
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername(), false);

        return new AuthResponse(accessToken, refreshToken, user.getUsername(), user.getNickname());
    }

    /**
     * 用户登录
     */
    public AuthResponse login(LoginRequest request) {
        // 查找用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户是否启用
        if (!user.getEnabled()) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成 Token
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername(), request.isRememberMe());

        return new AuthResponse(accessToken, refreshToken, user.getUsername(), user.getNickname());
    }

    /**
     * 刷新 Token
     */
    public AuthResponse refreshToken(String refreshToken) {
        // 验证 Refresh Token
        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh Token 无效或已过期");
        }

        // 从 Refresh Token 中提取用户名
        String username = jwtService.extractUsername(refreshToken);

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 生成新的 Token
        String newAccessToken = jwtService.generateAccessToken(user.getUsername());
        String newRefreshToken = jwtService.generateRefreshToken(user.getUsername(), false);

        return new AuthResponse(newAccessToken, newRefreshToken, user.getUsername(), user.getNickname());
    }

    /**
     * 获取当前用户信息
     */
    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}
