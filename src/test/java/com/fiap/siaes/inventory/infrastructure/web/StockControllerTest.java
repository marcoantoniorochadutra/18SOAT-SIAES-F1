package com.fiap.siaes.inventory.infrastructure.web;

import com.fiap.siaes.inventory.application.dto.StockItemResponse;
import com.fiap.siaes.inventory.application.dto.StockMovementResponse;
import com.fiap.siaes.inventory.application.usecase.GetStockItemUseCase;
import com.fiap.siaes.inventory.application.usecase.ListStockMovementsUseCase;
import com.fiap.siaes.inventory.application.usecase.ListStockMovementsUseCase.ListStockMovementsCommand;
import com.fiap.siaes.inventory.application.usecase.MoveStockUseCase;
import com.fiap.siaes.inventory.application.usecase.MoveStockUseCase.MoveStockCommand;
import com.fiap.siaes.inventory.domain.exception.InsufficientStockException;
import com.fiap.siaes.inventory.domain.exception.InvalidStockMovementTypeException;
import com.fiap.siaes.inventory.domain.exception.StockAdjustmentReasonRequiredException;
import com.fiap.siaes.inventory.domain.exception.StockItemNotFoundException;
import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.application.dto.SuppliesReferenceResponse;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.utils.ControllerTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.fiap.siaes.inventory.InventoryTestFactory.QUANTITY;
import static com.fiap.siaes.inventory.InventoryTestFactory.REASON;
import static com.fiap.siaes.inventory.InventoryTestFactory.RESERVED_QUANTITY;
import static com.fiap.siaes.inventory.InventoryTestFactory.createStockMovement;
import static com.fiap.siaes.supplies.SuppliesTestFactory.DESCRIPTION;
import static com.fiap.siaes.supplies.SuppliesTestFactory.SKU;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Controller Test - Stock")
class StockControllerTest extends ControllerTestUtils {

    @MockitoBean
    private GetStockItemUseCase getStockItemUseCase;

    @MockitoBean
    private MoveStockUseCase moveStockUseCase;

    @MockitoBean
    private ListStockMovementsUseCase listStockMovementsUseCase;

    @Test
    @DisplayName("[200] Deve consultar o estoque da peça/insumo com sucesso")
    void shouldGetStockItem() throws Exception {
        var suppliesId = SuppliesId.generate();
        var response = StockItemResponse.of(SuppliesReferenceResponse.of(suppliesId, SKU, DESCRIPTION), QUANTITY,
                                            RESERVED_QUANTITY, QUANTITY - RESERVED_QUANTITY);

        when(this.getStockItemUseCase.execute(suppliesId)).thenReturn(response);

        super.executeGetAuthenticated(this.endpoint(suppliesId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supplies.id").value(suppliesId.toString()))
                .andExpect(jsonPath("$.supplies.sku").value(SKU))
                .andExpect(jsonPath("$.quantity").value(QUANTITY))
                .andExpect(jsonPath("$.reservedQuantity").value(RESERVED_QUANTITY))
                .andExpect(jsonPath("$.availableQuantity").value(QUANTITY - RESERVED_QUANTITY));
    }

    @Test
    @DisplayName("[404] Não deve consultar estoque inexistente")
    void shouldNotGetNonExistentStockItem() throws Exception {
        var suppliesId = SuppliesId.generate();

        when(this.getStockItemUseCase.execute(suppliesId))
                .thenThrow(new StockItemNotFoundException(suppliesId.toString()));

        super.executeGetAuthenticated(this.endpoint(suppliesId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Estoque não encontrado"));
    }

    @Test
    @DisplayName("[200] Deve registrar movimentação de estoque com sucesso")
    void shouldMoveStock() throws Exception {
        var suppliesId = SuppliesId.generate();
        var command = this.moveCommandBuilder().build();

        when(this.moveStockUseCase.execute(command.with(suppliesId, AUTHENTICATED_USER_ADMIN))).thenReturn(suppliesId);

        super.executePatchAuthenticated(this.endpoint(suppliesId), command)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(suppliesId.toString()))
                .andExpect(header().string("Location", endsWith("/api/v1/supplies/" + suppliesId + "/stock")));

        verify(this.moveStockUseCase).execute(command.with(suppliesId, AUTHENTICATED_USER_ADMIN));
    }

    @Test
    @DisplayName("[400] Não deve registrar movimentação de estoque sem tipo")
    void shouldNotMoveStockWhenTypeIsNull() throws Exception {
        var command = this.moveCommandBuilder().type(null).build();

        super.executePatchAuthenticated(this.endpoint(SuppliesId.generate()), command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("MoveStockCommand.type.notNull"));

        verifyNoInteractions(this.moveStockUseCase);
    }

    @Test
    @DisplayName("[400] Não deve registrar movimentação de estoque com motivo acima do tamanho máximo")
    void shouldNotMoveStockWhenReasonExceedsMaxSize() throws Exception {
        var command = this.moveCommandBuilder().reason("M".repeat(256)).build();

        super.executePatchAuthenticated(this.endpoint(SuppliesId.generate()), command)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("reason"))
                .andExpect(jsonPath("$.fields[0].code").value("Size"));

        verifyNoInteractions(this.moveStockUseCase);
    }

    @Test
    @DisplayName("[400] Não deve registrar ajuste de estoque sem motivo")
    void shouldNotMoveStockAdjustmentWithoutReason() throws Exception {
        var suppliesId = SuppliesId.generate();
        var command = this.moveCommandBuilder().type(StockMovementType.ADJUSTMENT).build();

        when(this.moveStockUseCase.execute(command.with(suppliesId, AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new StockAdjustmentReasonRequiredException(suppliesId.toString()));

        super.executePatchAuthenticated(this.endpoint(suppliesId), command)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Motivo do ajuste não informado"));
    }

    @Test
    @DisplayName("[400] Não deve registrar movimentação manual com tipo reservado ao sistema")
    void shouldNotMoveStockWithSystemType() throws Exception {
        var suppliesId = SuppliesId.generate();
        var command = this.moveCommandBuilder().type(StockMovementType.CONSUMPTION).build();

        when(this.moveStockUseCase.execute(command.with(suppliesId, AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new InvalidStockMovementTypeException(suppliesId.toString(), StockMovementType.CONSUMPTION.name()));

        super.executePatchAuthenticated(this.endpoint(suppliesId), command)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Tipo de movimentação inválido"));
    }

    @Test
    @DisplayName("[409] Não deve registrar baixa maior que o estoque disponível")
    void shouldNotMoveStockWhenInsufficient() throws Exception {
        var suppliesId = SuppliesId.generate();
        var command = this.moveCommandBuilder().type(StockMovementType.ADJUSTMENT).quantity(-50).reason(REASON).build();

        when(this.moveStockUseCase.execute(command.with(suppliesId, AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new InsufficientStockException(suppliesId.toString(), 6, 50));

        super.executePatchAuthenticated(this.endpoint(suppliesId), command)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Estoque insuficiente"));
    }

    @Test
    @DisplayName("[404] Não deve registrar movimentação de estoque inexistente")
    void shouldNotMoveNonExistentStockItem() throws Exception {
        var suppliesId = SuppliesId.generate();
        var command = this.moveCommandBuilder().build();

        when(this.moveStockUseCase.execute(command.with(suppliesId, AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new StockItemNotFoundException(suppliesId.toString()));

        super.executePatchAuthenticated(this.endpoint(suppliesId), command)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Estoque não encontrado"));
    }

    @Test
    @DisplayName("[200] Deve listar as movimentações de estoque com sucesso")
    void shouldListMovements() throws Exception {
        var suppliesId = SuppliesId.generate();
        var movement = StockMovementResponse.from(createStockMovement());
        var page = new PageResponse<>(List.of(movement), true, 1, 5);

        when(this.listStockMovementsUseCase.execute(ListStockMovementsCommand.of(suppliesId, PageQuery.of(1, 5))))
                .thenReturn(page);

        this.mockMvc.perform(get(this.endpoint(suppliesId) + "/movements").param("page", "1").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(movement.id().toString()))
                .andExpect(jsonPath("$.content[0].type").value(movement.type().name()))
                .andExpect(jsonPath("$.content[0].quantity").value(movement.quantity()))
                .andExpect(jsonPath("$.content[0].reason").value(REASON))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    @DisplayName("[200] Deve listar as movimentações de estoque com a paginação padrão")
    void shouldListMovementsWithDefaultPagination() throws Exception {
        var suppliesId = SuppliesId.generate();
        var page = new PageResponse<StockMovementResponse>(List.of(), false, PageQuery.DEFAULT_PAGE, PageQuery.DEFAULT_SIZE);

        when(this.listStockMovementsUseCase.execute(ListStockMovementsCommand.of(suppliesId, PageQuery.of(null, null))))
                .thenReturn(page);

        super.executeGetAuthenticated(this.endpoint(suppliesId) + "/movements")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.size").value(PageQuery.DEFAULT_SIZE));
    }

    @Test
    @DisplayName("[400] Não deve listar movimentações com tamanho de página inválido")
    void shouldNotListMovementsWhenPageSizeExceedsLimit() throws Exception {
        this.mockMvc.perform(get(this.endpoint(SuppliesId.generate()) + "/movements").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("size"))
                .andExpect(jsonPath("$.fields[0].code").value("Max"));

        verifyNoInteractions(this.listStockMovementsUseCase);
    }

    @Test
    @DisplayName("[404] Não deve listar movimentações de estoque inexistente")
    void shouldNotListMovementsOfNonExistentStockItem() throws Exception {
        var suppliesId = SuppliesId.generate();

        when(this.listStockMovementsUseCase.execute(ListStockMovementsCommand.of(suppliesId, PageQuery.of(null, null))))
                .thenThrow(new StockItemNotFoundException(suppliesId.toString()));

        super.executeGetAuthenticated(this.endpoint(suppliesId) + "/movements")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Estoque não encontrado"));
    }

    private String endpoint(SuppliesId suppliesId) {
        return StockController.ENDPOINT.replace("{suppliesId}", suppliesId.toString());
    }

    private MoveStockCommand.MoveStockCommandBuilder moveCommandBuilder() {
        return MoveStockCommand.builder()
                .type(StockMovementType.ENTRY)
                .quantity(5);
    }
}
