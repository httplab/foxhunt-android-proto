package com.foxhunt.proto1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.foxhunt.proto1.entity.Fox;

import java.util.*;

public class MainActivity extends Activity
{
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
        setContentView(R.layout.main);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

    public void RefreshView()
    {
        FoxhuntService foxhuntService = ((FoxhuntClientApplication) getApplication()).getFoxhuntService();
        if(foxhuntService==null)
        {
            return;
        }

        if(foxhuntService.getLastKnownLocation()==null)
        {
            return;
        }

        FoxhuntMap fxmMap= (FoxhuntMap)findViewById(R.id.fxmMap);
        fxmMap.setPlayerPosition(foxhuntService.getLastKnownLocation());
        
        if(foxhuntService.getKnownFoxes()==null)
        {
            fxmMap.setFoxes(new ArrayList<Fox>());
            return;
        }

        fxmMap.setFoxes(new ArrayList<Fox>(Arrays.asList(foxhuntService.getKnownFoxes())));
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
}


