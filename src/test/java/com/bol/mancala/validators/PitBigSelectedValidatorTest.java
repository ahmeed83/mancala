package com.bol.mancala.validators;

import com.bol.mancala.model.PitPlace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PitBigSelectedValidatorTest {

    private PitBigSelectedValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PitBigSelectedValidator();
    }

    /**
     * This test will check if the player "have used" the one of the big pits.
     *
     * @param pitPlace         pitPlace
     * @param expected         expected
     */
    @ParameterizedTest
    @CsvSource({
            "PLAYER_ONE_PIT_BIG, true", // Player have used The player 1 big pit, return true.
            "PLAYER_TWO_PIT_BIG, true", // Player have used The player 2 big pit, return true.
    })
    void itShouldValidateBigPitUsed(PitPlace pitPlace, boolean expected) {
        // Given
        // When
        boolean isValid = underTest.test(pitPlace);
        // Then
        assertThat(isValid).isEqualTo(expected);
    }
}