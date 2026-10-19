package com.shelfwise.exception;

/**
 * Thrown when someone tries to borrow a book that has no copies left.
 */
public class BookNotAvailableException extends RuntimeException {

    public BookNotAvailableException(String message) {
        super(message);
    }
}
