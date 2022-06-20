package org.rudderstack;

import lombok.Data;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Data
public class ClientHandle implements  Runnable{

    public static Map<String,ClientHandle> usernameVsClientHandleMap = new ConcurrentHashMap<>();
    public static Map<String, UndeliveredMessageQueue> toUsernameVsUndeliveredMessageMap = new ConcurrentHashMap<>();
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

            if(socket.isConnected()){
                deliverUnhandledMessagesIfAny();
                showOnlineUsers();
            }

            while (socket.isConnected()){
                    String messageReceieved = reader.readLine();
                    if(messageReceieved.equalsIgnoreCase("exit")){
                        close(reader,writer,socket);
                        break;
                    }
                    String[] receivedMessageArray = messageReceieved.split(":");
                    String receiver = receivedMessageArray[0];
                    String message = receivedMessageArray[1];
                    if(receiver.equalsIgnoreCase("all")){
                        broadcastMessage(this.username + " : " + message);
                        continue;
                    }
                    sendMessage(this.username + " : " + message,receiver);
                }
            } catch (IOException e) {
                close(reader,writer,socket);
            }

    }

    private void broadcastMessage(String message) {
        for(String receiver : usernameVsClientHandleMap.keySet()){
            sendMessage(message,receiver);
        }
    }

    private void showOnlineUsers() throws IOException {

        writer.write("Online users :");
        for(String onlineUser : usernameVsClientHandleMap.keySet()){
            if(onlineUser.equalsIgnoreCase(this.username))continue;
            writer.write(onlineUser + " ");
        }
        writer.newLine();
        writer.flush();
    }

    private void deliverUnhandledMessagesIfAny() throws IOException {
        UndeliveredMessageQueue undeliveredMessageQueue = toUsernameVsUndeliveredMessageMap.get(this.username);

        if(undeliveredMessageQueue == null || !undeliveredMessageQueue.hasUndeliveredMesssages()){
            return;
        }

        // flushing all the messages undelivered to this user as soon he gets online
        for(LinkedBlockingQueue queue : undeliveredMessageQueue.getFromUsernameVsUndeliveredMessageMap().values()){
            while(!queue.isEmpty()){
                writer.write((String)queue.poll());
                writer.newLine();
            }
        }
        writer.flush();
        // Removing entry of this username from undelivered messsage map assuming all the messasges are deleivered;
        toUsernameVsUndeliveredMessageMap.remove(this.username);

    }

    private void sendMessage(String message, String receiver){
        try {
            if(receiver.equalsIgnoreCase(this.username))return;
            if(!usernameVsClientHandleMap.containsKey(receiver)){  // Adding message in message queue as user is offline
                writer.write("The user " +  receiver   +  " is currently offline. Message will be delivered next time user gets online");
                writer.newLine();
                writer.flush();
                if(toUsernameVsUndeliveredMessageMap.containsKey(receiver)){
                    toUsernameVsUndeliveredMessageMap.get(receiver).addUndeliveredMessage(this.username,message);
                }else{
                    UndeliveredMessageQueue undeliveredMessageQueue = new UndeliveredMessageQueue();
                    undeliveredMessageQueue.addUndeliveredMessage(this.username,message);
                    toUsernameVsUndeliveredMessageMap.put(receiver,undeliveredMessageQueue);
                }
                return;
            }
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
