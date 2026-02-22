package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for a course. Has an assigned teacher and enrolled students.
 */
public class Course {

    private int id;
    private String title;
    private Teacher assignedTeacher;
    private Integer teacherId; // for DB persistence
    private List<Student> students;

    public Course() {
        this.students = new ArrayList<>();
    }

    public Course(int id, String title, Teacher assignedTeacher) {
        this.id = id;
        this.title = title;
        this.assignedTeacher = assignedTeacher;
        this.teacherId = assignedTeacher != null ? assignedTeacher.getId() : null;
        this.students = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Teacher getAssignedTeacher() {
        return assignedTeacher;
    }

    public void setAssignedTeacher(Teacher assignedTeacher) {
        this.assignedTeacher = assignedTeacher;
        this.teacherId = assignedTeacher != null ? assignedTeacher.getId() : null;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students != null ? students : new ArrayList<>();
    }
}
