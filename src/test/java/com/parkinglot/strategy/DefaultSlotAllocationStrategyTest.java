package com.parkinglot.strategy;

import com.parkinglot.domain.ParkingSlot;
import com.parkinglot.domain.VehicleType;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DefaultSlotAllocationStrategyTest {
    private final SlotAllocationStrategy strategy = new DefaultSlotAllocationStrategy();

    @Test
    void testFindSlotForSmall() {
        List<ParkingSlot> slots = Arrays.asList(
            new ParkingSlot(1, VehicleType.SMALL),
            new ParkingSlot(2, VehicleType.LARGE)
        );
        var result = strategy.findSlot(VehicleType.SMALL, slots);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getSlotNumber());
    }

    @Test
    void testFindSlotForLarge() {
        List<ParkingSlot> slots = Arrays.asList(
            new ParkingSlot(1, VehicleType.SMALL),
            new ParkingSlot(2, VehicleType.LARGE)
        );
        var result = strategy.findSlot(VehicleType.LARGE, slots);
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getSlotNumber());
    }

    @Test
    void testNoAvailableSlot() {
        ParkingSlot slot = new ParkingSlot(1, VehicleType.SMALL);
        slot.setParkedVehicle(new com.parkinglot.domain.Vehicle("ABC", VehicleType.SMALL));
        List<ParkingSlot> slots = Arrays.asList(slot);
        var result = strategy.findSlot(VehicleType.SMALL, slots);
        assertFalse(result.isPresent());
    }
}