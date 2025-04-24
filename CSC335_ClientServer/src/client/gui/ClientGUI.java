package client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import common.User;
import client.Client;

@SuppressWarnings("unused")
public class ClientGUI extends JFrame {
	
	private static final long serialVersionUID = -6696937471697322499L;
	private Client client;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel registerPanel;
    private JPanel mainMenuPanel;
    private JPanel adminPanel;
    
    private JTextField serverAddressField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    
    public ClientGUI(Client client) {
        this.client = client;
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Client Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new CardLayout());
        
        // Login Panel
        loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "LOGIN");
        
        // Register Panel
        registerPanel = createRegisterPanel();
        mainPanel.add(registerPanel, "REGISTER");
        
        // Main Menu Panel
        mainMenuPanel = createMainMenuPanel();
        mainPanel.add(mainMenuPanel, "MAIN_MENU");
        
        // Admin Panel
        adminPanel = createAdminPanel();
        mainPanel.add(adminPanel, "ADMIN");
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Server Address
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Server Address:"), gbc);
        
        gbc.gridx = 1;
        serverAddressField = new JTextField(20);
        panel.add(serverAddressField, gbc);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        JButton connectButton = new JButton("Connect");
        
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> showPanel("REGISTER"));
        connectButton.addActionListener(e -> handleConnect());
        
        buttonPanel.add(connectButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");
        
        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> showPanel("LOGIN"));
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel buttonPanel = new JPanel();
        JButton logoutButton = new JButton("Logout");
        JButton disconnectButton = new JButton("Disconnect");
        JButton adminButton = new JButton("Admin Panel");
        
        logoutButton.addActionListener(e -> handleLogout());
        disconnectButton.addActionListener(e -> handleDisconnect());
        adminButton.addActionListener(e -> showPanel("ADMIN"));
        
        buttonPanel.add(logoutButton);
        buttonPanel.add(disconnectButton);
        buttonPanel.add(adminButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel buttonPanel = new JPanel();
        JButton queryUsersButton = new JButton("Query Users");
        JButton queryLoggedInButton = new JButton("Query Logged In Users");
        JButton backButton = new JButton("Back to Main Menu");
        
        queryUsersButton.addActionListener(e -> handleQueryUsers());
        queryLoggedInButton.addActionListener(e -> handleQueryLoggedIn());
        backButton.addActionListener(e -> showPanel("MAIN_MENU"));
        
        buttonPanel.add(queryUsersButton);
        buttonPanel.add(queryLoggedInButton);
        buttonPanel.add(backButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private void showPanel(String panelName) {
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, panelName);
    }
    
    private void handleConnect() {
        String serverAddress = serverAddressField.getText();
        if (serverAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter server address");
            return;
        }
        
        if (client.connect()) {
            JOptionPane.showMessageDialog(this, "Connected successfully");
        } else {
            JOptionPane.showMessageDialog(this, "Connection failed");
        }
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }
        
        if (client.login(username, password)) {
            showPanel("MAIN_MENU");
        } else {
            JOptionPane.showMessageDialog(this, "Login failed");
        }
    }
    
    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();
        
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }
        
        if (client.register(username, password, email)) {
            JOptionPane.showMessageDialog(this, "Registration successful");
            showPanel("LOGIN");
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed");
        }
    }
    
    private void handleLogout() {
        client.logout();
        showPanel("LOGIN");
    }
    
    private void handleDisconnect() {
        client.disconnect();
        showPanel("LOGIN");
    }
    
    private void handleQueryUsers() {
        List<User> users = client.queryUsers();
        StringBuilder message = new StringBuilder("Registered Users:\n");
        for (User user : users) {
            message.append(user.getUsername()).append("\n");
        }
        JOptionPane.showMessageDialog(this, message.toString());
    }
    
    private void handleQueryLoggedIn() {
        List<String> users = client.queryLoggedInUsers();
        StringBuilder message = new StringBuilder("Logged In Users:\n");
        for (String username : users) {
            message.append(username).append("\n");
        }
        JOptionPane.showMessageDialog(this, message.toString());
    }
} 