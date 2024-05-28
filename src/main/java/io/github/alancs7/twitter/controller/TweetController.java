package io.github.alancs7.twitter.controller;

import io.github.alancs7.twitter.controller.dto.CreateTweetDto;
import io.github.alancs7.twitter.exceptions.BusinessException;
import io.github.alancs7.twitter.exceptions.TweetNotFoundException;
import io.github.alancs7.twitter.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tweets")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateTweetDto dto, JwtAuthenticationToken token) {
        tweetService.create(dto, token.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, JwtAuthenticationToken token) {
        try {
            tweetService.delete(id, token.getName());
            return ResponseEntity.noContent().build();
        } catch (TweetNotFoundException | BusinessException e) {
            var error = Map.of("error", e.getMessage());
            return e instanceof TweetNotFoundException
                    ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
    }
}
