package com.parkinglot.controller;

import com.parkinglot.domain.ParkingSlot;
import com.parkinglot.domain.VehicleType;
import com.parkinglot.service.ParkingLotService;
import com.parkinglot.seeder.ParkingLotSeeder;
import java.util.List;
import java.util.Optional;

public class ParkingLotController {
    private final ParkingLotService service;
    private final ParkingLotSeeder seeder;

    public ParkingLotController(ParkingLotService service) {
        this.service = service;
        this.seeder = new ParkingLotSeeder(service);
    }

    public String processCommand(String command) {
        String[] parts = command.trim().split("\\s+");

        if (parts.length == 0) {
            return "Invalid command";
        }

        String action = parts[0].toUpperCase();

        try {
            switch (action) {
                case "CREATE":
                    return handleCreate(parts);
                case "PARK":
                    return handlePark(parts);
                case "EXIT":
                    return handleExit(parts);
                case "STATUS":
                    return handleStatus();
                case "EDIT":
                    return handleEdit(parts);
                case "DELETE":
                    return handleDelete(parts);
                case "SEED":
                    return handleSeed(parts);
                case "END":
                    return "END";
                default:
                    return "Invalid command";
            }
        } catch (Exception e) {
            return "Invalid command format";
        }
    }

    private String handleCreate(String[] parts) {
        if (parts.length != 4) {
            return "Invalid CREATE command. Usage: CREATE <small_count> <large_count> <oversize_count>";
        }

        try {
            int smallSlots = Integer.parseInt(parts[1]);
            int largeSlots = Integer.parseInt(parts[2]);
            int oversizeSlots = Integer.parseInt(parts[3]);

            if (smallSlots < 0 || largeSlots < 0 || oversizeSlots < 0) {
                return "Slot counts must be non-negative";
            }

            if (smallSlots + largeSlots + oversizeSlots == 0) {
                return "Total slots must be greater than 0";
            }

            service.createParkingLot(smallSlots, largeSlots, oversizeSlots);
            int totalSlots = smallSlots + largeSlots + oversizeSlots;
            return "Created a parking lot with " + totalSlots + " slots (" + smallSlots + " small, " + largeSlots + " large, " + oversizeSlots + " oversize)";
        } catch (NumberFormatException e) {
            return "Invalid number format";
        }
    }

    private String handlePark(String[] parts) {
        if (parts.length != 3) {
            return "Invalid PARK command. Usage: PARK <vehicle_number> <SMALL|LARGE|OVERSIZE>";
        }

        String vehicleNumber = parts[1];
        VehicleType vehicleType;

        try {
            vehicleType = VehicleType.valueOf(parts[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            return "Invalid vehicle type. Must be SMALL, LARGE, or OVERSIZE";
        }

        Optional<Integer> slotNumber = service.parkVehicle(vehicleNumber, vehicleType);
        if (slotNumber.isPresent()) {
            return "Allocated slot number: " + slotNumber.get();
        } else {
            return "Parking lot full or invalid vehicle type for available slots";
        }
    }

    private String handleExit(String[] parts) {
        if (parts.length != 2) {
            return "Invalid EXIT command. Usage: EXIT <vehicle_number>";
        }

        String vehicleNumber = parts[1];
        boolean success = service.exitVehicle(vehicleNumber);
        if (success) {
            return "Vehicle " + vehicleNumber + " exited successfully";
        } else {
            return "Vehicle " + vehicleNumber + " not found";
        }
    }

    private String handleStatus() {
        if (!service.isParkingLotCreated()) {
            return "Parking lot not created yet";
        }

        List<ParkingSlot> occupiedSlots = service.getStatus();
        if (occupiedSlots.isEmpty()) {
            return "No vehicles parked";
        }

        StringBuilder status = new StringBuilder();
        status.append("Slot No.\tRegistration No.\tType\n");
        for (ParkingSlot slot : occupiedSlots) {
            status.append(slot.getSlotNumber())
                  .append("\t\t")
                  .append(slot.getParkedVehicle().getVehicleNumber())
                  .append("\t\t")
                  .append(slot.getParkedVehicle().getType())
                  .append("\n");
        }
        return status.toString().trim();
    }

    private String handleEdit(String[] parts) {
        if (parts.length != 3) {
            return "Invalid EDIT command. Usage: EDIT <slot_number> <SMALL|LARGE|OVERSIZE>";
        }

        try {
            int slotNumber = Integer.parseInt(parts[1]);
            VehicleType newType;

            try {
                newType = VehicleType.valueOf(parts[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                return "Invalid vehicle type. Must be SMALL, LARGE, or OVERSIZE";
            }

            boolean success = service.editSlot(slotNumber, newType);
            if (success) {
                return "Slot " + slotNumber + " edited to " + newType + " successfully";
            } else {
                return "Failed to edit slot " + slotNumber + ". Slot may be occupied or not exist";
            }
        } catch (NumberFormatException e) {
            return "Invalid slot number";
        }
    }

    private String handleDelete(String[] parts) {
        if (parts.length != 2) {
            return "Invalid DELETE command. Usage: DELETE <slot_number>";
        }

        try {
            int slotNumber = Integer.parseInt(parts[1]);

            boolean success = service.deleteSlot(slotNumber);
            if (success) {
                return "Slot " + slotNumber + " deleted successfully. Remaining slots renumbered";
            } else {
                return "Failed to delete slot " + slotNumber + ". Slot may be occupied or not exist";
            }
        } catch (NumberFormatException e) {
            return "Invalid slot number";
        }
    }

    private String handleSeed(String[] parts) {
        if (parts.length > 2) {
            return "Invalid SEED command. Usage: SEED or SEED <OFFICE|MALL|RESIDENTIAL>";
        }

        String scenario = parts.length == 2 ? parts[1] : "DEFAULT";

        try {
            seeder.seedScenario(scenario);
            return "Parking lot seeded with " + scenario.toLowerCase() + " scenario data";
        } catch (Exception e) {
            return "Failed to seed data: " + e.getMessage();
        }
    }
}