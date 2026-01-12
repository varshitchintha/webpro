package com.parkinglot.integration;

import com.parkinglot.controller.ParkingLotController;
import com.parkinglot.service.ParkingLotService;
import com.parkinglot.storage.InMemoryParkingStorage;
import com.parkinglot.strategy.DefaultSlotAllocationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ParkingLotIntegrationTest {
    private ParkingLotController controller;

    @BeforeEach
    void setUp() {
        var service = new ParkingLotService(new InMemoryParkingStorage(), new DefaultSlotAllocationStrategy());
        controller = new ParkingLotController(service);
    }

    @Test
    void testFullFlow() {
        assertEquals("Created a parking lot with 10 slots (5 small, 3 large, 2 oversize)", controller.processCommand("CREATE 5 3 2"));
        assertTrue(controller.processCommand("PARK ABC123 SMALL").startsWith("Allocated slot number:"));
        assertEquals("Vehicle ABC123 exited successfully", controller.processCommand("EXIT ABC123"));
        assertTrue(controller.processCommand("STATUS").contains("No vehicles parked"));
    }

    @Test
    void testEditAndDeleteSlots() {
        assertEquals("Created a parking lot with 6 slots (3 small, 2 large, 1 oversize)", controller.processCommand("CREATE 3 2 1"));
        assertEquals("Slot 1 edited to LARGE successfully", controller.processCommand("EDIT 1 LARGE"));
        assertEquals("Slot 6 deleted successfully. Remaining slots renumbered", controller.processCommand("DELETE 6"));
        assertTrue(controller.processCommand("PARK TEST001 LARGE").startsWith("Allocated slot number:"));
    }

    @Test
    void testEditDeleteOccupiedSlot() {
        assertEquals("Created a parking lot with 6 slots (3 small, 2 large, 1 oversize)", controller.processCommand("CREATE 3 2 1"));
        assertTrue(controller.processCommand("PARK ABC123 SMALL").startsWith("Allocated slot number:"));
        assertEquals("Failed to edit slot 1. Slot may be occupied or not exist", controller.processCommand("EDIT 1 LARGE"));
        assertEquals("Failed to delete slot 1. Slot may be occupied or not exist", controller.processCommand("DELETE 1"));
    }

    @Test
    void testSeedCommand() {
        assertEquals("Parking lot seeded with office scenario data", controller.processCommand("SEED OFFICE"));
        assertTrue(controller.processCommand("STATUS").contains("EMP001"));
        assertTrue(controller.processCommand("STATUS").contains("VIS002"));
    }
}