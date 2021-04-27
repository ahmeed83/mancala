package com.bol.mancala.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PitGame {
    private PitPlace pitPlace;
    private Integer stones;

    public void emptyStone() {
        this.stones = 0;
    }
    
    public void addStones() {
        this.stones++;
    }

    public Integer getStones() {
        return stones;
    }

    public void addStonesToBigPit(final Integer stones) {
        this.stones += stones;
    }
}
