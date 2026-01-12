package com.parkinglot.seeder;

import com.parkinglot.domain.VehicleType;
import com.parkinglot.service.ParkingLotService;

public class ParkingLotSeeder {
    private final ParkingLotService service;

    public ParkingLotSeeder(ParkingLotService service) {
        this.service = service;
    }

    public void seed(int smallSlots, int largeSlots, int oversizeSlots, String[][] parkedVehicles) {
        service.createParkingLot(smallSlots, largeSlots, oversizeSlots);
        for (String[] vehicleData : parkedVehicles) {
            String vehicleNumber = vehicleData[0];
            VehicleType type = VehicleType.valueOf(vehicleData[1]);
            service.parkVehicle(vehicleNumber, type);
        }
    }

    public void seedScenario(String scenario) {
        switch (scenario.toUpperCase()) {
            case "OFFICE":
                seedOfficeScenario();
                break;
            case "MALL":
                seedMallScenario();
                break;
            case "RESIDENTIAL":
                seedResidentialScenario();
                break;
            default:
                seedDefaultScenario();
                break;
        }
    }

    private void seedOfficeScenario() {
        String[][] vehicles = {
            {"EMP001", "SMALL"},
            {"EMP002", "SMALL"},
            {"EMP003", "LARGE"},
            {"VIS001", "LARGE"},
            {"VIS002", "OVERSIZE"}
        };
        seed(5, 3, 2, vehicles);
    }

    private void seedMallScenario() {
        String[][] vehicles = {
            {"SHOP001", "SMALL"},
            {"SHOP002", "SMALL"},
            {"SHOP003", "SMALL"},
            {"CUST001", "LARGE"},
            {"CUST002", "LARGE"},
            {"DELIVERY001", "OVERSIZE"}
        };
        seed(4, 4, 3, vehicles);
    }

    private void seedResidentialScenario() {
        String[][] vehicles = {
            {"RES001", "SMALL"},
            {"RES002", "LARGE"},
            {"RES003", "LARGE"},
            {"RES004", "OVERSIZE"}
        };
        seed(3, 3, 2, vehicles);
    }

    private void seedDefaultScenario() {
        String[][] vehicles = {
            {"ABC123", "SMALL"},
            {"DEF456", "LARGE"},
            {"GHI789", "OVERSIZE"}
        };
        seed(3, 2, 1, vehicles);
    }
}