package com.example;

import java.time.Instant;

public class ServerPacketBuilder {
    
    public static String constructTokenPacket(String token) {
        return "TOKEN" + ";" + token + ";" + Instant.now();
    }

    public static String constructNewMessagePacket(String message){
        return "NEW_MESSAGE" + ";"+ message + ";" + Instant.now();
    }


    public static String constructSuccessfulResponsePacket(){
        return "SUCCESSFUL" + ";" + Instant.now();
    }


    public static String constructErrorPacket(int errorNum) {
        return "ERROR" + ";" + errorNum + ";" + Instant.now();
    }

}
