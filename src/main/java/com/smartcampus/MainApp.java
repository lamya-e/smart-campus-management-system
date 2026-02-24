package com.smartcampus;

import com.smartcampus.util.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Main entry point for the Smart Campus Management System.
 * Initializes database and launches the JavaFX application.
 */
public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        DBConnection.initialize();
        showLoginView();
    }

    /**
     * Shows the login view (or main view if auth is disabled).
     */
    public static void showLoginView() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(
                MainApp.class.getResource("/view/LoginView.fxml")));
        Scene scene = new Scene(root, 420, 380);
        scene.getStylesheets().add(Objects.requireNonNull(
                MainApp.class.getResource("/com/campus/css/app.css")).toExternalForm());
        primaryStage.setTitle("Smart Campus - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Switches to the main dashboard after successful login.
     */
    public static void showMainView() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(
                MainApp.class.getResource("/view/MainView.fxml")));
        Scene scene = new Scene(root, 1200, 750);
        scene.getStylesheets().add(Objects.requireNonNull(
                MainApp.class.getResource("/com/campus/css/app.css")).toExternalForm());
        primaryStage.setTitle("Smart Campus Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void stop() {
        DBConnection.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
