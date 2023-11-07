package com.example;

import java.sql.*;

public class DBManager {

    private static Connection conn = null;
    private static Statement stmt = null;

    // connect to the database
    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            System.out.println("Database connection established.");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    // disconnect from the database
    public static void disconnect() {
        try {
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
            System.out.println("Database connection terminated.");
        } catch (SQLException e) {
            System.err.println("Error closing connections: " + e.getMessage());
        }
    }

    // add a new user to the database
    public static void addUser(String username, String password, String email) {
        try {
            if (!usernameExists(username) && !emailExists(email)) {
                stmt = conn.createStatement();
                String sql = "INSERT INTO users (username, password, email) VALUES ('" + username + "', '" + password
                        + "', '" + email + "')";
                stmt.executeUpdate(sql);
                System.out.println("User added successfully.");
            } else {
                System.out.println("Username or email already exists.");
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // remove an existing user from the database
    public static void removeUser(String username) {
        try {
            stmt = conn.createStatement();
            String sql = "DELETE FROM users WHERE username='" + username + "'";
            stmt.executeUpdate(sql);
            System.out.println("User removed successfully.");
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // edit an existing user in the database
    public static void editUser(String username, String newUsername, String newPassword, String newEmail) {
        try {
            //if (usernameExists(username)) { } else { System.out.println("Username does not exist."); }
            stmt = conn.createStatement();
                String sql = "UPDATE users SET username='" + newUsername + "', password='" + newPassword + "', email='"
                        + newEmail + "' WHERE username='" + username + "'";
                stmt.executeUpdate(sql);
                System.out.println("User edited successfully.");
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // check if a username already exists in the database
    public static boolean usernameExists(String username) {
        try {
            stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE username='" + username + "'";
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    // get the id of a user by their username
    public static int getUserIdByUsername(String username) {
        try {
            stmt = conn.createStatement();
            String sql = "SELECT id FROM users WHERE username='" + username + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return -1; // return -1 if user not found
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return -1;
        }
    }

    // check if an email already exists in the database
    public static boolean emailExists(String email) {
        try {
            stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE email='" + email + "'";
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    // get the username by their email
    public static String getUsernameByEmail(String email) {
        try {
            stmt = conn.createStatement();
            String sql = "SELECT username FROM users WHERE email='" + email + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString("username");
            } else {
                return null; // return null if email not found
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }
    
    // check if password correct for email
    public static boolean passwordForEmail(String email, String password) {
        try {
            if (emailExists(email))
            { 
                stmt = conn.createStatement();
                String sql = "SELECT password FROM users WHERE email='" + email + "'";
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    String dbPassword =  rs.getString("password");
                    return password.equals(dbPassword);
                } else {
                    return false;
                }
            }
            else
            { 
                System.out.println("Email does not exist.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }
    
}
