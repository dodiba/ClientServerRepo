package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import common.NetworkMessage;
import common.User;
import server.database.DatabaseManager;
import server.util.EmailUtil;

public class Server {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private DatabaseManager dbManager;
    private Map<String, User> loggedInUsers;
    private boolean running;
    
    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            threadPool = Executors.newCachedThreadPool();
            try {
				dbManager = new DatabaseManager();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            loggedInUsers = new ConcurrentHashMap<>();
            running = true;
            
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void start() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket));
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void stop() {
        running = false;
        try {
            serverSocket.close();
            threadPool.shutdown();
            dbManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private User currentUser;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public void run() {
            try {
                while (running) {
                    NetworkMessage message = (NetworkMessage) input.readObject();
                    handleMessage(message);
                }
            } catch (EOFException e) {
                // Client disconnected
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                cleanup();
            }
        }
        
        private void handleMessage(NetworkMessage message) throws IOException {
            NetworkMessage response = new NetworkMessage(NetworkMessage.MessageType.RESPONSE);
            
            switch (message.getType()) {
                case CONNECT:
                    response.setSuccess(true);
                    break;
                    
                case DISCONNECT:
                    cleanup();
                    return;
                    
                case REGISTER:
                    handleRegister(message, response);
                    break;
                    
                case LOGIN:
                    handleLogin(message, response);
                    break;
                    
                case LOGOUT:
                    handleLogout(response);
                    break;
                    
                case PASSWORD_RECOVERY:
                    handlePasswordRecovery(message, response);
                    break;
                    
                case QUERY_USERS:
                    handleQueryUsers(response);
                    break;
                    
                case QUERY_LOGGED_IN:
                    handleQueryLoggedIn(response);
                    break;
                    
                default:
                    response.setSuccess(false);
                    response.setErrorMessage("Unknown message type");
            }
            
            output.writeObject(response);
            output.flush();
        }
        
        private void handleRegister(NetworkMessage message, NetworkMessage response) {
            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) message.getData();
            String username = data.get("username");
            String password = data.get("password");
            String email = data.get("email");
            
            if (dbManager.registerUser(username, password, email)) {
                response.setSuccess(true);
            } else {
                response.setSuccess(false);
                response.setErrorMessage("Registration failed");
            }
        }
        
        private void handleLogin(NetworkMessage message, NetworkMessage response) {
            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) message.getData();
            String username = data.get("username");
            String password = data.get("password");
            
            User user = dbManager.authenticateUser(username, password);
            if (user != null) {
                if (user.isLocked()) {
                    response.setSuccess(false);
                    response.setErrorMessage("Account is locked");
                } else {
                    currentUser = user;
                    loggedInUsers.put(username, user);
                    response.setSuccess(true);
                    response.setData(user);
                }
            } else {
                response.setSuccess(false);
                response.setErrorMessage("Invalid username or password");
            }
        }
        
        private void handleLogout(NetworkMessage response) {
            if (currentUser != null) {
                loggedInUsers.remove(currentUser.getUsername());
                currentUser = null;
            }
            response.setSuccess(true);
        }
        
        private void handlePasswordRecovery(NetworkMessage message, NetworkMessage response) {
            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) message.getData();
            String username = data.get("username");
            
            User user = dbManager.getUserByUsername(username);
            if (user != null) {
                String newPassword = EmailUtil.generateRandomPassword();
                if (dbManager.updatePassword(username, newPassword) &&
                    EmailUtil.sendPasswordRecoveryEmail(user.getEmail(), newPassword)) {
                    response.setSuccess(true);
                } else {
                    response.setSuccess(false);
                    response.setErrorMessage("Password recovery failed");
                }
            } else {
                response.setSuccess(false);
                response.setErrorMessage("User not found");
            }
        }
        
        private void handleQueryUsers(NetworkMessage response) {
            List<User> users = dbManager.getAllUsers();
            response.setSuccess(true);
            response.setData(users);
        }
        
        private void handleQueryLoggedIn(NetworkMessage response) {
            List<String> usernames = new ArrayList<>(loggedInUsers.keySet());
            response.setSuccess(true);
            response.setData(usernames);
        }
        
        private void cleanup() {
            if (currentUser != null) {
                loggedInUsers.remove(currentUser.getUsername());
            }
            try {
                if (input != null) input.close();
                if (output != null) output.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        Server server = new Server(8000);
        server.start();
    }
} 
