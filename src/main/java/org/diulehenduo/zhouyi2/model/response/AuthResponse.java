package org.diulehenduo.zhouyi2.model.response;

/**
 * 认证响应 DTO
 */
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String username;
    private String nickname;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, String username, String nickname) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.nickname = nickname;
    }

    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
