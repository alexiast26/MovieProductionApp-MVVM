# 🎬 Movie Production Management System (MVVM)

## 📌 Project Overview
A professional-grade desktop application designed for movie production houses to manage cinematic assets, artistic staff, and production workflows. This project demonstrates a robust implementation of the **MVVM (Model-View-ViewModel)** architectural pattern in Java.

## 🛠 Tech Stack
* **Language:** Java 17+
* **UI Framework:** JavaFX with FXML
* **Core Framework:** Spring Boot (Dependency Injection & Context Management)
* **Persistence:** Relational Database (MySQL/SQL Server) via Spring Data JPA
* **Tools:** Maven, Lombok, Scene Builder

## 🏗 Architectural Design (MVVM)
The project is built on a strict separation of concerns, ensuring high testability and a clean codebase:

* **View (JavaFX + FXML):** Completely passive. Its sole responsibility is **Data Binding** and triggering commands. It mirrors the behavior of `.xaml.cs` files in WPF.
* **ViewModel:** The "brain" of the application. It manages the state using `Properties` and exposes logic through an **ICommand** interface, exactly like the `PersonVM` structure.
* **Model & Services:** Handles business logic and database interactions, ensuring data integrity across the system.

## ✨ Key Features
* **Multimedia Integration:** Advanced image handling allowing up to 3 visual assets (posters/stills) per movie entry.
* **Relational Management:** Full CRUD operations for Movies, Actors, Directors, and Screenwriters.
* **Dynamic Cross-Referencing:** Real-time tracking of actor filmographies and production crew history.
* **Smart Filtering:** Production filtering by category and genre using optimized streams.
* **Command Pattern:** Decoupled event handling via a custom `ICommand` implementation, removing business logic from UI controllers.

## 🚀 Getting Started
1. **Prerequisites:** Ensure you have JDK 17 and a SQL database server running.
2. **Configuration:** Update `application.properties` with your database credentials.
3. **Build:** Run `mvn clean install`.
4. **Launch:** Execute the `MovieAppApplication` class.

**Note**: The images might not show up when running the app, so I recommend clearing the images and adding them again (can be done when pressing the **Modify** button). :)
