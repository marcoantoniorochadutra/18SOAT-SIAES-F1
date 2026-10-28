package com.fiap.siaes.vehicle.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.exception.InvalidVehicleYearException;
import com.fiap.siaes.vehicle.domain.exception.PlateValueIsNullException;
import com.fiap.siaes.vehicle.domain.exception.VehicleBrandRequiredException;
import com.fiap.siaes.vehicle.domain.exception.VehicleModelRequiredException;
import com.fiap.siaes.vehicle.domain.model.vo.Plate;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Year;
import java.time.ZoneId;

import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.vehicle.VehicleTestFactory.PLATE;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("UnitTest - Domain - Vehicle")
class VehicleDomainTest {

    private static final int NEXT_YEAR = Year.now(ZoneId.systemDefault()).getValue() + 1;

    @Test
    @DisplayName("Deve criar um novo veículo com sucesso")
    void shouldCreateVehicleSuccessfully() {
        var customerId = CustomerId.generate();

        Vehicle vehicle = Vehicle.builder()
                .customerId(customerId)
                .plate("abc-1d23")
                .brand("Fiat")
                .model("Uno")
                .year(2020)
                .build();

        assertNotNull(vehicle.getId());
        assertEquals(customerId, vehicle.getCustomerId());
        assertEquals(PLATE, vehicle.getPlate().getValue());
        assertEquals("Fiat", vehicle.getBrand());
        assertEquals("Uno", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
    }

    @Test
    @DisplayName("Deve gerar um id diferente para cada novo veículo")
    void shouldGenerateDifferentIdForEachNewVehicle() {
        Vehicle first = Vehicle.builder().plate(PLATE).brand("Fiat").model("Uno").year(2020).build();
        Vehicle second = Vehicle.builder().plate(PLATE).brand("Fiat").model("Uno").year(2020).build();

        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    @DisplayName("Deve aceitar veículo com ano até o próximo ano")
    void shouldAcceptVehicleUntilNextYear() {
        Vehicle vehicle = Vehicle.builder().plate(PLATE).brand("Fiat").model("Uno").year(NEXT_YEAR).build();

        assertEquals(NEXT_YEAR, vehicle.getYear());
    }

    @Test
    @DisplayName("Não deve criar veículo com ano posterior ao próximo ano")
    void shouldNotCreateVehicleWithYearAfterNextYear() {
        int invalidYear = NEXT_YEAR + 1;
        var builder = Vehicle.builder().plate(PLATE).brand("Fiat").model("Uno").year(invalidYear);

        var exception = assertThrows(InvalidVehicleYearException.class, builder::build);

        assertExceptionParameters(exception, invalidYear);
    }

    @Test
    @DisplayName("Não deve criar veículo sem placa")
    void shouldNotCreateVehicleWithoutPlate() {
        var builder = Vehicle.builder().plate(" ").brand("Fiat").model("Uno").year(2020);

        assertThrows(PlateValueIsNullException.class, builder::build);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Não deve criar veículo sem marca")
    void shouldNotCreateVehicleWithoutBrand(String brand) {
        var builder = Vehicle.builder().plate(PLATE).brand(brand).model("Uno").year(2020);

        assertThrows(VehicleBrandRequiredException.class, builder::build);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Não deve criar veículo sem modelo")
    void shouldNotCreateVehicleWithoutModel(String model) {
        var builder = Vehicle.builder().plate(PLATE).brand("Fiat").model(model).year(2020);

        assertThrows(VehicleModelRequiredException.class, builder::build);
    }

    @Test
    @DisplayName("Deve recriar um veículo mantendo os dados informados")
    void shouldRecreateVehicleKeepingGivenData() {
        var id = VehicleId.generate();
        var customerId = CustomerId.generate();

        Vehicle vehicle = Vehicle.recreate()
                .id(id)
                .customerId(customerId)
                .plate(Plate.recreate(PLATE))
                .brand("Fiat")
                .model("Uno")
                .year(2020)
                .build();

        assertEquals(id, vehicle.getId());
        assertEquals(customerId, vehicle.getCustomerId());
        assertEquals(PLATE, vehicle.getPlate().getValue());
        assertEquals("Fiat", vehicle.getBrand());
        assertEquals("Uno", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
    }

    @Test
    @DisplayName("Deve atualizar os dados do veículo mantendo id e cliente")
    void shouldUpdateVehicleKeepingIdAndCustomer() {
        Vehicle vehicle = createVehicle();
        var id = vehicle.getId();
        var customerId = vehicle.getCustomerId();

        vehicle.update("xyz-9k87", "Volkswagen", "Gol", 2021);

        assertEquals(id, vehicle.getId());
        assertEquals(customerId, vehicle.getCustomerId());
        assertEquals("XYZ9K87", vehicle.getPlate().getValue());
        assertEquals("Volkswagen", vehicle.getBrand());
        assertEquals("Gol", vehicle.getModel());
        assertEquals(2021, vehicle.getYear());
    }

    @Test
    @DisplayName("Não deve atualizar veículo com ano posterior ao próximo ano")
    void shouldNotUpdateVehicleWithYearAfterNextYear() {
        Vehicle vehicle = createVehicle();
        int invalidYear = NEXT_YEAR + 1;

        var exception = assertThrows(InvalidVehicleYearException.class,
                () -> vehicle.update("xyz-9k87", "Volkswagen", "Gol", invalidYear));

        assertExceptionParameters(exception, invalidYear);
        this.assertVehicleUnchanged(vehicle);
    }

    @Test
    @DisplayName("Não deve atualizar veículo sem placa")
    void shouldNotUpdateVehicleWithoutPlate() {
        Vehicle vehicle = createVehicle();

        assertThrows(PlateValueIsNullException.class,
                () -> vehicle.update(null, "Volkswagen", "Gol", 2021));

        this.assertVehicleUnchanged(vehicle);
    }

    private void assertVehicleUnchanged(Vehicle vehicle) {
        assertEquals(PLATE, vehicle.getPlate().getValue());
        assertEquals("Fiat", vehicle.getBrand());
        assertEquals("Uno", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
    }
}
