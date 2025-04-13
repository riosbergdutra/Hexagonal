package com.example.hexagonal.usuario.adapters.output.repository;

import com.example.hexagonal.usuario.adapters.output.entities.JpaUsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaUsuarioRepository extends JpaRepository<JpaUsuarioEntity, Long> {
    Optional<JpaUsuarioEntity> findByEmail(String email);
}
