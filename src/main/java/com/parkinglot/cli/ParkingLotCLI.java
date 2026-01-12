package com.parkinglot.cli;

import com.parkinglot.controller.ParkingLotController;
import com.parkinglot.service.ParkingLotService;
import com.parkinglot.storage.InMemoryParkingStorage;
import com.parkinglot.strategy.DefaultSlotAllocationStrategy;
import java.util.Scanner;

public class ParkingLotCLI {
    public static void main(String[] args) {
        ParkingLotService service = new ParkingLotService(
            new InMemoryParkingStorage(),
            new DefaultSlotAllocationStrategy()
        );

        ParkingLotController controller = new ParkingLotController(service);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Parking Lot Management System");
        System.out.println("Commands:");
        System.out.println("CREATE <small_count> <large_count> <oversize_count> - Create parking lot with specified slot counts");
        System.out.println("SEED [OFFICE|MALL|RESIDENTIAL] - Seed parking lot with predefined scenario data");
        System.out.println("PARK <vehicle_number> <SMALL|LARGE|OVERSIZE> - Park a vehicle");
        System.out.println("EXIT <vehicle_number> - Exit a vehicle");
        System.out.println("STATUS - Show parking status");
        System.out.println("EDIT <slot_number> <SMALL|LARGE|OVERSIZE> - Change slot type (only if unoccupied)");
        System.out.println("DELETE <slot_number> - Remove slot (only if unoccupied)");
        System.out.println("END - Exit application");
        System.out.println();

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();

            if (command.trim().equalsIgnoreCase("END")) {
                System.out.println("Exiting...");
                break;
            }

            String result = controller.processCommand(command);
            System.out.println(result);
            System.out.println();
        }

        scanner.close();
    }
}