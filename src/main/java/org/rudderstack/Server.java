package org.rudderstack;
import sun.nio.ch.ThreadPool;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

public class Server {

    private ServerSocket serverSocket;
    static int core_pool_size = 5;
    static int max_pool_size = 10;
    static int keep_alive_time = 60;
    private ThreadPoolExecutor threadPoolExecutor;


    public Server(ServerSocket serverSocket){
       this.serverSocket = serverSocket;
       this.threadPoolExecutor = new ThreadPoolExecutor(core_pool_size, max_pool_size,keep_alive_time,TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(50));
    }

    public void start(){
        System.out.println("Server has started");
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println(" A new user has been added to users list");
                ClientHandle clientHandle = new ClientHandle(socket);
                this.threadPoolExecutor.submit(clientHandle);
            }
        }catch (IOException e){
            System.out.println("Server has crashed");
            try {
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

}
