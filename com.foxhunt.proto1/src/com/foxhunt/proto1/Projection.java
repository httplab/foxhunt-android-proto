package com.foxhunt.proto1;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 13.01.12
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
public abstract class Projection
{
	protected Location center;
	protected double scale;
	public static final double EARTH_MEAN_RADIUS = 6371009;

	public Location getCenter()
	{
		return center;
	}

	public void setCenter(Location center)
	{
		this.center = center;
	}

	public double getScale()
	{
		return scale;
	}

	public void setScale(double scale)
	{
		this.scale = scale;
	}

	public abstract double getXCoord(Location location);
	public abstract double getYCoord(Location location);
	public abstract double getLength(double length);

	public Projection(Location center, double scale)
	{
		this.center = center;
		this.scale = scale;
	}

	public Projection()
	{
		center = new Location(LocationManager.PASSIVE_PROVIDER);
		center.setLatitude(0);
		center.setLongitude(0);
		scale = 1000;
	}
}
