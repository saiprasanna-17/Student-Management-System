
package com.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // ADD STUDENT
    public boolean addStudent(Student student) {

        String query = "INSERT INTO student (id, name, department, marks) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                System.out.println("Database connection failed!");
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(query)) {

                ps.setInt(1, student.getId());
                ps.setString(2, student.getName());
                ps.setString(3, student.getDepartment());
                ps.setDouble(4, student.getMarks());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error adding student:");
            e.printStackTrace();
            return false;
        }
    }

    // VIEW ALL STUDENTS
    public List<Student> getAllStudents() {

        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM student";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return students;
            }

            try (
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
            ) {
                while (rs.next()) {
                    Student student = new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getDouble("marks")
                    );

                    students.add(student);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    // SEARCH STUDENT BY ID
    public Student getStudentById(int id) {

        String query = "SELECT * FROM student WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(query)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return new Student(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getDouble("marks")
                        );
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // UPDATE STUDENT
    public boolean updateStudent(Student student) {

        String query = "UPDATE student SET name=?, department=?, marks=? WHERE id=?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, student.getName());
                ps.setString(2, student.getDepartment());
                ps.setDouble(3, student.getMarks());
                ps.setInt(4, student.getId());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE STUDENT
    public boolean deleteStudent(int id) {

        String query = "DELETE FROM student WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(query)) {

                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // TOTAL STUDENTS
    public int getStudentCount() {

        String query = "SELECT COUNT(*) FROM student";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return 0;
            }

            try (
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
            ) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // TOTAL DEPARTMENTS
    public int getDepartmentCount() {

        String query = "SELECT COUNT(DISTINCT department) FROM student";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return 0;
            }

            try (
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
            ) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // AVERAGE MARKS
    public double getAverageMarks() {

        String query = "SELECT AVG(marks) FROM student";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return 0;
            }

            try (
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
            ) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
