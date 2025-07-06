package com.example.studentmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class HelloController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField departmentField;

    @FXML
    private TextField gpaField;

    @FXML
    private TableView<Student> tableView;

    @FXML
    private TableColumn<Student, Integer> idColumn;

    @FXML
    private TableColumn<Student, String> nameColumn;

    @FXML
    private TableColumn<Student, String> departmentColumn;

    @FXML
    private TableColumn<Student, Double> gpaColumn;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    private final String DB_URL = "jdbc:mysql://localhost:3306/studentdb";
    private final String DB_USER = "root";
    private final String DB_PASS = "";

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        departmentColumn.setCellValueFactory(cell -> cell.getValue().departmentProperty());
        gpaColumn.setCellValueFactory(cell -> cell.getValue().gpaProperty().asObject());

        tableView.setItems(studentList);

        tableView.setOnMouseClicked(this::handleRowClick);

        loadStudents();
    }

    private void loadStudents() {
        studentList.clear();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {

            while (rs.next()) {
                studentList.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getDouble("gpa")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText();
        String dept = departmentField.getText();
        String gpaText = gpaField.getText();

        if (name.isEmpty() || dept.isEmpty() || gpaText.isEmpty()) {
            showAlert("Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            double gpa = Double.parseDouble(gpaText);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "INSERT INTO students (name, department, gpa) VALUES (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, dept);
                ps.setDouble(3, gpa);
                ps.executeUpdate();
            }

            clearFields();
            loadStudents();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "GPA must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select a student to edit.");
            return;
        }

        String name = nameField.getText();
        String dept = departmentField.getText();
        String gpaText = gpaField.getText();

        if (name.isEmpty() || dept.isEmpty() || gpaText.isEmpty()) {
            showAlert("Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            double gpa = Double.parseDouble(gpaText);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "UPDATE students SET name = ?, department = ?, gpa = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, dept);
                ps.setDouble(3, gpa);
                ps.setInt(4, selected.getId());
                ps.executeUpdate();
            }

            clearFields();
            loadStudents();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "GPA must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select a student to delete.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "DELETE FROM students WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, selected.getId());
            ps.executeUpdate();

            clearFields();
            loadStudents();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleRowClick(MouseEvent event) {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            departmentField.setText(selected.getDepartment());
            gpaField.setText(String.valueOf(selected.getGpa()));
        }
    }

    private void clearFields() {
        nameField.clear();
        departmentField.clear();
        gpaField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ID,Name,Department,GPA\n");
                for (Student s : studentList) {
                    writer.write(String.format("%d,%s,%s,%.2f\n",
                            s.getId(),
                            s.getName(),
                            s.getDepartment(),
                            s.getGpa()));
                }
                showAlert("Export Successful", "Data exported to CSV successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Export Failed", "Could not write to file.");
            }
        }
    }
}