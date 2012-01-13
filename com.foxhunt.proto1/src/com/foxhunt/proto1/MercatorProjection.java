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
		double longitude = location.getLongitude();
		double centerLongitude = center.getLongitude();
		return (longitude-centerLongitude) * (Projection.EARTH_MEAN_RADIUS/scale);
	}

	@Override public double getYCoord(Location location)
	{
		double latitude = location.getLatitude();
		double centerLatitude = center.getLatitude();
		return -Math.log(1+Math.sin(latitude-centerLatitude)/Math.cos(latitude-centerLatitude)) *  (Projection.EARTH_MEAN_RADIUS/scale);
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
