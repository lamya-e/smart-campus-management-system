package com.smartcampus.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Utility for showing consistent alerts across the application.
 */
public final class AlertHelper {

    private AlertHelper() {
    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        style(alert);
        alert.showAndWait();
    }

    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        style(alert);
        alert.showAndWait();
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        style(alert);
        alert.showAndWait();
    }

    /**
     * Shows confirmation dialog. Returns true if user clicked OK.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        style(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void style(Alert alert) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        try {
            stage.getScene().getStylesheets().add(
                AlertHelper.class.getResource("/com/campus/css/app.css").toExternalForm());
        } catch (Exception ignored) {
        }
    }
}
