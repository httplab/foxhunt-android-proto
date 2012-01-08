package com.foxhunt.proto1;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.maps.MapActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Main extends MapActivity
{
	private FixSender _fixSender;

	public void setLastKnownLocation(Location _lastKnownLocation)
	{
		this._lastKnownLocation = _lastKnownLocation;
	}

	private  Location _lastKnownLocation=null;

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
	    _fixSender = new FixSender(getString(R.string.fix_url),(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE));
	    _fixSender.AddFixResponseListener(new FixSender.FixResponseListener()
	    {
		    @Override public void OnFixResponse(ArrayList<Fox> foxes)
		    {
			    TextView txtStatus = (TextView) findViewById(R.id.txtStatus);
			    txtStatus.setText(String.format("%tT fix sent", new Date()));
			    RefreshFoxes(foxes);
		    }
	    });
        BeginPositionListen();
	    

    }

	protected void RefreshFoxes(ArrayList<Fox> foxes)
	{
		LinearLayout llFoxes =(LinearLayout) findViewById(R.id.llFoxes);
		llFoxes.removeAllViews();

		if(foxes==null)
			return;
		
		for(Fox fox :foxes)
		{
			TextView tw = new TextView(this);
			tw.setText(String.format("%1s : %2f; %3f : %4.1f m", fox.getName(), fox.getLat(), fox.getLon(), fox.getLocation().distanceTo(_lastKnownLocation)));
			tw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fox_red22, 0, 0, 0);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			tw.setLayoutParams(params);
			llFoxes.addView(tw);
		}

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
	  _frm.setLastKnownLocation(location);
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
