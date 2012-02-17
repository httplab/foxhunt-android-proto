package com.foxhunt.proto1;

import android.location.Location;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 17.02.12
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */
public class YandexMercatorProjection extends Projection
{
	private final double ab = 0.00335655146887969400;
	private final double bb = 0.00335655146887969400;
	private final double cb = 0.00335655146887969400;
	private final double db = 0.00335655146887969400;

	@Override public double getLatitude(double x, double y)
	{
		double xphi = Math.PI/2 - 2* Math.atan(1/Math.exp(y*scale/EARTH_EQUATORIAL_RADIUS));
		double latitudeRad = xphi + ab * Math.sin(2 * xphi) + bb * Math.sin(4 * xphi) + cb * Math.sin(6 * xphi) + db * Math.sin(8 * xphi);
		return  Math.toDegrees(latitudeRad);
	}

	@Override public double getLongitude(double x, double y)
	{
		double longitudeRad = x*scale/EARTH_EQUATORIAL_RADIUS;
		return Math.toDegrees(longitudeRad);
	}

	@Override public double getXCoord(Location location)
	{
		double longitudeRad = Math.toRadians(location.getLongitude());
		double meters = EARTH_EQUATORIAL_RADIUS * longitudeRad;
		return meters/scale;
	}

	@Override public double getYCoord(Location location)
	{
		double latitudeRad = Math.toRadians(location.getLatitude());
		double esinLat = EARTH_ECCENTRICITY * Math.sin(latitudeRad);
		double tanTemp = Math.tan(Math.PI/4.0 + latitudeRad / 2.0);
		double pow_temp = Math.pow(Math.tan(Math.PI/4.0 + Math.asin(esinLat)/2.0),2);
		double U = tanTemp/ pow_temp;
		double meters = EARTH_EQUATORIAL_RADIUS * Math.log(U);
		return meters/scale;
	}
	
	YandexMercatorProjection(double scale)
	{
		super(scale);
	}

}
