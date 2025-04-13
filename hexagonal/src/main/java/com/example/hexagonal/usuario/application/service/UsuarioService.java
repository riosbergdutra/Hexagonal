package com.example.hexagonal.usuario.application.service;

import com.example.hexagonal.usuario.domain.Usuario;
import com.example.hexagonal.usuario.domain.port.UsuarioRepository;
import com.example.hexagonal.usuario.dto.LoginRequestDTO;
import com.example.hexagonal.usuario.dto.SocialSignupDTO;
import com.example.hexagonal.usuario.dto.UsuarioDTO;
import com.example.hexagonal.usuario.enums.Role;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private Map<String, Long> confirmationTokens = new HashMap<>();

    public UsuarioService(UsuarioRepository usuarioRepository, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    public UsuarioDTO createUser(UsuarioDTO usuarioDTO) {
        // Ao criar, o usuário inicia com 'ativo' = false e Role padrão USER
        Usuario usuario = new Usuario(null, usuarioDTO.getNome(), usuarioDTO.getEmail(), usuarioDTO.getSenha(), Role.USER, false);
        Usuario savedUsuario = usuarioRepository.save(usuario);
        // Gera token de confirmação e envia e-mail
        String token = UUID.randomUUID().toString();
        confirmationTokens.put(token, savedUsuario.getIdUsuario());
        emailService.sendConfirmationEmail(savedUsuario.getEmail(), token);
        // Retorna DTO sem a senha
        return new UsuarioDTO(savedUsuario.getIdUsuario(), savedUsuario.getNome(), savedUsuario.getEmail(), null, savedUsuario.getRole(), savedUsuario.isAtivo());
    }

    public Optional<UsuarioDTO> getUserById(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(u -> new UsuarioDTO(u.getIdUsuario(), u.getNome(), u.getEmail(), null, u.getRole(), u.isAtivo()));
    }

    public Optional<UsuarioDTO> updateUser(Long id, UsuarioDTO usuarioDTO) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.setNome(usuarioDTO.getNome());
            usuario.setEmail(usuarioDTO.getEmail());
            if (usuarioDTO.getSenha() != null) {
                usuario.setSenha(usuarioDTO.getSenha());
            }
            Usuario updatedUsuario = usuarioRepository.save(usuario);
            return Optional.of(new UsuarioDTO(updatedUsuario.getIdUsuario(), updatedUsuario.getNome(), updatedUsuario.getEmail(), null, updatedUsuario.getRole(), updatedUsuario.isAtivo()));
        }
        return Optional.empty();
    }

    public boolean deleteUser(Long id) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            usuarioRepository.delete(optionalUsuario.get());
            return true;
        }
        return false;
    }

    // Método de changePassword: agora recebe oldPassword e newPassword separadamente
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            // Em produção, use criptografia e comparação segura
            if (usuario.getSenha() != null && usuario.getSenha().equals(oldPassword)) {
                usuario.setSenha(newPassword);
                usuarioRepository.save(usuario);
                return true;
            }
        }
        return false;
    }

    public UsuarioDTO socialSignup(SocialSignupDTO socialSignupDTO) {
        // Para cadastro via redes sociais, a senha pode não ser informada; Role padrão USER
        Usuario usuario = new Usuario(null, socialSignupDTO.getNome(), socialSignupDTO.getEmail(), null, Role.USER, false);
        Usuario savedUsuario = usuarioRepository.save(usuario);
        // Gera token de confirmação e envia e-mail
        String token = UUID.randomUUID().toString();
        confirmationTokens.put(token, savedUsuario.getIdUsuario());
        emailService.sendConfirmationEmail(savedUsuario.getEmail(), token);
        return new UsuarioDTO(savedUsuario.getIdUsuario(), savedUsuario.getNome(), savedUsuario.getEmail(), null, savedUsuario.getRole(), savedUsuario.isAtivo());
    }

    public boolean confirmEmail(String token) {
        if (confirmationTokens.containsKey(token)) {
            Long userId = confirmationTokens.get(token);
            Optional<Usuario> optionalUsuario = usuarioRepository.findById(userId);
            if (optionalUsuario.isPresent()) {
                Usuario usuario = optionalUsuario.get();
                usuario.setAtivo(true);
                usuarioRepository.save(usuario);
                confirmationTokens.remove(token);
                return true;
            }
        }
        return false;
    }

    // Método de login: valida email e senha e retorna o DTO se válido
    public Optional<UsuarioDTO> loginUser(LoginRequestDTO loginRequest) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(loginRequest.getEmail());
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            if (usuario.getSenha() != null && usuario.getSenha().equals(loginRequest.getSenha())) {
                return Optional.of(new UsuarioDTO(usuario.getIdUsuario(), usuario.getNome(), usuario.getEmail(), null, usuario.getRole(), usuario.isAtivo()));
            }
        }
        return Optional.empty();
    }
}
