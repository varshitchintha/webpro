package com.parkinglot.strategy;

import com.parkinglot.domain.ParkingSlot;
import com.parkinglot.domain.VehicleType;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DefaultSlotAllocationStrategy implements SlotAllocationStrategy {
    @Override
    public Optional<ParkingSlot> findSlot(VehicleType vehicleType, List<ParkingSlot> availableSlots) {
        return availableSlots.stream()
                .filter(slot -> !slot.isOccupied() && slot.canAccommodate(vehicleType))
                .min(Comparator.comparingInt(ParkingSlot::getSlotNumber));
    }
}