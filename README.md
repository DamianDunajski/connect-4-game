# Connect 4 Game

A RESTful multiplayer implementation of the classic **Connect 4** game built with **Java 25** and **Spring Boot 4**.

---

## Table of Contents

- [Overview](#overview)
- [Game Rules & Logic](#game-rules--logic)
- [Board Data Structure & Coordinate System](#board-data-structure--coordinate-system)
- [Domain Model & JSON Representations](#domain-model--json-representations)
- [Win Detection Algorithm](#win-detection-algorithm)
- [Project Architecture](#project-architecture)
- [REST API Reference](#rest-api-reference)
- [Getting Started](#getting-started)
- [Known Limitations & Future Improvements](#known-limitations--future-improvements)

---

## Overview

This project provides an HTTP REST API to manage and play Connect 4 games asynchronously across different clients and locations:
- **Game Lifecycle**: Games are initiated by one player creating a game session, yielding a unique UUID, which a second player then joins.
- **Stateless Domain Logic**: Immutable domain records and business logic ensure game state integrity.
- **API Documentation**: Interactive Swagger UI and OpenAPI documentation are built-in.

---

## Game Rules & Logic

1. **Players**: Exactly 2 players per game. Allowed colours are `Red` and `Yellow`. Two players cannot select the same colour.
2. **Turns**: Turns alternate. A single player cannot drop two discs in a row.
3. **Column Bounds**: Discs can only be dropped into columns `0` through `6`.
4. **Gravity Mechanic**: Dropped discs fall to the lowest unoccupied row (row `5` down to row `0`). If row `0` of a column is filled, the column is full and further drops into that column are rejected.
5. **Win Condition**: The game is won when a player connects 4 consecutive discs of their colour horizontally, vertically, or diagonally. Once won, no further moves can be made.

---

## Board Data Structure & Coordinate System

### Grid Dimensions & Indexing

The board is a **7-column × 6-row** grid (42 total fields) indexed with a zero-based coordinate system starting from the **top-left corner**:

- **Columns**: `0` (leftmost) to `6` (rightmost)
- **Rows**: `0` (topmost) to `5` (bottommost)

```
       Col 0   Col 1   Col 2   Col 3   Col 4   Col 5   Col 6
Row 0  (0,0)   (1,0)   (2,0)   (3,0)   (4,0)   (5,0)   (6,0)   <- Top of board
Row 1  (0,1)   (1,1)   (2,1)   (3,1)   (4,1)   (5,1)   (6,1)
Row 2  (0,2)   (1,2)   (2,2)   (3,2)   (4,2)   (5,2)   (6,2)
Row 3  (0,3)   (1,3)   (2,3)   (3,3)   (4,3)   (5,3)   (6,3)
Row 4  (0,4)   (1,4)   (2,4)   (3,4)   (4,4)   (5,4)   (6,4)
Row 5  (0,5)   (1,5)   (2,5)   (3,5)   (4,5)   (5,5)   (6,5)   <- Bottom of board
```

### Field and Board Object Models

The board state is stored as an immutable `Board` record containing:
- `fields`: A flat list of 42 `Field` records ordered row-by-row (row 0: col 0..6, row 1: col 0..6, ..., row 5: col 0..6).
- `lastPopulatedField`: A `Field` reference (or `null`) pointing to the location and colour of the most recent move.

Each `Field` consists of:
- `location`:
  - `column` (`int`): Column index (0–6).
  - `row` (`int`): Row index (0–5).
- `colour`: `Red`, `Yellow`, or `null` if unoccupied (unoccupied fields omit the colour field during JSON serialization).

### Disc Dropping Logic

When a disc is dropped into column `c`:
1. Find the lowest unoccupied row: scans `fields` where `column == c` and finds the minimum occupied row index. If none, the disc lands on row `5`. Otherwise, it lands on row `lastOccupiedRow - 1`.
2. If `lastOccupiedRow == 0`, an `IllegalStateException` ("Column X is already full") is thrown.
3. A new `Board` instance is created with the updated field and set as `lastPopulatedField`.

---

## Domain Model & JSON Representations

### `Game` Structure

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "board": {
    "fields": [
      {
        "location": {
          "column": 0,
          "row": 0
        }
      },
      {
        "location": {
          "column": 0,
          "row": 5
        },
        "colour": "Red"
      }
    ],
    "lastPopulatedField": {
      "location": {
        "column": 0,
        "row": 5
      },
      "colour": "Red"
    }
  },
  "players": [
    {
      "name": "Alice",
      "colour": "Red"
    },
    {
      "name": "Bob",
      "colour": "Yellow"
    }
  ],
  "outcome": {
    "winner": {
      "name": "Alice",
      "colour": "Red"
    }
  }
}
```

- `id` (`UUID`): Unique game identifier.
- `board` (`Board`): Current board state.
- `players` (`List<Player>`): List of registered players (max 2).
- `outcome` (`Outcome`, nullable): Contains `winner` when the game is won, or `null` while active.

---

## Win Detection Algorithm

Win evaluation is performed by `OutcomeAnalyser` following each move using Java 25 stream gatherers (`Gatherers.windowSliding(4)`):

1. **Trigger**: Evaluates lines passing through `board.lastPopulatedField()`.
2. **Direction Vectors**: Checks 4 possible alignments spanning `[-3, +3]` steps around the target move:
   - **Horizontal (Row)**: `(col + i, row)`
   - **Vertical (Column)**: `(col, row + i)`
   - **Diagonal (Bottom-Left to Top-Right)**: `(col + i, row - i)`
   - **Diagonal (Top-Left to Bottom-Right)**: `(col + i, row + i)`
3. **Sliding Window**: Extracts a sliding window of length 4 across the 7 generated locations in each direction. If all 4 entries in any window match the player's colour, that player is declared the winner.

---

## Project Architecture

The project is organised as a multi-module Gradle project:

```
connect-4-game/
├── build-logic/                # Gradle convention plugins
├── domain/                     # Pure domain layer (records, business rules, win evaluation)
│   ├── src/main/java/.../domain/
│   │   ├── Game.java           # Game, Board, Field, Location, Outcome records
│   │   ├── Player.java         # Player record and Colour enum (Red, Yellow)
│   │   └── OutcomeAnalyser.java# 4-in-a-row algorithm
│   └── src/test/java/          # Domain unit tests
└── rest-service/               # Spring Boot REST API layer
    ├── src/main/java/.../rest/
    │   ├── Application.java    # Spring Boot entry point
    │   ├── api/
    │   │   ├── GameResource.java          # REST controllers
    │   │   └── GlobalExceptionHandler.java# RFC 7807 ProblemDetail mappings
    │   └── repository/
    │       ├── GameRepository.java        # Repository contract
    │       └── InMemoryGameRepository.java# ConcurrentHashMap storage
    └── src/test/java/          # Controller tests and integration tests
```

---

## REST API Reference

Base URL: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger` (OpenAPI JSON: `/v3/api-docs`)

### 1. Create Game

- **Endpoint**: `POST /game/connect-4`
- **Request Body**:
  ```json
  {
    "name": "Alice",
    "colour": "Red"
  }
  ```
- **Responses**:
  - `200 OK`: Game created (returns `Game` JSON).
  - `400 Bad Request`: Invalid player payload.

### 2. Join Game

- **Endpoint**: `PUT /game/connect-4/{id}/join`
- **Request Body**:
  ```json
  {
    "name": "Bob",
    "colour": "Yellow"
  }
  ```
- **Responses**:
  - `200 OK`: Joined successfully (returns updated `Game` JSON).
  - `400 Bad Request`: Invalid player payload.
  - `404 Not Found`: Game ID does not exist.
  - `409 Conflict`: Game already has 2 players or colour conflict.

### 3. Drop Disc

- **Endpoint**: `PUT /game/connect-4/{id}/drop/{colour}/column/{column}`
- **Path Parameters**:
  - `id`: Game UUID
  - `colour`: `Red` or `Yellow`
  - `column`: Integer from `0` to `6`
- **Responses**:
  - `200 OK`: Disc dropped (returns updated `Game` JSON).
  - `400 Bad Request`: Invalid column index or unknown player colour.
  - `404 Not Found`: Game ID not found.
  - `409 Conflict`: Move out of turn, column full, or game already ended.

---

## Getting Started

### Prerequisites

- **Java 25** (with preview features enabled for Stream Gatherers)
- **GraalVM JDK 25** (optional, required for Ahead-Of-Time (AOT) native image compilation) with C build tools (`clang`/`gcc`)
- **Gradle** (or use the included `./gradlew` wrapper)

### Build and Test

Compile the project and run all unit and integration tests:

```bash
./gradlew clean build
```

Run tests only:

```bash
./gradlew test
```

### Run the Application (JVM)

Start the Spring Boot REST service:

```bash
./gradlew :rest-service:bootRun
```

Or run the compiled JAR:

```bash
java -jar rest-service/build/libs/rest-service-1.0.0-SNAPSHOT.jar
```

### GraalVM Native Image (AOT Compilation)

The project is configured with GraalVM Native Build Tools to compile into a standalone, instant-startup native executable.

#### 1. Compile Native Binary

Build the native executable ahead-of-time:

```bash
./gradlew :rest-service:nativeCompile
```

#### 2. Run Native Executable

Execute the generated binary directly without requiring a JVM:

```bash
./rest-service/build/native/nativeCompile/connect-4-game
```

#### 3. Run Native Tests

Execute integration and unit tests compiled directly into a native binary:

```bash
./gradlew :rest-service:nativeTest
```

#### 4. Build Native Container Image (via Buildpacks)

Package the native binary into a container image using Cloud Native Buildpacks (requires Docker):

```bash
./gradlew :rest-service:bootBuildImage
```

---

## Known Limitations & Future Improvements

- **In-Memory Storage**: `InMemoryGameRepository` uses a `ConcurrentHashMap`. Game state is lost on service restart, and horizontal scaling requires sticky sessions or shared storage (such as Redis or PostgreSQL).
- **Game Lifecycle & Expiration**: Active games are retained indefinitely in memory; adding TTL/timeout-based eviction will prevent resource exhaustion.
- **Draw Detection**: Full-board detection with no winner is currently not flagged as an explicit draw outcome.
- **Real-Time Push Notifications**: Clients currently rely on polling; WebSocket or Server-Sent Events (SSE) support would improve real-time player turn notifications.
