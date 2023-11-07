package com.example;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatRoom {
    private UUID id;
    private String name;
    private List<User> users;
    
    // otherChatUsersUserName stands for the username of the other party
    public ChatRoom(String name, User user1, User user2) {
        
        this.id = UUID.randomUUID();
        this.name = name;

        this.users = new ArrayList<>();
        this.users.add(user1);
        this.users.add(user2);
        
    }

    public String getId() {
        return id.toString();
    }

    public String getName(){
        return name;
    }

    public List<User> getUsers() {
        return users;
    }

    // public void addUser(User user) {
    //     users.add(user);
    // }

    // public void removeUser(User user) {
    //     users.remove(user);
    // }

    // // public void put(String roomId, ChatRoom chatRoom) {
    // //     // Add the chat room to the chatRooms map
    // //     // Implementation depends on the storage mechanism you choose
    // //     // You can use a map or a database to store and retrieve chat rooms
    // //     // Here's an example using a map:
    // //     chatRooms.put(roomId, chatRoom);
    // }
}
