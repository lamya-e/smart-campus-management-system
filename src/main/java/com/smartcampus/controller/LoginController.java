package com.smartcampus.controller;

import com.smartcampus.MainApp;
import com.smartcampus.service.AuthService;
import com.smartcampus.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller for the login view. Validates admin credentials and opens main app.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username == null || username.isBlank()) {
            showError("Please enter username.");
            return;
        }
        if (password == null || password.isEmpty()) {
            showError("Please enter password.");
            return;
        }
        if (!AuthService.login(username, password)) {
            showError("Invalid username or password.");
            return;
        }
        try {
            MainApp.showMainView();
        } catch (IOException e) {
            AlertHelper.showError("Error", "Could not open application.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
