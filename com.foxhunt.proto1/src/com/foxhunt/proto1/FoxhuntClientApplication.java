package com.foxhunt.proto1;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 05.03.12
 * Time: 19:31
 * To change this template use File | Settings | File Templates.
 */
public class FoxhuntClientApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = FoxhuntClientApplication.class.getSimpleName();
    private SharedPreferences preferences;
    private FoxhuntService service;
    private MainActivity mainActivity;


    public FoxhuntService getFoxhuntService() {
        return service;
    }

    public void setFoxhuntService(FoxhuntService service) {
        this.service = service;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        getFoxhuntService().OnPreferencesChange();
    }

    @Override
    public void onCreate() {
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.preferences.registerOnSharedPreferenceChangeListener(this);
        Log.i(TAG,"onCreate");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();    //To change body of overridden methods use File | Settings | File Templates.
        Log.i(TAG, "onTerminate");
    }

    public String getUsername(){
        return this.preferences.getString("username",null);
    }

    public int getPort(){
        String strPort = this.preferences.getString("port","9003");
        try
        {
            return Integer.parseInt(strPort);
        }
        catch (java.lang.NumberFormatException ex)
        {
            return 9003;
        }
    }
    
    public String getHost(){
        return this.preferences.getString("host",null);
    }
    
    public String getPassword(){
        return this.preferences.getString("password",null);
    }
}
