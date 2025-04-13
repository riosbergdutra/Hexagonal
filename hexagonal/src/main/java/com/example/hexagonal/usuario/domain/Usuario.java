package com.example.hexagonal.usuario.domain;

import com.example.hexagonal.usuario.enums.Role;

public class Usuario {
    private Long idUsuario;
    private String nome;
    private String email;
    private String senha;
    private Role role;
    private boolean ativo;

    public Usuario() {
    }

    public Usuario(Long idUsuario, String nome, String email, String senha, Role role, boolean ativo) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.ativo = ativo;
    }

    // Getters e Setters
    public Long getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
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
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public com.example.hexagonal.usuario.enums.Role getRole() {
        return role;
    }
    public void setRole(com.example.hexagonal.usuario.enums.Role role) {
        this.role = role;
    }
    public boolean isAtivo() {
        return ativo;
    }
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
