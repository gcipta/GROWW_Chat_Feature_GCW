package controllers;

import controllers.IMessageComponent;
import models.Message;

public class MessageController implements IMessageComponent {
    @Override
    public Message createMessage(String message) {
        return null;
    }

    @Override
    public void sendMessage(Message message, int userID) {

    }

    @Override
    public void displayMessage(Message message) {

    }
}
