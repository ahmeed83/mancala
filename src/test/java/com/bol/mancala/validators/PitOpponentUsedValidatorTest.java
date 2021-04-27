package com.bol.mancala.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PitOpponentUsedValidatorTest {

    private PitOpponentUsedValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PitOpponentUsedValidator();
    }

    /**
     * This test will check if the player "have used" the opponent pit.
     *
     * @param playerId         playerId
     * @param selectedPitIndex selectedPitIndex
     * @param expected         expected
     */
    @ParameterizedTest
    @CsvSource({
            "PLAYER_1, 10, true", // Player 1 have used player 2 opponent pit (10), return true.
            "PLAYER_1, 2, false", // Player 1 have used its pit (2), return false.
            "PLAYER_2, 5, true",  // Player 2 have used player 1 opponent pit (5), return true.
            "PLAYER_2, 9, false", // Player 2 have used its pit (9), return false.
    })
    void itShouldValidatePitOpponentUsed(String playerId, Integer selectedPitIndex, boolean expected) {
        // Given
        // When
        boolean isValid = underTest.test(playerId, selectedPitIndex);
        // Then
        assertThat(isValid).isEqualTo(expected);
    }
}