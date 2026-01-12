package com.parkinglot.strategy;

import com.parkinglot.domain.ParkingSlot;
import com.parkinglot.domain.VehicleType;
import java.util.List;
import java.util.Optional;

public interface SlotAllocationStrategy {
    Optional<ParkingSlot> findSlot(VehicleType vehicleType, List<ParkingSlot> availableSlots);
}