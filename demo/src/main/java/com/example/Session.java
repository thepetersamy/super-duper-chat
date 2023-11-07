package com.example;

import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class Session {
    private UUID id;
    private User user;
    private boolean isLoggedIn;
    private Instant lastAccessTime;
    private static final int SESSION_TIMEOUT_SECONDS = 1800; // 30 minutes

    private Socket clientSocket;


    public Session(User user, Socket clientSocket) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.isLoggedIn = false;
        updateLastAccessTime();
        setClientSocket(clientSocket); 
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    public Socket getClientSocket(){
        return this.clientSocket;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public Instant getLastAccessTime() {
        return lastAccessTime;
    }

    public void updateLastAccessTime() {
        this.lastAccessTime = Instant.now();
    }

    public boolean isExpired() {
        Instant currentTime = Instant.now();
        Duration sessionDuration = Duration.between(lastAccessTime, currentTime);
        return sessionDuration.getSeconds() > SESSION_TIMEOUT_SECONDS;
    }

    public boolean isValidToken(String token) {
        // TODO: Implement token validation logic here
        // Compare the token with a stored token for the session
        // Return true if the token is valid, false otherwise
        return true;
    }


    public String getToken() {
        return id.toString();
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", user=" + user +
                ", isLoggedIn=" + isLoggedIn +
                ", lastAccessTime=" + lastAccessTime +
                '}';
    }
}
