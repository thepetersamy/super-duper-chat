package com.example;
import java.util.UUID;

public class User {
    private UUID id;
    private String email;
    private String password;
    private String username;
    // other user attributes here
    

    //new
    //password?
    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }
    public User(String email, String username) {
        this.email = email;
        this.username = username;
    }


    
    // getters and setters for user attributes
    public UUID getId() {
        return id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    // other user methods here
    public boolean isValidEmail() {
        // TODO: implement email validation logic here
        return true;
    }
    
    public boolean isValidPassword() {
        // TODO: implement password validation logic here
        return true;
    }
    
    // Override toString() method for debugging
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
