package com.parkinglot.storage;

import com.parkinglot.domain.ParkingSlot;
import com.parkinglot.domain.Vehicle;
import com.parkinglot.domain.VehicleType;
import java.util.List;
import java.util.Optional;

public interface ParkingStorage {
    void initializeSlots(int smallSlots, int largeSlots, int oversizeSlots);
    List<ParkingSlot> getAllSlots();
    Optional<ParkingSlot> findSlotByVehicleNumber(String vehicleNumber);
    boolean parkVehicle(Vehicle vehicle, ParkingSlot slot);
    boolean exitVehicle(String vehicleNumber);
    boolean editSlot(int slotNumber, VehicleType newType);
    boolean deleteSlot(int slotNumber);
}