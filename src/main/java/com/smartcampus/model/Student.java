package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for a student. Extends Person with major and enrollment/grades.
 * Enrolled courses and grades are typically loaded via services.
 */
public class Student extends Person {

    private String major;
    private List<Course> enrolledCourses;
    private List<Grade> grades;

    public Student() {
        super();
        this.enrolledCourses = new ArrayList<>();
        this.grades = new ArrayList<>();
    }

    public Student(int id, String name, String email, String major) {
        super(id, name, email);
        this.major = major;
        this.enrolledCourses = new ArrayList<>();
        this.grades = new ArrayList<>();
    }

    @Override
    public String displayInfo() {
        return getName() + " (" + getEmail() + ") - " + major;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(List<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses != null ? enrolledCourses : new ArrayList<>();
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades != null ? grades : new ArrayList<>();
    }
}
