package com.bol.mancala.repositories;

import com.bol.mancala.entities.MancalaEntity;
import com.bol.mancala.exception.mancala.MancalaGeneralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_A;
import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_B;
import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_BIG;
import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_C;
import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_D;
import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_E;
import static com.bol.mancala.model.PitPlace.PLAYER_ONE_PIT_F;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_BIG;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_U;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_V;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_W;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_X;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_Y;
import static com.bol.mancala.model.PitPlace.PLAYER_TWO_PIT_Z;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;

/**
 * Mancala Repository Test.
 */
@DataJpaTest
class MancalaEntityRepositoryTest {

    /**
     * Start amount of the stones;
     */
    @Value("${start.stone.amount}")
    private Integer startStoneAmount;

    /**
     * Tested repository.
     */
    @Autowired
    private MancalaRepository mancalaRepository;

    /**
     * Test if the correct Mancala game with its correct data is saved in the database.
     *
     * 1- The player should be 1 or 2.
     * 2- The size of the Pits should be 14
     * 3- There should be exactly 12 pits contains the amount of the stones given in the
     * property file *startStoneAmount*.
     * 4- The size of the Pit places should be 14 and contains the names that are shown below
     * in the test.
     * 5- There should be a unique UUID for each Mancala game.
     */
    @Test
    @DisplayName("It should save a new Mancala game and find it by ID")
    void itShouldSaveMancalaGame() {
        // Given
        final UUID mockUUID = UUID.randomUUID();
        final MancalaEntity mancala = new MancalaEntity(mockUUID, startStoneAmount);
        // When
        mancalaRepository.save(mancala);
        // Then
        final Optional<MancalaEntity> optionalMancala = mancalaRepository.findById(mockUUID);
        assertThat(optionalMancala).isPresent().hasValueSatisfying(m -> {
            assertThat(m.getMancalaId()).isEqualTo(mockUUID);
            assertThat(m.getPlayerId() == 1 || m.getPlayerId() == 2).isTrue();
            assertThat(m.getPits().size()).isEqualTo(14);
            assertThat(m.getPits()
                               .stream()
                               .filter(man -> man.getStones().equals(startStoneAmount))
                               .count()).isSameAs(12L);
            assertThat(m.getPits().get(0).getPitId()).isNotNull();
            assertThat(m.getPits().get(0).getPitPlace()).isEqualTo(PLAYER_ONE_PIT_A);
            assertThat(m.getPits().get(1).getPitId()).isNotNull();
            assertThat(m.getPits().get(1).getPitPlace()).isEqualTo(PLAYER_ONE_PIT_B);
            assertThat(m.getPits().get(2).getPitPlace()).isEqualTo(PLAYER_ONE_PIT_C);
            assertThat(m.getPits().get(2).getPitId()).isNotNull();
            assertThat(m.getPits().get(3).getPitPlace()).isEqualTo(PLAYER_ONE_PIT_D);
            assertThat(m.getPits().get(3).getPitId()).isNotNull();
            assertThat(m.getPits().get(4).getPitPlace()).isEqualTo(PLAYER_ONE_PIT_E);
            assertThat(m.getPits().get(4).getPitId()).isNotNull();
            assertThat(m.getPits().get(5).getPitPlace()).isEqualTo(PLAYER_ONE_PIT_F);
            assertThat(m.getPits().get(6).getPitPlace()).isEqualTo(PLAYER_ONE_PIT_BIG);
            assertThat(m.getPits().get(7).getPitPlace()).isEqualTo(PLAYER_TWO_PIT_U);
            assertThat(m.getPits().get(8).getPitPlace()).isEqualTo(PLAYER_TWO_PIT_V);
            assertThat(m.getPits().get(9).getPitPlace()).isEqualTo(PLAYER_TWO_PIT_W);
            assertThat(m.getPits().get(10).getPitPlace()).isEqualTo(PLAYER_TWO_PIT_X);
            assertThat(m.getPits().get(11).getPitPlace()).isEqualTo(PLAYER_TWO_PIT_Y);
            assertThat(m.getPits().get(12).getPitPlace()).isEqualTo(PLAYER_TWO_PIT_Z);
            assertThat(m.getPits().get(13).getPitPlace()).isEqualTo(PLAYER_TWO_PIT_BIG);
        });
    }

    /**
     * Test if there is no stones are in the game when its created. 
     */
    @Test
    @DisplayName("It should not save Mancala game if the amount of stones is zero")
    void itShouldNotSaveMancalaGameIfAmountOfStonesIsZero() {
        // Given
        final UUID mockUUID = UUID.randomUUID();
        // When
        // Then
        assertThatExceptionOfType(MancalaGeneralException.class)
                .isThrownBy(() -> new MancalaEntity(mockUUID, 0))
                .withMessage("Stones amount should be greater than zero!");
    }
    
    /**
     * Test if thee amount of stones is less than zero when the game is created. 
     */
    @Test
    @DisplayName("It should not save Mancala game if the amount of stones less than zero")
    void itShouldNotSaveMancalaGameIfAmountOfStonesIsLessThanZero() {
        // Given
        final UUID mockUUID = UUID.randomUUID();
        // When
        // Then
        assertThatExceptionOfType(MancalaGeneralException.class)
                .isThrownBy(() -> new MancalaEntity(mockUUID, -1))
                .withMessage("Stones amount should be greater than zero!");
    }
}