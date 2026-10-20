package com.shelfwise.exception;

/**
 * Thrown when something tries to move an entity into a status it can't go to
 * from its current status (e.g. RECEIVED -> SUBMITTED).
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String from, String to) {
        super("Cannot change status from " + from + " to " + to);
    }
}
