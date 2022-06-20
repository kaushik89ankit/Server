package org.rudderstack;

import lombok.Data;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ClientHandle implements  Runnable{

    public static Map<String,ClientHandle> usernameVsClientHandleMap = new ConcurrentHashMap<>();
    private Socket socket;
    private String username;
    private BufferedReader reader;
    private BufferedWriter writer;


    public ClientHandle(Socket socket){
        this.socket = socket;
        try{
         this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
         this.username = reader.readLine();
         ClientHandle.usernameVsClientHandleMap.put(username,this);
        } catch (IOException e) {
            close(reader,writer,socket);
        }
    }

    @Override
    public void run() {

        try{
            while (socket.isConnected()){
                    String[] receivedMessageArray = reader.readLine().split(":");
                    String receiver = receivedMessageArray[0];
                    String message = receivedMessageArray[1];
                    sendMessage(this.username + " : " + message,receiver);
                }
            } catch (IOException e) {
                close(reader,writer,socket);
            }

    }

    private void sendMessage(String message, String receiver){
        try {
            if(receiver.equalsIgnoreCase(this.username))return;
            if(!usernameVsClientHandleMap.containsKey(receiver))return;
            BufferedWriter writer = usernameVsClientHandleMap.get(receiver).getWriter();
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
          close(this.reader,this.writer,this.socket);
        }
    }

    private void removeUser(){
        usernameVsClientHandleMap.remove(this.username);
        System.out.println("User : " + this.username + " has left the chat.");
    }


    private void close(BufferedReader reader, BufferedWriter writer, Socket socket) {

        removeUser();
        try{
            if(reader != null){
                reader.close();
            }

            if(writer != null){
                writer.close();
            }

            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
