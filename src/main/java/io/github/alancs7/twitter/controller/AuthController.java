package io.github.alancs7.twitter.controller;

import io.github.alancs7.twitter.controller.dto.LoginResponse;
import io.github.alancs7.twitter.controller.dto.UserRequest;
import io.github.alancs7.twitter.exceptions.UserAlreadyExistsException;
import io.github.alancs7.twitter.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(authService.authenticate(userRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {
        try {
            authService.registerNewUser(userRequest);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
