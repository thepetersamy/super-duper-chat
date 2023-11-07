package com.example;

import java.time.Instant;

public class ClientPacketBuilder {
    // when logging in serversocket that receives messages is also sent in login packet
    public static String constructLoginPacket(String email, String password, String inet, int port) {
        return "LOGIN" + ";" + email + ";" + password + ";" + inet + ";" + port + ";" + Instant.now();
    }

    public static String constructSignupPacket(String email, String userName, String password) {
        return "SIGNUP" + ";" + email + ";" + userName + ";" + password + ";" + Instant.now();
    }

    // that otherChatUsersUserName variable stands for the user name of the other
    // party that should be included in the chatroom
    public static String constructNewChatRoomPacket(String token, String chatName, String otherChatUsersEmail) {
        return "NEW_CHAT_ROOM" + ";" + token + ";" + chatName + ";" + otherChatUsersEmail + ";" + Instant.now();
    }

    public static String constructJoinChatRoomPacket(String token, String chatName) {
        return "JOIN_CHAT_ROOM" + ";" + token + ";" + chatName + ";" + Instant.now();
    }

    public static String constructNewMessagePacket(String token, String chatName, String message) {
        return "NEW_MESSAGE" + ";" + token + ";" + chatName + ";" + message + ";" + Instant.now();
    }

}
