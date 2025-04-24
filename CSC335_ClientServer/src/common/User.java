package common;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int userId;
    private String username;
    private String email;
    private boolean isLocked;
    private int failedAttempts;
    
    public User() {}
    
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.isLocked = false;
        this.failedAttempts = 0;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isLocked() {
        return isLocked;
    }
    
    public void setLocked(boolean locked) {
        isLocked = locked;
    }
    
    public int getFailedAttempts() {
        return failedAttempts;
    }
    
    public void setFailedAttempts(int attempts) {
        this.failedAttempts = attempts;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isLocked=" + isLocked +
                ", failedAttempts=" + failedAttempts +
                '}';
    }
} 