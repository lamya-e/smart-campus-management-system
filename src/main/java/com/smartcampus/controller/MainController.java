package com.smartcampus.controller;

import com.smartcampus.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

/**
 * Main window controller. Loads dashboard/student/course views into content area and handles theme toggle.
 */
public class MainController {

    @FXML
    private StackPane contentPane;

    @FXML
    private ToggleButton themeToggle;

    @FXML
    public void initialize() throws IOException {
        showDashboard();
    }

    @FXML
    private void showDashboard() throws IOException {
        loadView("/view/DashboardView.fxml");
    }

    @FXML
    private void showStudents() throws IOException {
        loadView("/view/StudentView.fxml");
    }

    @FXML
    private void showCourses() throws IOException {
        loadView("/view/CourseView.fxml");
    }

    @FXML
    private void toggleTheme() {
        boolean dark = themeToggle.isSelected();
        themeToggle.setText(dark ? "Light Mode" : "Dark Mode");
        var root = contentPane.getScene().getRoot();
        if (dark) {
            root.getStyleClass().add("theme-dark");
        } else {
            root.getStyleClass().remove("theme-dark");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            MainApp.showLoginView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String path) throws IOException {
        contentPane.getChildren().clear();
        var loader = new FXMLLoader(Objects.requireNonNull(MainApp.class.getResource(path)));
        contentPane.getChildren().add(loader.load());
    }
}
