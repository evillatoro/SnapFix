package edwinvillatoro.snapfix.objects;

import java.util.Date;

/**
 * Created by Bryan on 11/7/2017.
 */

public class ChatMessage {
    private long msgTime;
    private String msgText;
    private String msgUser;


    public ChatMessage(String messageText, String messageUser) {
        this.msgText = messageText;
        this.msgUser = messageUser;

        // Initialize to current time
        msgTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return msgText;
    }

    public void setMessageText(String messageText) {
        this.msgText = messageText;
    }

    public String getMessageUser() {
        return msgUser;
    }

    public void setMessageUser(String messageUser) {
        this.msgUser = messageUser;
    }

    public long getMessageTime() {
        return msgTime;
    }

    public void setMessageTime(long messageTime) {
        this.msgTime = messageTime;
    }
}
