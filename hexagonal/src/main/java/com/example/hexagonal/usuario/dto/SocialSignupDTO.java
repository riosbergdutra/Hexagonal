package com.example.hexagonal.usuario.dto;

public class SocialSignupDTO {
    private String nome;
    private String email;
    private String socialProvider; // exemplo: "GOOGLE", "FACEBOOK"
    private String socialId; // identificador da rede social

    public SocialSignupDTO() {}

    public SocialSignupDTO(String nome, String email, String socialProvider, String socialId) {
        this.nome = nome;
        this.email = email;
        this.socialProvider = socialProvider;
        this.socialId = socialId;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSocialProvider() {
        return socialProvider;
    }
    public void setSocialProvider(String socialProvider) {
        this.socialProvider = socialProvider;
    }
    public String getSocialId() {
        return socialId;
    }
    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }
}
