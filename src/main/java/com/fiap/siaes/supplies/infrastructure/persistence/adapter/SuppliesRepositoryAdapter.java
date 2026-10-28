package com.fiap.siaes.supplies.infrastructure.persistence.adapter;

import com.fiap.siaes.sk.infrastructure.persistence.PageResponses;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.application.dto.SuppliesFilter;
import com.fiap.siaes.supplies.application.dto.SuppliesResponse;
import com.fiap.siaes.supplies.application.port.SuppliesQueryPort;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.Sku;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import com.fiap.siaes.supplies.infrastructure.persistence.entity.SuppliesJpaEntity;
import com.fiap.siaes.supplies.infrastructure.persistence.mapper.SuppliesMapper;
import com.fiap.siaes.supplies.infrastructure.persistence.repository.SuppliesJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SuppliesRepositoryAdapter implements SuppliesRepository, SuppliesQueryPort {

    private final SuppliesJpaRepository suppliesJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Supplies save(Supplies supplies) {
        SuppliesJpaEntity entity = SuppliesMapper.toEntity(supplies);

        SuppliesJpaEntity saved = this.suppliesJpaRepository.save(entity);

        return SuppliesMapper.toDomain(saved);
    }

    @Override
    public Optional<Supplies> findById(SuppliesId id) {
        return this.suppliesJpaRepository.findById(id)
                .map(SuppliesMapper::toDomain);
    }

    @Override
    public boolean existsBySku(Sku sku) {
        return this.suppliesJpaRepository.existsBySku(sku.getValue());
    }

    @Override
    public boolean existsBySkuAndIdNot(Sku sku, SuppliesId id) {
        return this.suppliesJpaRepository.existsBySkuAndIdNot(sku.getValue(), id);
    }

    @Override
    public PageResponse<SuppliesResponse> findAllByFilter(SuppliesFilter filter, PageQuery pageQuery) {
        Slice<SuppliesResponse> results = this.suppliesJpaRepository.findAllSupplies(filter.sku(), filter.description(), filter.active(),
                                                                                      PageRequest.of(pageQuery.page(), pageQuery.size()))
                .map(SuppliesMapper::toResponse);

        return PageResponses.from(results);
    }
}
