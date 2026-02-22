package com.smartcampus.controller;

import com.smartcampus.service.CourseService;
import com.smartcampus.service.GradeService;
import com.smartcampus.service.StudentService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Dashboard controller. Displays statistics and quick action buttons.
 */
public class DashboardController {

    @FXML
    private Label statStudentsLabel;

    @FXML
    private Label statCoursesLabel;

    @FXML
    private Label statGradesLabel;

    @FXML
    private Label statAverageLabel;

    @FXML
    public void initialize() {
        refreshStats();
    }

    private void refreshStats() {
        statStudentsLabel.setText(String.valueOf(StudentService.count()));
        statCoursesLabel.setText(String.valueOf(CourseService.count()));
        statGradesLabel.setText(String.valueOf(GradeService.count()));
        double avg = GradeService.overallAverage();
        statAverageLabel.setText(avg > 0 ? String.format("%.1f", avg) : "—");
    }

    @FXML
    private void goToStudents() {
        try {
            var main = (javafx.scene.layout.BorderPane) statStudentsLabel.getScene().getRoot();
            var stack = (javafx.scene.layout.StackPane) main.getCenter();
            stack.getChildren().clear();
            stack.getChildren().add(javafx.fxml.FXMLLoader.load(
                getClass().getResource("/view/StudentView.fxml")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToCourses() {
        try {
            var main = (javafx.scene.layout.BorderPane) statStudentsLabel.getScene().getRoot();
            var stack = (javafx.scene.layout.StackPane) main.getCenter();
            stack.getChildren().clear();
            stack.getChildren().add(javafx.fxml.FXMLLoader.load(
                getClass().getResource("/view/CourseView.fxml")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
