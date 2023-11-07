package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final BufferedReader USER_INPUT_READER = new BufferedReader(new InputStreamReader(System.in));
    private static String token;
    static AtomicReference<Socket> socketRef = new AtomicReference<>();

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            boolean isRunning = true;
            System.out.println(isRunning);
            while (isRunning) {
                System.out.println("Choose an action:");
                System.out.println("1. Login");
                System.out.println("2. Sign up");
                System.out.println("3. Create a new chat room");
                System.out.println("4. Join a chat room");
                System.out.println("5. Log out");
                System.out.println("6. Send a message");
                System.out.println("0. Exit");

                int choice = readIntegerInput();

                if (choice == 1) {
                    performLogin(outputStream, inputStream);
                } else if (choice == 2) {
                    performSignup(outputStream, inputStream);
                } else if (choice == 3) {
                    createNewChatRoom(outputStream, inputStream);
                } else if (choice == 4) {
                    joinChatRoom(outputStream, inputStream);
                } else if (choice == 5) {
                    isRunning = false;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            }
            socket.close();
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

    private static void performLogin(ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        System.out.print("Email: ");
        String email = readInput();

        System.out.print("Password: ");
        String password = readInput();

        ServerSocket chatServerSocket = new ServerSocket(0);

        int port_ = chatServerSocket.getLocalPort();
        InetAddress ipAddress = InetAddress.getLocalHost();
        String inet = ipAddress.getHostAddress();

        String packet = ClientPacketBuilder.constructLoginPacket(email, password, inet, port_);
        outputStream.writeObject(packet);

        Socket clientSocket = chatServerSocket.accept();
        socketRef.set(clientSocket);
        String packetReceived = (String) inputStream.readObject();
        System.out.println(packetReceived);
        List<String> parts = Arrays.asList(packetReceived.split(";"));

        String commandReceived = parts.get(0);
        System.out.println(commandReceived);

        if (commandReceived.equals("TOKEN")) {
            token = parts.get(1);
        } else {
            System.out.println("Error receiving token");
        }
    }

    private static void performSignup(ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        System.out.print("Email: ");
        String email = readInput();

        System.out.print("Username: ");
        String userName = readInput();

        System.out.print("Password: ");
        String password = readInput();

        String packet = ClientPacketBuilder.constructSignupPacket(email, userName, password);
        outputStream.writeObject(packet);
        String packetReceived = (String) inputStream.readObject();
        System.out.println(packetReceived);

        List<String> partsReceived = new ArrayList<>(Arrays.asList(packetReceived.split(";")));
        String commandReceived = partsReceived.get(0);
        System.out.println("Command received: " + commandReceived);
        if (commandReceived.equals("SUCCESSFUL")) {
            System.out.println("Successfully signed up");
        } else {
            System.out.println("Sign up not successful");
        }
    }

    private static void createNewChatRoom(ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        System.out.print("Enter chat name: ");
        String chatName = readInput();

        System.out.print("Enter the email of the person to include in this chat: ");
        String otherChatUsersEmail = readInput();

        String packet = ClientPacketBuilder.constructNewChatRoomPacket(token, chatName, otherChatUsersEmail);
        outputStream.writeObject(packet);

        String packetReceived = (String) inputStream.readObject();
        System.out.println(packetReceived);

        List<String> partsReceived = new ArrayList<>(Arrays.asList(packetReceived.split(";")));
        String commandReceived = partsReceived.get(0);

        if (commandReceived.equals("SUCCESSFUL")) {
            System.out.println("New chat room created");
        } else {
            System.out.println("Chat room creation not successful");
        }
    }

    private static void joinChatRoom(ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        System.out.print("Enter the chat name: ");
        String chatName = readInput();
        String packet = ClientPacketBuilder.constructJoinChatRoomPacket(token, chatName);
        outputStream.writeObject(packet);

        Thread receiverThread = new Thread(() -> {
            try {
                while (true) {
                    Socket s = socketRef.get();
                    ObjectInputStream receiverInputStream = new ObjectInputStream(s.getInputStream());

                    String packetReceived = (String) receiverInputStream.readObject();
                    System.out.println("Packet received: " + packetReceived);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error receiving message: " + e.getMessage());
            }
        });
        receiverThread.start();
        String message = "";
        while (!message.equals("exit()")) {
            System.out.println("Enter message: ");
            message = readInput();
            String messagePacket = ClientPacketBuilder.constructNewMessagePacket(token, chatName, message);
            outputStream.writeObject(messagePacket);
        }
    }

    private static String readInput() throws IOException {
        return USER_INPUT_READER.readLine();
    }

    private static int readIntegerInput() throws IOException {
        String input = readInput();
        return Integer.parseInt(input);
    }
}
