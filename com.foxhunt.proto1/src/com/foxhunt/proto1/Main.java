package com.foxhunt.proto1;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.location.LocationManager;
import android.widget.TextView;
import com.google.android.maps.MapActivity;

import java.util.Date;

public class Main extends MapActivity
{
	private FixSender _fixSender;

	public  FixSender getFixSender()
	{
		return _fixSender;
	}

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    _fixSender = new FixSender(getString(R.string.fix_url));
	    _fixSender.AddFixResponseListener(new FixSender.FixResponseListener()
	    {
		    @Override public void OnFixResponse(Fox[] foxes)
		    {
			    TextView txtStatus = (TextView) findViewById(R.id.txtStatus);
			    txtStatus.setText(String.format("%tT fix sent", new Date()));
		    }
	    });
        BeginPositionListen();
	    

    }

    protected void BeginPositionListen()
    {
        LocationManager locationManager = (LocationManager )this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new MyListener((this)));
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new MyListener((this)));

    }

    @Override
    protected boolean isRouteDisplayed(){
        return  false;
    }
}

class MyListener implements LocationListener
{
    private Main _frm;

    public  MyListener(Main frm){
        _frm = frm;
    }


    @Override
    public void onLocationChanged(Location location) {
      Date ts = new Date(location.getTime());

      TextView txtCoords = (TextView) _frm.findViewById(R.id.txtCoords);
      txtCoords.setText(String.format("LAT %1f; LON %2f; ALT %3f m; TIME %4tT; ACC %5f m; SRC %6s", location.getLatitude(), location.getLongitude(), location.getAltitude(), ts,location.getAccuracy(), location.getProvider() ));
	  _frm.getFixSender().SendFix(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        TextView txtStatus = (TextView) _frm.findViewById(R.id.txtStatus);
        txtStatus.setText(s);
    }

    @Override
    public void onProviderEnabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onProviderDisabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
