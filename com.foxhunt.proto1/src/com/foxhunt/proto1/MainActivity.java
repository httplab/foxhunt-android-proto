package com.foxhunt.proto1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.foxhunt.proto1.entity.Fox;
import com.foxhunt.proto1.entity.Spot;
import com.google.android.maps.*;
import com.google.android.maps.Projection;

import java.util.*;

public class MainActivity extends MapActivity
{
    protected MapView mapView;
    protected MapController mapController;
    private Drawable drawable;
    private Drawable player;
    private Drawable otherPlayer;
    private RadarItemizedOverlay itemizedOverlay;
    private RadarItemizedOverlay playerOverlay;
    private Boolean isManuallyScrolled = false;
    
    private class TouchOverlay extends Overlay{
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent, MapView mapView) {
            if(motionEvent.getAction() == MotionEvent.ACTION_MOVE)
            {
                MainActivity.this.isManuallyScrolled = true;
            }
            return false;
        }
    }

    private class CircleOverlay extends Overlay{
        private Spot mainPlayer;
        private Spot[] otherPlayers;

        public CircleOverlay(Spot mainPlayer, Spot[] otherPlayers)
        {
            this.mainPlayer = mainPlayer;
            this.otherPlayers = otherPlayers;
        }
        
        @Override
        public void draw(Canvas canvas, MapView mapView, boolean b) {
            super.draw(canvas, mapView, b);    //To change body of overridden methods use File | Settings | File Templates.
            Projection projection = mapView.getProjection();

            Point mainPlayerPoint =  projection.toPixels(mainPlayer.getGeoPoint(),null);
            float mainPlayerRadius = projection.metersToEquatorPixels((float)mainPlayer.getRadius());
            
            
            Paint innerPaint = new Paint();
            Paint strokePaint = new Paint();

            innerPaint.setARGB(30,0,0,255);
            strokePaint.setARGB(255,0,0,255);
            strokePaint.setAntiAlias(true);
            strokePaint.setStrokeWidth(3);

            innerPaint.setStyle(Paint.Style.FILL);
            strokePaint.setStyle(Paint.Style.STROKE);
            

            canvas.drawCircle(mainPlayerPoint.x,mainPlayerPoint.y, mainPlayerRadius,innerPaint);
            canvas.drawCircle(mainPlayerPoint.x,mainPlayerPoint.y, mainPlayerRadius,strokePaint);

            innerPaint.setARGB(30,255,0,0);
            strokePaint.setARGB(255,255,0,0);

            for(int i=0; i<otherPlayers.length; i++)
            {
                Point p = projection.toPixels(otherPlayers[i].getGeoPoint(),null);
                float r = projection.metersToEquatorPixels((float) otherPlayers[i].getRadius());
                canvas.drawCircle(p.x,p.y, r,innerPaint);
                canvas.drawCircle(p.x,p.y, r,strokePaint);
            }

        }
    }

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
        player = this.getResources().getDrawable(R.drawable.marker_blue);
        otherPlayer = this.getResources().getDrawable(R.drawable.marker_red);
        itemizedOverlay = new RadarItemizedOverlay(drawable, this);
        playerOverlay = new RadarItemizedOverlay(player, this);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapView.getOverlays().add(new TouchOverlay());
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

    private void drawPlayer(double latitude, double longitude) {
        GeoPoint point = new GeoPoint((int) (latitude * 1e6), (int) (longitude * 1e6));
        OverlayItem overlayItem = new OverlayItem(point, "", "");
        playerOverlay.clearOverlay();
        playerOverlay.addOverlay(overlayItem);

        List<Overlay> mapOverlays = mapView.getOverlays();
        mapOverlays.add(playerOverlay);
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


        Location location = foxhuntService.getLastKnownLocation();
        if (location == null) { return; };
        drawPlayer(location.getLatitude(), location.getLongitude());
        CircleOverlay circleOverlay = new CircleOverlay(new Spot(0,location.getLatitude(), location.getLongitude(), 0, "",location.getAccuracy()), foxhuntService.getKnownSpots());
            

        mapOverlays.add(itemizedOverlay);
        mapOverlays.add(circleOverlay);
        mapOverlays.add(new TouchOverlay());
        
        if(!isManuallyScrolled)
        {
            centerOnPlayer();
        }
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


