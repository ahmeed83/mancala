package com.bol.mancala.exception.mancala;

import com.bol.mancala.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/**
 * Mancala big pits are not allowed to be used.
 */
public class MancalaBigPitNotAllowed extends ApplicationException {

    /**
     * Constructor.
     */
    public MancalaBigPitNotAllowed() {
        super("Please use another Pit! The Big one is not allowed to be used!", HttpStatus.FORBIDDEN);
    }
}