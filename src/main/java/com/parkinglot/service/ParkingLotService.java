package com.parkinglot.service;

import com.parkinglot.domain.ParkingSlot;
import com.parkinglot.domain.Vehicle;
import com.parkinglot.domain.VehicleType;
import com.parkinglot.storage.ParkingStorage;
import com.parkinglot.strategy.SlotAllocationStrategy;
import java.util.List;
import java.util.Optional;

public class ParkingLotService {
    private final ParkingStorage storage;
    private final SlotAllocationStrategy allocationStrategy;

    public ParkingLotService(ParkingStorage storage, SlotAllocationStrategy allocationStrategy) {
        this.storage = storage;
        this.allocationStrategy = allocationStrategy;
    }

    public void createParkingLot(int smallSlots, int largeSlots, int oversizeSlots) {
        storage.initializeSlots(smallSlots, largeSlots, oversizeSlots);
    }

    public Optional<Integer> parkVehicle(String vehicleNumber, VehicleType vehicleType) {
        Vehicle vehicle = new Vehicle(vehicleNumber, vehicleType);
        List<ParkingSlot> allSlots = storage.getAllSlots();

        Optional<ParkingSlot> availableSlot = allocationStrategy.findSlot(vehicleType, allSlots);

        if (availableSlot.isPresent() && storage.parkVehicle(vehicle, availableSlot.get())) {
            return Optional.of(availableSlot.get().getSlotNumber());
        }
        return Optional.empty();
    }

    public boolean exitVehicle(String vehicleNumber) {
        return storage.exitVehicle(vehicleNumber);
    }

    public List<ParkingSlot> getStatus() {
        return storage.getAllSlots().stream()
                .filter(ParkingSlot::isOccupied)
                .toList();
    }

    public List<ParkingSlot> getAllSlots() {
        return storage.getAllSlots();
    }

    public boolean isParkingLotCreated() {
        return !storage.getAllSlots().isEmpty();
    }

    public boolean editSlot(int slotNumber, VehicleType newType) {
        return storage.editSlot(slotNumber, newType);
    }

    public boolean deleteSlot(int slotNumber) {
        return storage.deleteSlot(slotNumber);
    }
}