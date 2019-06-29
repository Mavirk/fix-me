package com.wethinkcode.router;
import java.nio.channels.SocketChannel;

class ServerDataEvent {
    public Router server;
    public SocketChannel socket;
    public byte[] data;

    public ServerDataEvent(Router server, SocketChannel socket, byte[] data) {
        this.server = server;
        this.socket = socket;
        this.data = data;
    }
}
