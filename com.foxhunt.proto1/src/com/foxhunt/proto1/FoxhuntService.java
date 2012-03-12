package com.foxhunt.proto1;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.foxhunt.proto1.entity.*;
import com.foxhunt.proto1.packets.*;
import org.jboss.netty.util.ThreadRenamingRunnable;

import java.util.Date;

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
    private FoxhuntClient client;
    private Location lastKnownLocation = null;
    private LocationListener locationListener = null;
    private long lastFixTime = 0;
    private Fox[] knownFoxes;
    private Spot[] knownSpots;
    private Thread serviceThread;

    public Spot[] getKnownSpots() {
        return knownSpots ==null  ? new Spot[0] : knownSpots;
    }

    public Fox[] getKnownFoxes() {
        return knownFoxes;
    }

    private static final int STATUS = 1;
    private static final int SYSMESSAGE = 2;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
        if(application.getMainActivity()!=null)
        {
            application.getMainActivity().RefreshView();
        }
        SendLocation();
    }

    public void OnPreferencesChange()
    {
        client.setHost(application.getHost());
        client.setPort(application.getPort());
        client.setPassword(application.getPassword());
        client.setUsername(application.getUsername());
    }

    public void goOnline()
    {
        try
        {
            client.Connect();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    public void BeginLocationListen()
    {
        Log.i(TAG,"BeginLocationListen");
        LocationManager locationManager = (LocationManager )this.getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener );
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void EndLocationListen()
    {
        Log.i(TAG,"EndLocationListen");
        LocationManager locationManager = (LocationManager )this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
    }

    public void goOffline()
    {
        client.Disconnect();
    }
    
    private void ShowSystemMessage(String text)
    {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager manager = (NotificationManager) getSystemService(ns);
        Notification notification = new Notification(R.drawable.fox_blue32,"System message",0);
        Context context = getApplicationContext();
        CharSequence contentTitle = "Foxhunt";
        CharSequence contentText = text;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        manager.notify(SYSMESSAGE, notification);
    }

    public void SendLocation()
    {
        Log.i(TAG,"SendLocation");
        long now = new Date().getTime();
        if(now-lastFixTime<1000)
        {
            return;
        }

        if(lastKnownLocation==null)
        {
            return;
        }

        Fix fix = new Fix(lastKnownLocation);

        FixPacketU packet = new FixPacketU(fix);
        client.SendPacket(packet);
    }

    
    private void UpdateNotification(int notificationId, String tickerText, String statusText, int icon)
    {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager manager = (NotificationManager) getSystemService(ns);
        Notification notification = new Notification(icon,tickerText,0);
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        Context context = getApplicationContext();
        CharSequence contentTitle = "Foxhunt";
        CharSequence contentText = statusText;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        manager.notify(notificationId, notification);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onCreate() {
        
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
        application = (FoxhuntClientApplication) getApplication();
        locationListener = new FoxhuntLocationListener(this);
        application.setFoxhuntService(this);
        final Handler mHandler = new Handler();
        UpdateNotification(STATUS, "Offline", "Offline", R.drawable.fox_red32);
        client = new FoxhuntClient(application.getUsername(),application.getPassword(), application.getHost(), application.getPort());
        client.registerStateChangedListener(new FoxhuntClient.ClientStateChangeListener() {
            @Override
            public void OnStateChanged(FoxhuntClient client, FoxhuntClient.ClientState oldState, FoxhuntClient.ClientState newState, String message) {
                switch (newState)
                {
                    case Online:
                        UpdateNotification(STATUS, message !=null ? message : "Online", "Online", R.drawable.fox_blue32);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {                  
                                BeginLocationListen();
                            }
                        });
                        BeginLocationListen();
                        client.SendPacket(new EnvironmentUpdateRequestPacketU());
                        break;
                    case Offline:
                        UpdateNotification(STATUS, message !=null ? message : "Offline", "Offline", R.drawable.fox_red32);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                EndLocationListen();
                            }
                        });
                        break;
                    case Connecting:
                        UpdateNotification(STATUS, message !=null ? message : "Connecting", "Connecting", R.drawable.fox_gray32);
                        break;
                }
            }
        });
        
        
        client.registerIncomingPacketListener(new FoxhuntClient.IncomingPacketListener() {
            @Override
            public void OnIncomingPacket(FoxhuntPacket packet) {
                Log.i(TAG, "PacketReceived");
                if(packet instanceof SystemMessagePacketD)
                {
                    SystemMessagePacketD p = (SystemMessagePacketD) packet;
                    ShowSystemMessage(p.getMessage());
                }
                else if(packet instanceof EnvironmentUpdatePacketD)
                {
                    EnvironmentUpdatePacketD p = (EnvironmentUpdatePacketD) packet;
                    knownFoxes = p.getFoxes();
                    knownSpots = p.getSpots();
                    Log.i(TAG, "EnvironmentUpdate");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(application.getMainActivity()!=null)
                            {

                                application.getMainActivity().RefreshView();
                            }
                        }
                    }       );

                }
                else if(packet instanceof GameEventPacketD)
                {
                    final String msg = ((GameEventPacketD) packet).getMessage();
                    if(application.getMainActivity()!=null)
                    {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(application.getMainActivity(),msg, Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            }
        });
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager manager = (NotificationManager) getSystemService(ns);
        manager.cancelAll();

        application.setFoxhuntService(null);
        Log.i(TAG,"onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;

    }
}
