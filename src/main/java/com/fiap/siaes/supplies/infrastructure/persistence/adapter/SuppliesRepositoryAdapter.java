package com.fiap.siaes.supplies.infrastructure.persistence.adapter;

import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import com.fiap.siaes.supplies.infrastructure.persistence.mapper.SuppliesMapper;
import com.fiap.siaes.supplies.infrastructure.persistence.repository.SuppliesJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SuppliesRepositoryAdapter implements SuppliesRepository {

    private final SuppliesJpaRepository suppliesJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Supplies save(Supplies supplies) {
        var entity = SuppliesMapper.toEntity(supplies);
        var saved = this.suppliesJpaRepository.save(entity);
        return SuppliesMapper.toDomain(saved);
    }

    @Override
    public Optional<Supplies> findById(SuppliesId id) {
        return this.suppliesJpaRepository.findById(id).map(SuppliesMapper::toDomain);
    }

    @Override
    public List<Supplies> findAll() {
        return this.suppliesJpaRepository.findAll().stream().map(SuppliesMapper::toDomain).toList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(SuppliesId id) {
        this.suppliesJpaRepository.deleteById(id);
    }
}
