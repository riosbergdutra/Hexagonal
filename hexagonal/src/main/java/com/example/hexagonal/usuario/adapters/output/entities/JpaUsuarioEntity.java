package com.example.hexagonal.usuario.adapters.output.entities;

import com.example.hexagonal.usuario.domain.Usuario;
import com.example.hexagonal.usuario.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idUsuario")
public class JpaUsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idUsuario;
    
    private String nome;
    private String email;
    private String senha;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    private boolean ativo;

    public JpaUsuarioEntity(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.role = usuario.getRole();
        this.ativo = usuario.isAtivo();
    }
}
