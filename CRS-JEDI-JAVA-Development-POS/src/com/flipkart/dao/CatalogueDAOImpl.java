package com.flipkart.dao;

import com.flipkart.bean.Course;
import com.flipkart.exception.CourseNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.flipkart.constant.DBConnection.*;


public class CatalogueDAOImpl implements CatalogueDAO {
    private Connection conn = null;
    private PreparedStatement stmt = null;

    public CatalogueDAOImpl() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addCourseInDB(Course course, String semID) {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "insert into Catalogue values (?,?,?,?)";

            stmt = conn.prepareStatement(sql);

            stmt.setString(1, course.getCourseID());
            stmt.setString(2, semID);
            stmt.setString(3, null);
            stmt.setInt(4, course.getAvailableSeats());
            if (stmt.executeUpdate() == 1) {
                System.out.println("Catalogue Updated");
            } else {
                System.out.println("Catalogue db update failed");
            }
            stmt.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Course> fetchCatalogue() {
        List<Course> courseList = new ArrayList<>();

        try {
            String sql = "select Catalogue.courseId, Course.name, Catalogue.professorId, Catalogue.availableSeats from Catalogue, Course where Catalogue.courseId = Course.courseId";
            stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Course tempcourse = new Course(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4));
                courseList.add(tempcourse);
            }

            rs.close();
            return courseList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseList;
    }

    @Override
    public void deleteCourseInDB(String courseId) {
        try {
            String sql = "delete from Catalogue where courseId = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, courseId);

            int row = stmt.executeUpdate();
            stmt.close();
            conn.close();

            if (row == 0) {
                throw new CourseNotFoundException(courseId);
            }

        } catch (SQLException | CourseNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}