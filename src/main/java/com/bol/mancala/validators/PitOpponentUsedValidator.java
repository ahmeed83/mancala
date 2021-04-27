package com.bol.mancala.validators;

import org.springframework.stereotype.Component;

import java.util.function.BiPredicate;

import static com.bol.mancala.model.MancalaPlayer.PLAYER_1;
import static com.bol.mancala.model.MancalaPlayer.PLAYER_2;

/**
 * Mancala Big Pit Validator. This class prevents the usage of the Big Pits in both sides.
 */
@Component
public class PitOpponentUsedValidator implements BiPredicate<String, Integer> {

    /**
     * Check if the player chose the right pit.
     * The player is allowed only to use his own pit and not the opponent ones.
     *
     * @param playerId         player id
     * @param selectedPitIndex selected pit index
     */
    public boolean test(final String playerId, final Integer selectedPitIndex) {
        return playerId.equals(PLAYER_1.name()) && selectedPitIndex > 5 ||
                playerId.equals(PLAYER_2.name()) && selectedPitIndex < 7;
    }
}
