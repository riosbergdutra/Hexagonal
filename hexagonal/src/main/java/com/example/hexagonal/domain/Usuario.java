package com.example.hexagonal.domain;


public class Usuario {
    private Long idUsuario;
    private String nome;
    private String email;

    // Construtor padrão
    public Usuario() {
    }

    // Construtor com todos os atributos
    public Usuario(Long idUsuario, String nome, String email) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
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



}
