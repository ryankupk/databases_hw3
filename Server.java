/*
Webserver that acts as a relay/proxy for client requests to the indicated origin server.

Author: Ryan Kupka

*/
import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class Server {
    static final int PORT = 8080;
    private static final Executor exec = Executors.newCachedThreadPool();
    public static void main(String[] args) {
        ServerSocket sock;
        try {
            sock = new ServerSocket(PORT);
            while(true) {
                Runnable task = new Connection(sock.accept());
                exec.execute(task);
            }
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }
}
