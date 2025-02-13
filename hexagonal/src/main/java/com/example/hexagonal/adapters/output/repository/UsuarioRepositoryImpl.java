package com.example.hexagonal.adapters.output.repository;

import com.example.hexagonal.adapters.output.entities.JpaUsuarioEntity;
import com.example.hexagonal.domain.Usuario;
import com.example.hexagonal.domain.port.UsuarioRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {
    private final JpaUsuarioRepository jpaUsuarioRepository;

    public UsuarioRepositoryImpl(JpaUsuarioRepository jpaUsuarioRepository) {
        this.jpaUsuarioRepository = jpaUsuarioRepository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        JpaUsuarioEntity entity = new JpaUsuarioEntity(usuario);
        JpaUsuarioEntity savedEntity = jpaUsuarioRepository.save(entity);
        return new Usuario(savedEntity.getIdUsuario(), savedEntity.getNome(), savedEntity.getEmail());
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaUsuarioRepository.findById(id)
                .map(entity -> new Usuario(entity.getIdUsuario(), entity.getNome(), entity.getEmail()));
    }
}
