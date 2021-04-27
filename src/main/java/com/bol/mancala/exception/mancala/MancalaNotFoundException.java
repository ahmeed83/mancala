package com.bol.mancala.exception.mancala;

import com.bol.mancala.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/**
 * Mancala does not exists.
 */
public class MancalaNotFoundException extends ApplicationException {

    /**
     * Constructor.
     */
    public MancalaNotFoundException() {
        super("Mancala does not exists!", HttpStatus.NOT_FOUND);
    }
}