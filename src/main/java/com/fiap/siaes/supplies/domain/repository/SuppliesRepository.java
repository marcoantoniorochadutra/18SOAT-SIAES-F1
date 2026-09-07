package com.fiap.siaes.supplies.domain.repository;

import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.SuppliesId;

import java.util.List;
import java.util.Optional;

public interface SuppliesRepository {

    Supplies save(Supplies supplies);

    Optional<Supplies> findById(SuppliesId id);

    List<Supplies> findAll();

    void deleteById(SuppliesId id);
}
