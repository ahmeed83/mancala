package com.bol.mancala.service;

import com.bol.mancala.entities.MancalaEntity;
import com.bol.mancala.exception.mancala.MancalaBigPitNotAllowed;
import com.bol.mancala.exception.mancala.MancalaGeneralException;
import com.bol.mancala.exception.mancala.MancalaNotFoundException;
import com.bol.mancala.exception.mancala.MancalaPLayerNotAllowedToUseOpponentPits;
import com.bol.mancala.exception.mancala.MancalaPitIsEmpty;
import com.bol.mancala.model.MancalaGame;
import com.bol.mancala.model.PitGame;
import com.bol.mancala.repositories.MancalaRepository;
import com.bol.mancala.validators.PitBigSelectedValidator;
import com.bol.mancala.validators.PitNotExistsValidator;
import com.bol.mancala.validators.PitOpponentUsedValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.bol.mancala.model.MancalaPlayer.PLAYER_1;
import static com.bol.mancala.model.MancalaPlayer.PLAYER_2;
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
import static com.bol.mancala.model.PitPlace.PIT_NOT_EXISTS_IN_MANCALA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
class MancalaServiceTest {

    /**
     * Stones amount.
     */
    private static final Integer startStoneAmount = 6;

    /**
     * mancala UUID.
     */
    private UUID mancalaUUID;
    
    /**
     * String of mancala UUID.
     */
    private String stringMancalaUUID;

    /**
     * Mancala Entity.
     */
    private MancalaEntity mancalaEntity;;
    
    /**
     * Mock Mancala Repository.
     */
    @Mock
    private MancalaRepository mockMancalaRepository;

    /**
     * Mancala argument capture.
     */
    @Captor
    private ArgumentCaptor<MancalaEntity> mancalaEntityArgumentCaptor;
    
    /**
     * Tested Service.
     */
    private MancalaService mancalaService;
    
    /**
     * Validates if the player uses the opponent pit.
     */
    @Mock
    private PitOpponentUsedValidator pitOpponentUsedValidator;
    
    /**
     * Validates if the player uses the opponent pit.
     */
    @Mock
    private PitBigSelectedValidator pitBigSelectedValidator;
    
    /**
     * Validates if the player uses the opponent pit.
     */
    @Mock
    private PitNotExistsValidator pitNotExistsValidator;
    
    /**
     * Set up the test.
     */
    @BeforeEach
    void setUp() {
        mancalaService = new MancalaService(pitOpponentUsedValidator,
                                            pitNotExistsValidator, pitBigSelectedValidator,
                                            mockMancalaRepository);
        mancalaUUID = UUID.randomUUID();
        stringMancalaUUID = String.valueOf(mancalaUUID);
        mancalaEntity = new MancalaEntity(mancalaUUID, startStoneAmount);
        ReflectionTestUtils.setField(mancalaService, "startStoneAmount", startStoneAmount);
    }

    @Test
    @DisplayName("It should save a new Mancala game")
    void itShouldStartMancalaNewGame() {
        // Given
        given(mockMancalaRepository.save(any(MancalaEntity.class))).willReturn(mancalaEntity);
        // When
        final MancalaGame mancalaGame = mancalaService.startMancalaNewGame();
        // Then
        assertThat(mancalaGame.getGameId()).isEqualTo(mancalaUUID);
        assertThat(mancalaGame.getPlayer().getPlayerId()).isEqualTo(mancalaEntity.getPlayerId());
        assertThat(mancalaGame.getPlayerWinner()).isNull();
        assertThat(mancalaGame.getPits().size()).isSameAs(mancalaEntity.getPits().size());
        assertThat(mancalaGame.getPits()
                           .stream()
                           .filter(man -> man.getStones()
                                   .equals(startStoneAmount))
                           .count()).isSameAs(mancalaEntity.getPits().stream()
                                                              .filter(man -> man.getStones().equals(startStoneAmount))
                                                              .count());
        IntStream.range(0, mancalaEntity.getPits().size())
                .forEach(i ->  assertThat(mancalaGame.getPitGame(i).getPitPlace())
                        .isSameAs(mancalaEntity.getPits().get(i).getPitPlace()));
    }

    @Test
    @DisplayName("It should delete the game successfully when the the game is reset")
    void itShouldRestMancala() {
        // Given
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaEntity));
        // When
        mancalaService.deleteMancala(stringMancalaUUID);
        // Then
        then(mockMancalaRepository).should().delete(mancalaEntityArgumentCaptor.capture());
        final MancalaEntity mancalaEntityCaptorValue = mancalaEntityArgumentCaptor.getValue();
        assertThat(mancalaEntityCaptorValue).isEqualTo(mancalaEntity);
    }

    @Test
    @DisplayName("It should not DELETE the game that does not exists")
    void itShouldNotRestMancala() {
        // Given
        given(mockMancalaRepository.findById(any())).willReturn(Optional.empty());
        // When
        // Then
        assertThatExceptionOfType(MancalaNotFoundException.class)
                .isThrownBy(() -> mancalaService.deleteMancala(stringMancalaUUID))
                .withMessage("Mancala does not exists!");
        then(mockMancalaRepository).should(never()).delete(any(MancalaEntity.class));
    }

    @Test
    @DisplayName("It should not UPDATE the game that does not exists")
    void itShouldNotUpdateGameIfNotExists() {
        // Given
        given(mockMancalaRepository.findById(any())).willReturn(Optional.empty());
        // When
        // Then
        assertThatExceptionOfType(MancalaNotFoundException.class)
                .isThrownBy(() -> mancalaService.updateGame(stringMancalaUUID, PLAYER_ONE_PIT_A))
                .withMessage("Mancala does not exists!");
    }

    @Test
    @DisplayName("It should not update the game if the player 1 selected the opponent pit")
    void itShouldNotUpdateGameIfPlayer1UsesOpponentPits() {
        // Given
        given(mockMancalaRepository.findById(any()))
                .willReturn(Optional.of(mancalaEntity.toBuilder().playerId(1).build()));
        given(pitOpponentUsedValidator.test(PLAYER_1.name(), PLAYER_TWO_PIT_X.ordinal())).willReturn(true);
        // When
        // Then
        assertThatExceptionOfType(MancalaPLayerNotAllowedToUseOpponentPits.class)
                .isThrownBy(() -> mancalaService.updateGame(stringMancalaUUID, PLAYER_TWO_PIT_X))
                .withMessage("Please choose your own pit!");
    }

    @Test
    @DisplayName("It should not update the game if the player 2 selected the opponent pit")
    void itShouldNotUpdateGameIfPlayer2UsesOpponentPits() {
        // Given
        given(mockMancalaRepository.findById(any()))
                .willReturn(Optional.of(mancalaEntity.toBuilder().playerId(2).build()));
        given(pitOpponentUsedValidator.test(PLAYER_2.name(), PLAYER_ONE_PIT_A.ordinal())).willReturn(true);
        // When
        // Then
        assertThatExceptionOfType(MancalaPLayerNotAllowedToUseOpponentPits.class)
                .isThrownBy(() -> mancalaService.updateGame(stringMancalaUUID, PLAYER_ONE_PIT_A))
                .withMessage("Please choose your own pit!");
    }

    @Test
    @DisplayName("It should not update the game if any player selected the player 1 big pit")
    void itShouldNotUpdateGameIfPlayerUsesBigPit1() {
        // Given
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaEntity));
        given(pitBigSelectedValidator.test(PLAYER_ONE_PIT_BIG)).willReturn(true);
        // When
        // Then
        assertThatExceptionOfType(MancalaBigPitNotAllowed.class)
                .isThrownBy(() -> mancalaService.updateGame(stringMancalaUUID, PLAYER_ONE_PIT_BIG))
                .withMessage("Please use another Pit! The Big one is not allowed to be used!");
    }

    @Test
    @DisplayName("It should not update the game if any player selected the player 2 big pit")
    void itShouldNotUpdateGameIfPlayerUsesBigPit2() {
        // Given
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaEntity));
        given(pitBigSelectedValidator.test(PLAYER_TWO_PIT_BIG)).willReturn(true);
        // When
        // Then
        assertThatExceptionOfType(MancalaBigPitNotAllowed.class)
                .isThrownBy(() -> mancalaService.updateGame(stringMancalaUUID, PLAYER_TWO_PIT_BIG))
                .withMessage("Please use another Pit! The Big one is not allowed to be used!");
    }

    @Test
    @DisplayName("It should not update the game if any player selected not exists pit index")
    void itShouldNotUpdateGameIfPlayerUsesNegativeIndex() {
        // Given
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaEntity));
        given(pitNotExistsValidator.test(PIT_NOT_EXISTS_IN_MANCALA.ordinal())).willReturn(true);
        // When
        // Then
        assertThatExceptionOfType(MancalaGeneralException.class)
                .isThrownBy(() -> mancalaService.updateGame(stringMancalaUUID, PIT_NOT_EXISTS_IN_MANCALA))
                .withMessage("The selected index is not within the game range!");
    }
    
    @Test
    @DisplayName("It should not update the game if selected pit empty from stones")
    void itShouldNotUpdateGameIfSelectedPitEmpty() {
        // Given
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaEntity.toBuilder()
                                               .playerId(1)
                                               .pits(mancalaEntity.getPits()
                                                             .stream()
                                                             .filter(p -> p.getPitPlace() == PLAYER_ONE_PIT_A)
                                                             .map(p -> p.toBuilder().stones(0).build())
                                                             .collect(Collectors.toList()))
                                               .build()));
        // When
        // Then
        assertThatExceptionOfType(MancalaPitIsEmpty.class)
                .isThrownBy(() -> mancalaService.updateGame(stringMancalaUUID, PLAYER_ONE_PIT_A))
                .withMessage("Please use another Pit, this one is empty!");
    }

    @Test
    @DisplayName("It should empty the pit from the stones successfully")
    void itShouldEmptyThePitFromStones() {
        // Given
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaEntity));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_ONE_PIT_A);
        final PitGame pitGame = mancalaGame.getPitGame(PLAYER_ONE_PIT_A.ordinal());
        // Then
        assertThat(pitGame.getStones()).isZero();
    }

    @Test
    @DisplayName("It should determine the next player is 1 when it end at its own big pit")
    void itShouldDetermineTheNextPlayer1HasOneMoreTurn() {
        // Given
        given(mockMancalaRepository.findById(any()))
                .willReturn(Optional.of(mancalaEntity.toBuilder().playerId(1).build()));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_ONE_PIT_A);
        // Then
        assertThat(mancalaGame.getPlayer()).isSameAs(PLAYER_1);
    }

    @Test
    @DisplayName("It should not determine the next player is 1 when it end at its opponent bit")
    void itShouldNotDetermineTheNextPlayer1HasOneMoreTurn() {
        // Given
        given(mockMancalaRepository.findById(any()))
                .willReturn(Optional.of(mancalaEntity.toBuilder().playerId(1).build()));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_ONE_PIT_B);
        // Then
        assertThat(mancalaGame.getPlayer()).isSameAs(PLAYER_2);
    }

    @Test
    @DisplayName("It should determine the next player is 2 when it end at its own big pit")
    void itShouldDetermineTheNextPlayer2HasOneMoreTurn() {
        // Given
        given(mockMancalaRepository.findById(any()))
                .willReturn(Optional.of(mancalaEntity.toBuilder().playerId(2).build()));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_TWO_PIT_U);
        // Then
        assertThat(mancalaGame.getPlayer()).isSameAs(PLAYER_2);
    }

    @Test
    @DisplayName("It should not determine the next player is 2 when it end at its opponent bit")
    void itShouldNotDetermineTheNextPlayerHasOneMoreTurn() {
        // Given
        given(mockMancalaRepository.findById(any()))
                .willReturn(Optional.of(mancalaEntity.toBuilder().playerId(2).build()));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_TWO_PIT_W);
        // Then
        assertThat(mancalaGame.getPlayer()).isSameAs(PLAYER_1);
    }

    @Test
    @DisplayName("It should restart the pit index if it reached its size and there are still stones to be sowed")
    void itShouldRestartIndexIfReachedEnd() {
        // Given
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                    .playerId(1)
                                                                                    .pits(mancalaEntity.getPits()
                                                                                                  .stream()
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_F ?
                                                                                                          p.toBuilder().stones(20).build() : p)
                                                                                                  .collect(Collectors.toList()))
                                                                                    .build()));
        // When
        // Then
        assertThatNoException().isThrownBy(() -> mancalaService.updateGame(stringMancalaUUID, PLAYER_ONE_PIT_F));
    }

    @Test
    @DisplayName("It should skip the opponent big pit for player 1")
    void itShouldSkipOpponentPitForPlayerOne() {
        // Given
        // IF Player 1 picks 10 stones from PLAYER_ONE_PIT_F, THEN PLAYER_ONE_PIT_C SHOULD HAVE ITS STONES PLUS ONE
        // And PLAYER_ONE_PIT_D should stay the same with its stones. 
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                    .playerId(1)
                                                                                    .pits(mancalaEntity.getPits()
                                                                                                  .stream()
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_F ?
                                                                                                          p.toBuilder().stones(10).build() : p)
                                                                                                  .collect(Collectors.toList()))
                                                                                    .build()));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_ONE_PIT_F);
        // Then
        final PitGame pitGameC = mancalaGame.getPitGame(PLAYER_ONE_PIT_C.ordinal());
        assertThat(pitGameC.getStones()).isSameAs(startStoneAmount + 1);
        final PitGame pitGameD = mancalaGame.getPitGame(PLAYER_ONE_PIT_D.ordinal());
        assertThat(pitGameD.getStones()).isSameAs(startStoneAmount);
        final PitGame pitBig = mancalaGame.getPitGame(PLAYER_ONE_PIT_BIG.ordinal());
        assertThat(pitBig.getStones()).isOne();
        final PitGame pitBigOpponent = mancalaGame.getPitGame(PLAYER_TWO_PIT_BIG.ordinal());
        assertThat(pitBigOpponent.getStones()).isZero();
    }

    @Test
    @DisplayName("It should skip the opponent big pit for player 2")
    void itShouldSkipOpponentPitForPlayerTwo() {
        // Given
        // IF Player 2 picks 10 stones from PLAYER_ONE_PIT_Z, THEN PLAYER_TWO_PIT_W SHOULD HAVE ITS STONES PLUS ONE. 
        // And PLAYER_TWO_PIT_X should stay the same with its stones. 
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                    .playerId(2)
                                                                                    .pits(mancalaEntity.getPits()
                                                                                                  .stream()
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Z ?
                                                                                                          p.toBuilder().stones(10).build() : p)
                                                                                                  .collect(Collectors.toList()))
                                                                                    .build()));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_TWO_PIT_Z);
        // Then
        final PitGame pitGameW = mancalaGame.getPitGame(PLAYER_TWO_PIT_W.ordinal());
        assertThat(pitGameW.getStones()).isSameAs(startStoneAmount + 1);
        final PitGame pitGameX = mancalaGame.getPitGame(PLAYER_TWO_PIT_X.ordinal());
        assertThat(pitGameX.getStones()).isSameAs(startStoneAmount);
        final PitGame pitBig = mancalaGame.getPitGame(PLAYER_TWO_PIT_BIG.ordinal());
        assertThat(pitBig.getStones()).isOne();
        final PitGame pitBigOpponent = mancalaGame.getPitGame(PLAYER_ONE_PIT_BIG.ordinal());
        assertThat(pitBigOpponent.getStones()).isZero();
    }

    @Test
    @DisplayName("It should capture the opponent stones if the last reached pit belongs to  player 1 and its empty")
    void itShouldCaptureOpponentStonesIfTheIndexReachPlayerOwnEmptyPit() {
        // Given
        // Player 1 turn where the player pick up one stone from PLAYER_ONE_PIT_E and place it into an empty pit PLAYER_ONE_PIT_F
        // The application will capture all the stone on the opposite side from the opponent PLAYER_TWO_PIT_U
        // and place it in the player 1 big pit PLAYER_ONE_PIT_BIG. 
        final MancalaEntity mancalaWithPlayer1Turn = mancalaEntity.toBuilder()
                .playerId(1)
                .pits(mancalaEntity.getPits()
                              .stream()
                              .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_E ? p.toBuilder().stones(1).build() : p)
                              .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_F ? p.toBuilder().stones(0).build() : p)
                              .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_U ? p.toBuilder().stones(9).build() : p)
                              .collect(Collectors.toList())).build();
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaWithPlayer1Turn));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_ONE_PIT_E);
        // Then
        final PitGame pitBig = mancalaGame.getPitGame(PLAYER_ONE_PIT_BIG.ordinal());
        assertThat(pitBig.getStones()).isSameAs(10);
        final PitGame pitLast = mancalaGame.getPitGame(PLAYER_ONE_PIT_F.ordinal());
        assertThat(pitLast.getStones()).isZero();
        final PitGame pitOpponentCaptured = mancalaGame.getPitGame(PLAYER_TWO_PIT_U.ordinal());
        assertThat(pitOpponentCaptured.getStones()).isZero();
    }

    @Test
    @DisplayName("It should not capture the opponent stones if the last reached pit belongs to player 1" +
            " and its empty and the opponent pit is also empty")
    void itShouldNotCaptureOpponentStonesIfTheIndexReachPlayerOneEmptyPitAndOpponentEmpty() {
        // Given
        final MancalaEntity mancalaWithPlayer1Turn = mancalaEntity.toBuilder()
                .playerId(1)
                .pits(mancalaEntity.getPits()
                              .stream()
                              .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_E ? p.toBuilder().stones(1).build() : p)
                              .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_F ? p.toBuilder().stones(0).build() : p)
                              .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_U ? p.toBuilder().stones(0).build() : p)
                              .collect(Collectors.toList())).build();
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaWithPlayer1Turn));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_ONE_PIT_E);
        // Then
        final PitGame pitBig = mancalaGame.getPitGame(PLAYER_ONE_PIT_BIG.ordinal());
        assertThat(pitBig.getStones()).isSameAs(0);
        final PitGame pitLast = mancalaGame.getPitGame(PLAYER_ONE_PIT_F.ordinal());
        assertThat(pitLast.getStones()).isOne();
    }

    @Test
    @DisplayName("It should capture the opponent stones if the last reached pit belongs to player 2 and its empty")
    void itShouldCaptureOpponentStonesIfTheIndexReachPlayerTwoEmptyPit() {
        // Given
        // Player 2 turn where the player pick up one stone from PLAYER_TWO_PIT_V and place it into an empty pit PLAYER_TWO_PIT_Y
        // The application will capture all the stone on the opposite side from the opponent PLAYER_ONE_PIT_B
        // and place it in the player 2 big pit PLAYER_TWO_PIT_BIG. 
        final MancalaEntity mancalaWithPlayer1Turn = mancalaEntity.toBuilder()
                .playerId(2)
                .pits(mancalaEntity.getPits()
                              .stream()
                              .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_V ? p.toBuilder().stones(3).build() : p)
                              .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Y ? p.toBuilder().stones(0).build() : p)
                              .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_B ? p.toBuilder().stones(5).build() : p)
                              .collect(Collectors.toList())).build();
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaWithPlayer1Turn));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_TWO_PIT_V);
        // Then
        final PitGame pitBig = mancalaGame.getPitGame(PLAYER_TWO_PIT_BIG.ordinal());
        assertThat(pitBig.getStones()).isSameAs(6);
        final PitGame pitLast = mancalaGame.getPitGame(PLAYER_TWO_PIT_Y.ordinal());
        assertThat(pitLast.getStones()).isZero();
        final PitGame pitOpponentCaptured = mancalaGame.getPitGame(PLAYER_ONE_PIT_B.ordinal());
        assertThat(pitOpponentCaptured.getStones()).isZero();
    }


    @Test
    @DisplayName("It should not capture the opponent stones if the last reached pit belongs to player 2" +
            " and its empty and the opponent pit is also empty")
    void itShouldNotCaptureOpponentStonesIfTheIndexReachPlayerTwoEmptyPitAndOpponentEmpty() {
        // Given
        final MancalaEntity mancalaWithPlayer1Turn = mancalaEntity.toBuilder()
                .playerId(2)
                .pits(mancalaEntity.getPits()
                              .stream()
                              .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_X ? p.toBuilder().stones(1).build() : p)
                              .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Y ? p.toBuilder().stones(0).build() : p)
                              .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_B ? p.toBuilder().stones(0).build() : p)
                              .collect(Collectors.toList())).build();
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaWithPlayer1Turn));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_TWO_PIT_X);
        // Then
        final PitGame pitBig = mancalaGame.getPitGame(PLAYER_TWO_PIT_BIG.ordinal());
        assertThat(pitBig.getStones()).isSameAs(0);
        final PitGame pitLast = mancalaGame.getPitGame(PLAYER_TWO_PIT_Y.ordinal());
        assertThat(pitLast.getStones()).isOne();
    }
    
    @Test
    @DisplayName("It should make player 1 as a winner if he has no stones left.")
    void itShouldAddWinnerIfPlayer1HasNoMoreStones() {
        // Given
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                    .playerId(1)
                                                                                    .pits(mancalaEntity.getPits()
                                                                                                  .stream()
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_A ? p.toBuilder().stones(0).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_B ? p.toBuilder().stones(0).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_C ? p.toBuilder().stones(0).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_D ? p.toBuilder().stones(0).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_E ? p.toBuilder().stones(0).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_F ? p.toBuilder().stones(1).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_BIG ? p.toBuilder().stones(9).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_BIG ? p.toBuilder().stones(5).build() : p)
                                                                                                  .collect(Collectors.toList()))
                                                                                    .build()));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_ONE_PIT_F);
        // Then
        //TODO: add test
        assertThat(mancalaGame.getPlayerWinner()).isSameAs(PLAYER_1);
    }
    
    @Test
    @DisplayName("It should make player 2 as a winner if he has no stones left.")
    void itShouldAddWinnerIfPlayer2HasNoMoreStones() {
        // Given
        given(mockMancalaRepository.findById(any())).willReturn(Optional.of(mancalaEntity.toBuilder()
                                                                                    .playerId(2)
                                                                                    .pits(mancalaEntity.getPits()
                                                                                                  .stream()
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_U ? p.toBuilder().stones(0).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_V ? p.toBuilder().stones(0).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_W ? p.toBuilder().stones(0).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_X ? p.toBuilder().stones(0).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Y ? p.toBuilder().stones(0).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_Z ? p.toBuilder().stones(1).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_TWO_PIT_BIG ? p.toBuilder().stones(9).build() : p)
                                                                                                  .map(p -> p.getPitPlace() == PLAYER_ONE_PIT_BIG ? p.toBuilder().stones(5).build() : p)
                                                                                                  .collect(Collectors.toList()))
                                                                                    .build()));
        // When
        final MancalaGame mancalaGame = mancalaService.updateGame(stringMancalaUUID, PLAYER_TWO_PIT_Z);
        // Then
        //TODO: add test
        assertThat(mancalaGame.getPlayerWinner()).isSameAs(PLAYER_2);
    }
}