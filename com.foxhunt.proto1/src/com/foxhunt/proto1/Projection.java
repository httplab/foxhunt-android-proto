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
	protected double scale;
	public static final double EARTH_MEAN_RADIUS = 6371009;
	public static final double EARTH_EQUATORIAL_RADIUS = 6378137;
	public static final double EARTH_POLAR_RADIUS = 6356752.3;
	public static final double EARTH_ECCENTRICITY = 0.0818191908426;

	public static Double RadiusAtLatitude(double latitude)
	{
		double radians = Math.toRadians(latitude); 
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);
		double num =
				EARTH_EQUATORIAL_RADIUS * EARTH_EQUATORIAL_RADIUS * EARTH_EQUATORIAL_RADIUS * EARTH_EQUATORIAL_RADIUS *
				cos * cos +
				EARTH_POLAR_RADIUS * EARTH_POLAR_RADIUS * EARTH_POLAR_RADIUS * EARTH_POLAR_RADIUS *
				sin * sin;
		double denom =
				EARTH_EQUATORIAL_RADIUS * EARTH_EQUATORIAL_RADIUS * cos * cos +
				EARTH_POLAR_RADIUS * EARTH_POLAR_RADIUS * sin * sin;
		return Math.sqrt(num/denom);
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
	public abstract double getLatitude(double x, double  y);
	public abstract double getLongitude(double x, double y);

	public Projection(double scale)
	{
		this();
		this.scale = scale;
	}

	public Projection()
	{
		scale = 20;
	}
}
