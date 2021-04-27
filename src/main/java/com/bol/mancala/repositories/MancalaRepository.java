package com.bol.mancala.repositories;

import com.bol.mancala.entities.MancalaEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MancalaRepository extends CrudRepository<MancalaEntity, UUID> {
}
