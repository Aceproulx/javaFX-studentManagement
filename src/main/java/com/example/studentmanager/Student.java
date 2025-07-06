package com.example.studentmanager;

import javafx.beans.property.*;

public class Student {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty department;
    private final DoubleProperty gpa;

    public Student(int id, String name, String department, double gpa) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.department = new SimpleStringProperty(department);
        this.gpa = new SimpleDoubleProperty(gpa);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDepartment() {
        return department.get();
    }

    public StringProperty departmentProperty() {
        return department;
    }

    public double getGpa() {
        return gpa.get();
    }

    public DoubleProperty gpaProperty() {
        return gpa;
    }
}
