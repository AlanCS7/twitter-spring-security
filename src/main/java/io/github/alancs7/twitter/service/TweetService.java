package io.github.alancs7.twitter.service;

import io.github.alancs7.twitter.controller.dto.CreateTweetDto;
import io.github.alancs7.twitter.controller.dto.FeedDto;
import io.github.alancs7.twitter.controller.dto.FeedItemDto;
import io.github.alancs7.twitter.entities.Tweet;
import io.github.alancs7.twitter.exceptions.BusinessException;
import io.github.alancs7.twitter.exceptions.TweetNotFoundException;
import io.github.alancs7.twitter.repository.TweetRepository;
import io.github.alancs7.twitter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    public FeedDto getFeed(Integer page, Integer pageSize) {
        var tweets = tweetRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"))
                .map(tweet ->
                        new FeedItemDto(tweet.getTweetId(), tweet.getContent(), tweet.getUser().getUsername())
                );

        return new FeedDto(tweets.getContent(), page, pageSize, tweets.getTotalPages(), tweets.getTotalElements());
    }
}
