package com.bol.mancala.controllers;

import com.bol.mancala.model.MancalaGame;
import com.bol.mancala.model.PlayTurnRequest;
import com.bol.mancala.service.MancalaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Mancala API to take the request from the frontend.
 */
@RestController
@RequestMapping("mancala/api/v1")
@AllArgsConstructor
public class MancalaApi {

    /**
     * Mancala service to do all the game logic.
     */
    private final MancalaService mancalaService;

    /**
     * Rest endpoint to start a new Mancala Game.
     *
     * @return a new Mancala game.
     */
    @GetMapping("/create-game")
    public ResponseEntity<MancalaGame> startMancala() {
        return new ResponseEntity<>(mancalaService.startMancalaNewGame(), HttpStatus.OK);
    }

    /**
     * Rest endpoint to reset a Mancala Game.
     *
     * @param gameId the game id.
     */
    @DeleteMapping("/delete-game/{gameId}")
    public ResponseEntity<HttpStatus> deleteMancala(@PathVariable final UUID gameId) {
        mancalaService.deleteMancala(gameId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * Rest endpoint to update a Mancala Game.
     *
     * @param playTurn contains the the game id and the pit id.
     * @return a new Mancala game.
     */
    @PostMapping("/update-game")
    public ResponseEntity<MancalaGame> updateGame(@Valid @RequestBody final PlayTurnRequest playTurn) {
        return new ResponseEntity<>(mancalaService.updateGame(playTurn.getGameId(),
                                                              playTurn.getSelectedPit()), HttpStatus.OK);
    }
}
