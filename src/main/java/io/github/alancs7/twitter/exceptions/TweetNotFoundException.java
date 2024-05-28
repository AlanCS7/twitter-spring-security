package io.github.alancs7.twitter.exceptions;

public class TweetNotFoundException extends RuntimeException {

    public TweetNotFoundException(String message) {
        super(message);
    }
}
