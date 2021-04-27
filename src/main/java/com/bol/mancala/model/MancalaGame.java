package com.bol.mancala.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MancalaGame {
    private UUID gameId;
    List<PitGame> pits;
    MancalaPlayer player;
    MancalaPlayer playerWinner;
    

    public PitGame getPitGame(final Integer pitId) {
        return pits.get(pitId);
    }
}
