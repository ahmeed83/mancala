package com.bol.mancala.exception.mancala;

import com.bol.mancala.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/**
 * Mancala pit is empty.
 */
public class MancalaPitIsEmpty extends ApplicationException {

    /**
     * Constructor.
     */
    public MancalaPitIsEmpty() {
        super("Please use another Pit, this one is empty!", HttpStatus.FORBIDDEN);
    }
}