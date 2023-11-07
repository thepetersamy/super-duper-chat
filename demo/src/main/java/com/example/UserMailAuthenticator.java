package com.example;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Random;
import java.util.Scanner;

public class UserMailAuthenticator {

    // This method establishes a connection to the mail server
    public static Session getSession(String host, String port, final String username, final String password) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(username, password);
            }
        });

        return session;
    }

    

    // This method sends an email with the token to the user
    public static void sendToken(String recipient, String token, String serverEmail, String serverPassword) {
        String host = "smtp.office365.com";
        String port = "587";
        String subject = "Your authentication token for the Texting App";
        String body = "Your authentication token is: " + token.split(":")[0]
                + ". Please enter this in the app to create a new account.";
                System.out.println(token.split(":")[0]);

        try {
            Session session = getSession(host, port, serverEmail, serverPassword);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(serverEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Token sent successfully to " + recipient);
        } catch (MessagingException e) {
            System.out.println("Error sending token: " + e.getMessage());
        }
    }

    // This method generates a random token and returns it along with the timestamp
public static String generateToken(int length) {
    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder token = new StringBuilder();
    Random rnd = new Random();
    while (token.length() < length) { // length of the random string.
        int index = (int) (rnd.nextFloat() * chars.length());
        token.append(chars.charAt(index));
    }
    long timestamp = System.currentTimeMillis();
    return token.toString() + ":" + timestamp;
}

// This method checks if the entered token matches the generated token and has not expired
public static boolean checkToken(String enteredToken, String generatedToken, int timeoutInSeconds) {
    String[] tokenParts = generatedToken.split(":");
    if (tokenParts.length != 2) {
        System.out.println("Invalid format for generated token.");
        return false;
    }
    String token = tokenParts[0];
    long timestamp = Long.parseLong(tokenParts[1]);
    long currentTime = System.currentTimeMillis();
    if (enteredToken.equals(token) && (currentTime - timestamp) < (timeoutInSeconds * 1000)) {
        return true;
    }
    // Token has expired
    if ((currentTime - timestamp) >= (timeoutInSeconds * 1000)) {
        System.out.println("Token has expired.");
    }
    System.out.println("Invalid token. Please try again.");
    return false;
}

    // This method is called by the server to authenticate the user
public static void authenticateUser(String recipient, String serverEmail, String serverPassword, int tokenLength, int timeoutInSeconds) {
    // Generate a random token and send it to the user's email
    String token = generateToken(tokenLength);
    sendToken(recipient, token, serverEmail, serverPassword);

    // Wait for the user to enter the token
    Scanner scanner = new Scanner(System.in);
    System.out.println("Please enter the token sent to your email: ");
    String enteredToken = scanner.nextLine();
    scanner.close();
    // Validate the entered token
    if (checkToken(enteredToken, token, timeoutInSeconds)) {
        //
        // Token is valid, authenticate the user
        System.out.println("Authentication successful. User " + recipient + " is now logged in.");
        // Implement code to allow the user to access the application or perform any other action required for authentication
    } else {
        // Token is invalid, prompt the user to try again
        System.out.println("Invalid token. Please try again.");
    }
}

    

    // Sample usage of the authenticateUser method
    public static void main(String[] args) {
        String recipient = "";
        String serverEmail = "";
        String serverPassword = "";
        int tokenLength = 6;
        int timeoutInSeconds = 300; // 5 minutes

        authenticateUser(recipient, serverEmail, serverPassword, tokenLength, timeoutInSeconds);
    }
}