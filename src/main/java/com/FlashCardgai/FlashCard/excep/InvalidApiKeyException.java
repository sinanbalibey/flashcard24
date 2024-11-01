package com.FlashCardgai.FlashCard.excep;

public class InvalidApiKeyException extends RuntimeException{
    public InvalidApiKeyException(String message) {
        super(message);
    }
}
