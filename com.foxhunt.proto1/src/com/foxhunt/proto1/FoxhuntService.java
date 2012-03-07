package com.foxhunt.proto1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 05.03.12
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
public class FoxhuntService extends Service {
    private static final String TAG = FoxhuntService.class.getSimpleName();
    private FoxhuntClientApplication application;
    private FoxhuntTCPClient client;
    private static final int STATUS = 1;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void goOnline()
    {
        client = new FoxhuntTCPClient(application.getHost(),application.getPort(), application.getUsername(), application.getPassword());
        client.start();
        UpdateNotification(STATUS, "Online", "Online", R.drawable.fox_blue32);
    }

    public void goOffline()
    {
        client.interrupt();
        client = null;

        UpdateNotification(STATUS, "Offline", "Offline", R.drawable.fox_gray32);
    }
    
    private void UpdateNotification(int notificationId, String tickerText, String statusText, int icon)
    {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager manager = (NotificationManager) getSystemService(ns);
        Notification notification = new Notification(icon,tickerText,0);
        notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE;
        Context context = getApplicationContext();
        CharSequence contentTitle = "Foxhunt";
        CharSequence contentText = statusText;
        Intent notificationIntent = new Intent(this, Main.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        manager.notify(notificationId, notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
        application = (FoxhuntClientApplication) getApplication();
        application.setFoxhuntService(this);
        UpdateNotification(STATUS, "Offline","Offline", R.drawable.fox_gray32);
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        goOffline();
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
        application.setFoxhuntService(null);
        Log.i(TAG,"onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
