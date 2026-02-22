package com.smartcampus.controller;

import com.smartcampus.model.Student;
import com.smartcampus.service.GradeService;
import com.smartcampus.service.StudentService;
import com.smartcampus.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class StudentController implements Initializable {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @FXML private TextField searchField;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colEmail;
    @FXML private TableColumn<Student, String> colMajor;
    @FXML private TableColumn<Student, Void> colActions;
    @FXML private Button editStudentBtn;
    @FXML private Button deleteStudentBtn;
    @FXML private Button viewGradesBtn;

    private final ObservableList<Student> students = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colMajor.setCellValueFactory(new PropertyValueFactory<>("major"));
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Delete");
            {
                btn.getStyleClass().add("primary-button");
                btn.setOnAction(e -> {
                    Student s = getTableRow().getItem();
                    if (s != null && AlertHelper.showConfirmation("Confirm", "Delete student " + s.getName() + "?")) {
                        StudentService.delete(s.getId());
                        loadStudents();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        studentTable.setItems(students);
        studentTable.getSelectionModel().selectedItemProperty().addListener((o, old, selected) -> {
            boolean hasSelection = selected != null;
            editStudentBtn.setDisable(!hasSelection);
            deleteStudentBtn.setDisable(!hasSelection);
            viewGradesBtn.setDisable(!hasSelection);
        });
        loadStudents();
    }

    private void loadStudents() {
        students.clear();
        students.addAll(StudentService.findAll());
    }

    @FXML
    private void searchStudents() {
        String q = searchField.getText();
        if (q == null || q.isBlank()) {
            loadStudents();
            return;
        }
        q = q.trim().toLowerCase();
        students.clear();
        for (Student s : StudentService.findAll()) {
            if (s.getName().toLowerCase().contains(q) || s.getEmail().toLowerCase().contains(q)) {
                students.add(s);
            }
        }
    }

    @FXML
    private void showAddStudent() {
        showStudentForm(null);
    }

    @FXML
    private void editSelected() {
        Student s = studentTable.getSelectionModel().getSelectedItem();
        if (s != null) showStudentForm(s);
    }

    @FXML
    private void deleteSelected() {
        Student s = studentTable.getSelectionModel().getSelectedItem();
        if (s == null) return;
        if (!AlertHelper.showConfirmation("Confirm Delete", "Delete student " + s.getName() + "? This will remove enrollments and grades.")) {
            return;
        }
        StudentService.delete(s.getId());
        loadStudents();
    }

    @FXML
    private void viewGrades() {
        Student s = studentTable.getSelectionModel().getSelectedItem();
        if (s == null) return;
        var grades = GradeService.findByStudent(s.getId());
        StringBuilder sb = new StringBuilder();
        if (grades.isEmpty()) {
            sb.append("No grades recorded.");
        } else {
            for (var g : grades) {
                sb.append(g.getCourse() != null ? g.getCourse().getTitle() : "Course " + g.getCourseId())
                  .append(": ").append(String.format("%.1f", g.getValue())).append("\n");
            }
            sb.append("\nAverage: ").append(String.format("%.1f", GradeService.averageForStudent(s.getId())));
        }
        AlertHelper.showInfo("Grades for " + s.getName(), sb.toString());
    }

    private void showStudentForm(Student existing) {
        TextField nameField = new TextField();
        nameField.setPromptText("Full name");
        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");
        TextField majorField = new TextField();
        majorField.setPromptText("Major");
        if (existing != null) {
            nameField.setText(existing.getName());
            emailField.setText(existing.getEmail());
            majorField.setText(existing.getMajor());
        }
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Name *"), nameField);
        grid.addRow(1, new Label("Email *"), emailField);
        grid.addRow(2, new Label("Major"), majorField);
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Student" : "Edit Student");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? existing : null);
        dialog.showAndWait().ifPresent(s -> {
            String name = nameField.getText();
            String email = emailField.getText() != null ? emailField.getText().trim() : "";
            String major = majorField.getText();
            if (name == null || name.isBlank()) {
                AlertHelper.showWarning("Validation", "Name is required.");
                return;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                AlertHelper.showWarning("Validation", "Valid email is required.");
                return;
            }
            try {
                if (existing != null) {
                    existing.setName(name);
                    existing.setEmail(email);
                    existing.setMajor(major != null ? major : "");
                    StudentService.save(existing);
                    AlertHelper.showInfo("Success", "Student updated.");
                } else {
                    Student newStudent = new Student(0, name, email, major != null ? major : "");
                    StudentService.save(newStudent);
                    AlertHelper.showInfo("Success", "Student added.");
                }
                loadStudents();
            } catch (Exception e) {
                AlertHelper.showError("Error", e.getMessage());
            }
        });
    }
}
