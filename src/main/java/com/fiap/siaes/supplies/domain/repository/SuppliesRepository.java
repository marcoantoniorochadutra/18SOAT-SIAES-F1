package com.fiap.siaes.supplies.domain.repository;

import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

import java.util.List;

public interface SuppliesRepository extends RepositoryBase<Supplies, SuppliesId> {

    List<Supplies> findAll();

    void deleteById(SuppliesId id);
}
