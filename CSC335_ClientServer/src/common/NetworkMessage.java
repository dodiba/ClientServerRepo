package common;

import java.io.Serializable;

public class NetworkMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum MessageType {
        CONNECT,
        DISCONNECT,
        REGISTER,
        LOGIN,
        LOGOUT,
        PASSWORD_RECOVERY,
        QUERY_USERS,
        QUERY_LOGGED_IN,
        QUERY_LOCKED_OUT,
        QUERY_CONNECTED,
        RESPONSE,
        ERROR
    }
    
    private MessageType type;
    private Object data;
    private String sessionToken;
    private boolean success;
    private String errorMessage;
    
    public NetworkMessage(MessageType type) {
        this.type = type;
        this.success = true;
    }
    
    public NetworkMessage(MessageType type, Object data) {
        this.type = type;
        this.data = data;
        this.success = true;
    }
    
    // Getters and Setters
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public String getSessionToken() {
        return sessionToken;
    }
    
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @Override
    public String toString() {
        return "NetworkMessage{" +
                "type=" + type +
                ", data=" + data +
                ", sessionToken='" + sessionToken + '\'' +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
} 