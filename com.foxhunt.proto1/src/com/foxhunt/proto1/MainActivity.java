package com.foxhunt.proto1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.foxhunt.proto1.entity.Fox;
import com.google.android.maps.*;

import java.util.*;

public class MainActivity extends MapActivity
{
    protected MapView mapView;
    protected MapController mapController;
    Drawable drawable;
    RadarItemizedOverlay itemizedOverlay;
    
    private Boolean isManuallyScrolled = false;

	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		RefreshView();
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
        setContentView(R.layout.radar);

        drawable = this.getResources().getDrawable(R.drawable.fox_red16);
        itemizedOverlay = new RadarItemizedOverlay(drawable, this);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((FoxhuntClientApplication) getApplication()).setMainActivity(this);
        RefreshView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((FoxhuntClientApplication) getApplication()).setMainActivity(null);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

    private void centerOnPlayer() {
        FoxhuntService foxhuntService = ((FoxhuntClientApplication) getApplication()).getFoxhuntService();
        if(foxhuntService==null) {
            return;
        }

        if(foxhuntService.getLastKnownLocation()==null) {
            return;
        }

        Location location = foxhuntService.getLastKnownLocation();
        if (!isManuallyScrolled) {
            mapController.setCenter(new GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6)));
        }
    }

    public void RefreshView()
    {
        Log.i("MAIN","RefreshView");
        FoxhuntService foxhuntService = ((FoxhuntClientApplication) getApplication()).getFoxhuntService();
        if(foxhuntService==null)
        {
            return;
        }
        
        Fox[] foxes = foxhuntService.getKnownFoxes();
        
        if(foxes == null)
        {
            return;
        }

        List<Overlay> mapOverlays = mapView.getOverlays();
        itemizedOverlay.clearOverlay();
        mapOverlays.clear();

        for (int i = 0; i < foxes.length; i++) {
            GeoPoint point = new GeoPoint((int) (foxes[i].getLatitude() * 1e6), (int) (foxes[i].getLongitude() * 1e6));
            OverlayItem overlayItem = new OverlayItem(point, foxes[i].getName(), "");
            itemizedOverlay.addOverlay(overlayItem);
        }

        mapOverlays.add(itemizedOverlay);
        mapView.invalidate();
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
                        stopService(new Intent(MainActivity.this, FoxhuntService.class));
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
				Intent intent = new Intent(MainActivity.this, Preferences.class);
				startActivity(intent);
				return true;

			case R.id.miLoginInfo:
				return true;
			case R.id.miMyLocation:
				//FoxhuntMap map = (FoxhuntMap)findViewById(R.id.fxmMap);
				//map.setCenterOnPlayer(true);
                isManuallyScrolled = false;
                centerOnPlayer();
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
}


