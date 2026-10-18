package com.fiap.siaes.inventory.infrastructure.persistence.adapter;

import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.inventory.infrastructure.persistence.entity.StockItemJpaEntity;
import com.fiap.siaes.inventory.infrastructure.persistence.mapper.StockItemMapper;
import com.fiap.siaes.inventory.infrastructure.persistence.repository.StockItemJpaRepository;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StockItemRepositoryAdapter implements StockItemRepository {

    private final StockItemJpaRepository stockItemJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public StockItem save(StockItem stockItem) {
        StockItemJpaEntity entity = StockItemMapper.toEntity(stockItem);

        StockItemJpaEntity saved = this.stockItemJpaRepository.save(entity);

        return StockItemMapper.toDomain(saved);
    }

    @Override
    public Optional<StockItem> findById(SuppliesId suppliesId) {
        return this.stockItemJpaRepository.findById(suppliesId).map(StockItemMapper::toDomain);
    }

    @Override
    public boolean existsById(SuppliesId suppliesId) {
        return this.stockItemJpaRepository.existsById(suppliesId);
    }
}
