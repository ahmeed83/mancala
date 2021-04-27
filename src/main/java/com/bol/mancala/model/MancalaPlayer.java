package com.bol.mancala.model;

import lombok.Getter;

@Getter
public enum MancalaPlayer {
    PLAYER_1(1), PLAYER_2(2);

    private Integer playerId;
    
    MancalaPlayer(final int playerId) {
        this.playerId = playerId;
    }
}
