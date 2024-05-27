package io.github.alancs7.twitter.service;

import io.github.alancs7.twitter.controller.dto.LoginRequest;
import io.github.alancs7.twitter.controller.dto.LoginResponse;
import io.github.alancs7.twitter.entities.User;
import io.github.alancs7.twitter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.expiration.time}")
    private Long expiresIn;

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse authenticate(LoginRequest loginRequest) {
        var user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        var jwtValue = generateToken(user);

        return new LoginResponse(jwtValue, expiresIn);
    }

    private String generateToken(User user) {
        var claims = JwtClaimsSet.builder()
                .issuer("twitter-spring-security")
                .subject(user.getUserId().toString())
                .expiresAt(Instant.now().plusSeconds(expiresIn))
                .issuedAt(Instant.now())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
