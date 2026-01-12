package com.parkinglot.service;

import com.parkinglot.domain.VehicleType;
import com.parkinglot.storage.InMemoryParkingStorage;
import com.parkinglot.strategy.DefaultSlotAllocationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ParkingLotServiceTest {
    private ParkingLotService service;

    @BeforeEach
    void setUp() {
        service = new ParkingLotService(new InMemoryParkingStorage(), new DefaultSlotAllocationStrategy());
    }

    @Test
    void testCreateParkingLot() {
        service.createParkingLot(5, 3, 2);
        assertTrue(service.isParkingLotCreated());
    }

    @Test
    void testParkVehicle() {
        service.createParkingLot(5, 3, 2);
        var result = service.parkVehicle("ABC123", VehicleType.SMALL);
        assertTrue(result.isPresent());
        assertEquals(1, result.get());
    }

    @Test
    void testExitVehicle() {
        service.createParkingLot(5, 3, 2);
        service.parkVehicle("ABC123", VehicleType.SMALL);
        assertTrue(service.exitVehicle("ABC123"));
        assertFalse(service.exitVehicle("XYZ789"));
    }

    @Test
    void testGetStatus() {
        service.createParkingLot(5, 3, 2);
        service.parkVehicle("ABC123", VehicleType.SMALL);
        var status = service.getStatus();
        assertEquals(1, status.size());
        assertEquals("ABC123", status.get(0).getParkedVehicle().getVehicleNumber());
    }

    @Test
    void testEditSlot() {
        service.createParkingLot(5, 3, 2);
        assertTrue(service.editSlot(1, VehicleType.LARGE)); // Edit empty slot
        var slots = service.getStatus(); // Should be empty since no vehicles parked
        assertEquals(0, slots.size());
    }

    @Test
    void testEditOccupiedSlot() {
        service.createParkingLot(5, 3, 2);
        service.parkVehicle("ABC123", VehicleType.SMALL);
        assertFalse(service.editSlot(1, VehicleType.LARGE)); // Cannot edit occupied slot
    }

    @Test
    void testDeleteSlot() {
        service.createParkingLot(5, 3, 2);
        assertTrue(service.deleteSlot(10)); // Delete empty slot
        // Should now have 9 slots total
        assertEquals(9, service.getStatus().size() + service.getAllSlots().stream().filter(slot -> !slot.isOccupied()).count());
    }

    @Test
    void testDeleteOccupiedSlot() {
        service.createParkingLot(5, 3, 2);
        service.parkVehicle("ABC123", VehicleType.SMALL);
        assertFalse(service.deleteSlot(1)); // Cannot delete occupied slot
    }
}