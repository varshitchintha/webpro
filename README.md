# Parking Lot Management System

A CLI-based parking lot management application built with Java 17 that models a parking lot with automatic slot size distribution.

## Tech Stack
- Java 17
- JUnit 5 for testing
- Clean Architecture with SOLID principles

## Approach and Design

### Architecture Overview
The application follows Clean Architecture principles with clear separation of concerns:

```
CLI Layer → Controller Layer → Service Layer → Strategy Layer → Storage Layer → Domain Models
```

- **Domain Layer**: Core business entities (Vehicle, VehicleType, ParkingSlot)
- **Storage Layer**: Data persistence (InMemoryParkingStorage, FileBasedParkingStorage)
- **Strategy Layer**: Slot allocation logic (DefaultSlotAllocationStrategy)
- **Service Layer**: Business logic orchestration (ParkingLotService)
- **Controller Layer**: Command parsing and validation (ParkingLotController)
- **CLI Layer**: User interface (ParkingLotCLI)

### Slot Distribution (User Controlled)
Users have full control over slot distribution by specifying exact counts for each vehicle type:

**CREATE Command Format:**
```
CREATE <small_count> <large_count> <oversize_count>
```

**Examples:**
- `CREATE 5 3 2` → 5 Small + 3 Large + 2 Oversize slots (10 total)
- `CREATE 2 1 0` → 2 Small + 1 Large + 0 Oversize slots (3 total)
- `CREATE 10 5 3` → 10 Small + 5 Large + 3 Oversize slots (18 total)

**Slot Numbering:** Slots are numbered sequentially starting from 1, with Small slots first, then Large, then Oversize.

### Slot Allocation Strategy
- Allocates the smallest available slot number that can accommodate the vehicle type
- **Small vehicles**: Can only park in Small slots
- **Large vehicles**: Can park in Small or Large slots (prefers smallest available)
- **Oversize vehicles**: Can park in any slot type

## Key Files and Folders

### Core Implementation
- `src/main/java/com/parkinglot/domain/` - Business entities
  - `VehicleType.java` - Enum for vehicle sizes
  - `Vehicle.java` - Vehicle representation
  - `ParkingSlot.java` - Parking slot with size compatibility logic
- `src/main/java/com/parkinglot/storage/` - Data persistence
  - `ParkingStorage.java` - Interface for storage operations
  - `InMemoryParkingStorage.java` - Memory-based storage
  - `FileBasedParkingStorage.java` - JSON file-based persistence
- `src/main/java/com/parkinglot/strategy/` - Allocation algorithms
  - `SlotAllocationStrategy.java` - Strategy interface
  - `DefaultSlotAllocationStrategy.java` - Smallest-slot-first allocation
- `src/main/java/com/parkinglot/service/ParkingLotService.java` - Business logic
- `src/main/java/com/parkinglot/controller/ParkingLotController.java` - Command handling
- `src/main/java/com/parkinglot/cli/ParkingLotCLI.java` - Main application entry point

### Testing
- `src/test/java/com/parkinglot/service/ParkingLotServiceTest.java` - Service layer tests
- `src/test/java/com/parkinglot/strategy/DefaultSlotAllocationStrategyTest.java` - Strategy tests
- `src/test/java/com/parkinglot/storage/` - Storage implementation tests
- `src/test/java/com/parkinglot/integration/ParkingLotIntegrationTest.java` - End-to-end tests

## How to Compile and Run

**Compile:**
```powershell
& "C:\Program Files\Java\jdk-17\bin\javac.exe" -d out -cp out src/main/java/com/parkinglot/domain/*.java src/main/java/com/parkinglot/storage/ParkingStorage.java src/main/java/com/parkinglot/storage/InMemoryParkingStorage.java src/main/java/com/parkinglot/storage/FileBasedParkingStorage.java src/main/java/com/parkinglot/strategy/SlotAllocationStrategy.java src/main/java/com/parkinglot/strategy/DefaultSlotAllocationStrategy.java src/main/java/com/parkinglot/service/ParkingLotService.java src/main/java/com/parkinglot/controller/ParkingLotController.java src/main/java/com/parkinglot/cli/ParkingLotCLI.java src/main/java/com/parkinglot/seeder/ParkingLotSeeder.java
```

**Run:**
```powershell
& "C:\Program Files\Java\jdk-17\bin\java.exe" -cp out com.parkinglot.cli.ParkingLotCLI
```

## How to Run Tests

**Download JUnit:**
```powershell
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.9.2/junit-platform-console-standalone-1.9.2.jar" -OutFile "junit-platform-console-standalone-1.9.2.jar"
```

**Compile tests:**
```powershell
& "C:\Program Files\Java\jdk-17\bin\javac.exe" -d out -cp "out;junit-platform-console-standalone-1.9.2.jar" src/test/java/com/parkinglot/service/ParkingLotServiceTest.java src/test/java/com/parkinglot/strategy/DefaultSlotAllocationStrategyTest.java src/test/java/com/parkinglot/storage/InMemoryParkingStorageTest.java src/test/java/com/parkinglot/storage/FileBasedParkingStorageTest.java src/test/java/com/parkinglot/integration/ParkingLotIntegrationTest.java
```

**Run tests:**
```powershell
& "C:\Program Files\Java\jdk-17\bin\java.exe" -jar junit-platform-console-standalone-1.9.2.jar --class-path out --scan-classpath
```

## Process to Run, Test, and Verify

### Running the Application

1. **Prerequisites**: Java 17 installed
2. **Compile**: Run the compile command from the section below
3. **Execute**: Run the application and use CLI commands

**Seeding Scenarios:**

**OFFICE Scenario** (5 Small, 3 Large, 2 Oversize):
- Employee vehicles (small/large)
- Visitor vehicles (large/oversize)

**MALL Scenario** (4 Small, 4 Large, 3 Oversize):
- Shop employee vehicles (small)
- Customer vehicles (large)
- Delivery vehicles (oversize)

**RESIDENTIAL Scenario** (3 Small, 3 Large, 2 Oversize):
- Resident vehicles (mixed sizes)

**Sample Session with Seeding:**
```
> SEED OFFICE
Parking lot seeded with office scenario data

> STATUS
Slot No.	Registration No.	Type
1		EMP001		SMALL
2		EMP002		SMALL
4		EMP003		LARGE
6		VIS001		LARGE
9		VIS002		OVERSIZE

> END
Exiting...
```

**Sample Session with Custom Slot Distribution & Management:**
```
> CREATE 3 2 1
Created a parking lot with 6 slots (3 small, 2 large, 1 oversize)
(Slots distributed: 1-3 Small, 4-5 Large, 6 Oversize)

> PARK CAR1 SMALL
Allocated slot number: 1

> PARK CAR2 LARGE
Allocated slot number: 4

> PARK CAR3 OVERSIZE
Allocated slot number: 6

> STATUS
Slot No.	Registration No.	Type
1		CAR1		SMALL
4		CAR2		LARGE
6		CAR3		OVERSIZE

> EDIT 1 LARGE
Failed to edit slot 1. Slot may be occupied or not exist

> EXIT CAR1
Vehicle CAR1 exited successfully

> EDIT 1 LARGE
Slot 1 edited to LARGE successfully

> DELETE 6
Slot 6 deleted successfully. Remaining slots renumbered

> STATUS
Slot No.	Registration No.	Type
4		CAR2		LARGE

> END
Exiting...
```

**Another Example - Office Parking:**
```
> CREATE 10 5 2
Created a parking lot with 17 slots (10 small, 5 large, 2 oversize)

> PARK EMP001 SMALL
Allocated slot number: 1

> PARK VISITOR001 LARGE
Allocated slot number: 11
```

### Testing and Verification

**Unit Tests**: 14 comprehensive tests covering:
- ParkingLotService business logic
- Slot allocation strategy
- In-memory and file-based storage
- Integration scenarios

**Run Tests:**
1. Download JUnit JAR (command below)
2. Compile tests
3. Execute test suite

### Test Data and Seed Data

**Custom Slot Distribution Examples:**
- `CREATE 5 3 2`: 5 Small, 3 Large, 2 Oversize slots
- `CREATE 10 0 0`: 10 Small, 0 Large, 0 Oversize slots (compact car only lot)
- `CREATE 0 0 5`: 0 Small, 0 Large, 5 Oversize slots (truck/SUV only lot)

**Sample Test Scenarios:**
```java
// Using ParkingLotSeeder for testing
String[][] testVehicles = {
    {"ABC123", "SMALL"},
    {"DEF456", "LARGE"},
    {"GHI789", "OVERSIZE"}
};
ParkingLotSeeder seeder = new ParkingLotSeeder(service);
// Create 3 small, 2 large, 1 oversize slots
seeder.seed(3, 2, 1, testVehicles);
```

**Verification Commands:**
```
CREATE 3 2 1
PARK TEST001 SMALL    # Should allocate slot 1
PARK TEST002 LARGE    # Should allocate slot 4 (Small=1-3, Large=4-5)
PARK TEST003 OVERSIZE # Should allocate slot 6 (Oversize=6)
STATUS
EXIT TEST001
STATUS
```

## Supported CLI Commands
- CREATE <small_count> <large_count> <oversize_count> - Create parking lot with specified slot counts
- SEED [OFFICE|MALL|RESIDENTIAL] - Seed parking lot with predefined scenario data
- PARK <vehicle_number> <SMALL|LARGE|OVERSIZE> - Park a vehicle
- EXIT <vehicle_number> - Exit a vehicle
- STATUS - Show parking status in tabular format
- EDIT <slot_number> <SMALL|LARGE|OVERSIZE> - Change slot type (only if unoccupied)
- DELETE <slot_number> - Remove slot and renumber remaining slots (only if unoccupied)
- END - Exit application

## GitHub Repository Structure

This complete Parking Lot Management System should be submitted as a public GitHub repository containing:

```
parking-lot-management/
├── src/
│   ├── main/java/com/parkinglot/
│   │   ├── cli/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── strategy/
│   │   ├── storage/
│   │   ├── domain/
│   │   └── seeder/
│   └── test/java/com/parkinglot/
├── out/                          # Compiled classes (generated)
├── junit-platform-console-standalone-1.9.2.jar  # Test framework
└── README.md                     # This documentation
```

## Key Features Demonstrated

✅ **User-Controlled Slot Division**: User specifies exact counts for Small/Large/Oversize slots
✅ **Data Seeding**: Predefined scenarios (Office, Mall, Residential) for quick setup
✅ **Dynamic Slot Management**: EDIT and DELETE slots (only when unoccupied)
✅ **Flexible Configuration**: Create specialized lots (e.g., compact-car only, SUV only, etc.)
✅ **Smart Allocation**: Smallest available compatible slot selection
✅ **Multiple Storage Options**: In-memory and file-based persistence
✅ **Clean Architecture**: SOLID principles with clear layer separation
✅ **Comprehensive Testing**: 27 unit and integration tests
✅ **CLI Interface**: User-friendly command-line operation
✅ **Safety Constraints**: Cannot edit/delete occupied slots
✅ **Error Handling**: Graceful handling of invalid commands and edge cases