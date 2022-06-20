package org.rudderstack;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Data
public class UndeliveredMessageQueue {

    Map<String,LinkedBlockingQueue<String>> fromUsernameVsUndeliveredMessageMap;

    public UndeliveredMessageQueue(){
        this.fromUsernameVsUndeliveredMessageMap = new ConcurrentHashMap<>();
    }

    public boolean hasUndeliveredMesssages(){
        return (fromUsernameVsUndeliveredMessageMap.size() > 0);
    }


    public void addUndeliveredMessage(String fromUsername, String message) {
        if(fromUsernameVsUndeliveredMessageMap.containsKey(fromUsername)){
            fromUsernameVsUndeliveredMessageMap.get(fromUsername).offer(message);
        }else{
            LinkedBlockingQueue  messageQueue = new LinkedBlockingQueue();
            messageQueue.offer(message);
            fromUsernameVsUndeliveredMessageMap.put(fromUsername,messageQueue);
        }
    }
}
