package controllers;

public interface INotificationComponent {

    /**
     * Showing the notification on the device.
     * @param title
     * @param content
     */
    public void showNotification(String title, String content);
}
