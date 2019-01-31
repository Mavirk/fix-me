package com.wethinkcode.router;

import java.net.Socket;

public class Client {
    public long id;
    public Socket socket;
    public Client(Socket s, long id){
        this.socket = s;
        this.id = id;
    }
}
