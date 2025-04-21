package com.example.hexagonal.usuario.application.service;

import com.example.hexagonal.usuario.domain.Usuario;
import com.example.hexagonal.usuario.domain.port.UsuarioRepository;
import com.example.hexagonal.usuario.dto.UsuarioDTO;
import com.example.hexagonal.usuario.application.CookieService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UsuarioRepository usuarioRepository;
    private final long accessTokenExpiryDuration = 5L;   // exemplo: 5 segundos
    private final long refreshTokenExpiryDuration = 20L; // exemplo: 20 segundos

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UsuarioRepository usuarioRepository) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.usuarioRepository = usuarioRepository;
    }

    public String generateAccessToken(UsuarioDTO user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(String.valueOf(user.getIdUsuario()))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenExpiryDuration))
                .claim("role", user.getRole().toString())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateRefreshToken(UsuarioDTO user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(String.valueOf(user.getIdUsuario()))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenExpiryDuration))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public void setTokensInCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        try {
            CookieService.setCookie(response, "accessToken", accessToken, (int) accessTokenExpiryDuration);
            CookieService.setCookie(response, "refreshToken", refreshToken, (int) refreshTokenExpiryDuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<Long> getUserIdFromToken(String token) {
        try {
            Jwt decodedJwt = jwtDecoder.decode(token);
            String subject = decodedJwt.getSubject();
            return Optional.of(Long.valueOf(subject));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return;
        
        Optional<String> refreshTokenOpt = Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();

        if (refreshTokenOpt.isPresent()) {
            String refreshToken = refreshTokenOpt.get();
            Optional<Long> userIdOpt = getUserIdFromToken(refreshToken);
            if (userIdOpt.isPresent()) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findById(userIdOpt.get());
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.getIdUsuario(), usuario.getNome(), usuario.getEmail(), null, usuario.getRole(), usuario.isAtivo());
                    String newAccessToken = generateAccessToken(usuarioDTO);
                    String newRefreshToken = generateRefreshToken(usuarioDTO);
                    setTokensInCookies(response, newAccessToken, newRefreshToken);
                }
            }
        }
    }

    public Optional<UsuarioDTO> authenticateUser(String accessToken) {
        Optional<Long> userIdOpt = getUserIdFromToken(accessToken);
        if (userIdOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(userIdOpt.get());
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                return Optional.of(new UsuarioDTO(usuario.getIdUsuario(), usuario.getNome(), usuario.getEmail(), null, usuario.getRole(), usuario.isAtivo()));
            }
        }
        return Optional.empty();
    }
}
