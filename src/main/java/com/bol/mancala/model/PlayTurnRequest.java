package com.bol.mancala.model;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Builder
@Getter
public class PlayTurnRequest {
    @NotNull
    private final PitPlace selectedPit;
    @NotNull
    private final String gameId;
}
