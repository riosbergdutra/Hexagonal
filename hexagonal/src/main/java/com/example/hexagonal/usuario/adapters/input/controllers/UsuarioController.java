package com.example.hexagonal.usuario.adapters.input.controllers;

import com.example.hexagonal.usuario.application.service.JwtService;
import com.example.hexagonal.usuario.application.service.UsuarioService;
import com.example.hexagonal.usuario.dto.ChangePasswordDTO;
import com.example.hexagonal.usuario.dto.LoginRequestDTO;
import com.example.hexagonal.usuario.dto.UsuarioDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public UsuarioController(UsuarioService usuarioService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    // Método auxiliar para extrair o token do header ou cookie
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // Endpoint público para criação de conta
    @PostMapping
    public ResponseEntity<UsuarioDTO> createUser(@RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO createdUser = usuarioService.createUser(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    // Endpoint de Login
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        Optional<UsuarioDTO> userOpt = usuarioService.loginUser(loginRequest);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UsuarioDTO userDTO = userOpt.get();
        String accessToken = jwtService.generateAccessToken(userDTO);
        String refreshToken = jwtService.generateRefreshToken(userDTO);
        jwtService.setTokensInCookies(response, accessToken, refreshToken);
        return ResponseEntity.ok(userDTO);
    }

    // Endpoint para obter os dados do usuário autenticado
    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getAuthenticatedUser(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<UsuarioDTO> usuarioOpt = jwtService.authenticateUser(token);
        return usuarioOpt.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // Endpoint para atualizar os dados do usuário autenticado
    @PutMapping("/me")
    public ResponseEntity<UsuarioDTO> updateAuthenticatedUser(HttpServletRequest request,
                                                                @RequestBody UsuarioDTO usuarioDTO) {
        String token = extractToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<UsuarioDTO> authUserOpt = jwtService.authenticateUser(token);
        if (!authUserOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = authUserOpt.get().getIdUsuario();
        Optional<UsuarioDTO> updatedUser = usuarioService.updateUser(userId, usuarioDTO);
        return updatedUser.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    // Endpoint para exclusão da conta do usuário autenticado
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteAuthenticatedUser(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<UsuarioDTO> authUserOpt = jwtService.authenticateUser(token);
        if (!authUserOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = authUserOpt.get().getIdUsuario();
        boolean deleted = usuarioService.deleteUser(userId);
        return deleted ? ResponseEntity.ok("Conta excluída com sucesso!")
                       : ResponseEntity.badRequest().body("Falha ao excluir conta.");
    }

    // Endpoint para alteração de senha (passando oldPassword e newPassword extraídos do DTO)
    @PostMapping("/me/change-password")
    public ResponseEntity<String> changePassword(HttpServletRequest request,
                                                 @RequestBody ChangePasswordDTO changePasswordDTO) {
        String token = extractToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<UsuarioDTO> authUserOpt = jwtService.authenticateUser(token);
        if (!authUserOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = authUserOpt.get().getIdUsuario();
        // Aqui, desmembramos o DTO para passar as credenciais separadas
        boolean changed = usuarioService.changePassword(userId, changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword());
        return changed ? ResponseEntity.ok("Senha alterada com sucesso!")
                       : ResponseEntity.badRequest().body("Falha ao alterar senha.");
    }

    // Endpoint para refresh do token
    @GetMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        jwtService.refreshToken(request, response);
        return ResponseEntity.ok("Token atualizado com sucesso!");
    }

    // Endpoint para confirmação de e-mail
@GetMapping("/confirm")
public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
    boolean confirmed = usuarioService.confirmEmail(token);
    if (confirmed) {
        return ResponseEntity.ok("E-mail confirmado com sucesso!");
    } else {
        return ResponseEntity.badRequest().body("Token inválido ou expirado.");
    }
}

}
