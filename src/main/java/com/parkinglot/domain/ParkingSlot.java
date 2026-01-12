package com.parkinglot.domain;

public class ParkingSlot {
    private final int slotNumber;
    private final VehicleType supportedType;
    private Vehicle parkedVehicle;

    public ParkingSlot(int slotNumber, VehicleType supportedType) {
        this.slotNumber = slotNumber;
        this.supportedType = supportedType;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public VehicleType getSupportedType() {
        return supportedType;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    public void setParkedVehicle(Vehicle parkedVehicle) {
        this.parkedVehicle = parkedVehicle;
    }

    public boolean isOccupied() {
        return parkedVehicle != null;
    }

    public boolean canAccommodate(VehicleType vehicleType) {
        switch (supportedType) {
            case SMALL:
                return vehicleType == VehicleType.SMALL;
            case LARGE:
                return vehicleType == VehicleType.SMALL || vehicleType == VehicleType.LARGE;
            case OVERSIZE:
                return true;
            default:
                return false;
        }
    }
}