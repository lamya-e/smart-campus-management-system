package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for a teacher. Extends Person with department and assigned courses.
 */
public class Teacher extends Person {

    private String department;
    private List<Course> assignedCourses;

    public Teacher() {
        super();
        this.assignedCourses = new ArrayList<>();
    }

    public Teacher(int id, String name, String email, String department) {
        super(id, name, email);
        this.department = department;
        this.assignedCourses = new ArrayList<>();
    }

    @Override
    public String displayInfo() {
        return getName() + " (" + getEmail() + ") - " + department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Course> getAssignedCourses() {
        return assignedCourses;
    }

    public void setAssignedCourses(List<Course> assignedCourses) {
        this.assignedCourses = assignedCourses != null ? assignedCourses : new ArrayList<>();
    }
}
