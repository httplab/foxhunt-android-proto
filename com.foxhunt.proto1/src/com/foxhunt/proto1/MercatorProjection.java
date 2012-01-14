package com.foxhunt.proto1;

import android.location.Location;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 13.01.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class MercatorProjection extends Projection
{
	@Override public double getXCoord(Location location)
	{
		double longitude = Math.toRadians(location.getLongitude());
		double centerLongitude = Math.toRadians(center.getLongitude());
		double meters = (longitude-centerLongitude) * Projection.EARTH_MEAN_RADIUS;
		return meters/scale;
	}

	@Override public double getLongitude(double x, double y)
	{
		double centerLongitude = Math.toRadians(center.getLongitude());
		return Math.toDegrees(x*scale/Projection.EARTH_MEAN_RADIUS + centerLongitude);
	}

	@Override public double getLatitude(double x, double y)
	{
		double centerLatitude = Math.toRadians(center.getLatitude());
		return Math.toDegrees(Math.atan(Math.sinh(-y*scale/Projection.EARTH_MEAN_RADIUS)) + centerLatitude);
	}

	@Override public double getYCoord(Location location)
	{
		double latitude = Math.toRadians(location.getLatitude());
		double centerLatitude = Math.toRadians(center.getLatitude());
		double meters = Math.log(1+Math.sin(latitude-centerLatitude)/Math.cos(latitude-centerLatitude)) *  Projection.EARTH_MEAN_RADIUS;
		return -meters/scale;
	}

	@Override public double getLength(double length)
	{
		return length/scale;
	}

	public MercatorProjection(Location center, double scale)
	{
		super(center, scale);
	}

	public MercatorProjection()
	{
		super();
	}
}
