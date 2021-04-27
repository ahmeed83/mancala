package com.bol.mancala.entities;

import com.bol.mancala.exception.mancala.MancalaGeneralException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

/**
 * Mancala Entity.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class MancalaEntity {

    /**
     * Mancala unique ID. Each game will have it's own ID.
     */
    @Id
    @NotNull
    @Column(name = "mancala_id", unique = true)
    private UUID mancalaId;

    /**
     * The player ID wil be generated for every new game. It can be 1 or 2.
     */
    private Integer playerId;

    /**
     * List of the Pits that the game will have. For every game a list of the pits will be
     * generated with its own data.
     * Cascade type all so that we do all the operations for the pits that we do for the
     * game, by deleting the game the pits will be also deleted or updated.
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    private List<PitEntity> pits;

    /**
     * Mancala constructor. Generate a game with a list of pits contains stones.
     *
     * @param mancalaId        mancala game id.
     * @param startStoneAmount stones amount.
     */
    public MancalaEntity(final UUID mancalaId, final Integer startStoneAmount) {
        if (startStoneAmount <= 0)
            throw new MancalaGeneralException("Stones amount should be greater than zero!");
        final List<PitEntity> pitEntityList = new ArrayList<>();
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_ONE_PIT_A).stones(startStoneAmount).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_ONE_PIT_B).stones(startStoneAmount).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_ONE_PIT_C).stones(startStoneAmount).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_ONE_PIT_D).stones(startStoneAmount).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_ONE_PIT_E).stones(startStoneAmount).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_ONE_PIT_F).stones(startStoneAmount).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_ONE_PIT_BIG).stones(0).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_TWO_PIT_U).stones(startStoneAmount).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_TWO_PIT_V).stones(startStoneAmount).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_TWO_PIT_W).stones(startStoneAmount).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_TWO_PIT_X).stones(startStoneAmount).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_TWO_PIT_Y).stones(startStoneAmount).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_TWO_PIT_Z).stones(startStoneAmount).build());
        pitEntityList.add(PitEntity.builder().pitPlace(PLAYER_TWO_PIT_BIG).stones(0).build());
        this.pits = pitEntityList;
        this.mancalaId = mancalaId;
        playerId = new Random().nextInt(2) + 1;
    }
}