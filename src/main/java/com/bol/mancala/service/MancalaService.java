package com.bol.mancala.service;

import com.bol.mancala.entities.MancalaEntity;
import com.bol.mancala.entities.PitEntity;
import com.bol.mancala.exception.mancala.MancalaBigPitNotAllowed;
import com.bol.mancala.exception.mancala.MancalaGeneralException;
import com.bol.mancala.exception.mancala.MancalaNotFoundException;
import com.bol.mancala.exception.mancala.MancalaPLayerNotAllowedToUseOpponentPits;
import com.bol.mancala.exception.mancala.MancalaPitIsEmpty;
import com.bol.mancala.model.MancalaGame;
import com.bol.mancala.model.MancalaPlayer;
import com.bol.mancala.model.PitGame;
import com.bol.mancala.model.PitPlace;
import com.bol.mancala.repositories.MancalaRepository;
import com.bol.mancala.validators.PitBigSelectedValidator;
import com.bol.mancala.validators.PitNotExistsValidator;
import com.bol.mancala.validators.PitOpponentUsedValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

/**
 * Mancala service class.
 */
@Service
@Slf4j
public class MancalaService {

    /**
     * Amount of the stones given by the application properties.
     */
    @Value("${start.stone.amount}")
    private Integer startStoneAmount;
    
    /**
     * Validates if the player uses the opponent pit.
     */
    private final PitOpponentUsedValidator pitOpponentUsedValidator;
    
    /**
     * Validates if the player uses one not existing pit.
     */
    private final PitNotExistsValidator pitNotExistsValidator;
    
    /**
     * Validates if the player uses one of the big pits.
     */
    private final PitBigSelectedValidator pitBigSelectedValidator;

    /**
     * Mancala Repository
     */
    private final MancalaRepository mancalaRepository;

    /**
     * Constructor.
     *
     * @param pitOpponentUsedValidator pitOpponentUsedValidator
     * @param pitNotExistsValidator    pitNotExistsValidator
     * @param pitBigSelectedValidator  pitBigSelectedValidator
     * @param mancalaRepository        mancalaRepository
     */
    public MancalaService(final PitOpponentUsedValidator pitOpponentUsedValidator,
                          final PitNotExistsValidator pitNotExistsValidator,
                          final PitBigSelectedValidator pitBigSelectedValidator,
                          final MancalaRepository mancalaRepository) {
        this.pitOpponentUsedValidator = pitOpponentUsedValidator;
        this.pitNotExistsValidator = pitNotExistsValidator;
        this.pitBigSelectedValidator = pitBigSelectedValidator;
        this.mancalaRepository = mancalaRepository;
    }

    /**
     * Generates a new Mancala Game with all the pits and the stones.
     *
     * @return a new Mancala game with a list of pits and its stones. The stones can be
     * adjust in the property file. A unique ID (UUID) belongs to the Game. This UUID will
     * be used for each next request to update the game.
     */
    public MancalaGame startMancalaNewGame() {
        final MancalaEntity mancala = mancalaRepository.save(new MancalaEntity(UUID.randomUUID(), startStoneAmount));
        return mapMancalaEntityToMancalaGame(mancala);
    }

    /**
     * Reset the Mancala Game by deleting the game id.
     *
     * @param gameId the game id the is previously created by the startMancala endpoint.
     */
    public void deleteMancala(final UUID gameId) {
        final MancalaEntity mancala = mancalaRepository.findById(gameId)
                .orElseThrow(MancalaNotFoundException::new);
        mancalaRepository.delete(mancala);
    }

    /**
     * Update Mancala Game, the following steps will be occurring:
     *
     * 1- Get the Mancala Game from the Database by the Game ID.
     * 2- Map the Entity Object to the Domain Object.
     * 3- Get the stones from the selected pit and empty that pit afterwords.
     * 4- Add stones to each next pit of the game.
     * 5- Do manipulation in the Domain object, capturing, etc..
     * 6- Determine next Player
     * 7- Check game winner.
     * 8- Update the database entity.
     *
     * @param gameId game Id
     * @param pitId  pit Id
     * @return updated Mancala Game.
     */
    public MancalaGame updateGame(final UUID gameId, final PitPlace pitId) {
        
        // Get the Mancala Game from the Database by the Game ID
        final var mancalaEntity = mancalaRepository.findById(gameId)
                .orElseThrow(MancalaNotFoundException::new);

        // Validate selected pit
        validateSelectedPit(mancalaEntity.getPlayerId(), pitId);

        // Map the Entity Object to the Domain Object
        final var mancalaGame = mapMancalaEntityToMancalaGame(mancalaEntity);

        // Get the stones from the selected pit and empty that pit afterwords
        final var selectedPit = mancalaGame.getPitGame(pitId.ordinal());
        final int stonesFromSelectedPit = getStonesFromSelectedPit(selectedPit);
        selectedPit.emptyStone();

        // Add stones to each next pit of the game.
        final int currentPitIndex = addStonesToEachNextPit(mancalaGame, stonesFromSelectedPit, pitId.ordinal());

        // Determine next Player
        determineNextPlayer(mancalaGame, currentPitIndex);

        // Check if one of the Players wins. Add the winner accordingly and delete the game from the db. 
        // If there is no winner, save the game in the database. 
        setWinnerIfAnyAndUpdateEntity(mancalaGame);

        // return the Mancala Game
        return mancalaGame;
    }

    /**
     * Get the stones from the selected pit. An exception will be thrown if there are not stone available.
     *
     * @param selectedPit the selected pit.
     * @return the amount of the stones.
     */
    private int getStonesFromSelectedPit(final PitGame selectedPit) {
        final int stonesFromSelectedPit = selectedPit.getStones();
        if (stonesFromSelectedPit == 0)
            throw new MancalaPitIsEmpty();
        return stonesFromSelectedPit;
    }

    /**
     * Add stones to each next pit.
     *
     * @param mancalaGame           mancalaGame
     * @param stonesFromSelectedPit stones fromSelected pit
     * @param currentPitIndex          currentPitIndex
     * @return the new pit index.
     */
    private int addStonesToEachNextPit(final MancalaGame mancalaGame,
                                       final int stonesFromSelectedPit,
                                       int currentPitIndex) {
        int stones = stonesFromSelectedPit;
        while (stones > 0) {
            // increase the pit index when there are still stones left to be placed
            currentPitIndex++;
            currentPitIndex = restartIndexIfReachedEnd(currentPitIndex);
            final var currentPit = mancalaGame.getPitGame(currentPitIndex);
            if (!isOpponentBigPit(mancalaGame.getPlayer(), currentPit)) {
                if (isPlayerEligibleToCaptureStones(stones, currentPit,
                                                    mancalaGame.getPlayer())) {
                    captureOpponentStones(mancalaGame, currentPitIndex, currentPit);
                } else {
                    currentPit.addStones();
                }
                stones--;
            } 
        }
        return currentPitIndex;
    }

    /**
     * Determine if the player eligible to capture the opponent stones.
     *
     * @param stones          stones
     * @param currentPit      currentPit
     * @param player          player
     * @return check if turn or false
     */
    private boolean isPlayerEligibleToCaptureStones(final int stones,
                                                    final PitGame currentPit,
                                                    final MancalaPlayer player) {
        if (stones != 1) {
            return false;
        }
        if (currentPit.getStones() != 0) {
            return false;
        }
        if (player.equals(PLAYER_1)) {
            return currentPit.getPitPlace().ordinal() < PLAYER_ONE_PIT_BIG.ordinal();
        } else {
            return currentPit.getPitPlace().ordinal() > PLAYER_ONE_PIT_BIG.ordinal()
                    && currentPit.getPitPlace().ordinal() < PLAYER_TWO_PIT_BIG.ordinal(); 
        }
    }

    /**
     * Capture the stones of the opponent.
     * 
     * @param mancalaGame   mancalaGame
     * @param currentPitIndex  currentPitIndex
     * @param currentPit       currentPit
     */
    private void captureOpponentStones(final MancalaGame mancalaGame, final int currentPitIndex,
                                       final PitGame currentPit) {
        final var pitListSizeWithoutBigPits = mancalaGame.getPits().size() - 2;
        final var pitOpponent = mancalaGame.getPitGame(pitListSizeWithoutBigPits - currentPitIndex);
        final var stonesOpponent = pitOpponent.getStones();
        if (stonesOpponent == 0) {
            currentPit.addStones();
        } else {
            pitOpponent.emptyStone();
            final int totalCapturedStones = stonesOpponent + 1;
            final PitGame bigPitPlayer;

            if (mancalaGame.getPlayer().equals(PLAYER_1)) {
                bigPitPlayer = mancalaGame.getPitGame(PLAYER_ONE_PIT_BIG.ordinal());
            } else {
                bigPitPlayer = mancalaGame.getPitGame(PLAYER_TWO_PIT_BIG.ordinal());
            }
            bigPitPlayer.addStonesToBigPit(totalCapturedStones);
        }
    }

    /**
     * If the current pit index reached its end, restart it by setting it to zero.
     *
     * @param currentPitIndex current pit index
     * @return 0 if the current pit index is 14, otherwise rerun the same.
     */
    private int restartIndexIfReachedEnd(final int currentPitIndex) {
        return currentPitIndex % 14;
    }

    /**
     * Skip the opponent big pit when the current player reach it.
     *
     * @param player  mancala player
     * @param nextPit nextPit
     * @return the check if the pit is the opponent big pit or not.
     */
    private boolean isOpponentBigPit(final MancalaPlayer player, final PitGame nextPit) {
        if (player.equals(PLAYER_1)) {
            return nextPit.getPitPlace().equals(PLAYER_TWO_PIT_BIG);
        } else {
            return nextPit.getPitPlace().equals(PLAYER_ONE_PIT_BIG);
        }
    }

    /**
     * Determine the next player turn. If the player end at his own big pit, he may
     * play one more time. Otherwise the next player will have the turn.
     *
     * @param mancalaGame mancala game
     * @param currentPitIndex the pit index where the current player end at.
     */
    private void determineNextPlayer(final MancalaGame mancalaGame, final int currentPitIndex) {
        if (mancalaGame.getPlayer().equals(PLAYER_1) 
                && currentPitIndex != PLAYER_ONE_PIT_BIG.ordinal()) {
            mancalaGame.setPlayer(PLAYER_2);
        } else if (mancalaGame.getPlayer().equals(PLAYER_2) 
                && currentPitIndex != PLAYER_TWO_PIT_BIG.ordinal()) {
            mancalaGame.setPlayer(PLAYER_1);
        }
    }

    /**
     * Check if the pits of the current player or the opponent player are empty. If so decide the winner
     * based on the total of the stones in each big pit. The total will be counted based on all pits available.
     * After the the stones are add to opponent player, empty the pit list.
     * The one who has more stones in his own big bit, is the winner. After adding the winner,
     * delete the game from the database. If there is no winner, save the game again in the database.
     *
     * @param mancalaGame mancala game.
     */
    private void setWinnerIfAnyAndUpdateEntity(final MancalaGame mancalaGame) {
        final var bigBitPlayer1 = mancalaGame.getPitGame(PLAYER_ONE_PIT_BIG.ordinal());
        final var bigBitPlayer2 = mancalaGame.getPitGame(PLAYER_TWO_PIT_BIG.ordinal());
        List<PitPlace> pitsPlayer1 = List.of(PLAYER_ONE_PIT_A, PLAYER_ONE_PIT_B, PLAYER_ONE_PIT_C, PLAYER_ONE_PIT_D,
                                             PLAYER_ONE_PIT_E, PLAYER_ONE_PIT_F);
        List<PitPlace> pitsPlayer2 = List.of(PLAYER_TWO_PIT_U, PLAYER_TWO_PIT_V, PLAYER_TWO_PIT_W, PLAYER_TWO_PIT_X,
                                             PLAYER_TWO_PIT_Y, PLAYER_TWO_PIT_Z);

        if (arePitsEmptyForPlayer(mancalaGame.getPits(), pitsPlayer1)) {
            bigBitPlayer2.addStonesToBigPit(getSumStonesForPlayer(mancalaGame.getPits(), pitsPlayer2));
            emptyGamePitsFromStonesForPlayer(mancalaGame, pitsPlayer2);
            if (bigBitPlayer1.getStones() > bigBitPlayer2.getStones()) {
                mancalaGame.setPlayerWinner(PLAYER_1);
            } else {
                mancalaGame.setPlayerWinner(PLAYER_2);
            }
            mancalaRepository.delete(mapMancalaGameToMancalaEntity(mancalaGame));
        } else if (arePitsEmptyForPlayer(mancalaGame.getPits(), pitsPlayer2)) {
            bigBitPlayer1.addStonesToBigPit(getSumStonesForPlayer(mancalaGame.getPits(), pitsPlayer1));
            emptyGamePitsFromStonesForPlayer(mancalaGame, pitsPlayer1);
            if (bigBitPlayer1.getStones() > bigBitPlayer2.getStones()) {
                mancalaGame.setPlayerWinner(PLAYER_1);
            } else {
                mancalaGame.setPlayerWinner(PLAYER_2);
            }
            mancalaRepository.delete(mapMancalaGameToMancalaEntity(mancalaGame));
        } else {
            mancalaRepository.save(mapMancalaGameToMancalaEntity(mancalaGame));
        }
    }

    /**
     * Are all the pits for the provided player empty from stones?
     *
     * @param pitsGame all game pits
     * @param pits     player pits
     * @return check if all the provided player pits are empty
     */
    private boolean arePitsEmptyForPlayer(final List<PitGame> pitsGame, final List<PitPlace> pits) {
        return pitsGame.stream()
                .filter(p -> pits.contains(p.getPitPlace()))
                .allMatch(p -> p.getStones() == 0);
    }

    /**
     * Get the sum of the stones in all pits for the provided player.
     *
     * @param pitsGame all game pits
     * @param pits     player pits
     * @return sum of the stones in all pits.
     */
    private int getSumStonesForPlayer(final List<PitGame> pitsGame, final List<PitPlace> pits) {
        return pitsGame.stream()
                .filter(p -> pits.contains(p.getPitPlace()))
                .map(PitGame::getStones)
                .mapToInt(e -> e)
                .sum();
    }

    /**
     * Empty the list from the stones.
     *
     * @param mancalaGame mancalaGame
     * @param pits        player pits
     */
    private void emptyGamePitsFromStonesForPlayer(final MancalaGame mancalaGame, final List<PitPlace> pits) {
        final var emptiedPits = mancalaGame.getPits()
                .stream()
                .map(p -> pits.contains(p.getPitPlace()) ? p.toBuilder().stones(0).build() : p)
                .collect(Collectors.toList());
        mancalaGame.setPits(emptiedPits);
    }

    /**
     * Map mancala game domain to mancala entity.
     *
     * @param mancalaGame mancala game domain.
     * @return mancala entity.
     */
    private MancalaEntity mapMancalaGameToMancalaEntity(final MancalaGame mancalaGame) {
        final List<PitEntity> pits = mancalaGame.getPits()
                .stream()
                .map(p -> PitEntity.builder().pitPlace(p.getPitPlace()).stones(p.getStones()).build())
                .collect(Collectors.toList());
        return MancalaEntity.builder()
                .mancalaId(mancalaGame.getGameId())
                .playerId(mancalaGame.getPlayer().getPlayerId())
                .pits(pits)
                .build();
    }

    /**
     * Map mancalaEntity entity to mancala game domain.
     *
     * @param mancalaEntity mancala entity.
     * @return mancala game domain.
     */
    private MancalaGame mapMancalaEntityToMancalaGame(final MancalaEntity mancalaEntity) {
        final List<PitGame> pitGames = mancalaEntity.getPits()
                .stream()
                .map(p -> PitGame.builder().stones(p.getStones()).pitPlace(p.getPitPlace()).build())
                .collect(Collectors.toList());
        return MancalaGame.builder()
                .gameId(mancalaEntity.getMancalaId())
                .player(mancalaEntity.getPlayerId() == 1 ? PLAYER_1 : PLAYER_2)
                .pits(pitGames)
                .build();
    }

    /**
     * Validate if the chosen Pit is correct. These are not correct chosen pit:
     * 
     * 1. Pit index is within game range.
     * 2. Pit index is one of the big pits. 
     * 3. Pit index is the one of the opponent pits.
     *
     * @param mancalaPlayerId player ID
     * @param selectedPit     selectedPit
     */
    private void validateSelectedPit(final Integer mancalaPlayerId, PitPlace selectedPit) {
        if (pitNotExistsValidator.test(selectedPit.ordinal())) {
            throw new MancalaGeneralException("The selected index is not within the game range!");
        }
        if (pitBigSelectedValidator.test(selectedPit)) {
            throw new MancalaBigPitNotAllowed();
        }
        if (pitOpponentUsedValidator.test(mancalaPlayerId == 1 ? PLAYER_1.name() : PLAYER_2.name(), selectedPit.ordinal())) {
            throw new MancalaPLayerNotAllowedToUseOpponentPits();
        }
    }
}
