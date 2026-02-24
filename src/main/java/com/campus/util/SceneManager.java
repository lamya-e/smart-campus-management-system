package com.campus.util;

import com.campus.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Gestion centralisée des scènes / vues JavaFX.
 * Toutes les navigations entre vues passent par ce gestionnaire.
 */
public final class SceneManager {

    private static Stage primaryStage;

    private SceneManager() {
    }

    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Smart Campus Management System");
    }

    public static void showDashboard() {
        setScene("/com/campus/view/dashboard.fxml");
    }

    public static void showEtudiants() {
        setScene("/com/campus/view/etudiants.fxml");
    }

    public static void showProfesseurs() {
        setScene("/com/campus/view/professeurs.fxml");
    }

    public static void showModules() {
        setScene("/com/campus/view/modules.fxml");
    }

    public static void showNotes() {
        setScene("/com/campus/view/notes.fxml");
    }

    private static void setScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(App.class.getResource(fxmlPath)));
            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root, 1200, 750);
            } else {
                scene.setRoot(root);
            }
            if (scene.getStylesheets().isEmpty()) {
                scene.getStylesheets().add(
                    Objects.requireNonNull(App.class.getResource("/com/campus/css/style.css")).toExternalForm()
                );
            }
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger la vue: " + fxmlPath, e);
        }
    }
}

