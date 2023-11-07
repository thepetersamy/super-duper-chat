package com.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.concurrent.atomic.AtomicReference;

public class Server {
    private static final int PORT = 12345;
    private Map<String, Session> activeSessions;
    // private Map<String, User> registeredUsers;
    private Map<String, ChatRoom> chatRooms;

    public Server() {
        activeSessions = new HashMap<>();
        // registeredUsers = new HashMap<>();
        chatRooms = new HashMap<>();
        // AtomicReference<Socket> socketRef = new AtomicReference<>();

        DBManager.connect();

        // Create dummy users (replace with your user registration logic)
        // User user1 = new User("a", "a", "a");
        // User user2 = new User("b", "b", "b");

        // //new
        // DBManager.addUser("a", "a", "a");
        // DBManager.addUser("b", "b", "b");

        // ChatRoom chatRoom1 = new ChatRoom("chat", user1, user2);
        // chatRooms.put(chatRoom1.getName(), chatRoom1);
        // registeredUsers.put(user1.getEmail(), user1);
        // registeredUsers.put(user2.getEmail(), user2);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                Thread thread = new Thread(() -> handleClient(clientSocket));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());) {
            // String packetString;

            while (true) {
                String packet = (String) inputStream.readObject();
                System.out.println("Received packet from client: " + packet);

                // Process the packet and send a response packet back to the client
                processPacket(packet, clientSocket, inputStream, outputStream);
                // outputStream.writeObject(responsePacket);
                // System.out.println("Sent response packet to client: " + responsePacket);
            }
            // inputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }

    public void processPacket(String packet, Socket clientSocket, ObjectInputStream inputStream,
            ObjectOutputStream outputStream)
            throws IOException, ClassNotFoundException {

        List<String> parts = new ArrayList<>(Arrays.asList(packet.split(";"))); // Convert array to a new ArrayList
        String command = parts.get(0);

        int port_;
        String inet;

        if (command.equals("LOGIN")) {
            // handleLogin(packet);
            if (parts.size() > 1) {
                String email = parts.get(1);
                String password = parts.get(2);
                inet = parts.get(3);
                port_ = Integer.parseInt((String) parts.get(4));
                System.out.println(inet + "  " + port_);

                if (DBManager.emailExists(email)) {
                    User user = new User(email, DBManager.getUsernameByEmail(email));

                    if (DBManager.passwordForEmail(email, password)) { // user.getPassword().equals(password))
                                                                                      
                        System.out.println("recveid ip and port     " + inet + port_);
                        Socket s = new Socket(inet, port_);

                        System.out.println("created socket");

                        Session session = new Session(user, s);
                        activeSessions.put(session.getToken(), session);
                        // activeSessions.put(session.getToken(), session);

                        System.out.println(user.getEmail() + " has logged in");
                        String tokenPacket = ServerPacketBuilder.constructTokenPacket(session.getToken());
                        outputStream.writeObject(tokenPacket);
                    }

                    else {
                        System.out.println("malicious attempt at logging in as " + email);
                        String feedbackPacket = ServerPacketBuilder.constructErrorPacket(3);
                        outputStream.writeObject(feedbackPacket);
                    }
                }
                else {
                    // System.out.println("malicious attempt at logging in as " + email);
                    String feedbackPacket = ServerPacketBuilder.constructErrorPacket(3);
                    outputStream.writeObject(feedbackPacket);
                }
            }

        } else if (command.equals("SIGNUP")) {
            // TODO check integrity of packet!!!!!!!
            if (parts.size() > 1) {
                String email = parts.get(1);
                String userName = parts.get(2);
                String password = parts.get(3);
                System.out.println("                                           " + email + password + userName);
                // new
                DBManager.addUser(userName, password, email);

                String feedbackPacket = ServerPacketBuilder.constructSuccessfulResponsePacket();
                outputStream.writeObject(feedbackPacket);

            } else {
                System.out.println("unsuccessful sign up ");
                String feedbackPacket = ServerPacketBuilder.constructErrorPacket(0);
                outputStream.writeObject(feedbackPacket);

            }
        } else if (command.equals("NEW_CHAT_ROOM")) {
            // TODO check integrity of packet!!!!!!!
            if (parts.size() > 1) {

                // verify user
                String token = parts.get(1);

                Session session = activeSessions.get(token);

                if (session != null) {

                    String chatName = parts.get(2);
                    String otherChatUsersEmail = parts.get(3); // email of other party that should be in chatroom

                    boolean chatAlreadyExists = false;
                    // checking if a chat with the same name exists, if so dont create
                    // modify in future to not use name as a prim key
                    for (Map.Entry<String, ChatRoom> entry : chatRooms.entrySet()) {
                        String currentChatName = entry.getValue().getName();
                        if (currentChatName.equals(chatName)) {
                            System.out.println("chat with same name already exists");
                            String feedbackPacket = ServerPacketBuilder.constructErrorPacket(5);
                            outputStream.writeObject(feedbackPacket);
                            chatAlreadyExists = true;
                        }
                        System.out.println(currentChatName);
                    }

                    if (chatAlreadyExists) {
                        return;
                    }

                    // new
                    if (DBManager.emailExists(otherChatUsersEmail)) {
                        User otherUser = new User(otherChatUsersEmail,
                                DBManager.getUsernameByEmail(otherChatUsersEmail));
                        ChatRoom chatRoom = new ChatRoom(chatName, session.getUser(), otherUser);
                        chatRooms.put(chatRoom.getName(), chatRoom);

                        String feedbackPacket = ServerPacketBuilder.constructSuccessfulResponsePacket();
                        outputStream.writeObject(feedbackPacket);
                        System.out.println("new chat room created");
                        for (Map.Entry<String, ChatRoom> entry : chatRooms.entrySet()) {
                            String key = entry.getKey();
                            ChatRoom value = entry.getValue();
                            System.out.println(key + " -> " + value.getUsers());
                        }
                        // outputStream.flush();
                        // outputStream.close();
                    } else {
                        System.out.println("user doesnt exist");
                        String feedbackPacket = ServerPacketBuilder.constructErrorPacket(5);
                        outputStream.writeObject(feedbackPacket);
                    }
                }

                else {
                    System.out.println("unsuccessful new chat room NOT IMPLEMENTED YET");
                    String feedbackPacket = ServerPacketBuilder.constructErrorPacket(1);
                    outputStream.writeObject(feedbackPacket);

                }
            } else {
                System.out.println("unsuccessful sign up NOT IMPLEMENTED YET");
            }
        } else if (command.equals("JOIN_CHAT_ROOM")) {

            if (parts.size() > 1) {
                String chatName = parts.get(0);
                String token = parts.get(1);
                Session session = activeSessions.get(token);
                if (session != null) {
                    ChatRoom chatRoom = chatRooms.get(chatName);
                    User sendUser = session.getUser();
                } else {
                    String feedbackPacket = ServerPacketBuilder.constructErrorPacket(6);
                    outputStream.writeObject(feedbackPacket);
                    outputStream.flush();
                }
            } else {
                String feedbackPacket = ServerPacketBuilder.constructErrorPacket(1);
                outputStream.writeObject(feedbackPacket);
                outputStream.flush();
            }
        } else if (command.equals("NEW_MESSAGE")) {

            if (parts.size() > 1) {
                String chatName = parts.get(0);
                String token = parts.get(1);
                Session session = activeSessions.get(token);
                String message = parts.get(3);
                if (session != null) {
                    ChatRoom chatRoom = chatRooms.get(chatName);
                    User sendUser = session.getUser();
                    // String inet = (String)inputStream.readObject();
                    // int port_ = Integer.parseInt((String)inputStream.readObject());
                    // System.out.println("recveid ip and port "+inet + port_);
                    // Socket s = new Socket(inet, port_);

                    // Continuously handle incoming messages in a separate thread

                    Thread messageThread = new Thread(() -> {
                        try {

                            handleChatRoomMessages(chatRoom, outputStream, inputStream, sendUser, message);

                        } catch (ClassNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    });
                    // messageThread.start();
                    try {

                        handleChatRoomMessages(chatRoom, outputStream, inputStream, sendUser, message);

                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    String feedbackPacket = ServerPacketBuilder.constructErrorPacket(6);
                    outputStream.writeObject(feedbackPacket);
                    outputStream.flush();
                }
            } else {
                String feedbackPacket = ServerPacketBuilder.constructErrorPacket(1);
                outputStream.writeObject(feedbackPacket);
                outputStream.flush();
            }
        }
    }

    private void handleChatRoomMessages(ChatRoom chatRoom, ObjectOutputStream outputStream,
            ObjectInputStream inputStream, User sendUser, String message) throws ClassNotFoundException {
        try {
            // AtomicReference<Socket> socketRef = new AtomicReference<>();
            while (true) {

                for (Map.Entry<String, Session> entry : activeSessions.entrySet()) {
                    User currentUser = entry.getValue().getUser();

                    if (!currentUser.getEmail().equals(sendUser.getEmail())) {
                        System.out.println("Entered if condition");
                        Socket receiverSocket = entry.getValue().getClientSocket();
                        // socketRef.set(receiverSocket);

                        ObjectOutputStream receiverOutputStream = new ObjectOutputStream(
                                receiverSocket.getOutputStream());

                        // ObjectInputStream receiverInputStream = new
                        // ObjectInputStream(receiverSocket.getInputStream());

                        System.out.println("Message received: " + message);

                        receiverOutputStream.writeObject(message);
                        receiverOutputStream.flush();
                        return;
                    }
                }
                // }
                // message = null;
            }
        } catch (IOException e) {
            System.err.println("Error handling chat room messages: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
