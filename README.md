# ToxicCleanup

A Java-based 2D tile game developed as part of **CSSE2002 – Programming in the Large** at The University of Queensland.

ToxicCleanup challenges the player to restore contaminated fields using a collection of machines powered by a shared energy system. The project focuses on object-oriented design, component interaction, state management, and integration with a provided game engine.

## Overview

The player explores a tile-based world containing contaminated fields and must clean all toxic areas before losing all health.

To restore the environment, the player can pave terrain and construct machines including solar panels, pumps, and teleporters. These machines interact through a shared power system, requiring the player to manage available energy while cleaning the map.

The game ends when:

- All toxic fields have been successfully cleaned, resulting in a win.
- The player's health reaches zero, resulting in a loss.

## Features

### Player System
- Tile-based movement using **WASD controls**
- Health system with a maximum of 10 HP
- Collision and tile interaction handling
- Movement boundaries based on the game world
- Dynamic player state and sprite behaviour

### Toxic Field Cleanup
- Toxic fields begin with a toxicity level of 6
- Fields visually change as their toxicity decreases
- Pumps progressively remove toxicity from contaminated fields
- Fully restored fields automatically remove completed cleanup equipment
- Cleaning every toxic field triggers the game win condition

### Machine & Power System

The game uses a shared power economy managed by `MachinesManager`.

| Machine | Build Cost | Behaviour |
|---|---:|---|
| Solar Panel | 3 | Generates 1 power every 120 ticks |
| Pump | 5 | Removes toxicity every 100 ticks while sufficiently powered |
| Teleporter | 2 | Moves the player between teleporter locations |

Available power is capped at **14 units**.

Solar panels regenerate shared power over time, while pumps require sufficient power to continue operating. Teleporters allow fast movement around the map when enough power is available.

### World Interaction
- Dirt tiles can be paved before machines are constructed
- Solar panels and teleporters can be placed on paved dirt
- Pumps can be placed directly on active toxic fields
- Tile entities react dynamically when the player moves over them
- Maps are loaded from external resource files

### HUD & Game State
The in-game HUD displays:

- Current player health
- Available machine power
- Remaining game time
- Win and loss overlays

The game also applies periodic environmental damage while toxic fields remain active.

## Controls

| Input | Action |
|---|---|
| `W` | Move up |
| `A` | Move left |
| `S` | Move down |
| `D` | Move right |
| `F` | Pave a dirt tile |
| Left Click | Build a Solar Panel on paved dirt / Pump on a toxic field |
| Right Click | Build a Teleporter on paved dirt |
| `E` | Activate a Teleporter |

## Architecture

The project is organised into several object-oriented components:

```text
src/toxiccleanup/
├── Main.java
└── builder/
    ├── entities/
    │   └── tiles/
    ├── machines/
    ├── player/
    ├── ui/
    └── world/
```

### Core Components

**Player**
- `PlayerManager`
- `Cleaner`
- Player movement, health, rendering, and world interactions

**World**
- `ToxicWorld`
- `WorldBuilder`
- Tile management, map loading, ticking, and rendering

**Tiles**
- Dirt
- Grass
- Toxic fields
- Chasms
- Interactive tile behaviours

**Machines**
- `MachinesManager`
- `SolarPanel`
- `Pump`
- `Teleporter`
- Shared power management and machine interactions

**UI**
- `GuiManager`
- Health display
- Power display
- Countdown timer
- Win/loss overlays

## Technical Highlights

- Object-oriented Java architecture
- Interface-driven component design
- Game-loop and tick-based state updates
- Shared resource management
- Timer-based gameplay mechanics
- Dynamic entity and tile interactions
- Map parsing and world construction
- Sprite-based rendering and animation
- Modular separation of player, world, machine, and UI systems

## Running the Project

The project was developed using **Java** and integrates with the game engine supplied for CSSE2002.

The provided engine dependency is intentionally not included in this repository.

To run the project in its original course environment:

1. Add the required CSSE2002 `engine.jar` dependency.
2. Ensure the `resources` directory is available from the project root.
3. Compile the Java source files.
4. Run:

```text
toxiccleanup.Main
```

## Academic Context

This project was completed as part of **CSSE2002 – Programming in the Large** at The University of Queensland.

The game engine, API specification, starter framework, and selected resources were supplied as part of the course. The implementation in this repository extends the provided framework to implement the game systems and behaviours required by the project specification.

This repository is presented as a software engineering portfolio project demonstrating Java development, object-oriented programming, specification-driven implementation, and integration with an existing codebase.

