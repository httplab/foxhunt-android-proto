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
		double meters = longitude * Projection.EARTH_MEAN_RADIUS;
		return meters/scale;
	}

	@Override public double getLongitude(double x, double y)
	{
		return Math.toDegrees(x*scale/Projection.EARTH_MEAN_RADIUS);
	}

	@Override public double getLatitude(double x, double y)
	{
		return Math.toDegrees(Math.atan(Math.sinh(y*scale/Projection.EARTH_MEAN_RADIUS)));
	}

	@Override public double getYCoord(Location location)
	{
		double latitude = Math.toRadians(location.getLatitude());
		double meters = Math.log(  (1+Math.sin(latitude)) /Math.cos(latitude) ) *  Projection.EARTH_MEAN_RADIUS;
		return meters/scale;
	}

	public MercatorProjection(double scale)
	{
		super(scale);
	}

	public MercatorProjection()
	{
		super();
	}
}
