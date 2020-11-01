/**
 * Program that is run in each thread that represents a connection with a client
 * 
 * Author: Ryan Kupka
 */

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;


public class Handler {
    //i really wanted to use a regex for this but it was getting too complicated
    //gets relevant information from client string and returns HTTP header


    static private String[] parseInput(String input) {
        System.out.println("parseinput called");
        // boolean firstSlash = false;
        boolean secondSlash = false;
        //start iteration after first slash
        int i = 5;
        String host = "";
        String resource = "";
        while (true) {
            //first if is ugly but it extracts the host
            if (!secondSlash) { //if second slash not found, start extracting host
                if (input.charAt(i) == '/') {//continue extracting characters until second slash is found
                    secondSlash = true; //skip adding second slash to host string
                    ++i;
                    continue;
                }
                //add characters to host string until second slash is found
                host += input.charAt(i);
            }
            else {
                //ounce the second slash is found, the resource can be extracted
                resource = input.substring(i, input.indexOf("HTTP/1.1"));
                break;
            }
            ++i;
        }
        //construct response string
        String response ="GET /" + resource + "HTTP/1.1\r\n" + "HOST: " + host + "\r\n" + "Connection: close\r\n\r\n";
        //return host along with response to open socket connection with origin server
        return new String[]{response, host};
    }


    public void process(Socket client) {
        final int CHUNK = 256;
        byte[] buffer = new byte[CHUNK];

        Socket originServer = null;

        BufferedReader fromClient;
        BufferedOutputStream toClient;
        DataOutputStream toServer;
        BufferedInputStream fromServer;

        //initialize client to/from streams
        try {
            fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            toClient = new BufferedOutputStream(client.getOutputStream());
        } catch (Exception e) {System.err.println(e); return;}

        try {
            String line;
            String input = "";
            //read all input from client and concatenate 
            while ((line = fromClient.readLine()) != null) {
                if (line.length() == 0) break;
                //concatenate all input to single string
                input += line;
            }

            //get full response and host from input
            String[] responseHost = parseInput(input);

            //connect to origin server
            originServer = new Socket(responseHost[1], 80);

            //initialize server i/o
            try {
                fromServer = new BufferedInputStream((originServer.getInputStream()));
                toServer = new DataOutputStream(originServer.getOutputStream());
            } catch (Exception e) {System.err.println(e); return;}

            //write response to origin server
            toServer.writeBytes(responseHost[0]);

            //read from server and write to client
            while((fromServer.read(buffer)) != -1) {
                toClient.write(buffer);
            }
            //flush output to client
            toClient.flush();

            client.close();
            originServer.close();
            
        } catch (Exception e) {System.err.println(e); return;}
    }
}
