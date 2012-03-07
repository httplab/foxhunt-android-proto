package com.foxhunt.proto1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.*;

public class Main extends Activity
{
	public static final String PREFS_NAME = "FoxhuntSettings";


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

	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		FoxhuntMap fxmMap= (FoxhuntMap)findViewById(R.id.fxmMap);
		fxmMap.invalidate();
		return;
	}

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, FoxhuntService.class));
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	    //String fixUrl = getSharedPreferences(PREFS_NAME,0).getString("fix_url", getResources().getString(R.string.fix_url));
	    
	    int userId = getSharedPreferences(PREFS_NAME,0).getInt("user_id", -1);
	    if(userId>=0)
	    {
//		    _fixSender = new FixSender(fixUrl,(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE),userId);
//		    _fixSender.AddFixResponseListener(new FixSender.FixResponseListener()
//		    {
//			    @Override public void OnFixResponse(ArrayList<Fox> foxes)
//			    {
//				    TextView txtStatus = (TextView) findViewById(R.id.txtStatus);
//				    txtStatus.setText(String.format("%tT fix sent", new Date()));
//				    RefreshFoxes(foxes);
//			    }
//		    });
//	        BeginPositionListen();
	    }
	    else
	    {
		    Intent intent2 = new Intent(Main.this, LoginInfoActivity.class);
		    startActivity(intent2);
	    }
    }
	
	public void btnZoomIn_click(View v)
	{
		FoxhuntMap fxmMap= (FoxhuntMap)findViewById(R.id.fxmMap);
		fxmMap.setScale(fxmMap.getScale() / 1.3);
	}

	public void btnZoomOut_click(View v)
	{
		FoxhuntMap fxmMap= (FoxhuntMap)findViewById(R.id.fxmMap);
		fxmMap.setScale(fxmMap.getScale() * 1.3);
	}

	protected void RefreshFoxes(ArrayList<Fox> foxes)
	{
		LinearLayout llFoxes =(LinearLayout) findViewById(R.id.llFoxes);
		llFoxes.removeAllViews();

		if(foxes==null)
			return;



		Collections.sort(foxes,new Comparator<Fox>()
		{
			@Override public int compare(Fox o, Fox o1)
			{
				double d1 = o.getLocation().distanceTo(_lastKnownLocation);
				double d2 = o1.getLocation().distanceTo(_lastKnownLocation);
				return Double.compare(d1,d2);
			}
		});		
		
		for(Fox fox :  foxes)
		{
			TextView tw = new TextView(this);
			tw.setText(String.format("%1s : %2f; %3f : %4.1f m", fox.getName(), fox.getLat(), fox.getLon(), fox.getLocation().distanceTo(_lastKnownLocation)));
			tw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fox_red22, 0, 0, 0);
			tw.setTextSize(2,16);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			tw.setLayoutParams(params);
			llFoxes.addView(tw);
		}

		FoxhuntMap fxmMap= (FoxhuntMap)findViewById(R.id.fxmMap);
		fxmMap.setFoxes(foxes);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
        FoxhuntClientApplication application = (FoxhuntClientApplication) getApplication();


		switch (item.getItemId()) {
			case R.id.miExit:
				AlertDialog alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.setTitle("Foxhunt");
				alertDialog.setTitle("Are you sure you want to quit Foxhunt?");
				alertDialog.setButton("Yes", new DialogInterface.OnClickListener()
				{
					@Override public void onClick(DialogInterface dialogInterface, int i)
					{
						dialogInterface.dismiss();
                        stopService(new Intent(Main.this, FoxhuntService.class));
						finish();

					}
				});

				alertDialog.setButton2("No", new DialogInterface.OnClickListener()
				{
					@Override public void onClick(DialogInterface dialogInterface, int i)
					{
						dialogInterface.dismiss();
					}
				});

				alertDialog.show();
				return true;
			case R.id.miSettings:
				Intent intent = new Intent(Main.this, Preferences.class);
				startActivity(intent);
				return true;

			case R.id.miLoginInfo:
				Intent intent2 = new Intent(Main.this, LoginInfoActivity.class);
				startActivity(intent2);
				return true;
			case R.id.miMyLocation:
				FoxhuntMap map = (FoxhuntMap)findViewById(R.id.fxmMap);
				map.setCenterOnPlayer(true);
                return true;
            case R.id.miGoOnline:
                application.getFoxhuntService().goOnline();
                return true;
            case R.id.miGoOffline:
                application.getFoxhuntService().goOffline();
                return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
    protected void BeginPositionListen()
    {
        LocationManager locationManager = (LocationManager )this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new MyListener((this)));
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new MyListener((this)));

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
	  FoxhuntMap fxmMap= (FoxhuntMap)_frm.findViewById(R.id.fxmMap);
	  fxmMap.setPlayerPosition(location);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        TextView txtStatus = (TextView) _frm.findViewById(R.id.txtStatus);
        txtStatus.setText(s);
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
