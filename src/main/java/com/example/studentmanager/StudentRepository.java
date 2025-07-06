package com.example.studentmanager;

import java.util.List;

public interface StudentRepository {
    List<Student> getAll();
    void save(Student student);
    void update(Student student);
    void delete(int id);
}
