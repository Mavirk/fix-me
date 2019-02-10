package test;

import com.wethinkcode.router.Router;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class RouterWorkerThread implements Runnable{
    private Socket socket;

    public RouterWorkerThread(Router router, Socket socket){
        if (!socket.isClosed())this.socket = socket;
        else System.out.println("socket is closed");

    }
    public void run(){
        System.out.println("i am doing work");
        checkSocket(socket);
        try(
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ){
            output.println("You said: " + input.readLine());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void checkSocket(Socket socket){
        if (socket.isClosed()){
            System.out.println("Broker is Closed");
        }
    }
}
