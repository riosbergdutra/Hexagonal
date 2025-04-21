package com.example.hexagonal.usuario.dto;

public class PasswordResetRequestDTO {
    private String email;

    public PasswordResetRequestDTO() {}

    public PasswordResetRequestDTO(String email) {
        this.email = email;
    }

    // Getters e Setters
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
