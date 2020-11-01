/**
 * basic program that launches thread that reads and writes from origin server
 * Author: Ryan Kupka
 */

import java.net.*;

public class Connection implements Runnable {
    private Socket client;
    private static Handler handler = new Handler();

    public Connection(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            handler.process(client);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
