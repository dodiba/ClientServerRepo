package client;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import common.NetworkMessage;
import common.User;
import client.gui.ClientGUI;

public class Client {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String serverAddress;
    private int serverPort;
    private User currentUser;
    private ClientGUI gui;
    
    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.gui = new ClientGUI(this);
    }
    
    public boolean connect() {
        try {
            socket = new Socket(serverAddress, serverPort);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            
            NetworkMessage connectMessage = new NetworkMessage(NetworkMessage.MessageType.CONNECT);
            output.writeObject(connectMessage);
            output.flush();
            
            NetworkMessage response = (NetworkMessage) input.readObject();
            return response.isSuccess();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean register(String username, String password, String email) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("username", username);
            data.put("password", password);
            data.put("email", email);
            
            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.REGISTER, data);
            output.writeObject(message);
            output.flush();
            
            NetworkMessage response = (NetworkMessage) input.readObject();
            return response.isSuccess();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean login(String username, String password) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("username", username);
            data.put("password", password);
            
            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.LOGIN, data);
            output.writeObject(message);
            output.flush();
            
            NetworkMessage response = (NetworkMessage) input.readObject();
            if (response.isSuccess()) {
                currentUser = (User) response.getData();
            }
            return response.isSuccess();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void logout() {
        try {
            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.LOGOUT);
            output.writeObject(message);
            output.flush();
            currentUser = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void disconnect() {
        try {
            if (currentUser != null) {
                logout();
            }
            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.DISCONNECT);
            output.writeObject(message);
            output.flush();
            
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean requestPasswordRecovery(String username) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("username", username);
            
            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.PASSWORD_RECOVERY, data);
            output.writeObject(message);
            output.flush();
            
            NetworkMessage response = (NetworkMessage) input.readObject();
            return response.isSuccess();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<User> queryUsers() {
        try {
            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.QUERY_USERS);
            output.writeObject(message);
            output.flush();
            
            NetworkMessage response = (NetworkMessage) input.readObject();
            if (response.isSuccess()) {
                return (List<User>) response.getData();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    public List<String> queryLoggedInUsers() {
        try {
            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.QUERY_LOGGED_IN);
            output.writeObject(message);
            output.flush();
            
            NetworkMessage response = (NetworkMessage) input.readObject();
            if (response.isSuccess()) {
                return (List<String>) response.getData();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String serverAddress = JOptionPane.showInputDialog("Enter server address:");
            if (serverAddress != null && !serverAddress.isEmpty()) {
                new Client(serverAddress, 8000);
            }
        });
    }
} 