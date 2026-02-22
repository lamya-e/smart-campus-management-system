package com.smartcampus.controller;

import com.smartcampus.model.Course;
import com.smartcampus.model.Student;
import com.smartcampus.model.Teacher;
import com.smartcampus.service.CourseService;
import com.smartcampus.service.GradeService;
import com.smartcampus.service.StudentService;
import com.smartcampus.service.TeacherService;
import com.smartcampus.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for course management. Handles CRUD, enrollment, and grades.
 */
public class CourseController implements Initializable {

    @FXML
    private TableView<Course> courseTable;

    @FXML
    private TableColumn<Course, Integer> colCourseId;

    @FXML
    private TableColumn<Course, String> colTitle;

    @FXML
    private TableColumn<Course, String> colTeacher;

    @FXML
    private TableColumn<Course, Integer> colEnrolled;

    @FXML
    private TableColumn<Course, Void> colCourseActions;

    @FXML
    private Button editCourseBtn;

    @FXML
    private Button deleteCourseBtn;

    @FXML
    private Button enrollBtn;

    @FXML
    private Button gradesBtn;

    private final ObservableList<Course> courses = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCourseId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTeacher.setCellValueFactory(cell -> {
            Teacher t = cell.getValue().getAssignedTeacher();
            return new javafx.beans.property.SimpleStringProperty(t != null ? t.getName() : "—");
        });
        colEnrolled.setCellValueFactory(cell -> {
            Course c = cell.getValue();
            int n = c.getStudents() != null ? c.getStudents().size() : 0;
            if (n == 0 && c.getId() > 0) {
                n = CourseService.getEnrolledStudents(c.getId()).size();
            }
            return new javafx.beans.property.SimpleIntegerProperty(n).asObject();
        });
        colCourseActions.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Delete");
            {
                btn.getStyleClass().add("primary-button");
                btn.setOnAction(e -> {
                    Course c = getTableRow().getItem();
                    if (c != null && AlertHelper.showConfirmation("Confirm", "Delete course " + c.getTitle() + "?")) {
                        CourseService.delete(c.getId());
                        loadCourses();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        courseTable.setItems(courses);
        courseTable.getSelectionModel().selectedItemProperty().addListener((o, old, selected) -> {
            boolean hasSelection = selected != null;
            editCourseBtn.setDisable(!hasSelection);
            deleteCourseBtn.setDisable(!hasSelection);
            enrollBtn.setDisable(!hasSelection);
            gradesBtn.setDisable(!hasSelection);
        });
        loadCourses();
    }

    private void loadCourses() {
        courses.clear();
        courses.addAll(CourseService.findAll());
    }

    @FXML
    private void showAddCourse() {
        showCourseForm(null);
    }

    @FXML
    private void editSelected() {
        Course c = courseTable.getSelectionModel().getSelectedItem();
        if (c != null) showCourseForm(c);
    }

    @FXML
    private void deleteSelected() {
        Course c = courseTable.getSelectionModel().getSelectedItem();
        if (c == null) return;
        if (!AlertHelper.showConfirmation("Confirm Delete", "Delete course " + c.getTitle() + "? Enrollments and grades will be removed.")) {
            return;
        }
        CourseService.delete(c.getId());
        loadCourses();
    }

    @FXML
    private void showEnrollDialog() {
        Course c = courseTable.getSelectionModel().getSelectedItem();
        if (c == null) return;
        List<Student> all = StudentService.findAll();
        List<Student> enrolled = CourseService.getEnrolledStudents(c.getId());
        ListView<Student> list = new ListView<>();
        ObservableList<Student> items = FXCollections.observableArrayList(all);
        list.setItems(items);
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Enroll students in: " + c.getTitle());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.getDialogPane().setContent(list);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.showAndWait().ifPresent(btn -> {
            for (Student s : list.getSelectionModel().getSelectedItems()) {
                CourseService.enrollStudent(c.getId(), s.getId());
            }
            loadCourses();
            if (!list.getSelectionModel().getSelectedItems().isEmpty()) {
                AlertHelper.showInfo("Enrollment", "Students enrolled.");
            }
        });
    }

    @FXML
    private void showGradesDialog() {
        Course c = courseTable.getSelectionModel().getSelectedItem();
        if (c == null) return;
        var grades = GradeService.findByCourse(c.getId());
        StringBuilder sb = new StringBuilder();
        if (grades.isEmpty()) {
            sb.append("No grades for this course yet. Enroll students and add grades.");
        } else {
            for (var g : grades) {
                sb.append(g.getStudent() != null ? g.getStudent().getName() : "Student " + g.getStudentId())
                  .append(": ").append(String.format("%.1f", g.getValue())).append("\n");
            }
        }
        AlertHelper.showInfo("Grades: " + c.getTitle(), sb.toString());
        // Optional: open a dialog to add/edit grade per student
        addOrEditGradesForCourse(c);
    }

    private void addOrEditGradesForCourse(Course c) {
        List<Student> enrolled = CourseService.getEnrolledStudents(c.getId());
        if (enrolled.isEmpty()) {
            return;
        }
        ChoiceBox<Student> studentChoice = new ChoiceBox<>(FXCollections.observableArrayList(enrolled));
        Spinner<Double> valueSpinner = new Spinner<>(0, 100, 0, 0.5);
        valueSpinner.setEditable(true);
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Student"), studentChoice);
        grid.addRow(1, new Label("Grade (0-100)"), valueSpinner);
        var existingGrade = new Object() { com.smartcampus.model.Grade g; };
        studentChoice.getSelectionModel().selectedItemProperty().addListener((o, old, s) -> {
            if (s != null) {
                existingGrade.g = GradeService.findForStudentAndCourse(s.getId(), c.getId());
                valueSpinner.getValueFactory().setValue(existingGrade.g != null ? existingGrade.g.getValue() : 0);
            }
        });
        if (!enrolled.isEmpty()) {
            studentChoice.getSelectionModel().selectFirst();
        }
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add/Edit grade: " + c.getTitle());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.showAndWait().ifPresent(btn -> {
            Student s = studentChoice.getSelectionModel().getSelectedItem();
            if (s != null) {
                com.smartcampus.model.Grade g = existingGrade.g != null ? existingGrade.g : new com.smartcampus.model.Grade();
                g.setStudentId(s.getId());
                g.setCourseId(c.getId());
                g.setValue(valueSpinner.getValue());
                GradeService.save(g);
                AlertHelper.showInfo("Saved", "Grade saved.");
            }
        });
    }

    private void showCourseForm(Course existing) {
        TextField titleField = new TextField();
        titleField.setPromptText("Course title");
        ComboBox<Teacher> teacherCombo = new ComboBox<>();
        teacherCombo.getItems().addAll(TeacherService.findAll());
        teacherCombo.setPromptText("Select teacher (optional)");
        if (existing != null) {
            titleField.setText(existing.getTitle());
            if (existing.getAssignedTeacher() != null) {
                teacherCombo.getSelectionModel().select(existing.getAssignedTeacher());
            }
        }
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Title *"), titleField);
        grid.addRow(1, new Label("Teacher"), teacherCombo);
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Course" : "Edit Course");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? existing : null);
        dialog.showAndWait().ifPresent(c -> {
            String title = titleField.getText();
            if (title == null || title.isBlank()) {
                AlertHelper.showWarning("Validation", "Title is required.");
                return;
            }
            try {
                Teacher t = teacherCombo.getSelectionModel().getSelectedItem();
                if (existing != null) {
                    existing.setTitle(title);
                    existing.setAssignedTeacher(t);
                    existing.setTeacherId(t != null ? t.getId() : null);
                    CourseService.save(existing);
                    AlertHelper.showInfo("Success", "Course updated.");
                } else {
                    Course newCourse = new Course();
                    newCourse.setTitle(title);
                    newCourse.setAssignedTeacher(t);
                    newCourse.setTeacherId(t != null ? t.getId() : null);
                    CourseService.save(newCourse);
                    AlertHelper.showInfo("Success", "Course added.");
                }
                loadCourses();
            } catch (Exception e) {
                AlertHelper.showError("Error", e.getMessage());
            }
        });
    }
}
