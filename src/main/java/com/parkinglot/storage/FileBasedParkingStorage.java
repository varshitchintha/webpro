package com.parkinglot.storage;

import com.parkinglot.domain.ParkingSlot;
import com.parkinglot.domain.Vehicle;
import com.parkinglot.domain.VehicleType;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileBasedParkingStorage implements ParkingStorage {
    private final String filePath;
    private final List<ParkingSlot> slots = new ArrayList<>();

    public FileBasedParkingStorage(String filePath) {
        this.filePath = filePath;
        loadFromFile();
    }

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
        saveToFile();
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
            saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public boolean exitVehicle(String vehicleNumber) {
        Optional<ParkingSlot> slotOpt = findSlotByVehicleNumber(vehicleNumber);
        if (slotOpt.isPresent()) {
            slotOpt.get().setParkedVehicle(null);
            saveToFile();
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
            saveToFile();
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
            saveToFile();
            return true;
        }
        return false;
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("{");
            writer.println("  \"slots\": [");
            for (int i = 0; i < slots.size(); i++) {
                ParkingSlot slot = slots.get(i);
                writer.println("    {");
                writer.println("      \"slotNumber\": " + slot.getSlotNumber() + ",");
                writer.println("      \"supportedType\": \"" + slot.getSupportedType() + "\",");
                if (slot.isOccupied()) {
                    writer.println("      \"vehicleNumber\": \"" + slot.getParkedVehicle().getVehicleNumber() + "\",");
                    writer.println("      \"vehicleType\": \"" + slot.getParkedVehicle().getType() + "\"");
                } else {
                    writer.println("      \"vehicleNumber\": null,");
                    writer.println("      \"vehicleType\": null");
                }
                writer.println("    }" + (i < slots.size() - 1 ? "," : ""));
            }
            writer.println("  ]");
            writer.println("}");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save parking data", e);
        }
    }

    private void loadFromFile() {
        if (!Files.exists(Paths.get(filePath))) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line.trim());
            }
            parseJson(json.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load parking data", e);
        }
    }

    private void parseJson(String json) {
        slots.clear();
        String slotsJson = json.substring(json.indexOf("[") + 1, json.lastIndexOf("]"));
        String[] slotEntries = slotsJson.split("\\},\\s*\\{");
        for (String entry : slotEntries) {
            entry = entry.trim();
            if (entry.startsWith("{")) entry = entry.substring(1);
            if (entry.endsWith("}")) entry = entry.substring(0, entry.length() - 1);

            String[] lines = entry.split(",");
            int slotNumber = 0;
            VehicleType supportedType = null;
            String vehicleNumber = null;
            VehicleType vehicleType = null;

            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("\"slotNumber\":")) {
                    slotNumber = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
                } else if (line.startsWith("\"supportedType\":")) {
                    String typeStr = line.substring(line.indexOf(":") + 1).trim().replace("\"", "");
                    supportedType = VehicleType.valueOf(typeStr);
                } else if (line.startsWith("\"vehicleNumber\":")) {
                    String numStr = line.substring(line.indexOf(":") + 1).trim();
                    if (!numStr.equals("null")) {
                        vehicleNumber = numStr.replace("\"", "");
                    }
                } else if (line.startsWith("\"vehicleType\":")) {
                    String typeStr = line.substring(line.indexOf(":") + 1).trim();
                    if (!typeStr.equals("null")) {
                        vehicleType = VehicleType.valueOf(typeStr.replace("\"", ""));
                    }
                }
            }

            ParkingSlot slot = new ParkingSlot(slotNumber, supportedType);
            if (vehicleNumber != null && vehicleType != null) {
                slot.setParkedVehicle(new Vehicle(vehicleNumber, vehicleType));
            }
            slots.add(slot);
        }
    }
}