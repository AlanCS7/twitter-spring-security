package io.github.alancs7.twitter.controller;

import io.github.alancs7.twitter.controller.dto.CreateTweetDto;
import io.github.alancs7.twitter.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tweets")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateTweetDto dto, JwtAuthenticationToken token) {
        tweetService.create(dto, token.getName());
        return ResponseEntity.ok().build();
    }
}
