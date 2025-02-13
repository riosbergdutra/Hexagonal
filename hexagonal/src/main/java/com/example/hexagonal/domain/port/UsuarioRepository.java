package com.example.hexagonal.domain.port;

import com.example.hexagonal.domain.Usuario;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
}