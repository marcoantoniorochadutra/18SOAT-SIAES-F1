package com.fiap.siaes.supplies.infrastructure.web;

import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.application.dto.SuppliesResponse;
import com.fiap.siaes.supplies.application.usecase.ActivateSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.CreateSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.CreateSuppliesUseCase.CreateSuppliesCommand;
import com.fiap.siaes.supplies.application.usecase.DeactivateSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.GetSuppliesByIdUseCase;
import com.fiap.siaes.supplies.application.usecase.ListSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.UpdateSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.UpdateSuppliesUseCase.UpdateSuppliesCommand;
import com.fiap.siaes.supplies.domain.exception.SuppliesAlreadyActiveException;
import com.fiap.siaes.supplies.domain.exception.SuppliesAlreadyInactiveException;
import com.fiap.siaes.supplies.domain.exception.SuppliesNotFoundException;
import com.fiap.siaes.supplies.domain.exception.SuppliesSkuAlreadyExistsException;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.utils.ControllerTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static com.fiap.siaes.supplies.SuppliesTestFactory.DESCRIPTION;
import static com.fiap.siaes.supplies.SuppliesTestFactory.SKU;
import static com.fiap.siaes.supplies.SuppliesTestFactory.UNIT_PRICE;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SuppliesController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Controller Test - Supplies")
class SuppliesControllerTest extends ControllerTestUtils {

    @MockitoBean
    private CreateSuppliesUseCase createSuppliesUseCase;

    @MockitoBean
    private UpdateSuppliesUseCase updateSuppliesUseCase;

    @MockitoBean
    private GetSuppliesByIdUseCase getSuppliesByIdUseCase;

    @MockitoBean
    private ListSuppliesUseCase listSuppliesUseCase;

    @MockitoBean
    private DeactivateSuppliesUseCase deactivateSuppliesUseCase;

    @MockitoBean
    private ActivateSuppliesUseCase activateSuppliesUseCase;

    @Test
    @DisplayName("[201] Deve criar peça/insumo com sucesso")
    void shouldCreateSupplies() throws Exception {
        var command = this.createCommandBuilder().build();
        var suppliesId = SuppliesId.generate();

        when(this.createSuppliesUseCase.execute(command.with(AUTHENTICATED_USER_ADMIN))).thenReturn(suppliesId);

        super.executePostAuthenticated(SuppliesController.ENDPOINT, command)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(suppliesId.toString())));

        verify(this.createSuppliesUseCase).execute(command.with(AUTHENTICATED_USER_ADMIN));
    }

    @Test
    @DisplayName("[400] Não deve criar peça/insumo com SKU em branco")
    void shouldNotCreateSuppliesWhenSkuIsBlank() throws Exception {
        var command = this.createCommandBuilder().sku(" ").build();

        super.executePost(SuppliesController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("SuppliesCommand.sku.notBlank"));

        verifyNoInteractions(this.createSuppliesUseCase);
    }

    @Test
    @DisplayName("[400] Não deve criar peça/insumo com SKU acima do tamanho máximo")
    void shouldNotCreateSuppliesWhenSkuExceedsMaxSize() throws Exception {
        var command = this.createCommandBuilder().sku("S".repeat(51)).build();

        super.executePost(SuppliesController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("sku"))
                .andExpect(jsonPath("$.fields[0].code").value("Size"));
    }

    @Test
    @DisplayName("[400] Não deve criar peça/insumo com descrição em branco")
    void shouldNotCreateSuppliesWhenDescriptionIsBlank() throws Exception {
        var command = this.createCommandBuilder().description(" ").build();

        super.executePost(SuppliesController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("SuppliesCommand.description.notBlank"));
    }

    @Test
    @DisplayName("[400] Não deve criar peça/insumo sem preço unitário")
    void shouldNotCreateSuppliesWhenUnitPriceIsNull() throws Exception {
        var command = this.createCommandBuilder().unitPrice(null).build();

        super.executePost(SuppliesController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("SuppliesCommand.unitPrice.notNull"));
    }

    @Test
    @DisplayName("[400] Não deve criar peça/insumo com preço unitário negativo")
    void shouldNotCreateSuppliesWhenUnitPriceIsNegative() throws Exception {
        var command = this.createCommandBuilder().unitPrice(new BigDecimal("-1.00")).build();

        super.executePost(SuppliesController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("SuppliesCommand.unitPrice.positiveOrZero"));
    }

    @Test
    @DisplayName("[400] Não deve criar peça/insumo com estoque inicial negativo")
    void shouldNotCreateSuppliesWhenInitialStockIsNegative() throws Exception {
        var command = this.createCommandBuilder().initialStock(-1).build();

        super.executePost(SuppliesController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("SuppliesCommand.initialStock.positiveOrZero"));
    }

    @Test
    @DisplayName("[409] Não deve criar peça/insumo com SKU já cadastrado")
    void shouldNotCreateSuppliesWhenSkuAlreadyExists() throws Exception {
        var command = this.createCommandBuilder().build();

        when(this.createSuppliesUseCase.execute(command.with(AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new SuppliesSkuAlreadyExistsException(SKU));

        super.executePostAuthenticated(SuppliesController.ENDPOINT, command)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("SKU já cadastrado"));
    }

    @Test
    @DisplayName("[200] Deve atualizar peça/insumo com sucesso")
    void shouldUpdateSupplies() throws Exception {
        var id = SuppliesId.generate();
        var command = UpdateSuppliesCommand.of(null, SKU, DESCRIPTION, UNIT_PRICE);

        when(this.updateSuppliesUseCase.execute(command.with(id))).thenReturn(id);

        super.executePutAuthenticated(SuppliesController.ENDPOINT + "/" + id, command)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(id.toString()))
                .andExpect(header().string("Location", containsString(id.toString())));

        verify(this.updateSuppliesUseCase).execute(command.with(id));
    }

    @Test
    @DisplayName("[400] Não deve atualizar peça/insumo com SKU em branco")
    void shouldNotUpdateSuppliesWhenSkuIsBlank() throws Exception {
        var id = SuppliesId.generate();
        var command = UpdateSuppliesCommand.of(null, " ", DESCRIPTION, UNIT_PRICE);

        super.executePut(SuppliesController.ENDPOINT + "/" + id, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("SuppliesCommand.sku.notBlank"));

        verifyNoInteractions(this.updateSuppliesUseCase);
    }

    @Test
    @DisplayName("[404] Não deve atualizar peça/insumo inexistente")
    void shouldNotUpdateNonExistentSupplies() throws Exception {
        var id = SuppliesId.generate();
        var command = UpdateSuppliesCommand.of(null, SKU, DESCRIPTION, UNIT_PRICE);

        when(this.updateSuppliesUseCase.execute(command.with(id)))
                .thenThrow(new SuppliesNotFoundException(id.toString()));

        super.executePutAuthenticated(SuppliesController.ENDPOINT + "/" + id, command)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Peça/insumo não encontrado"));
    }

    @Test
    @DisplayName("[409] Não deve atualizar peça/insumo com SKU já cadastrado")
    void shouldNotUpdateSuppliesWhenSkuAlreadyExists() throws Exception {
        var id = SuppliesId.generate();
        var command = UpdateSuppliesCommand.of(null, SKU, DESCRIPTION, UNIT_PRICE);

        when(this.updateSuppliesUseCase.execute(command.with(id)))
                .thenThrow(new SuppliesSkuAlreadyExistsException(SKU));

        super.executePutAuthenticated(SuppliesController.ENDPOINT + "/" + id, command)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("SKU já cadastrado"));
    }

    @Test
    @DisplayName("[200] Deve buscar peça/insumo pelo id com sucesso")
    void shouldGetSuppliesById() throws Exception {
        var id = SuppliesId.generate();
        var response = SuppliesResponse.builder()
                .id(id)
                .sku(SKU)
                .description(DESCRIPTION)
                .unitPrice(UNIT_PRICE)
                .active(true)
                .build();

        when(this.getSuppliesByIdUseCase.execute(id)).thenReturn(response);

        super.executeGetAuthenticated(SuppliesController.ENDPOINT + "/" + id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.sku").value(SKU))
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.unitPrice").value(UNIT_PRICE.doubleValue()))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("[404] Não deve buscar peça/insumo inexistente")
    void shouldNotGetNonExistentSupplies() throws Exception {
        var id = SuppliesId.generate();

        when(this.getSuppliesByIdUseCase.execute(id)).thenThrow(new SuppliesNotFoundException(id.toString()));

        super.executeGetAuthenticated(SuppliesController.ENDPOINT + "/" + id)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Peça/insumo não encontrado"));
    }

    @Test
    @DisplayName("[200] Deve listar peças/insumos com sucesso")
    void shouldListSupplies() throws Exception {
        var response = SuppliesResponse.builder()
                .id(SuppliesId.generate())
                .sku(SKU)
                .description(DESCRIPTION)
                .unitPrice(UNIT_PRICE)
                .active(true)
                .build();
        var page = new PageResponse<>(List.of(response), false, 0, 20);

        when(this.listSuppliesUseCase.execute(any())).thenReturn(page);

        this.mockMvc.perform(get(SuppliesController.ENDPOINT).param("sku", SKU))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].sku").value(SKU))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("[400] Não deve listar peças/insumos com tamanho de página inválido")
    void shouldNotListSuppliesWhenPageSizeExceedsLimit() throws Exception {
        this.mockMvc.perform(get(SuppliesController.ENDPOINT).param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("size"))
                .andExpect(jsonPath("$.fields[0].code").value("Max"));

        verifyNoInteractions(this.listSuppliesUseCase);
    }

    @Test
    @DisplayName("[204] Deve inativar peça/insumo com sucesso")
    void shouldDeactivateSupplies() throws Exception {
        var id = SuppliesId.generate();

        super.executePatch(SuppliesController.ENDPOINT + "/" + id + "/deactivate")
                .andExpect(status().isNoContent());

        verify(this.deactivateSuppliesUseCase).execute(id);
    }

    @Test
    @DisplayName("[404] Não deve inativar peça/insumo inexistente")
    void shouldNotDeactivateNonExistentSupplies() throws Exception {
        var id = SuppliesId.generate();

        doThrow(new SuppliesNotFoundException(id.toString())).when(this.deactivateSuppliesUseCase).execute(id);

        super.executePatch(SuppliesController.ENDPOINT + "/" + id + "/deactivate")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Peça/insumo não encontrado"));
    }

    @Test
    @DisplayName("[409] Não deve inativar peça/insumo já inativa")
    void shouldNotDeactivateAlreadyInactiveSupplies() throws Exception {
        var id = SuppliesId.generate();

        doThrow(new SuppliesAlreadyInactiveException(id.toString())).when(this.deactivateSuppliesUseCase).execute(id);

        super.executePatch(SuppliesController.ENDPOINT + "/" + id + "/deactivate")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Peça/insumo já inativo"));
    }

    @Test
    @DisplayName("[204] Deve ativar peça/insumo com sucesso")
    void shouldActivateSupplies() throws Exception {
        var id = SuppliesId.generate();

        super.executePatch(SuppliesController.ENDPOINT + "/" + id + "/activate")
                .andExpect(status().isNoContent());

        verify(this.activateSuppliesUseCase).execute(id);
    }

    @Test
    @DisplayName("[404] Não deve ativar peça/insumo inexistente")
    void shouldNotActivateNonExistentSupplies() throws Exception {
        var id = SuppliesId.generate();

        doThrow(new SuppliesNotFoundException(id.toString())).when(this.activateSuppliesUseCase).execute(id);

        super.executePatch(SuppliesController.ENDPOINT + "/" + id + "/activate")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Peça/insumo não encontrado"));
    }

    @Test
    @DisplayName("[409] Não deve ativar peça/insumo já ativa")
    void shouldNotActivateAlreadyActiveSupplies() throws Exception {
        var id = SuppliesId.generate();

        doThrow(new SuppliesAlreadyActiveException(id.toString())).when(this.activateSuppliesUseCase).execute(id);

        super.executePatch(SuppliesController.ENDPOINT + "/" + id + "/activate")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Peça/insumo já ativo"));
    }

    private CreateSuppliesCommand.CreateSuppliesCommandBuilder createCommandBuilder() {
        return CreateSuppliesCommand.builder()
                .sku(SKU)
                .description(DESCRIPTION)
                .unitPrice(UNIT_PRICE)
                .initialStock(10);
    }

}
