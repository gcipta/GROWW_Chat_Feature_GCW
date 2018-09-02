package models;

/**
 * interface for messages
 */
public interface IMessageComponent {

    /**
     * create a message from a string
     * @param message   text string of the message
     * @return
     */
    Message createMessage(String message);

    /**
     * send a message to a user
     * @param message   message to send to recipient
     * @param userID    userID of the message recipient
     */
    void sendMessage(Message message, int userID);

    /**
     * display a message to the UI
     * @param message   message to be displayed
     */
    void displayMessage(Message message);
}
