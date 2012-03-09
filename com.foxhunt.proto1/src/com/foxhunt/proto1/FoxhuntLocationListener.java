package com.foxhunt.proto1;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 09.03.12
 * Time: 3:09
 * To change this template use File | Settings | File Templates.
 */
class FoxhuntLocationListener implements LocationListener
{
    private FoxhuntService service;

    public  FoxhuntLocationListener(FoxhuntService service){
        this.service = service;
    }

    @Override
    public void onLocationChanged(Location location) {
        Date ts = new Date(location.getTime());
        service.setLastKnownLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        //To change body of implemented methods use File | com.foxhunt.proto1.Settings | File Templates.
    }

    @Override
    public void onProviderDisabled(String s) {
        //To change body of implemented methods use File | com.foxhunt.proto1.Settings | File Templates.
    }
}
