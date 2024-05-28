package io.github.alancs7.twitter.service;

import io.github.alancs7.twitter.controller.dto.LoginResponse;
import io.github.alancs7.twitter.controller.dto.UserRequest;
import io.github.alancs7.twitter.entities.Role;
import io.github.alancs7.twitter.entities.User;
import io.github.alancs7.twitter.exceptions.UserAlreadyExistsException;
import io.github.alancs7.twitter.repository.RoleRepository;
import io.github.alancs7.twitter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration.time}")
    private Long expiresIn;

    public LoginResponse authenticate(UserRequest userRequest) {
        var user = userRepository.findByUsername(userRequest.username())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(userRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        var jwtValue = generateToken(user);

        return new LoginResponse(jwtValue, expiresIn);
    }

    @Transactional
    public void registerNewUser(UserRequest userRequest) {
        var roleBasic = roleRepository.findByName(Role.Values.BASIC.name());

        userRepository
                .findByUsername(userRequest.username())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException(String.format("User %s already exists", user.getUsername()));
                });

        var user = new User();
        user.setUsername(userRequest.username());
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setRoles(Set.of(roleBasic));
        userRepository.save(user);
    }

    private String generateToken(User user) {
        var scopes = user.getRoles()
                .stream()
                .map(role -> role.getName().toUpperCase())
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("twitter-spring-security")
                .subject(user.getUserId().toString())
                .expiresAt(Instant.now().plusSeconds(expiresIn))
                .issuedAt(Instant.now())
                .claim("scope", scopes)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
