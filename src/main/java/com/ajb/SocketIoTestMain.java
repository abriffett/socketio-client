package com.ajb;

public class SocketIoTestMain {
    public static void main (String[] args) {
        // Could potentially spin this URL out to config file/pass via command line args
        StarWarsQueryClient client = new StarWarsQueryClient("http://localhost:3000");
        client.start();
    }
}
