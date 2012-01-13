package com.foxhunt.proto1;

import android.location.Location;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 07.01.12
 * Time: 1:44
 * To change this template use File | com.foxhunt.proto1.Settings | File Templates.
 */
public class Fox {
    private double _lat;
    private double _lon;
    private String _name;

	public int getId()
	{
		return _id;
	}

	public void setId(int _id)
	{
		this._id = _id;
	}
	
	public Location getLocation()
	{
		Location location = new  Location("fox");
		location.setLatitude(_lat);
		location.setLongitude(_lon);
		location.setTime(new Date().getTime());
		location.setSpeed(0);
		location.setAccuracy(0);
		location.setAltitude(0);
		return location;
	}

	private int _id;

    public double getLat() {
        return _lat;
    }

    public void setLat(double _lat) {
        this._lat = _lat;
    }

    public double getLon() {
        return _lon;
    }

    public void setLon(double _lon) {
        this._lon = _lon;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }
}
