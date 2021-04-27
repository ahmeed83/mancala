package com.bol.mancala.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PitNotExistsValidatorTest {

    private PitNotExistsValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PitNotExistsValidator();
    }

    /**
     * This test will check if pit is not exists.
     *
     * @param selectedPitIndex selectedPitIndex
     * @param expected         expected
     */
    @ParameterizedTest
    @CsvSource({
            "20, true", // Player have used does not exist pit, return true.
            "300, true", // Player have used does not exist pit, return true.
            "-5, true", // Player have used does not exist pit, return true.
    })
    void itShouldValidateNotExistsPitUsed(Integer selectedPitIndex, boolean expected) {
        // Given
        // When
        boolean isValid = underTest.test(selectedPitIndex);
        // Then
        assertThat(isValid).isEqualTo(expected);
    }
}