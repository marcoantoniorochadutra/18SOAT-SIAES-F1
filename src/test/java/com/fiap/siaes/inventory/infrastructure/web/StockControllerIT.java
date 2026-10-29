package com.fiap.siaes.inventory.infrastructure.web;

import com.fiap.siaes.inventory.InventoryTestFactory;
import com.fiap.siaes.inventory.application.usecase.MoveStockUseCase.MoveStockCommand;
import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.supplies.SuppliesTestFactory;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.utils.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.utils.TestFactory.persist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Test - Stock")
class StockControllerIT extends IntegrationTestUtils {

    private static final int INITIAL_STOCK = 10;

    private String managerToken;
    private Supplies supplies;

    @BeforeEach
    void setUp() {
        this.managerToken = super.loginAs(UserRole.MANAGER);
        this.supplies = persist(super.suppliesRepository, SuppliesTestFactory.createSupplies());
        persist(super.stockItemRepository, InventoryTestFactory.createStockItem(this.supplies.getId(), INITIAL_STOCK));
    }

    @Test
    @DisplayName("[200] Deve buscar o estoque do insumo")
    void shouldGetStock() throws Exception {
        super.executeGet(this.stockUrl(), this.managerToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.supplies.id", this.supplies.getId().toString()))
                .andExpect(jsonValue("$.quantity", INITIAL_STOCK))
                .andExpect(jsonValue("$.availableQuantity", INITIAL_STOCK));
    }

    @Test
    @DisplayName("[200] Deve movimentar o estoque e registrar a movimentação")
    void shouldMoveStockAndListMovements() throws Exception {
        var command = MoveStockCommand.builder()
                .type(StockMovementType.ENTRY)
                .quantity(5)
                .reason("Compra")
                .build();

        super.executePatch(this.stockUrl(), this.managerToken, command)
                .andExpect(status().isOk());

        assertEquals(INITIAL_STOCK + 5, super.stockItemRepository.findByIdOrThrowNotFound(this.supplies.getId()).getQuantity());

        var movements = super.stockMovementRepository.findAllBySuppliesId(this.supplies.getId(), PageQuery.of(0, 10)).content();
        assertEquals(1, movements.size());
        assertEquals(StockMovementType.ENTRY, movements.getFirst().type());
    }

    @Test
    @DisplayName("[403] Mecânico não deve consultar o estoque")
    void shouldNotGetStockAsMechanic() throws Exception {
        var mechanicToken = super.loginAs(UserRole.MECHANIC);

        super.executeGet(this.stockUrl(), mechanicToken)
                .andExpect(status().isForbidden());
    }

    private String stockUrl() {
        return StockController.ENDPOINT.replace("{suppliesId}", this.supplies.getId().toString());
    }
}
