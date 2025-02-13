// src/main/java/com/example/hexagonal/adapters/output/repository/JpaUsuarioRepository.java
package com.example.hexagonal.adapters.output.repository;

import com.example.hexagonal.adapters.output.entities.JpaUsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUsuarioRepository extends JpaRepository<JpaUsuarioEntity, Long> {
}
