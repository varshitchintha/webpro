package com.parkinglot.storage;

import com.parkinglot.domain.Vehicle;
import com.parkinglot.domain.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryParkingStorageTest {
    private ParkingStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryParkingStorage();
    }

    @Test
    void testInitializeSlots() {
        storage.initializeSlots(5, 3, 2);
        var slots = storage.getAllSlots();
        assertEquals(10, slots.size());
        // Verify slot types: first 5 should be SMALL, next 3 LARGE, last 2 OVERSIZE
        assertEquals(VehicleType.SMALL, slots.get(0).getSupportedType());
        assertEquals(VehicleType.SMALL, slots.get(4).getSupportedType());
        assertEquals(VehicleType.LARGE, slots.get(5).getSupportedType());
        assertEquals(VehicleType.LARGE, slots.get(7).getSupportedType());
        assertEquals(VehicleType.OVERSIZE, slots.get(8).getSupportedType());
        assertEquals(VehicleType.OVERSIZE, slots.get(9).getSupportedType());
    }

    @Test
    void testParkVehicle() {
        storage.initializeSlots(5, 3, 2);
        var slots = storage.getAllSlots();
        var vehicle = new Vehicle("ABC123", VehicleType.SMALL);
        assertTrue(storage.parkVehicle(vehicle, slots.get(0)));
    }

    @Test
    void testExitVehicle() {
        storage.initializeSlots(5, 3, 2);
        var slots = storage.getAllSlots();
        var vehicle = new Vehicle("ABC123", VehicleType.SMALL);
        storage.parkVehicle(vehicle, slots.get(0));
        assertTrue(storage.exitVehicle("ABC123"));
    }

    @Test
    void testEditSlot() {
        storage.initializeSlots(5, 3, 2);
        assertTrue(storage.editSlot(1, VehicleType.LARGE));
        var updatedSlot = storage.getAllSlots().get(0);
        assertEquals(VehicleType.LARGE, updatedSlot.getSupportedType());
    }

    @Test
    void testEditOccupiedSlot() {
        storage.initializeSlots(5, 3, 2);
        var vehicle = new Vehicle("ABC123", VehicleType.SMALL);
        storage.parkVehicle(vehicle, storage.getAllSlots().get(0));
        assertFalse(storage.editSlot(1, VehicleType.LARGE)); // Cannot edit occupied slot
    }

    @Test
    void testDeleteSlot() {
        storage.initializeSlots(5, 3, 2);
        assertTrue(storage.deleteSlot(10)); // Delete last slot
        assertEquals(9, storage.getAllSlots().size());
        // Check renumbering - slot 9 should now be the last slot
        assertEquals(9, storage.getAllSlots().get(8).getSlotNumber());
    }

    @Test
    void testDeleteOccupiedSlot() {
        storage.initializeSlots(5, 3, 2);
        var vehicle = new Vehicle("ABC123", VehicleType.SMALL);
        storage.parkVehicle(vehicle, storage.getAllSlots().get(0));
        assertFalse(storage.deleteSlot(1)); // Cannot delete occupied slot
    }
}