package com.fiap.siaes.inventory.domain.model;

import com.fiap.siaes.inventory.domain.event.StockMovedEvent;
import com.fiap.siaes.inventory.domain.exception.InsufficientStockException;
import com.fiap.siaes.inventory.domain.exception.InvalidStockMovementTypeException;
import com.fiap.siaes.inventory.domain.exception.InvalidStockQuantityException;
import com.fiap.siaes.inventory.domain.exception.ReservedStockExceededException;
import com.fiap.siaes.inventory.domain.exception.StockAdjustmentReasonRequiredException;
import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static com.fiap.siaes.inventory.InventoryTestFactory.QUANTITY;
import static com.fiap.siaes.inventory.InventoryTestFactory.REASON;
import static com.fiap.siaes.inventory.InventoryTestFactory.RESERVED_QUANTITY;
import static com.fiap.siaes.inventory.InventoryTestFactory.VERSION;
import static com.fiap.siaes.inventory.InventoryTestFactory.createStockItem;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("UnitTest - Domain - Stock Item")
class StockItemDomainTest {

    private static final int AVAILABLE_QUANTITY = QUANTITY - RESERVED_QUANTITY;

    @Test
    @DisplayName("Deve criar o estoque com a quantidade inicial e registrar a movimentação inicial")
    void shouldCreateStockItemWithInitialQuantity() {
        var suppliesId = SuppliesId.generate();
        var performedBy = UserId.generate();

        StockItem stockItem = StockItem.builder()
                .suppliesId(suppliesId)
                .initialQuantity(QUANTITY)
                .performedBy(performedBy)
                .build();

        assertEquals(suppliesId, stockItem.getSuppliesId());
        assertEquals(QUANTITY, stockItem.getQuantity());
        assertEquals(0, stockItem.getReservedQuantity());
        assertEquals(QUANTITY, stockItem.getAvailableQuantity());
        assertNull(stockItem.getVersion());

        StockMovedEvent event = this.pullSingleEvent(stockItem);

        assertEquals(suppliesId, event.suppliesId());
        assertEquals(StockMovementType.INITIAL, event.type());
        assertEquals(QUANTITY, event.quantity());
        assertEquals(QUANTITY, event.stockAfter());
        assertEquals(0, event.reservedAfter());
        assertNull(event.reason());
        assertNull(event.referenceId());
        assertEquals(performedBy, event.performedBy());
        assertNotNull(event.occurredAt());
    }

    @Test
    @DisplayName("Deve criar o estoque zerado sem registrar movimentação quando a quantidade inicial for zero")
    void shouldCreateEmptyStockItemWithoutMovement() {
        StockItem stockItem = StockItem.builder()
                .suppliesId(SuppliesId.generate())
                .initialQuantity(0)
                .performedBy(UserId.generate())
                .build();

        assertEquals(0, stockItem.getQuantity());
        assertEquals(0, stockItem.getReservedQuantity());
        assertThat(stockItem.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Não deve criar o estoque com quantidade inicial negativa")
    void shouldNotCreateStockItemWithNegativeInitialQuantity() {
        var suppliesId = SuppliesId.generate();
        var builder = StockItem.builder().suppliesId(suppliesId).initialQuantity(-1).performedBy(UserId.generate());

        var exception = assertThrows(InvalidStockQuantityException.class, builder::build);

        assertExceptionParameters(exception, suppliesId.toString(), -1);
    }

    @Test
    @DisplayName("Deve recriar o estoque mantendo os dados informados sem registrar eventos")
    void shouldRecreateStockItemKeepingGivenData() {
        var suppliesId = SuppliesId.generate();

        StockItem stockItem = StockItem.recreate()
                .suppliesId(suppliesId)
                .quantity(QUANTITY)
                .reservedQuantity(RESERVED_QUANTITY)
                .version(VERSION)
                .build();

        assertEquals(suppliesId, stockItem.getSuppliesId());
        assertEquals(QUANTITY, stockItem.getQuantity());
        assertEquals(RESERVED_QUANTITY, stockItem.getReservedQuantity());
        assertEquals(AVAILABLE_QUANTITY, stockItem.getAvailableQuantity());
        assertEquals(VERSION, stockItem.getVersion());
        assertThat(stockItem.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Deve registrar a entrada de estoque aumentando a quantidade")
    void shouldMoveStockEntry() {
        StockItem stockItem = createStockItem();
        var performedBy = UserId.generate();

        stockItem.moveStock(StockMovementType.ENTRY, 5, null, performedBy);

        assertEquals(QUANTITY + 5, stockItem.getQuantity());
        assertEquals(RESERVED_QUANTITY, stockItem.getReservedQuantity());

        StockMovedEvent event = this.pullSingleEvent(stockItem);

        assertEquals(StockMovementType.ENTRY, event.type());
        assertEquals(5, event.quantity());
        assertEquals(QUANTITY + 5, event.stockAfter());
        assertEquals(RESERVED_QUANTITY, event.reservedAfter());
        assertNull(event.reason());
        assertNull(event.referenceId());
        assertEquals(performedBy, event.performedBy());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("Não deve registrar entrada de estoque com quantidade não positiva")
    void shouldNotMoveStockEntryWithNonPositiveQuantity(int quantity) {
        StockItem stockItem = createStockItem();

        var exception = assertThrows(InvalidStockQuantityException.class,
                () -> stockItem.moveStock(StockMovementType.ENTRY, quantity, null, UserId.generate()));

        assertExceptionParameters(exception, stockItem.getSuppliesId().toString(), quantity);
        assertEquals(QUANTITY, stockItem.getQuantity());
        assertThat(stockItem.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Deve registrar ajuste positivo de estoque com o motivo informado")
    void shouldMoveStockPositiveAdjustment() {
        StockItem stockItem = createStockItem();

        stockItem.moveStock(StockMovementType.ADJUSTMENT, 3, REASON, UserId.generate());

        assertEquals(QUANTITY + 3, stockItem.getQuantity());

        StockMovedEvent event = this.pullSingleEvent(stockItem);

        assertEquals(StockMovementType.ADJUSTMENT, event.type());
        assertEquals(3, event.quantity());
        assertEquals(QUANTITY + 3, event.stockAfter());
        assertEquals(REASON, event.reason());
    }

    @Test
    @DisplayName("Deve registrar ajuste negativo de estoque reduzindo a quantidade")
    void shouldMoveStockNegativeAdjustment() {
        StockItem stockItem = createStockItem();

        stockItem.moveStock(StockMovementType.ADJUSTMENT, -AVAILABLE_QUANTITY, REASON, UserId.generate());

        assertEquals(RESERVED_QUANTITY, stockItem.getQuantity());
        assertEquals(RESERVED_QUANTITY, stockItem.getReservedQuantity());
        assertEquals(0, stockItem.getAvailableQuantity());

        StockMovedEvent event = this.pullSingleEvent(stockItem);

        assertEquals(-AVAILABLE_QUANTITY, event.quantity());
        assertEquals(RESERVED_QUANTITY, event.stockAfter());
        assertEquals(REASON, event.reason());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Não deve registrar ajuste de estoque sem motivo")
    void shouldNotMoveStockAdjustmentWithoutReason(String reason) {
        StockItem stockItem = createStockItem();

        var exception = assertThrows(StockAdjustmentReasonRequiredException.class,
                () -> stockItem.moveStock(StockMovementType.ADJUSTMENT, 3, reason, UserId.generate()));

        assertExceptionParameters(exception, stockItem.getSuppliesId().toString());
        assertEquals(QUANTITY, stockItem.getQuantity());
        assertThat(stockItem.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Não deve registrar ajuste de estoque com quantidade zero")
    void shouldNotMoveStockAdjustmentWithZeroQuantity() {
        StockItem stockItem = createStockItem();

        var exception = assertThrows(InvalidStockQuantityException.class,
                () -> stockItem.moveStock(StockMovementType.ADJUSTMENT, 0, REASON, UserId.generate()));

        assertExceptionParameters(exception, stockItem.getSuppliesId().toString(), 0);
        assertEquals(QUANTITY, stockItem.getQuantity());
    }

    @Test
    @DisplayName("Não deve registrar ajuste negativo que consuma unidades reservadas")
    void shouldNotMoveStockNegativeAdjustmentBeyondAvailable() {
        StockItem stockItem = createStockItem();
        int requested = AVAILABLE_QUANTITY + 1;

        var exception = assertThrows(InsufficientStockException.class,
                () -> stockItem.moveStock(StockMovementType.ADJUSTMENT, -requested, REASON, UserId.generate()));

        assertExceptionParameters(exception, stockItem.getSuppliesId().toString(), AVAILABLE_QUANTITY, requested);
        assertEquals(QUANTITY, stockItem.getQuantity());
        assertThat(stockItem.pullDomainEvents()).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = StockMovementType.class, names = {"ENTRY", "ADJUSTMENT"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Não deve registrar movimentação manual com tipo reservado ao sistema")
    void shouldNotMoveStockWithSystemType(StockMovementType type) {
        StockItem stockItem = createStockItem();

        var exception = assertThrows(InvalidStockMovementTypeException.class,
                () -> stockItem.moveStock(type, 1, REASON, UserId.generate()));

        assertExceptionParameters(exception, stockItem.getSuppliesId().toString(), type.name());
        assertEquals(QUANTITY, stockItem.getQuantity());
        assertEquals(RESERVED_QUANTITY, stockItem.getReservedQuantity());
        assertThat(stockItem.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Deve reservar estoque disponível registrando a referência")
    void shouldReserveStock() {
        StockItem stockItem = createStockItem();
        var referenceId = UUID.randomUUID();
        var performedBy = UserId.generate();

        stockItem.reserve(2, referenceId, performedBy);

        assertEquals(QUANTITY, stockItem.getQuantity());
        assertEquals(RESERVED_QUANTITY + 2, stockItem.getReservedQuantity());
        assertEquals(AVAILABLE_QUANTITY - 2, stockItem.getAvailableQuantity());

        StockMovedEvent event = this.pullSingleEvent(stockItem);

        assertEquals(StockMovementType.RESERVATION, event.type());
        assertEquals(2, event.quantity());
        assertEquals(QUANTITY, event.stockAfter());
        assertEquals(RESERVED_QUANTITY + 2, event.reservedAfter());
        assertNull(event.reason());
        assertEquals(referenceId, event.referenceId());
        assertEquals(performedBy, event.performedBy());
    }

    @Test
    @DisplayName("Deve reservar exatamente toda a quantidade disponível")
    void shouldReserveAllAvailableStock() {
        StockItem stockItem = createStockItem();

        stockItem.reserve(AVAILABLE_QUANTITY, UUID.randomUUID(), UserId.generate());

        assertEquals(QUANTITY, stockItem.getReservedQuantity());
        assertEquals(0, stockItem.getAvailableQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("Não deve reservar estoque com quantidade não positiva")
    void shouldNotReserveNonPositiveQuantity(int amount) {
        StockItem stockItem = createStockItem();

        var exception = assertThrows(InvalidStockQuantityException.class,
                () -> stockItem.reserve(amount, UUID.randomUUID(), UserId.generate()));

        assertExceptionParameters(exception, stockItem.getSuppliesId().toString(), amount);
        assertEquals(RESERVED_QUANTITY, stockItem.getReservedQuantity());
    }

    @Test
    @DisplayName("Não deve reservar quantidade maior que a disponível")
    void shouldNotReserveMoreThanAvailable() {
        StockItem stockItem = createStockItem();
        int requested = AVAILABLE_QUANTITY + 1;

        var exception = assertThrows(InsufficientStockException.class,
                () -> stockItem.reserve(requested, UUID.randomUUID(), UserId.generate()));

        assertExceptionParameters(exception, stockItem.getSuppliesId().toString(), AVAILABLE_QUANTITY, requested);
        assertEquals(RESERVED_QUANTITY, stockItem.getReservedQuantity());
        assertThat(stockItem.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Deve liberar a reserva de estoque mantendo a quantidade em estoque")
    void shouldReleaseReservation() {
        StockItem stockItem = createStockItem();
        var referenceId = UUID.randomUUID();

        stockItem.releaseReservation(RESERVED_QUANTITY, referenceId, UserId.generate(), null);

        assertEquals(QUANTITY, stockItem.getQuantity());
        assertEquals(0, stockItem.getReservedQuantity());
        assertEquals(QUANTITY, stockItem.getAvailableQuantity());

        StockMovedEvent event = this.pullSingleEvent(stockItem);

        assertEquals(StockMovementType.RESERVATION_RELEASE, event.type());
        assertEquals(RESERVED_QUANTITY, event.quantity());
        assertEquals(QUANTITY, event.stockAfter());
        assertEquals(0, event.reservedAfter());
        assertEquals(referenceId, event.referenceId());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("Não deve liberar reserva com quantidade não positiva")
    void shouldNotReleaseNonPositiveQuantity(int amount) {
        StockItem stockItem = createStockItem();

        var exception = assertThrows(InvalidStockQuantityException.class,
                () -> stockItem.releaseReservation(amount, UUID.randomUUID(), UserId.generate(), null));

        assertExceptionParameters(exception, stockItem.getSuppliesId().toString(), amount);
        assertEquals(RESERVED_QUANTITY, stockItem.getReservedQuantity());
    }

    @Test
    @DisplayName("Não deve liberar quantidade maior que a reservada")
    void shouldNotReleaseMoreThanReserved() {
        StockItem stockItem = createStockItem();
        int requested = RESERVED_QUANTITY + 1;

        var exception = assertThrows(ReservedStockExceededException.class,
                () -> stockItem.releaseReservation(requested, UUID.randomUUID(), UserId.generate(), null));

        assertExceptionParameters(exception, stockItem.getSuppliesId().toString(), RESERVED_QUANTITY, requested);
        assertEquals(RESERVED_QUANTITY, stockItem.getReservedQuantity());
        assertThat(stockItem.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Deve consumir a reserva baixando a quantidade em estoque e a reservada")
    void shouldConsumeReservation() {
        StockItem stockItem = createStockItem();
        var referenceId = UUID.randomUUID();

        stockItem.consumeReservation(3, referenceId, null, "João da Silva");

        assertEquals(QUANTITY - 3, stockItem.getQuantity());
        assertEquals(RESERVED_QUANTITY - 3, stockItem.getReservedQuantity());
        assertEquals(AVAILABLE_QUANTITY, stockItem.getAvailableQuantity());

        StockMovedEvent event = this.pullSingleEvent(stockItem);

        assertEquals(StockMovementType.CONSUMPTION, event.type());
        assertEquals(3, event.quantity());
        assertEquals(QUANTITY - 3, event.stockAfter());
        assertEquals(RESERVED_QUANTITY - 3, event.reservedAfter());
        assertEquals(referenceId, event.referenceId());
        assertNull(event.performedBy());
        assertEquals("João da Silva", event.performedByName());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("Não deve consumir reserva com quantidade não positiva")
    void shouldNotConsumeNonPositiveQuantity(int amount) {
        StockItem stockItem = createStockItem();

        var exception = assertThrows(InvalidStockQuantityException.class,
                () -> stockItem.consumeReservation(amount, UUID.randomUUID(), UserId.generate(), null));

        assertExceptionParameters(exception, stockItem.getSuppliesId().toString(), amount);
        assertEquals(QUANTITY, stockItem.getQuantity());
    }

    @Test
    @DisplayName("Não deve consumir quantidade maior que a reservada")
    void shouldNotConsumeMoreThanReserved() {
        StockItem stockItem = createStockItem();
        int requested = RESERVED_QUANTITY + 1;

        var exception = assertThrows(ReservedStockExceededException.class,
                () -> stockItem.consumeReservation(requested, UUID.randomUUID(), UserId.generate(), null));

        assertExceptionParameters(exception, stockItem.getSuppliesId().toString(), RESERVED_QUANTITY, requested);
        assertEquals(QUANTITY, stockItem.getQuantity());
        assertEquals(RESERVED_QUANTITY, stockItem.getReservedQuantity());
        assertThat(stockItem.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Deve acumular uma movimentação por operação e limpar os eventos após consumi-los")
    void shouldAccumulateEventsAndClearAfterPull() {
        StockItem stockItem = createStockItem();

        stockItem.reserve(1, UUID.randomUUID(), UserId.generate());
        stockItem.consumeReservation(1, UUID.randomUUID(), UserId.generate(), null);

        assertThat(stockItem.pullDomainEvents())
                .extracting(event -> ((StockMovedEvent) event).type())
                .containsExactly(StockMovementType.RESERVATION, StockMovementType.CONSUMPTION);
        assertThat(stockItem.pullDomainEvents()).isEmpty();
    }

    private StockMovedEvent pullSingleEvent(StockItem stockItem) {
        var events = stockItem.pullDomainEvents();

        assertThat(events).singleElement().isInstanceOf(StockMovedEvent.class);

        return (StockMovedEvent) events.getFirst();
    }
}
