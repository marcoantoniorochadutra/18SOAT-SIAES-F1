package com.fiap.siaes.supplies.infrastructure.web;

import com.fiap.siaes.inventory.InventoryTestFactory;
import com.fiap.siaes.supplies.SuppliesTestFactory;
import com.fiap.siaes.supplies.application.usecase.CreateSuppliesUseCase.CreateSuppliesCommand;
import com.fiap.siaes.supplies.application.usecase.UpdateSuppliesUseCase.UpdateSuppliesCommand;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.utils.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.fiap.siaes.utils.TestFactory.persist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Test - Supplies")
class SuppliesControllerIT extends IntegrationTestUtils {

    private String managerToken;

    @BeforeEach
    void setUp() {
        this.managerToken = super.loginAs(UserRole.MANAGER);
    }

    @Test
    @DisplayName("[201] Deve criar insumo com o estoque inicial")
    void shouldCreateSupplies() throws Exception {
        var command = CreateSuppliesCommand.builder()
                .sku("PST-FREIO-001")
                .description("Pastilha de freio")
                .unitPrice(new BigDecimal("120.00"))
                .initialStock(10)
                .build();

        var id = new SuppliesId(super.createdId(super.executePost(SuppliesController.ENDPOINT, this.managerToken, command)));

        var supplies = super.suppliesRepository.findByIdOrThrowNotFound(id);
        assertEquals("PST-FREIO-001", supplies.getSku().getValue());
        assertTrue(supplies.isActive());
        assertEquals(10, super.stockItemRepository.findByIdOrThrowNotFound(id).getQuantity());
    }

    @Test
    @DisplayName("[200] Deve buscar insumo")
    void shouldGetSupplies() throws Exception {
        var supplies = this.persistSupplies();

        super.executeGet(this.url(supplies), this.managerToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.id", supplies.getId().toString()))
                .andExpect(jsonValue("$.sku", supplies.getSku().getValue()))
                .andExpect(jsonValue("$.active", true));
    }

    @Test
    @DisplayName("[409] Não deve criar insumo com SKU já cadastrado")
    void shouldNotCreateSuppliesWithDuplicatedSku() throws Exception {
        this.persistSupplies();

        super.executePost(SuppliesController.ENDPOINT, this.managerToken,
                          CreateSuppliesCommand.builder()
                                  .sku(SuppliesTestFactory.SKU)
                                  .description("Outro filtro")
                                  .unitPrice(BigDecimal.TEN)
                                  .initialStock(1)
                                  .build())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("[200] Deve atualizar insumo")
    void shouldUpdateSupplies() throws Exception {
        var supplies = this.persistSupplies();

        super.executePut(this.url(supplies), this.managerToken,
                         UpdateSuppliesCommand.of(null, SuppliesTestFactory.SKU, "Filtro de óleo premium", new BigDecimal("75.00")))
                .andExpect(status().isOk());

        var updated = super.suppliesRepository.findByIdOrThrowNotFound(supplies.getId());
        assertEquals("Filtro de óleo premium", updated.getDescription());
        assertEquals(0, new BigDecimal("75.00").compareTo(updated.getUnitPrice()));
    }

    @Test
    @DisplayName("[200] Deve listar insumos")
    void shouldListSupplies() throws Exception {
        var supplies = this.persistSupplies();

        super.executeGet(SuppliesController.ENDPOINT, this.managerToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.content.length()", 1))
                .andExpect(jsonValue("$.content[0].id", supplies.getId().toString()));
    }

    @Test
    @DisplayName("[204] Deve desativar e reativar insumo")
    void shouldDeactivateAndActivateSupplies() throws Exception {
        var supplies = this.persistSupplies();

        super.executePatch(this.url(supplies) + "/deactivate", this.managerToken, null)
                .andExpect(status().isNoContent());
        assertFalse(super.suppliesRepository.findByIdOrThrowNotFound(supplies.getId()).isActive());

        super.executePatch(this.url(supplies) + "/activate", this.managerToken, null)
                .andExpect(status().isNoContent());
        assertTrue(super.suppliesRepository.findByIdOrThrowNotFound(supplies.getId()).isActive());
    }

    @Test
    @DisplayName("[403] Mecânico não deve listar insumos")
    void shouldNotListSuppliesAsMechanic() throws Exception {
        var mechanicToken = super.loginAs(UserRole.MECHANIC);

        super.executeGet(SuppliesController.ENDPOINT, mechanicToken)
                .andExpect(status().isForbidden());
    }

    private Supplies persistSupplies() {
        var supplies = persist(super.suppliesRepository, SuppliesTestFactory.createSupplies());
        persist(super.stockItemRepository, InventoryTestFactory.createStockItem(supplies.getId(), 10));

        return supplies;
    }

    private String url(Supplies supplies) {
        return SuppliesController.ENDPOINT + "/" + supplies.getId();
    }
}
