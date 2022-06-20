package org.rudderstack;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        int port = 9999;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Server server = new Server(serverSocket);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
