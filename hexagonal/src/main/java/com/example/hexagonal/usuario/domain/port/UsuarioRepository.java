package com.example.hexagonal.usuario.domain.port;

import com.example.hexagonal.usuario.domain.Usuario;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmail(String email);
    void delete(Usuario usuario);
}
