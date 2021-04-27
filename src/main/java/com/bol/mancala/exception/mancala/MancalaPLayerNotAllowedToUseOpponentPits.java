package com.bol.mancala.exception.mancala;

import com.bol.mancala.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/**
 * Opponent player pits are not allowed to be used by the current player.
 */
public class MancalaPLayerNotAllowedToUseOpponentPits extends ApplicationException {

    /**
     * Constructor.
     */
    public MancalaPLayerNotAllowedToUseOpponentPits() {
        super("Please choose your own pit!", HttpStatus.FORBIDDEN);
    }
}