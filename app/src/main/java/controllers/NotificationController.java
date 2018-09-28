package controllers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.example.gavv.my_groww_project.R;

public class NotificationController {

    private Context context;
    private NotificationManager notificationManager;
    private Resources resources;
    private String title;
    private String content;


    public NotificationController(Context context, NotificationManager notificationManager,
                                  Resources resources) {

        this.context = context;
        this.notificationManager = notificationManager;
        this.resources = resources;
    }

    /**
     * Showing the notification on the device.
     * @param title
     * @param content
     */
    public void showNotification(String title, String content) {

        this.title = title;
        this.content = content;

        // Show notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder)
                new NotificationCompat.Builder(context,
                        "1")
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setLargeIcon(BitmapFactory.decodeResource(resources,
                                R.drawable.ic_stat_name))
                        .setContentTitle(this.title)
                        .setContentText(this.content);

        notificationManager.notify(1, notificationBuilder. build());
    }

    /**
     * Set the title for the notification.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set the content for the notification.
     * @param content
     */
    public void setContent(String content){
        this.content = content;
    }
}
