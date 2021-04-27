package com.bol.mancala.entities;

import com.bol.mancala.model.PitPlace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Pit Entity.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class PitEntity {

    /**
     * Pit unique ID. Each pit has it's own ID.
     */
    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "pitId", unique = true)
    private UUID pitId;

    /**
     * Pit unique Name. Each pit has it's unique name.
     */
    private PitPlace pitPlace;

    /**
     * Number of stones each Pit has.
     */
    private Integer stones;
}