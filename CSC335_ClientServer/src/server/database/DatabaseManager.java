package server.database;

import java.sql.*;
import java.util.*;
import common.User;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/client_server_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    
    private Connection connection;
    
    public DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    // User Authentication Methods
    public boolean registerUser(String username, String password, String email) {
        try {
            String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password); // Note: In production, password should be hashed
                stmt.setString(3, email);
                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public User authenticateUser(String username, String password) {
        try {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password); // Note: In production, password should be hashed
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setLocked(rs.getBoolean("is_locked"));
                    user.setFailedAttempts(rs.getInt("failed_attempts"));
                    
                    // Update last login time
                    updateLastLogin(user.getUserId());
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public User getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setLocked(rs.getBoolean("is_locked"));
                user.setFailedAttempts(rs.getInt("failed_attempts"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void updateLastLogin(int userId) {
        try {
            String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean updatePassword(String email, String newPassword) {
        try {
            String sql = "UPDATE users SET password = ? WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, newPassword); // Note: In production, password should be hashed
                stmt.setString(2, email);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean incrementFailedAttempts(String username) {
        try {
            String sql = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.executeUpdate();
                
                // Check if account should be locked
                sql = "UPDATE users SET is_locked = TRUE WHERE username = ? AND failed_attempts >= " +
                      "(SELECT setting_value FROM system_settings WHERE setting_key = 'max_login_attempts')";
                try (PreparedStatement lockStmt = connection.prepareStatement(sql)) {
                    lockStmt.setString(1, username);
                    lockStmt.executeUpdate();
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean resetFailedAttempts(String username) {
        try {
            String sql = "UPDATE users SET failed_attempts = 0, is_locked = FALSE WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Session Management Methods
    public String createSession(int userId, String ipAddress) {
        try {
            String sessionToken = UUID.randomUUID().toString();
            String sql = "INSERT INTO sessions (user_id, session_token, ip_address, expires_at) " +
                        "VALUES (?, ?, ?, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL " +
                        "(SELECT setting_value FROM system_settings WHERE setting_key = 'session_timeout_minutes') " +
                        "MINUTE))";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setString(2, sessionToken);
                stmt.setString(3, ipAddress);
                stmt.executeUpdate();
                return sessionToken;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean validateSession(String sessionToken) {
        try {
            String sql = "SELECT * FROM sessions WHERE session_token = ? AND expires_at > CURRENT_TIMESTAMP";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, sessionToken);
                return stmt.executeQuery().next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void removeSession(String sessionToken) {
        try {
            String sql = "DELETE FROM sessions WHERE session_token = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, sessionToken);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Audit Logging Methods
    public void logAction(Integer userId, String action, String description, String ipAddress) {
        try {
            String sql = "INSERT INTO audit_log (user_id, action, description, ip_address) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setObject(1, userId);
                stmt.setString(2, action);
                stmt.setString(3, description);
                stmt.setString(4, ipAddress);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // User Query Methods
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            String sql = "SELECT * FROM users";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setLocked(rs.getBoolean("is_locked"));
                    user.setFailedAttempts(rs.getInt("failed_attempts"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public List<User> getLoggedInUsers() {
        List<User> users = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT u.* FROM users u " +
                        "JOIN sessions s ON u.id = s.user_id " +
                        "WHERE s.expires_at > CURRENT_TIMESTAMP";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setLocked(rs.getBoolean("is_locked"));
                    user.setFailedAttempts(rs.getInt("failed_attempts"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}