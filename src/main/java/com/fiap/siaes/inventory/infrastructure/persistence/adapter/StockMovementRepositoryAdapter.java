package com.fiap.siaes.inventory.infrastructure.persistence.adapter;

import com.fiap.siaes.inventory.application.dto.StockMovementResponse;
import com.fiap.siaes.inventory.domain.model.StockMovement;
import com.fiap.siaes.inventory.domain.model.vo.StockMovementId;
import com.fiap.siaes.inventory.domain.repository.StockMovementRepository;
import com.fiap.siaes.inventory.infrastructure.persistence.entity.StockMovementJpaEntity;
import com.fiap.siaes.inventory.infrastructure.persistence.mapper.StockMovementMapper;
import com.fiap.siaes.inventory.infrastructure.persistence.repository.StockMovementJpaRepository;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StockMovementRepositoryAdapter implements StockMovementRepository {

    private final StockMovementJpaRepository stockMovementJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public StockMovement save(StockMovement stockMovement) {
        StockMovementJpaEntity entity = StockMovementMapper.toEntity(stockMovement);

        StockMovementJpaEntity saved = this.stockMovementJpaRepository.save(entity);

        return StockMovementMapper.toDomain(saved);
    }

    @Override
    public Optional<StockMovement> findById(StockMovementId id) {
        return this.stockMovementJpaRepository.findById(id).map(StockMovementMapper::toDomain);
    }

    @Override
    public PageResponse<StockMovementResponse> findAllBySuppliesId(SuppliesId suppliesId, PageQuery pageQuery) {
        Slice<StockMovementResponse> results = this.stockMovementJpaRepository
                .findAllBySuppliesIdOrderByOccurredAtDesc(suppliesId, PageRequest.of(pageQuery.page(), pageQuery.size()))
                .map(StockMovementMapper::toDomain)
                .map(StockMovementResponse::from);

        return PageResponse.from(results);
    }
}
