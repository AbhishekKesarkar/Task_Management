package com.task_manager.dto.response;

import com.task_manager.entity.enums.Role;

public class JwtResponse {

    private String token;
    private String tokenType = "Bearer";
    private String username;
    private Role role;

    public JwtResponse() {
    }

    public JwtResponse(String token, String username, Role role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}