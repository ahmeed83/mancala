package com.bol.mancala.exception.mancala;

import com.bol.mancala.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/**
 * General Game Exception and can be used for general purposes.
 */
public class MancalaGeneralException extends ApplicationException {

    /**
     * Constructor.
     *
     * @param errorMessage generic error message.
     */
    public MancalaGeneralException(final String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}