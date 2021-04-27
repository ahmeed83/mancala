package com.bol.mancala.validators;

import com.bol.mancala.model.PitPlace;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

/**
 * Mancala Big Pit Validator. This class prevents the usage of the Big Pits in both sides.
 */
@Component
public class PitBigSelectedValidator implements Predicate<PitPlace> {

    /**
     * Check if the player chooses the right pit. The player is not allowed to use both big pits.
     *
     * @param pitPlace chosen pit place
     */
    @Override
    public boolean test(final PitPlace pitPlace) {
        return pitPlace.equals(PitPlace.PLAYER_ONE_PIT_BIG) || pitPlace.equals(PitPlace.PLAYER_TWO_PIT_BIG);
    }
}
