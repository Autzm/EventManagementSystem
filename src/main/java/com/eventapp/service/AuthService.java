package com.eventapp.service;

import com.eventapp.database.DatabaseConnection;
import com.eventapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AuthService {

    public static User login(String email, String password, String selectedRole) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, email);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );

                if ("audience".equals(selectedRole)) {
                    if ("audience".equals(user.role) || "admin".equals(user.role)) {
                        return user;
                    }
                }

                if ("volunteer".equals(selectedRole)) {
                    if ("volunteer".equals(user.role)) {
                        return user;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean register(String email, String password, String role) {
        String checkSql = "SELECT * FROM users WHERE email = ?";
        String insertSql = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkPst = conn.prepareStatement(checkSql);
             PreparedStatement insertPst = conn.prepareStatement(insertSql)) {

            checkPst.setString(1, email);
            ResultSet rs = checkPst.executeQuery();

            if (rs.next()) {
                return false;
            }

            insertPst.setString(1, email);
            insertPst.setString(2, password);
            insertPst.setString(3, role);
            insertPst.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static List<User> getVolunteers() {
        List<User> volunteers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'volunteer'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                volunteers.add(new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return volunteers;
    }
}