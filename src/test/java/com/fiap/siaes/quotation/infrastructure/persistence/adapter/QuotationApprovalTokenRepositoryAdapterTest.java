package com.fiap.siaes.quotation.infrastructure.persistence.adapter;

import com.fiap.siaes.quotation.domain.exception.QuotationApprovalTokenInvalidException;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationApprovalTokenJpaEntity;
import com.fiap.siaes.quotation.infrastructure.persistence.mapper.QuotationApprovalTokenMapper;
import com.fiap.siaes.quotation.infrastructure.persistence.repository.QuotationApprovalTokenJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.fiap.siaes.quotation.QuotationTestFactory.createApprovalToken;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Persistence Adapter - Quotation Approval Token")
class QuotationApprovalTokenRepositoryAdapterTest {

    @Mock
    private QuotationApprovalTokenJpaRepository quotationApprovalTokenJpaRepository;

    @InjectMocks
    private QuotationApprovalTokenRepositoryAdapter quotationApprovalTokenRepositoryAdapter;

    @Test
    @DisplayName("Deve salvar o token de aprovação e retornar o domínio reconstruído")
    void shouldSaveApprovalToken() {
        QuotationApprovalToken token = createApprovalToken(QuotationId.generate());

        when(this.quotationApprovalTokenJpaRepository.save(any(QuotationApprovalTokenJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        QuotationApprovalToken saved = this.quotationApprovalTokenRepositoryAdapter.save(token);

        QuotationApprovalTokenJpaEntity savedEntity = captureSave(this.quotationApprovalTokenJpaRepository,
                                                                  QuotationApprovalTokenJpaEntity.class);

        assertEquals(token.getId(), savedEntity.getId());
        assertEquals(token.getQuotationId(), savedEntity.getQuotationId());
        assertEquals(token.getId(), saved.getId());
    }

    @Test
    @DisplayName("Deve buscar o token de aprovação pelo id")
    void shouldFindApprovalTokenById() {
        QuotationApprovalTokenJpaEntity entity = QuotationApprovalTokenMapper.toEntity(createApprovalToken(QuotationId.generate()));

        when(this.quotationApprovalTokenJpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        QuotationApprovalToken found = this.quotationApprovalTokenRepositoryAdapter.findByIdOrThrowInvalid(entity.getId());

        assertEquals(entity.getId(), found.getId());
        assertEquals(entity.getQuotationId(), found.getQuotationId());
    }

    @Test
    @DisplayName("Deve lançar erro de token inválido quando o token não existir")
    void shouldThrowInvalidWhenApprovalTokenNotFound() {
        var id = QuotationApprovalTokenId.generate();

        when(this.quotationApprovalTokenJpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(QuotationApprovalTokenInvalidException.class,
                     () -> this.quotationApprovalTokenRepositoryAdapter.findByIdOrThrowInvalid(id));
    }

    @Test
    @DisplayName("Deve buscar o token mais recente do orçamento")
    void shouldFindLatestApprovalTokenByQuotationId() {
        var quotationId = QuotationId.generate();
        QuotationApprovalTokenJpaEntity entity = QuotationApprovalTokenMapper.toEntity(createApprovalToken(quotationId));

        when(this.quotationApprovalTokenJpaRepository.findFirstByQuotationIdOrderByCreatedAtDesc(quotationId))
                .thenReturn(Optional.of(entity));

        assertThat(this.quotationApprovalTokenRepositoryAdapter.findLatestByQuotationId(quotationId))
                .hasValueSatisfying(token -> assertEquals(entity.getId(), token.getId()));
    }
}
