package com.example.hexagonal.application.service;


import com.example.hexagonal.domain.Usuario;
import com.example.hexagonal.domain.port.UsuarioRepository;
import com.example.hexagonal.dto.UsuarioDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioDTO createUser(UsuarioDTO usuarioDTO) {
Usuario usuario = new Usuario(null, usuarioDTO.getNome(), usuarioDTO.getEmail());
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return new UsuarioDTO(savedUsuario.getIdUsuario(), savedUsuario.getNome(), savedUsuario.getEmail());
    }

    public Optional<UsuarioDTO> getUserById(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(u -> new UsuarioDTO(u.getIdUsuario(), u.getNome(), u.getEmail()));
    }
}
