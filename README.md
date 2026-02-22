# Smart Campus Management System

A professional JavaFX desktop application for university management, built with **MVC architecture**, **SQLite** persistence, and a modern UI with optional dark/light theme.

## Features

- **Dashboard** – Statistics (students, courses, grades, overall average) and quick navigation
- **Student Management** – Full CRUD, search, view grades
- **Course Management** – Create/edit courses, assign teacher, enroll students, manage grades
- **Grades** – Add and edit grades per student per course; averages calculated automatically
- **Authentication** – Admin login (default: `admin` / `admin`)
- **Theme** – Toggle between light and dark mode from the main window

## Requirements

- **Java 17+**
- **Maven 3.6+**

## Run the Application

```bash
mvn clean javafx:run
```

Or compile and run the main class with module path including JavaFX:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.smartcampus.MainApp"
```

For a runnable JAR with JavaFX, use the [javafx-maven-plugin](https://github.com/openjfx/javafx-maven-plugin) or build with a tool that packages JavaFX (e.g. jlink).

## Project Structure (MVC)

```
src/main/java/com/smartcampus/
├── MainApp.java                 # Application entry, stage management
├── model/                       # Business data
│   ├── Person.java (abstract)
│   ├── Student.java
│   ├── Teacher.java
│   ├── Course.java
│   └── Grade.java
├── view/                        # FXML (loaded by controllers)
│   ├── LoginView.fxml
│   ├── MainView.fxml
│   ├── DashboardView.fxml
│   ├── StudentView.fxml
│   └── CourseView.fxml
├── controller/                  # UI logic
│   ├── LoginController.java
│   ├── MainController.java
│   ├── DashboardController.java
│   ├── StudentController.java
│   └── CourseController.java
├── service/                     # Business logic & persistence
│   ├── AuthService.java
│   ├── TeacherService.java
│   ├── StudentService.java
│   ├── CourseService.java
│   └── GradeService.java
└── util/
    ├── DBConnection.java        # SQLite singleton, schema
    └── AlertHelper.java         # Alerts and dialogs
```

## Data

- **Database**: SQLite file `smart_campus.db` created in the current working directory on first run.
- **Default login**: username `admin`, password `admin` (change in production).
- One seed teacher is created for demo: "Dr. Jane Smith", Computer Science.

## Future: Spring Boot Backend

The package layout and service layer are designed so that:

- **Models** can stay as DTOs/entities.
- **Services** can be replaced or delegated to `@Service` beans and REST clients.
- **Controllers** can be refactored to call REST APIs instead of static service methods.
- **Views** (FXML) and UI logic can remain; only the data source would switch to HTTP.

## License

This project is for educational use.
