package com.bol.mancala.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Mancala Application Exception.
 */
@Getter
public abstract class ApplicationException extends RuntimeException {

    /**
     * Http status for the exception.
     */
    private final HttpStatus httpStatus;

    /**
     * Constructor.
     *
     * @param errorMessage error message.
     * @param httpStatus   http status
     */
    protected ApplicationException(final String errorMessage, final HttpStatus httpStatus) {
        super(errorMessage);
        this.httpStatus = httpStatus;
    }
}
