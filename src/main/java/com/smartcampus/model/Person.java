package com.smartcampus.model;

/**
 * Abstract base class for all persons in the campus system (Student, Teacher).
 * Holds common attributes and enforces displayInfo() implementation.
 */
public abstract class Person {

    private int id;
    private String name;
    private String email;

    public Person() {
    }

    public Person(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Returns a short display string for lists and combo boxes.
     */
    public abstract String displayInfo();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
