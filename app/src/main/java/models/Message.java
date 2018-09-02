package models;

import java.sql.Time;
import java.util.Date;

/**
 * contains a message
 */
public class Message {

    private Long id;
    private Date date;
    private String content;
    private User sender;
    private User recipient;
    private Boolean isRead;


    public Message(User sender, User recipient, Date date, String content){
        setSender(sender);
        setRecipient(recipient);
        setDate(date);
        setContent(content);
        setIsRead(false);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }
}
