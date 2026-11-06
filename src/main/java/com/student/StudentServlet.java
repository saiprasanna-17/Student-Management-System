
package com.student;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StudentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final StudentDAO dao = new StudentDAO();

    // GET: VIEW, SEARCH, EDIT AND DASHBOARD
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String type = request.getParameter("type");
        String id = request.getParameter("id");

        // Total students
        if ("count".equals(type)) {
            response.setContentType("text/plain");
            response.getWriter().print(dao.getStudentCount());
            return;
        }

        // Total departments
        if ("departments".equals(type)) {
            response.setContentType("text/plain");
            response.getWriter().print(dao.getDepartmentCount());
            return;
        }

        // Average marks
        if ("average".equals(type)) {
            response.setContentType("text/plain");
            response.getWriter().print(
                String.format(Locale.US, "%.2f", dao.getAverageMarks())
            );
            return;
        }

        // Get one student for Edit
        if ("student".equals(type)) {
            response.setContentType("application/json");

            try {
                int studentId = Integer.parseInt(id);
                Student student = dao.getStudentById(studentId);

                if (student == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().print(
                        "{\"error\":\"Student not found\"}"
                    );
                    return;
                }

                response.getWriter().print(
                    "{\"id\":" + student.getId()
                    + ",\"name\":\"" + jsonEscape(student.getName())
                    + "\",\"department\":\""
                    + jsonEscape(student.getDepartment())
                    + "\",\"marks\":" + student.getMarks() + "}"
                );

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(
                    "{\"error\":\"Invalid student ID\"}"
                );
            }

            return;
        }

        // Search student
        if (id != null && !id.trim().isEmpty()) {
            response.setContentType("text/html");

            try {
                Student student =
                    dao.getStudentById(Integer.parseInt(id));

                if (student == null) {
                    response.getWriter().print(
                        "<h3>Student Not Found!</h3>"
                    );
                } else {
                    response.getWriter().print(
                        "<h3>Student Found</h3>"
                        + "<p>ID: " + student.getId() + "</p>"
                        + "<p>Name: " + htmlEscape(student.getName()) + "</p>"
                        + "<p>Department: "
                        + htmlEscape(student.getDepartment()) + "</p>"
                        + "<p>Marks: " + student.getMarks() + "</p>"
                    );
                }

            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("<h3>Invalid Student ID</h3>");
            }

            return;
        }

        // View all students
        response.setContentType("text/html");

        List<Student> students = dao.getAllStudents();

        for (Student student : students) {
            response.getWriter().print(
                "<tr>"
                + "<td>" + student.getId() + "</td>"
                + "<td>" + htmlEscape(student.getName()) + "</td>"
                + "<td>" + htmlEscape(student.getDepartment()) + "</td>"
                + "<td>" + student.getMarks() + "</td>"
                + "<td>"
                + "<button type='button' class='edit-btn' "
                + "onclick='editStudent(" + student.getId() + ")'>Edit</button> "
                + "<button type='button' class='delete-btn' "
                + "onclick='deleteStudent(" + student.getId() + ")'>Delete</button>"
                + "</td>"
                + "</tr>"
            );
        }
    }

    // POST: ADD, UPDATE AND DELETE
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {

            // Delete student
            if ("delete".equals(action)) {

                int id = Integer.parseInt(request.getParameter("id"));

                if (dao.deleteStudent(id)) {
                    response.getWriter().print("Student deleted successfully!");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().print("Student could not be deleted.");
                }

                return;
            }

            // Read form values
            int id = Integer.parseInt(request.getParameter("id"));

            String name = request.getParameter("name");
            String department = request.getParameter("department");

            String marksValue = request.getParameter("marks");

            if (id <= 0 || name == null || name.trim().isEmpty()
                    || department == null || department.trim().isEmpty()
                    || marksValue == null || marksValue.trim().isEmpty()) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("Please fill in all fields correctly.");
                return;
            }

            double marks = Double.parseDouble(marksValue);

            if (!Double.isFinite(marks) || marks < 0 || marks > 100) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("Marks must be between 0 and 100.");
                return;
            }

            Student student = new Student(
                id,
                name.trim(),
                department.trim(),
                marks
            );

            // Update student
            if ("update".equals(action)) {

                if (dao.updateStudent(student)) {
                    response.getWriter().print("Student updated successfully!");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().print(
                        "Update failed. Check the student ID."
                    );
                }

                return;
            }

            // Add student
            if (dao.addStudent(student)) {
                response.getWriter().print("Student added successfully!");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(
                    "Add failed. Student ID may already exist, or database connection failed."
                );
            }

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Enter a valid Student ID and marks.");

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("Server error. Check Eclipse Console.");
        }
    }

    // JSON special character handling
    private String jsonEscape(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    // HTML special character handling
    private String htmlEscape(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}
