package com.parkinglot.storage;

import com.parkinglot.domain.Vehicle;
import com.parkinglot.domain.VehicleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

class FileBasedParkingStorageTest {
    private static final String TEST_FILE = "test_parking.json";
    private ParkingStorage storage;

    @BeforeEach
    void setUp() {
        storage = new FileBasedParkingStorage(TEST_FILE);
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    void testInitializeSlots() {
        storage.initializeSlots(5, 3, 2);
        var slots = storage.getAllSlots();
        assertEquals(10, slots.size());
        assertTrue(new File(TEST_FILE).exists());
    }

    @Test
    void testParkVehicle() {
        storage.initializeSlots(5, 3, 2);
        var slots = storage.getAllSlots();
        var vehicle = new Vehicle("ABC123", VehicleType.SMALL);
        assertTrue(storage.parkVehicle(vehicle, slots.get(0)));
        var reloadedStorage = new FileBasedParkingStorage(TEST_FILE);
        var reloadedSlots = reloadedStorage.getAllSlots();
        assertTrue(reloadedSlots.get(0).isOccupied());
    }

    @Test
    void testExitVehicle() {
        storage.initializeSlots(5, 3, 2);
        var slots = storage.getAllSlots();
        var vehicle = new Vehicle("ABC123", VehicleType.SMALL);
        storage.parkVehicle(vehicle, slots.get(0));
        assertTrue(storage.exitVehicle("ABC123"));
        var reloadedStorage = new FileBasedParkingStorage(TEST_FILE);
        var reloadedSlots = reloadedStorage.getAllSlots();
        assertFalse(reloadedSlots.get(0).isOccupied());
    }

    @Test
    void testEditSlot() {
        storage.initializeSlots(5, 3, 2);
        assertTrue(storage.editSlot(1, VehicleType.LARGE));
        var reloadedStorage = new FileBasedParkingStorage(TEST_FILE);
        var reloadedSlots = reloadedStorage.getAllSlots();
        assertEquals(VehicleType.LARGE, reloadedSlots.get(0).getSupportedType());
    }

    @Test
    void testDeleteSlot() {
        storage.initializeSlots(5, 3, 2);
        assertTrue(storage.deleteSlot(10)); // Delete last slot
        var reloadedStorage = new FileBasedParkingStorage(TEST_FILE);
        var reloadedSlots = reloadedStorage.getAllSlots();
        assertEquals(9, reloadedSlots.size());
    }
}