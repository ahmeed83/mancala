package com.bol.mancala.validators;

import org.springframework.stereotype.Component;

import java.util.function.IntPredicate;

/**
 * Mancala Not exists pit Validator. This class prevents the usage of out of range pit index.
 */
@Component
public class PitNotExistsValidator implements IntPredicate {
    
    /**
     * Check if the player chooses the right pit. The player is not allowed to use out of range index.
     *
     * @param selectedPitIndex selected pit index
     */
    @Override
    public boolean test(final int selectedPitIndex) {
        return selectedPitIndex < 0 || selectedPitIndex > 13;
    }
}
