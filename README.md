# Tetris Refactoring Project

## GitHub
[Tetris Refactoring Project Repository](https://github.com/cylim0823/CW2025)

---
## Requirements
Before building the project, ensure the following versions are installed:

- **Java 17** or later
- **Maven 3.9** or later
- **JavaFX 21** or later (retrieved automatically through Maven)

---

## Compilation Instructions
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/cylim0823/CW2025.git
    ```
2.  **Navigate to the project folder:**
    ```bash
    cd CW2025
    ```
3.  **Build and run using Maven:**
    ```bash
    mvn clean install
    mvn javafx:run
    ```

## Basic Key Controls
-   **Move Left:** A
-   **Move Right:** D
-   **Move Down:** S
-   **Hard Drop:** Spacebar
-   **Rotate:** Up Arrow / W
-   **Hold Piece:** C
-   **Undo Move:** R (Limit depends on Mode)
-   **Pause:** P
-   **Mute Audio:** M
-   **New Game:** N

---

---

## Implemented and Working Properly

### 1. New Feature: Zen Mode
* **What it is:** A stress-free, infinite game mode designed for practice.
* **Implementation:** Implemented via the **Strategy Design Pattern** (`ZenMode.java`).
* **Features:**
    * **Auto-Restart:** The game automatically clears the board and continues when you top out, rather than ending.
    * **Unlimited Undos:** The standard 3-undo limit is removed (`Integer.MAX_VALUE`).
    * **Relaxing:** No "Danger" music, no screen shaking, and gravity stays constant.

### 2. Core Gameplay Mechanics
* **Hard Drop:** A dedicated input (Spacebar) that instantly drops the piece to the lowest valid position and locks it, speeding up gameplay for advanced players.
* **Audio Control (Mute):** A toggle feature (`M` key) that instantly silences all background music and sound effects without stopping the game loop.
* **Pause System:** A robust pause feature (`P` key) that freezes the timeline, animations, and input handling.
* **Hold & Swap:** Implemented the ability to hold a piece (`C` key) and swap it back later, adding strategic depth.
* **High Score System:** Persistent scoring engine that saves your personal best to a local file (`highest_score.txt`) and loads it on startup.
* **Next 4 Bricks:** The UI displays a preview of the next 4 upcoming pieces, helping players plan ahead.

### 3. Visual & Audio Polish
* **Ghost Piece:** Added a semi-transparent projection showing exactly where the piece will land.
* **Danger Effects:** Dynamic feedback system where the screen shakes (via `EffectManager`) and music speeds up when the stack reaches a critical height.
* **Smart Audio:** Implemented audio debouncing in `SoundManager` to prevent sound distortion when multiple bricks land in milliseconds.

### 4. Major Refactoring (Architecture & Maintainability)
The core goal of this project was to transform a monolithic codebase into a professional, decoupled architecture.

* **MVC Pattern (Model-View-Controller):**
    * **Change:** Split the code into distinct packages (`model`, `ui`, `controllers`, `managers`).
    * **Benefit:** Separation of concerns. The `SimpleBoard` (Model) no longer contains drawing logic; `GameRenderer` (View) handles all JavaFX operations.

* **Strategy Pattern (Open/Closed Principle):**
    * **Change:** Replaced hardcoded `if (isZenMode)` checks with a `GameMode` interface.
    * **Benefit:** New game modes (e.g., Hardcore) can be added by creating a new class without modifying the core `GameController`.

* **Observer Pattern (Decoupling):**
    * **Change:** The Game Logic now communicates via events (`GameObserver`).
    * **Benefit:** The UI (`GuiController`) and Audio (`SoundManager`) react to game events independently. The game logic works even if the UI is removed.

* **Memento Pattern (State Management):**
    * **Change:** Implemented `GameHistory` and `BoardMemento` to snapshot the grid state.
    * **Benefit:** Enables a robust "Undo" feature that can restore the exact board, score, and level state.

* **Single Responsibility Principle (SRP):**
    * **Change:** Extracted file I/O from `ScoreManager` into `ScoreFileHandler`.
    * **Change:** Centralized all magic numbers (speeds, colors, paths) into `GameConfiguration`.

### 5. Logic Improvements
* **7-Bag Randomizer:** Replaced true randomness with a standard "Bag" generator to ensure fair piece distribution and prevent piece droughts.
* **Wall Kicks:** Implemented standard rotation system offsets in `SimpleBoard`, allowing pieces to rotate even when touching walls.

---
## Implemented but not working properly
All implemented features are working correctly.

---

## Features Not Implemented

1.  **Timed Modes (40 Lines / Blitz):**
    * *Reason:* These modes require a complex Stopwatch/Timer system. I prioritized perfecting the Core Architecture (Strategy/Observer patterns) and the Zen Mode logic over adding time-based mechanics.
2.  **Multiplayer Mode:**
    * *Reason:* Real-time multiplayer requires networking logic (Sockets/Server) which was outside the scope of this refactoring assignment.
3.  **Theme Manager (UI):**
    * *Reason:* While the backend `ColorManager` supports different colors, building a full UI menu for swapping themes was de-prioritized to focus on the gameplay physics (Wall Kicks) and Undo system.
4.  **Persistent Score in Zen Mode:**
    * *Reason:* Currently, the score resets on auto-restart in Zen Mode. Keeping the score accumulating would require significantly changing the `ScoreManager` reset logic, which was deemed too high-risk for the submission stability.

---

## New Java Classes

I introduced these classes to break down the original monolithic structure into specific responsibilities.

### **1. Logic & Strategy (`com.comp2042.logic.mode`)**
* **`GameMode` (Interface):** Defines the contract for game rules (scoring, leveling, game over behavior).
* **`NormalMode`:** Implements standard competitive rules.
* **`ZenMode`:** Implements relaxed rules (Unlimited undo, Auto-restart).

### **2. Managers (`com.comp2042.managers`)**
* **`SoundManager`:** Handles loading audio resources, playing music/SFX, and muting. Implements `GameObserver`.
* **`EffectManager`:** Handles procedural animations (Screen Shake) for visual feedback.
* **`GameLoopManager`:** Manages the JavaFX Timeline, game ticks, and pause state.
* **`KeyManager`:** Centralizes keyboard input handling and state checking.
* **`ScoreManager`:** Handles scoring math and level progression (Logic only).
* **`ColorManager`:** Decouples logical brick IDs (1, 2, 3) from visual JavaFX Paints.

### **3. Logic (`com.comp2042.logic`)**
* **`GameHistory`:** The Caretaker for the Memento Pattern. Manages the stack of board states for Undo functionality.

### **4. UI & Controllers (`com.comp2042.ui` / `controllers`)**
* **`GameRenderer`:** Responsible for drawing the board, ghost piece, and next queue onto the JavaFX GridPane.
* **`MainMenuController`:** Controls the entry screen and mode selection.

### **5. Utilities (`com.comp2042.util`)**
* **`GameConfiguration`:** Centralized registry for all magic numbers (speeds, dimensions, paths).
* **`ScoreFileHandler`:** Handles reading/writing high scores to disk (SRP extraction).

### **6. Model (`com.comp2042.model`)**
* **`GameObserver` (Interface):** The core communication contract.
* **`BoardMemento`:** Immutable state snapshot for the Undo system.

---

## Project Structure

The project is organized into a modular **MVC** architecture with dedicated packages for logic, strategies, and managers.

```text
src
└── main
    ├── java
    │   └── com.comp2042
    │       ├── controllers      # MVC Controllers (Mediators)
    │       │   ├── GameController.java
    │       │   ├── GuiController.java
    │       │   └── MainMenuController.java
    │       ├── logic            # Core Game Physics & Rules
    │       │   ├── board        # Board Implementation
    │       │   ├── bricks       # Brick Factory & Shapes
    │       │   ├── mode         # Strategy Pattern (Game Modes)
    │       │   ├── BrickRotator.java
    │       │   ├── GameHistory.java
    │       │   └── InputEventListener.java
    │       ├── managers         # Helper Systems (SRP)
    │       │   ├── ColorManager.java
    │       │   ├── EffectManager.java
    │       │   ├── GameLoopManager.java
    │       │   ├── KeyManager.java
    │       │   ├── ScoreManager.java
    │       │   └── SoundManager.java
    │       ├── model            # Data Transfer Objects (DTOs)
    │       │   ├── BoardMemento.java
    │       │   ├── ClearRow.java    
    │       │   ├── GameObserver.java
    │       │   ├── MoveEvent.java
    │       │   ├── NextShapeInfo.java
    │       │   ├── Score.java                        
    │       │   └── ViewData.java
    │       ├── ui               # View Components
    │       │   ├── GameRenderer.java
    │       │   └── NotificationPanel.java
    │       ├── util             # Utilities & Configuration
    │       │   ├── GameConfiguration.java
    │       │   ├── ScoreFileHandler.java
    │       │   └── MatrixOperations.java
    │       └── Main.java        # Entry Point
    └── resources
        ├── audio                # Sound effects & Music
        ├── css      
        ├── font                 # Custom digital font
        ├── fxml                 # UI layouts       
        └── image                
        
```

## Modified Java Classes

### 1. `GuiController.java`
* **Role Transformation:**
    * Originally, this was a monolithic "God Class" that handled game logic, input, and rendering. It has been refactored to act strictly as the **View-Controller**.
* **Modifications:**
    * **Extracted Responsibilities:** Moved Timeline to `GameLoopManager`, Input to `KeyManager`, and Drawing to `GameRenderer`.
    * **Implemented `GameObserver`:** The UI now reacts to state changes passively rather than calculating them.
    * **Added `initGameMode`:** Bridges the user's selection from the Main Menu to the internal game engine.

### 2. `GameController.java`
* **Role Transformation:**
    * Refactored to serve as the **Mediator** between Model and View.
* **Modifications:**
    * **Strategy Pattern:** Replaced `isZenMode` boolean with `GameMode` interface.
    * **Observer Pattern:** Added notification methods to broadcast events to the UI and Audio systems.
    * **Public API:** Exposed `notifyGameOver()` and `createNewGame()` for Strategies to call.

### 3. `SimpleBoard.java`
* **Role Transformation:**
    * Refined to act as a pure **Physics Model**.
* **Modifications:**
    * **Clean Code:** Replaced hardcoded dimensions and offsets with `GameConfiguration` constants.
    * **Logic:** Added `resetCurrentBrick()` for Zen Mode and `isDangerState()` for visual effects.

### 4. `ScoreManager.java`
* **Role Transformation:**
    * Refactored to focus solely on mathematical calculations.
* **Modifications:**
    * **SRP:** Removed all file I/O code (moved to `ScoreFileHandler`).
    * **Configuration:** Added `setSavingEnabled()` and `setLevelingEnabled()` flags to support Zen Mode rules.

### 5. `RandomBrickGenerator.java`
* **Role Transformation:**
    * Enhanced fairness logic.
* **Modifications:**
    * **7-Bag System:** Replaced `ThreadLocalRandom` with a Collections-based shuffle system. This ensures players receive every piece at least once every 7 turns, preventing unfair "droughts."
    * **Preview Support:** Added `getUpcomingBricks()` to generate the list needed for the Next 4 Pieces display.

### 6. `NotificationPanel.java`
* **Modifications:**
    * **Modernization:** Replaced anonymous inner classes with Lambda expressions (`event -> list.remove(...)`).
    * **Clean Code:** Replaced magic numbers with named constants (`FADE_DURATION`).

### 7. `ViewData.java` & `MoveEvent.java`
* **Modifications:**
    * **Immutability:** Refactored to ensure deep copies are used when passing arrays, protecting the internal game state from accidental modification by the UI.

---

## Unexpected Problems & Solutions

**1. Full Screen / Stage Resizing Bug**
* *Issue:* When starting the game while in Full Screen mode, the window would abruptly resize to the default dimensions or exit full screen.
* *Solution:* Instead of initializing a new `Scene` object, I modified `MainMenuController` to retrieve the existing scene (`event.getSource().getScene()`) and simply swap its root node (`setRoot()`). This preserves the Stage's state and dimensions.

**2. Strategy Pattern Visibility**
* *Issue:* The new `NormalMode` class (in a separate package) could not trigger the "Game Over" sequence because the controller methods were private.
* *Solution:* Refactored `GameController` to expose safe, public command methods (`notifyGameOver`, `createNewGame`) that strategies can invoke.

**3. Audio Distortion (Debouncing)**
* *Issue:* Rapidly hard-dropping blocks caused the landing sound to overlap and distort.
* *Solution:* Implemented a timestamp check in `SoundManager` to enforce a minimum cooldown (`DROP_SOUND_COOLDOWN_MS`) between sound effects.

**4. JavaFX Focus Loss**
* *Issue:* When switching from Menu to Game, the keyboard was unresponsive until the window was clicked.
* *Solution:* Added `Platform.runLater(root::requestFocus)` in the scene loader to force focus onto the game window immediately after the scene swap.

---

## Summary

### Key Features Implemented
* **Zen Mode:** A relaxed game mode implemented using the **Strategy Pattern**, featuring unlimited undo, auto-restart mechanics, and a stress-free environment.
* **High Score System:** A persistent scoring engine that tracks and saves the player's personal best to a local file, creating long-term engagement.
* **Hold & Swap Mechanics:** Implemented the ability to hold a piece for later use, adding a layer of strategic depth to the gameplay.
* **Advanced Undo System:** A robust "Time Machine" feature built on the **Memento Pattern**, allowing players to revert mistakes.
* **Reactive UI & Audio:** Utilized the **Observer Pattern** for seamless UI updates, sound triggers, and game state communication without tight coupling.
* **Enhanced Gameplay:** Modernized the feel of the game with a **7-Bag Randomizer** for fairness, **Ghost Piece** rendering for accuracy, **Wall Kicks** for smoother rotation, and dynamic **Danger Visuals**.
* **Smart Audio System:** Implemented audio logic with debouncing to prevent sound distortion during rapid actions.

### Major Refactoring Achievements
* **Architectural Overhaul:** Transitioned from a monolithic codebase to a strict **MVC (Model-View-Controller)** architecture, ensuring clear separation of concerns.
* **Component Extraction:** Successfully split the original "God Class" controller into organized, independent components for Input, Timing, and Logic.
* **Single Responsibility Principle:** Extracted rendering, input handling, scoring, and file I/O into dedicated Manager and Utility classes (`ScoreFileHandler`, `GameRenderer`, `KeyManager`).
* **Clean Code:** Centralized all magic numbers and paths into `GameConfiguration`, significantly improving readability and ease of tuning.
* **Decoupling:** Fully decoupled the UI from the Game Logic, making the system highly testable, modular, and extensible for future features.

### Background
* **Prior Experience:** Zero prior experience with Java or JavaFX before this module.
* **Learning Curve:** Learned OOP principles, Design Patterns, and JavaFX event handling specifically for this coursework.
* **Effort:** This refactoring represents significant research into software architecture to ensure the code is not just "working," but "maintainable."