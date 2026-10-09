package com.fiap.siaes.supplies.infrastructure.persistence.adapter;

import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SuppliesRepositoryAdapter implements SuppliesRepository {

//    private final SuppliesJpaRepository suppliesJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Supplies save(Supplies supplies) {
//        var entity = SuppliesMapper.toEntity(supplies);
//        var saved = this.suppliesJpaRepository.save(entity);
//        return SuppliesMapper.toDomain(saved);
        return null;
    }

    @Override
    public Optional<Supplies> findById(SuppliesId id) {
//        return this.suppliesJpaRepository.findById(id).map(SuppliesMapper::toDomain);
        return null;
    }

    @Override
    public List<Supplies> findAll() {
//        return this.suppliesJpaRepository.findAll().stream().map(SuppliesMapper::toDomain).toList();
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(SuppliesId id) {
//        this.suppliesJpaRepository.deleteById(id);
    }
}
