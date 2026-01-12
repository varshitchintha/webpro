package com.parkinglot.storage;

import com.parkinglot.domain.ParkingSlot;
import com.parkinglot.domain.Vehicle;
import com.parkinglot.domain.VehicleType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryParkingStorage implements ParkingStorage {
    private final List<ParkingSlot> slots = new ArrayList<>();

    @Override
    public void initializeSlots(int smallSlots, int largeSlots, int oversizeSlots) {
        slots.clear();
        int slotNumber = 1;
        for (int i = 0; i < smallSlots; i++) {
            slots.add(new ParkingSlot(slotNumber++, VehicleType.SMALL));
        }
        for (int i = 0; i < largeSlots; i++) {
            slots.add(new ParkingSlot(slotNumber++, VehicleType.LARGE));
        }
        for (int i = 0; i < oversizeSlots; i++) {
            slots.add(new ParkingSlot(slotNumber++, VehicleType.OVERSIZE));
        }
    }

    @Override
    public List<ParkingSlot> getAllSlots() {
        return new ArrayList<>(slots);
    }

    @Override
    public Optional<ParkingSlot> findSlotByVehicleNumber(String vehicleNumber) {
        return slots.stream()
                .filter(slot -> slot.isOccupied() && slot.getParkedVehicle().getVehicleNumber().equals(vehicleNumber))
                .findFirst();
    }

    @Override
    public boolean parkVehicle(Vehicle vehicle, ParkingSlot slot) {
        if (!slot.isOccupied() && slot.canAccommodate(vehicle.getType())) {
            slot.setParkedVehicle(vehicle);
            return true;
        }
        return false;
    }

    @Override
    public boolean exitVehicle(String vehicleNumber) {
        Optional<ParkingSlot> slotOpt = findSlotByVehicleNumber(vehicleNumber);
        if (slotOpt.isPresent()) {
            slotOpt.get().setParkedVehicle(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean editSlot(int slotNumber, VehicleType newType) {
        Optional<ParkingSlot> slotOpt = slots.stream()
                .filter(slot -> slot.getSlotNumber() == slotNumber)
                .findFirst();

        if (slotOpt.isPresent()) {
            ParkingSlot slot = slotOpt.get();
            if (slot.isOccupied()) {
                return false; // Cannot edit occupied slot
            }
            // Create new slot with same number but different type
            ParkingSlot newSlot = new ParkingSlot(slotNumber, newType);
            slots.set(slotNumber - 1, newSlot); // slots are 0-indexed in list
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSlot(int slotNumber) {
        Optional<ParkingSlot> slotOpt = slots.stream()
                .filter(slot -> slot.getSlotNumber() == slotNumber)
                .findFirst();

        if (slotOpt.isPresent()) {
            ParkingSlot slot = slotOpt.get();
            if (slot.isOccupied()) {
                return false; // Cannot delete occupied slot
            }
            slots.remove(slot);
            // Renumber remaining slots
            for (int i = 0; i < slots.size(); i++) {
                ParkingSlot updatedSlot = new ParkingSlot(i + 1, slots.get(i).getSupportedType());
                if (slots.get(i).isOccupied()) {
                    updatedSlot.setParkedVehicle(slots.get(i).getParkedVehicle());
                }
                slots.set(i, updatedSlot);
            }
            return true;
        }
        return false;
    }
}