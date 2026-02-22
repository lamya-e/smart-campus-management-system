package com.smartcampus.model;

/**
 * Model for a grade: links a student, course, and numeric value.
 */
public class Grade {

    private int id;
    private int studentId;
    private int courseId;
    private double value;
    private Student student;   // optional, for display
    private Course course;     // optional, for display

    public Grade() {
    }

    public Grade(int id, int studentId, int courseId, double value) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
