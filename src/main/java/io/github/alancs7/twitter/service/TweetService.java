package io.github.alancs7.twitter.service;

import io.github.alancs7.twitter.controller.dto.CreateTweetDto;
import io.github.alancs7.twitter.entities.Tweet;
import io.github.alancs7.twitter.exceptions.BusinessException;
import io.github.alancs7.twitter.exceptions.TweetNotFoundException;
import io.github.alancs7.twitter.repository.TweetRepository;
import io.github.alancs7.twitter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public void create(CreateTweetDto dto, String userId) {
        userRepository.findById(UUID.fromString(userId))
                .ifPresent((user) -> {
                            var tweet = new Tweet();
                            tweet.setUser(user);
                            tweet.setContent(dto.content());
                            tweetRepository.save(tweet);
                        }
                );
    }

    @Transactional
    public void delete(Long id, String userId) {
        var tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new TweetNotFoundException("Tweet not found"));

        var tweetUserId = tweet.getUser().getUserId();
        var loggedUserId = UUID.fromString(userId);

        var userLogged = userRepository.findById(loggedUserId);

        if (!userLogged.get().isAdmin() && !tweetUserId.equals(loggedUserId)) {
            throw new BusinessException("Action not permitted");
        }

        tweetRepository.delete(tweet);
    }
}
